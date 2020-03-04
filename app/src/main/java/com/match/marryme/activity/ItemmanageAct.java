package com.match.marryme.activity;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.billingclient.api.Purchase;
import com.match.marryme.R;
import com.match.marryme.adapters.list.PurchaseHistoryAdapter;
import com.match.marryme.billing.BIllingManager;
import com.match.marryme.databinding.ActivityItemmanageBinding;
import com.match.marryme.listDatas.PurchaseData;
import com.match.marryme.network.ReqBasic;
import com.match.marryme.network.netUtil.HttpResult;
import com.match.marryme.network.netUtil.NetUrls;
import com.match.marryme.sharedPref.UserPref;
import com.match.marryme.utils.Common;
import com.match.marryme.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ItemmanageAct extends Activity implements View.OnClickListener {

    ActivityItemmanageBinding binding;
    Activity act;

    private static final int MESSAGE = 1000;
    private static final int INTEREST = 1001;

    private static final String TAG = "TEST_HOME";

    String iname, iprice, itype, selectedCode;
    String purchaseType;

    /* billing */
    BIllingManager manager;
    String isAvailable = "Y";

    ArrayList<PurchaseData> pur_list = new ArrayList<>();
    PurchaseHistoryAdapter adapter;

    String from;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_itemmanage);
        act = this;

        if (StringUtil.isNull(UserPref.getUidx(act))) {
            Common.showToast(act, "앱을 종료 후 재실행해주시기 바랍니다.");
            finish();
        }

        setClickListener();
        getMyProfile();
        setBilling();

        // set recycler view
        binding.rcvPurchase.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PurchaseHistoryAdapter(this, pur_list);
        binding.rcvPurchase.setAdapter(adapter);


        // first setting
        from = getIntent().getStringExtra("from");

        binding.llItemmanaget01.performClick();

        if (StringUtil.isNull(from)) {
            binding.llMessage.performClick();
        } else {
            if(from.equalsIgnoreCase("read")) {
                binding.llRead.performClick();
            } else if (from.equalsIgnoreCase("main")) {
                binding.llHome.setVisibility(View.VISIBLE);
                binding.llHome.setOnClickListener(this);
            } else {
                binding.llMessage.performClick();
            }
        }
    }

    private void setClickListener() {
        binding.flBack.setOnClickListener(this);
        binding.llItemmanaget01.setOnClickListener(this);
        binding.llItemmanaget02.setOnClickListener(this);

        binding.llMessage.setOnClickListener(this);
        binding.llRead.setOnClickListener(this);
        binding.tvBuy.setOnClickListener(this);
    }

    private void getMyProfile() {
        ReqBasic myprofile = new ReqBasic(this, NetUrls.MYPROFILE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "MyProfile List Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            JSONObject obj = new JSONObject(jo.getString("value"));

                            // 메시지 이용권 남은 일수
                            if (obj.getString("message_expiredate").equalsIgnoreCase("0000-00-00 00:00:00")) {
                                binding.tvMessageRemain.setText("0");
                            } else {
//                                binding.tvMessageRemain.setText(StringUtil.calDateBetweenAandBSub(obj.getString("message_expiredate")));
                                binding.tvMessageRemain.setText(StringUtil.calDateBetweenAandBSub("2019-03-27 00:00:00"));
                            }

                            // 관심있어요 발송권 남은 개수
                            binding.tvInterestRemain.setText(obj.getString("interest_sendcnt"));

                            // 열람권 남은 일수
                            binding.tvReadRemain.setText(obj.getString("profile_viewcnt"));

                        } else {
                            Toast.makeText(act, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        myprofile.addParams("uidx", UserPref.getUidx(this));
        myprofile.execute(true, true);
    }

    public String calDateBetweenAandB(String cmpDate) {
//        SimpleDateFormat formatNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String currentDate = formatNow.format(System.currentTimeMillis());

        try { // String Type을 Date Type으로 캐스팅하면서 생기는 예외로 인해 여기서 예외처리 해주지 않으면 컴파일러에서 에러가 발생해서 컴파일을 할 수 없다.
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // date1, date2 두 날짜를 parse()를 통해 Date형으로 변환.
            Date FirstDate = format.parse(cmpDate);
//            Date SecondDate = format.parse(String.valueOf(System.currentTimeMillis()));
            Date SecondDate = new Date();

            // Date로 변환된 두 날짜를 계산한 뒤 그 리턴값으로 long type 변수를 초기화 하고 있다.
            // 연산결과 -950400000. long type 으로 return 된다.
            long calDate = FirstDate.getTime() - SecondDate.getTime();

            // Date.getTime() 은 해당날짜를 기준으로1970년 00:00:00 부터 몇 초가 흘렀는지를 반환해준다.
            // 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
            long calDateDays = calDate / (24 * 60 * 60 * 1000);

            calDateDays = Math.abs(calDateDays);

            return String.valueOf(calDateDays);
        } catch (ParseException e) {
            // 예외 처리
            return "0";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setBilling() {
        manager = new BIllingManager(act, new BIllingManager.AfterBilling() {
            @Override
            public void sendResult(com.android.billingclient.api.Purchase purchase) {
                Log.e(TAG, "result purchase: " + purchase);
                Log.e(TAG, "purchase.getSku(): " + purchase.getSku());
                Log.e(TAG, "purchase.getPurchaseTime(): " + purchase.getPurchaseTime());
                Log.e(TAG, "purchase.getPurchaseToken(): " + purchase.getPurchaseToken());
                Log.e(TAG, "purchase.getPackageName(): " + purchase.getPackageName());
                Log.e(TAG, "purchase.getOrderId(): " + purchase.getOrderId());
                Log.e(TAG, "purchase.getDeveloperPayload(): " + purchase.getDeveloperPayload());
                Log.e(TAG, "purchase.getPurchaseState(): " + purchase.getPurchaseState());
                Log.e(TAG, "purchase.getOriginalJson(): " + purchase.getOriginalJson());
                Log.e(TAG, "purchase.getSignature(): " + purchase.getSignature());

                sendPurchaseResult(purchase);
            }

            @Override
            public void getSubsriptionState(String subscription, Purchase purchase) {
                isAvailable = subscription;
            }
        });
    }

    private void sendPurchaseResult(Purchase purchase) {
        ReqBasic buyItem = new ReqBasic(this, NetUrls.BUYITEM) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Purchase Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            Common.showToast(act, "결제가 완료되었습니다.");
                            if (!StringUtil.isNull(from)) {
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                setResult(RESULT_OK);
                                getMyProfile();
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
        buyItem.addParams("uidx", UserPref.getUidx(this));
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

    //결제테스트
    private void sendPurchaseResultTest(String itype, String iname, String iprice, String icode) {
        ReqBasic buyItem = new ReqBasic(this, NetUrls.BUYITEM) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Purchase Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            Common.showToast(act, "결제가 완료되었습니다.");
                        } else {
//                            Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
//                        Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }

                } else {
//                    Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        buyItem.addParams("uidx", UserPref.getUidx(this));
        buyItem.addParams("itype", itype);
        buyItem.addParams("isubject", iname);
        buyItem.addParams("icode", icode);

        buyItem.addParams("p_orderid", "test");
        buyItem.addParams("p_store_type", "GOOGLE");
        buyItem.addParams("p_purchasetime", "1569483662887");
        buyItem.addParams("p_purchasePrice", iprice);
        buyItem.addParams("p_signature", "test");
        buyItem.addParams("p_info", "");

        buyItem.execute(true, true);
    }


    private void getPurchaseInfo() {
        ReqBasic buyItem = new ReqBasic(this, NetUrls.BUYITEMINFO) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Purchase Info Get Info: " + jo);

                        pur_list = new ArrayList<>();
                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            JSONArray ja = jo.getJSONArray("data");
                            for (int i = ja.length() - 1; i >= 0; i--) {
                                JSONObject job = ja.getJSONObject(i);
                                Log.e(TAG, "getPurchaseInfo (" + i + "): " + job);
//                                if(!StringUtil.isNull(job.getString("p_info"))) {
//                                    JSONObject job2 = new JSONObject(job.getString("p_info"));
//                                    Log.e(TAG, "getPurchaseToken: " + job2.getString("purchaseToken"));
//                                }

                                PurchaseData data = new PurchaseData();
                                data.setType(job.getString("i_type"));
                                data.setName(job.getString("i_subject"));
                                data.setPrice(job.getString("p_purchasePrice"));

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
                                data.setDate(sdf.format(job.getLong("p_purchasetime")));

                                pur_list.add(data);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(pur_list);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
//                        Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        buyItem.addParams("uidx", UserPref.getUidx(this));
        buyItem.execute(true, true);
    }

    private void getPurchaseRemain(final String type) {
        ReqBasic buyItem = new ReqBasic(this, NetUrls.BUYITEMREMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        final JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Purchase Remain Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (type.equalsIgnoreCase("message")) {
                                            if (StringUtil.isNull(jo.getString("remain"))) {
                                                binding.tvMessageRemain.setText(jo.getString("0"));
                                            } else {
                                                binding.tvMessageRemain.setText(jo.getString("remaintime"));
                                            }
                                        } else {
                                            if (StringUtil.isNull(jo.getString("remaintime"))) {
                                                binding.tvInterestRemain.setText(jo.getString("0"));
                                            } else {
                                                binding.tvInterestRemain.setText(jo.getString("remaintime"));
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
//                        Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        buyItem.addParams("uidx", UserPref.getUidx(this));
        buyItem.addParams("itype", type);
        buyItem.execute(true, true);
    }

    private void init() {
        binding.llMessage.setSelected(false);
        binding.llRead.setSelected(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;

            case R.id.ll_home:
                ((MainActivity) MainActivity.act).fromHomeBtn();
                finish();
                break;

            case R.id.ll_message:
                init();
                binding.llMessage.setSelected(true);

                itype = "message";
                iprice = "19000";
                iname = "30일";
                selectedCode = StringUtil.MESSAGE02;
                break;

            case R.id.ll_read:
                init();
                binding.llRead.setSelected(true);

                itype = "profile";
                iprice = "3300";
                iname = "5건";
                selectedCode = StringUtil.PROFILE01;
                break;

            case R.id.ll_itemmanaget01:
                binding.llItemmanaget01.setSelected(true);
                binding.llItemmanaget02.setSelected(false);
                binding.llLeftArea.setVisibility(View.VISIBLE);
                binding.llRightArea.setVisibility(View.GONE);
                getMyProfile();
                break;

            case R.id.ll_itemmanaget02:
                binding.llItemmanaget01.setSelected(false);
                binding.llItemmanaget02.setSelected(true);
                binding.llLeftArea.setVisibility(View.GONE);
                binding.llRightArea.setVisibility(View.VISIBLE);
                getPurchaseInfo();
                break;

            case R.id.tv_buy:
                if (UserPref.getGender(act).equalsIgnoreCase("female")) {
                    Common.showToast(act, "여성회원은 무료로 적용되므로 구매하실 수 없습니다");
                } else {
                    if(StringUtil.isNull(itype)) {
                        Common.showToast(act, "구매할 아이템을 선택해주세요");
                        return;
                    }

                    if (isAvailable.equalsIgnoreCase("Y")) {
                        manager.purchase(selectedCode);
                    } else if (isAvailable.equalsIgnoreCase("pending")) {
                        Common.showToast(act, "보류중인 결제가 있습니다. 몇분후에 앱을 재실행하여 결제승인여부를 확인해주시기 바랍니다.");
                    } else if (isAvailable.equalsIgnoreCase("noAccount")) {
                        Common.showToast(act, "플레이스토어 계정정보가 없습니다");
                    } else {
                        Common.showToast(act, "플레이스토어 접속중입니다\n잠시 후 다시 시도해주세요");
                    }
                }
                break;
        }
    }
}
