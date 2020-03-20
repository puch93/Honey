package com.match.honey.billing;

import android.app.Activity;
import androidx.annotation.Nullable;
import android.util.Log;

import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.Common;
import com.match.honey.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class BIllingCheckManager implements PurchasesUpdatedListener {
    final String TAG = "TEST_INAPP";

    private BillingClient mBillingClient;
    private Activity act;

    public enum connectStatusTypes {wating, connected, fail, disconnected}

    public connectStatusTypes connectStatus = connectStatusTypes.wating;

    public List<SkuDetails> mSkuDetailsList;

    private ConsumeResponseListener mConsumeListener;
    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;

    public AfterBilling afterListener;

    String subscription_state;

    public interface AfterBilling {
        void sendResult(Purchase purchase);

        void getSubsriptionState(String subscription, Purchase purchase);
    }


    public BIllingCheckManager(final Activity act, AfterBilling after) {
        this.act = act;
        this.afterListener = after;

        Log.e(TAG, "구글 결제 매니저를 초기화 하고 있습니다.");

        mBillingClient = BillingClient.newBuilder(act).setListener(this).enablePendingPurchases().build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    connectStatus = connectStatusTypes.connected;
                    Log.e(TAG, "구글 결제 서버에 접속을 성공하였습니다.");

                    getSkuDetailList();

                    Purchase.PurchasesResult result = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);

                    Log.i(TAG, "result: " + result);
                    Log.i(TAG, "getPurchasesList: " + result.getPurchasesList());
                    Log.i(TAG, "getResponseCode: " + result.getResponseCode());

                    // 카드승인이 늦어져서, 소모가 안된 상품 존재시 소모시켜줌
                    List<Purchase> list = result.getPurchasesList();

                    for (int i = 0; i < list.size(); i++) {
                        Purchase purchase = list.get(i);
                        Log.e(TAG, "check billing(" + i + "): " + purchase);

                        // 계속 보류중일때
                        if (purchase.getPurchaseState() != Purchase.PurchaseState.PURCHASED) {
                            Log.e(TAG, "purchase.getPurchaseState(): " + purchase.getPurchaseState());
                            if (purchase.getSku().equalsIgnoreCase("message_01")) {
                                Common.showToastLong(act, "15일 이용권에대한 결제가 결제사 승인중입니다. 몇 분 후에 재로그인해주시기 바랍니다.");
                            } else {
                                Common.showToastLong(act, "30일 이용권에대한 결제가 결제사 승인중입니다. 몇 분 후에 재로그인해주시기 바랍니다.");
                            }
                        } else {
                            // 카드사 승인된경우
                            handlePurchase(list.get(i));
                        }
                    }

//                    String message = "";
//                    if (nonePurchaseList.size() != 0) {
//                        message += "카드사 승인 대기 주문: ";
//                        for (int i = 0; i < nonePurchaseList.size(); i++) {
//                            message += nonePurchaseList.get(i);
//                            if (i != nonePurchaseList.size() - 1) {
//                                message += ", ";
//                            }
//                        }
//
//                        if (successPurchaseList.size() != 0) {
//                            message += "\n";
//                        }
//                    }
//
//                    if (successPurchaseList.size() != 0) {
//                        message += "결제 성공 주문: ";
//                        for (int i = 0; i < successPurchaseList.size(); i++) {
//                            message += successPurchaseList.get(i);
//                            if (i != successPurchaseList.size() - 1) {
//                                message += ", ";
//                            }
//                        }
//                    }
//
//                    if(!message.equalsIgnoreCase("")) {
//                        Common.showToastLong(act, message);
//                    }

                    //오류
                } else {
                    connectStatus = connectStatusTypes.fail;
                    Log.e(TAG, "구글 결제 서버 접속에 실패하였습니다.\n오류코드: " + billingResult.getResponseCode());
                    subscription_state = "noAccount";
                    afterListener.getSubsriptionState(subscription_state, null);
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                connectStatus = connectStatusTypes.disconnected;
                Log.e(TAG, "구글 결제 서버와 접속이 끊어졌습니다.");
            }
        });

        mConsumeListener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.e(TAG, "onConsumeResponse true in");
                    Log.e(TAG, "상품을 성공적으로 소모하였습니다. 소모된 상품 => " + purchaseToken);
                    return;
                } else {
                    Log.e(TAG, "onConsumeResponse false in");
                    Log.e(TAG, "상품 소모에 실패하였습니다. 오류코드 (" + billingResult.getResponseCode() + "), 대상 상품 코드: " + purchaseToken);
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.DEVELOPER_ERROR) {
//                        Common.showToastLong(act, "카드사 승인이 늦어지고 있습니다. 몇분 후 앱을 재실행하여 결제가 정상적으로 진행되었는지 확인해주시기 바랍니다.");
                    }
                    return;
                }
            }
        };

        acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.e(TAG, "상품을 성공적으로 소모하였습니다.");
                    return;
                } else {
                    Log.e(TAG, "상품 소모에 실패하였습니다. 오류코드 (" + billingResult.getResponseCode() + ")");
                    return;
                }
            }
        };
    }

    //구입 가능한 상품의 리스트를 받아 오는 메소드
    public void getSkuDetailList() {
        //구글 상품 정보들의 ID를 만들어 줌
        List<String> Sku_ID_List = new ArrayList<>();
        Sku_ID_List.add(StringUtil.MESSAGE02);
        Sku_ID_List.add(StringUtil.PROFILE01);


        //1회성 소모품에 대한 SkuDetailsList 객체를 만듬
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();

        params.setSkusList(Sku_ID_List).setType(BillingClient.SkuType.INAPP);


        //비동기 상태로 앱의 정보를 가지고 옴
        mBillingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                //상품 정보를 가지고 오지 못한 경우
                if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    Log.e(TAG, "상품 정보를 가지고 오던 중 오류가 발생했습니다.\n오류코드: " + billingResult.getResponseCode());
                    subscription_state = "error";
                    return;
                }

                if (skuDetailsList == null) {
                    Log.e(TAG, "상품 정보가 존재하지 않습니다.");
                    return;
                }

                //응답 받은 데이터들의 숫자를 출력
                Log.e(TAG, "응답 받은 데이터 숫자: " + skuDetailsList.size());

                //받아온 상품 정보를 차례로 호출
                for (int sku_idx = 0; sku_idx < skuDetailsList.size(); sku_idx++) {
                    //해당 인덱스의 객체를 가지고 옴
                    SkuDetails _skuDetail = skuDetailsList.get(sku_idx);

                    //해당 인덱스의 상품 정보를 출력
                    Log.e(TAG, _skuDetail.getSku() + ": " + _skuDetail.getTitle() + ", " + _skuDetail.getPrice());
                    Log.e(TAG, _skuDetail.getOriginalJson());
                }

                //받은 값을 멤버 변수로 저장
                mSkuDetailsList = skuDetailsList;
            }
        });
    }


    //실제 구입 처리를 하는 메소드
    public void purchase(String item) {
        Log.e(TAG, "purchase: " + item);

        if (mSkuDetailsList != null) {
            SkuDetails skuDetails = null;
            for (int i = 0; i < mSkuDetailsList.size(); i++) {
                SkuDetails details = mSkuDetailsList.get(i);
                if (details.getSku().equals(item)) {
                    skuDetails = details;
                    break;
                }
            }

            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetails)
                    .build();
            BillingResult billingResult = mBillingClient.launchBillingFlow(act, flowParams);
        } else {
            subscription_state = "noAccount";
            afterListener.getSubsriptionState(subscription_state, null);
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        //결제에 성공한 경우
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            Log.e(TAG, "결제에 성공했으며, 아래에 구매한 상품들이 나열됨");

            for (Purchase _pur : purchases) {
                handlePurchase(_pur);
            }
        }

        //사용자가 결제를 취소한 경우
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.e(TAG, "사용자에 의해 결제취소");
        }

        //그 외에 다른 결제 실패 이유
        else {
            Log.e(TAG, "결제가 취소 되었습니다. 종료코드: " + billingResult.getResponseCode());
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                Common.showToastLong(act, "이미 진행중인 결제입니다. 몇분 후 앱을 재실행하여 결제가 정상적으로 진행되었는지 확인해주시기 바랍니다.");
            }
        }
    }


    void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            // Grant entitlement to the user.
//            afterListener.sendResult(purchase);

            sendPurchaseResult(purchase);

            //기본인앱상품
            ConsumeParams consumeParams =
                    ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
            mBillingClient.consumeAsync(consumeParams, mConsumeListener);


            // Acknowledge the purchase if it hasn't already been acknowledged.
            //구독
//            if (!purchase.isAcknowledged()) {
//                AcknowledgePurchaseParams acknowledgePurchaseParams =
//                        AcknowledgePurchaseParams.newBuilder()
//                                .setPurchaseToken(purchase.getPurchaseToken())
//                                .build();
//                mBillingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
//
//
//                afterListener.sendResult(purchase);
//                afterListener.getSubsriptionState("Y", null);
//            }

        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
            // Here you can confirm to the user that they've started the pending
            // purchase, and to complete it, they should follow instructions that
            // are given to them. You can also choose to remind the user in the
            // future to complete the purchase if you detect that it is still
            // pending.
        }
    }

    private void sendPurchaseResult(final Purchase purchase) {
        ReqBasic buyItem = new ReqBasic(act, NetUrls.BUYITEM) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Purchase Get Info (Check): " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            if (purchase.getSku().equalsIgnoreCase("message_02")) {
                                Common.showToastLong(act, "30일 이용권에대한 충전이 완료되었습니다.");
                            } else {
                                Common.showToastLong(act, "프로필 열람권에대한 충전이 완료되었습니다.");
                            }
                        } else {
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        };

        String sku = purchase.getSku();
        String price;
        String name;
        String type;
        if (StringUtil.isNull(sku)) {
            return;
        } else {
            if (sku.equalsIgnoreCase("message_02")) {
                price = "19000";
                name = "30일";
                type = "message";
            } else {
                price = "3300";
                name = "5건";
                type = "profile";
            }
        }
        buyItem.addParams("uidx", UserPref.getUidx(act));
        buyItem.addParams("itype", type);
        buyItem.addParams("isubject", name);

        buyItem.addParams("p_orderid", purchase.getOrderId());
        buyItem.addParams("p_store_type", "GOOGLE");
        buyItem.addParams("p_purchasetime", String.valueOf(purchase.getPurchaseTime()));
        buyItem.addParams("p_purchasePrice", price);
        buyItem.addParams("p_signature", purchase.getPurchaseToken());
        buyItem.addParams("p_info", purchase.getOriginalJson());
        buyItem.addParams("icode", purchase.getSku());

        buyItem.execute(true, true);
    }
}
