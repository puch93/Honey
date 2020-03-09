package com.match.honey.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.Purchase;
import com.match.honey.R;
import com.match.honey.adapters.list.MemberListMainAdapter;
import com.match.honey.billing.BIllingCheckManager;
import com.match.honey.databinding.ActivityMainBinding;
import com.match.honey.dialog.DlgAdviewFullScreen;
import com.match.honey.fragment.BaseFrag;
import com.match.honey.fragment.InterestFrag;
import com.match.honey.fragment.MessageBoxFrag;
import com.match.honey.fragment.NewMemberFrag;
import com.match.honey.fragment.TodayMemberFrag;
import com.match.honey.fragment.ViewingFrag;
import com.match.honey.listDatas.LastnameData;
import com.match.honey.listDatas.MemberData;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.ChangeProfVal;
import com.match.honey.utils.Common;
import com.match.honey.utils.DefaultValue;
import com.match.honey.utils.ItemOffsetDecoration;
import com.match.honey.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityMainBinding binding;
    public static Activity act;

    ChangeProfVal cpv;
    private static final String TAG = "TEST_HOME";

    private onKeyBackPressedListener mOnKeyBackPressedListener = null;

    ArrayList<LastnameData> lastnames_arr = new ArrayList<>();
    ArrayList<MemberData> memlist = new ArrayList<>();
    MemberListMainAdapter adapter;
//    LinearLayoutManagerWrapper layoutManager2;
    LinearLayoutManager layoutManager;

    String addsearch = "";
    String scval1 = "";
    String scval2 = "";
    String scloc = "";
    String sclat = "";
    String srloc1 = "";
    String srloc2 = "";
    String sclon = "";
    String scgen = "";
    String scage = "";
    String screli = "";
    String scann = "";
    String scprop = "";
    String scblood = "";
    String scedu = "";
    String scMarriage = "";
    String scsmoke = "";
    String scdrink = "";
    String pictureYN = "";
    String childExist = "all";

    String enter, msg_from, room_idx;

    public static String currentFrag = "main";

    /* billing */
    BIllingCheckManager manager;

    /* paging */
    private boolean isScroll = true;
    private boolean isPossibleRefresh = false;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        act = this;
        cpv = new ChangeProfVal();

        enter = getIntent().getStringExtra("enter");
        msg_from = getIntent().getStringExtra("msg_from");
        room_idx = getIntent().getStringExtra("room_idx");

//        getHashKey();
//        getReleaseHashKey();

        setBilling();

        if (!StringUtil.isNull(enter)) {
            if (enter.equalsIgnoreCase("push")) {
                Intent main = new Intent(this, ChatAct.class);
                main.putExtra("enter", enter);
                main.putExtra("msg_from", msg_from);
                main.putExtra("room_idx", room_idx);
                startActivity(main);
            }
        }

        setClick();
        setListener();

        if (!UserPref.isRegProf(this)) {
            showDlgRegProf();
        }

//        layoutManager2 = new LinearLayoutManagerWrapper(act, LinearLayoutManager.VERTICAL, false);
        layoutManager = new LinearLayoutManager(act);
        binding.frameMain.rcvMember.setLayoutManager(layoutManager);
        adapter = new MemberListMainAdapter(act, memlist);
        binding.frameMain.rcvMember.setAdapter(adapter);
        binding.frameMain.rcvMember.addItemDecoration(new ItemOffsetDecoration(act));

        binding.frameMain.rcvMember.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalCount = layoutManager.getItemCount();
                int lastItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                if (!isScroll) {
                    if (lastItemPosition == totalCount - 1) {
                        isScroll = true;
                        ++page;
                        getListCheck(false);
                    }
                }
            }
        });

        getListCheck(true);


        sendAccessState();

        //팝업광고확인
        checkPopUpAds();

        setMessageCount();

//        setMenuCount(NetUrls.TODAYCOUNT);
        setMenuCount(NetUrls.VIEWCOUNT);
        setMenuCount(NetUrls.NEWCOUNT);
        setMenuCount(NetUrls.LIKECOUNT);


        Log.e(TAG, "package: " + getComponentName().getPackageName() + ", " + getComponentName().getClassName());


        /* 1 */
//        int badgeCount = 10;
//        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
//        intent.putExtra("badge_count", badgeCount);
//        //앱의  패키지 명
//        intent.putExtra("badge_count_package_name",getPackageName());
//        // AndroidManifest.xml에 정의된 메인 activity 명
//        intent.putExtra("badge_count_class_name", getComponentName().getClassName());
//        sendBroadcast(intent);

        /* 2 */
//        String launcherClassName = getLauncherClassName(act);
//        Log.e(TAG, "launcherClassName: " + launcherClassName);
//        if (launcherClassName == null) { return; }
//        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
//        intent.putExtra("badge_count", 50);
//        intent.putExtra("badge_count_package_name", getPackageName());
//        intent.putExtra("badge_count_class_name", launcherClassName);
//        sendBroadcast(intent);
    }


//    public static String getLauncherClassName(Context context) {
//        PackageManager pm = context.getPackageManager();
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
//        for (ResolveInfo resolveInfo : resolveInfos) {
//            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
//            if(pkgName.equalsIgnoreCase(context.getPackageName())) {
//                String className = resolveInfo.activityInfo.name; return className;
//            }
//        }
//        return null;
//    }

    private void setBilling() {
        manager = new BIllingCheckManager(act, new BIllingCheckManager.AfterBilling() {
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
            }

            @Override
            public void getSubsriptionState(String subscription, Purchase purchase) {
            }
        });
    }

    private void checkPopUpAds() {
        ReqBasic logout = new ReqBasic(this, NetUrls.POPUPADS) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            JSONObject jo2 = jo.getJSONObject("value");
                            JSONObject job = jo2.getJSONObject("frontinfo");

                            String imgUrl = job.getString("filename");
                            String url = job.getString("url");
                            Intent intent = new Intent(act, DlgAdviewFullScreen.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("imgurl", imgUrl);
                            intent.putExtra("targeturl", url);
                            intent.putExtra("from", "main");

                            PendingIntent pendingIntent = PendingIntent.getActivity(act, 1, intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                            try {
                                pendingIntent.send();
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }

//                            Intent intent = new Intent(act, DlgPopupAds.class);
//                            intent.putExtra("url", jo.getString("url"));
//                            intent.putExtra("imageUrl", jo.getString("imgUrl"));
                            startActivity(intent);
                        } else {
                            Log.e(TAG, "message: " + jo.getString("comment"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        };
        logout.setTag("Main Popup Ads");
        logout.execute(true, false);
    }

    public void setMessageCount() {
        Log.e(TAG, "setMessageCount 실행");
        ReqBasic chatcount = new ReqBasic(this, NetUrls.CHATCOUNT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            final String msgcnt = jo.getString("value");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 안읽은 갯수 새로고침
                                    int msgcntInt = Integer.parseInt(msgcnt);
                                    if (msgcntInt == 0) {
                                        binding.llMessageCountArea.setVisibility(View.GONE);
                                    } else {
                                        binding.llMessageCountArea.setVisibility(View.VISIBLE);
                                        if (msgcntInt > 99) {
                                            binding.tvMessageCount.setText("99");
                                        } else {
                                            binding.tvMessageCount.setText(msgcnt);
                                        }
                                    }

                                    // 채팅 리스트 새로고침
                                    if (MsgboxAct.act != null) {
                                        ((MsgboxAct) MsgboxAct.act).getChatList();
                                    }

                                    if (currentFrag.equalsIgnoreCase("chat")) {
                                        MessageBoxFrag fragment = (MessageBoxFrag) getSupportFragmentManager().findFragmentById(R.id.replacements);
                                        fragment.getChatList();
                                    }
                                }
                            });
                        } else {
                            final String message = jo.getString("message");
                            Log.e(StringUtil.TAG, "message: " + message);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.tvMessageCount.setText(null);
                                    binding.llMessageCountArea.setVisibility(View.GONE);

                                    // 채팅 리스트 새로고침
                                    if (MsgboxAct.act != null) {
                                        ((MsgboxAct) MsgboxAct.act).getChatList();
                                    }

                                    if (currentFrag.equalsIgnoreCase("chat")) {
                                        MessageBoxFrag fragment = (MessageBoxFrag) getSupportFragmentManager().findFragmentById(R.id.replacements);
                                        if (fragment != null) {
                                            fragment.getChatList();
                                        }
                                    }
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        };

        chatcount.setTag("Message Count");
        chatcount.addParams("uidx", UserPref.getUidx(this));
        chatcount.execute(true, false);
    }

    public void setMenuCount(final String address) {
        ReqBasic chatcount = new ReqBasic(this, address) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            int count = Integer.parseInt(jo.getString("value"));
                            setMenuCountSub(address, count);
                        } else {
                            setMenuCountSub(address, 0);
                            Log.e(StringUtil.TAG, "fail");
                            String message = jo.getString("message");
                            Log.e(StringUtil.TAG, "fail message: " + message);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        };

        chatcount.setTag(address + " Count");
        chatcount.addParams("u_idx", UserPref.getUidx(this));
        chatcount.execute(true, false);
    }

    public void setReadProcess(final String type) {
        ReqBasic chatcount = new ReqBasic(this, NetUrls.SETMENUCOUNT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {

                        } else {
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    switch (type) {
                        case "today":
                            setMenuCount(NetUrls.TODAYCOUNT);
                            break;
                        case "view":
                            setMenuCount(NetUrls.VIEWCOUNT);
                            break;
                        case "new":
                            setMenuCount(NetUrls.NEWCOUNT);
                            break;
                        case "like":
                            setMenuCount(NetUrls.LIKECOUNT);
                            break;
                    }
                } else {

                }
            }
        };

        chatcount.setTag(type + " Read Process");
        chatcount.addParams("uidx", UserPref.getUidx(this));
        chatcount.addParams("mtype", type);
        chatcount.execute(true, false);
    }

    private void setMenuCountSub(final String address, final int count) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                LinearLayout ll_count_area = null;
                TextView tv_count = null;

                switch (address) {
                    case NetUrls.TODAYCOUNT:
                        tv_count = binding.tvTodayCount;
                        ll_count_area = binding.llTodayCountArea;
                        break;

                    case NetUrls.VIEWCOUNT:
                        tv_count = binding.tvViewCount;
                        ll_count_area = binding.llViewCountArea;
                        break;

                    case NetUrls.NEWCOUNT:
                        tv_count = binding.tvNewCount;
                        ll_count_area = binding.llNewCountArea;
                        break;

                    case NetUrls.LIKECOUNT:
                        tv_count = binding.tvLikeCount;
                        ll_count_area = binding.llLikeCountArea;
                        break;
                }


                if (count == 0) {
                    ll_count_area.setVisibility(View.GONE);
                } else {
                    ll_count_area.setVisibility(View.VISIBLE);
                    if (count > 99) {
                        tv_count.setText("99");
                    } else {
                        Log.e(TAG, "count: " + count);
                        tv_count.setText(String.valueOf(count));
                    }
                }
            }
        });
    }

    private void getHashKey() {
        try {                                                        // 패키지이름을 입력해줍니다.
            PackageInfo info = getPackageManager().getPackageInfo("com.match.honey", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d(TAG, "key_hash=" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void getReleaseHashKey() {
        byte[] sha1 = {
                0x72, 0x4B, (byte) 0x9D, 0x75, (byte) 0x9D, 0x5F, 0x1D, 0x47, (byte) 0xCF, (byte) 0xA7, 0x6E, 0x21, (byte) 0xB0, (byte) 0xAE, 0x01, 0x61, (byte) 0x81, (byte) 0xCB, 0x32, (byte) 0xD5
        };
        Log.e(TAG, "getReleaseHashKey: " + Base64.encodeToString(sha1, Base64.NO_WRAP));
    }

    public void sendAccessState() {
        ReqBasic logout = new ReqBasic(this, NetUrls.ACCESSCHECK) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {

                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Access Check Get Info: " + jo);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        };

        logout.addParams("uidx", UserPref.getUidx(this));
        logout.execute(true, false);
    }

    public void switchFrame(boolean isMain) {
        View home = findViewById(R.id.frame_main);
        if (isMain) {
            currentFrag = "main";

            initBottomBtn();
            binding.replacements.setVisibility(View.INVISIBLE);
            home.setVisibility(View.VISIBLE);
        } else {
            binding.replacements.setVisibility(View.VISIBLE);
            home.setVisibility(View.INVISIBLE);
        }
    }

    private void initBottomBtn() {
        binding.llTodayMember.setSelected(false);
        binding.llViewMember.setSelected(false);
        binding.llMessageBox.setSelected(false);
        binding.llNewMember.setSelected(false);
        binding.llLikeMember.setSelected(false);
    }


    private void setListener() {
        binding.frameMain.layoutTotalmenu.allArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (binding.frameMain.layoutTotalmenu.getRoot().getVisibility() == View.VISIBLE) {
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void setClick() {
        /* 하단바 */
        binding.llTodayMember.setOnClickListener(this);
        binding.llViewMember.setOnClickListener(this);
        binding.llMessageBox.setOnClickListener(this);
        binding.llNewMember.setOnClickListener(this);
        binding.llLikeMember.setOnClickListener(this);


        /* 검색창 */
        binding.frameMain.btnTotalmenu.setOnClickListener(this);
        binding.frameMain.btnMyprofile.setOnClickListener(this);
        binding.frameMain.btnRefresh.setOnClickListener(this);
        binding.frameMain.flRefresh.setOnClickListener(this);
        binding.frameMain.llFilterTotal.setOnClickListener(this);
        binding.frameMain.llFilterRecency.setOnClickListener(this);
        binding.frameMain.llFilterGender.setOnClickListener(this);
        binding.frameMain.llFilterLastname.setOnClickListener(this);
        binding.frameMain.llBtnAddsearch.setOnClickListener(this);


        /* 메뉴 */
        ((LinearLayout) findViewById(R.id.ll_notice)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.ll_block_know_member)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.ll_review)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.ll_profile_revise)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.ll_block_member)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.ll_buy_item)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.ll_service_center)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.ll_report)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.ll_alliance)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.ll_talk_board)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.ll_record)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.ll_alarm)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.ll_invite_message)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.ll_invite_kakao)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.ll_dormant)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.ll_leaving)).setOnClickListener(this);
    }

    private void getMyProfile() {
        if (checkIsSearch()) {
            getMemberList();
        } else {
            isScroll = true;

            ReqBasic myprofile = new ReqBasic(this, NetUrls.MYPROFILE) {
                @Override
                public void onAfter(int resultCode, HttpResult resultData) {

                    if (resultData.getResult() != null) {

                        try {
                            JSONObject jo = new JSONObject(resultData.getResult());
                            Log.e("TEST_HOME", "ModifyAct List Get Info: " + jo);

                            if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                                JSONObject obj = new JSONObject(jo.getString("value"));

                                MemberData data = new MemberData();
                                data.setMe(true);

                                SimpleDateFormat orgin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm");

                                try {
                                    Date old = orgin.parse(obj.getString("recentdate"));
                                    data.setRegDate(sdf.format(old));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                data.setMidx(obj.getString("u_idx"));
                                data.setContent(obj.getString("p_introduce"));
                                data.setMembertype(obj.getString("couple_type"));
                                data.setNickname(obj.getString("nick"));

                                data.setAge(StringUtil.calcAge(obj.getString("byear")));
                                data.setFashion01(obj.getString("passionstyle1"));
                                data.setFashion02(obj.getString("passionstyle2"));
                                data.setGender(obj.getString("gender"));
//                                data.setIntroduce(obj.getString("p_introduce"));
                                data.setLocation(obj.getString("addr1"));
                                data.setLocation2(obj.getString("addr2"));
                                data.setProfimg(obj.getString("pimg"));
                                data.setPimg_ck(obj.getString("pimg_ck"));
                                data.setPint_ck(obj.getString("pint_ck"));
                                data.setCharacter(Common.getCharacterDrawable(obj.getString("character"), obj.getString("gender")));

                                data.setFamilyname(obj.getString("familyname"));
                                data.setName(obj.getString("name"));
                                data.setLastnameState(obj.getString("lastnameYN"));

                                data.setLat(obj.getString("lat"));
                                data.setLon(obj.getString("lon"));
                                if (obj.has("piimgcnt")) {
                                    data.setPiimgcnt(obj.getString("piimgcnt"));
                                }

                                memlist.add(data);
                                isScroll = false;

                                getMemberList();
                            } else {
                                isScroll = false;
                                Toast.makeText(act, jo.getString("value"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            isScroll = false;
                            e.printStackTrace();
                            Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        isScroll = false;
                        Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }

                }
            };

            myprofile.addParams("uidx", UserPref.getUidx(this));
            myprofile.execute(true, false);
        }
    }

    private void getListCheck(boolean isFirst) {
        if(isFirst) {
            page = 1;
            memlist.clear();
            memlist = new ArrayList<>();

            getMyProfile();
        } else {
            getMemberList();
        }
    }

    private void getMemberList() {
        isScroll = true;

        ReqBasic memList = new ReqBasic(act, NetUrls.MEMBERLIST) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {

                            JSONArray ja = new JSONArray(jo.getString("value"));

                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject obj = ja.getJSONObject(i);
                                Log.e(StringUtil.TAG, "Member List Get Info(" + i + "): " + obj);

                                MemberData data = new MemberData();

                                // 내껀지 아닌지 체크
                                data.setMe(false);

                                // 접속 날짜
                                SimpleDateFormat orgin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm");
                                try {
                                    Date old = orgin.parse(obj.getString("recentdate"));
                                    data.setRegDate(sdf.format(old));
                                    Log.e("TEST_TEST", "Member List " + i + " : " + obj.getString("recentdate"));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                // 프로필 사진 카운트
                                if (obj.has("piimgcnt")) {
                                    data.setPiimgcnt(obj.getString("piimgcnt"));
                                }

                                data.setMidx(obj.getString("u_idx"));
                                data.setContent(obj.getString("p_introduce"));
                                data.setMembertype(obj.getString("couple_type"));
                                data.setNickname(obj.getString("nick"));

                                data.setAge(StringUtil.calcAge(obj.getString("byear")));
                                data.setFashion01(obj.getString("passionstyle1"));
                                data.setFashion02(obj.getString("passionstyle2"));
                                data.setGender(obj.getString("gender"));
//                                data.setIntroduce(obj.getString("p_introduce"));
                                data.setLocation(obj.getString("addr1"));
                                data.setLocation2(obj.getString("addr2"));
                                data.setProfimg(obj.getString("pimg"));
                                data.setPimg_ck(obj.getString("pimg_ck"));
                                data.setPint_ck(obj.getString("pint_ck"));
                                data.setCharacter(Common.getCharacterDrawable(obj.getString("character"), obj.getString("gender")));
                                data.setZodiac(obj.getString("zodiac"));

                                data.setFamilyname(obj.getString("familyname"));
                                data.setName(obj.getString("name"));
                                data.setLastnameState(obj.getString("lastnameYN"));

                                data.setLat(obj.getString("lat"));
                                data.setLon(obj.getString("lon"));

                                memlist.add(data);
                            }

                            if (scval2.equalsIgnoreCase("distance")) {
                                // 정렬
                                AscendingObj asc = new AscendingObj();
                                Collections.sort(memlist, asc);
                            }

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    isScroll = false;

                                    adapter.setList(memlist);
                                    adapter.notifyDataSetChanged();

                                    if(page == 1) {
                                        binding.frameMain.rcvMember.scrollToPosition(0);
                                    }
                                }
                            });

                        } else {
                            isScroll = false;
                        }
                    } catch (JSONException e) {
                        isScroll = false;

                        e.printStackTrace();
                        Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    isScroll = false;

                    Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }

            }
        };

        memList.setTag("Member List");

        memList.addParams("uidx", UserPref.getUidx(act));
        memList.addParams("srloc1", srloc1);
        memList.addParams("srloc2", srloc2);
        if (scval2.equalsIgnoreCase("distance")) {
            memList.addParams("lat", sclat);
            memList.addParams("lon", sclon);
        }
        memList.addParams("gender", scgen);
        memList.addParams("newYN", "N");
        memList.addParams("byear", scage);
        memList.addParams("ct", scMarriage);

        memList.addParams("religion", screli);
        memList.addParams("annual", scann);
        memList.addParams("property", scprop);
        memList.addParams("blood", scblood);
        memList.addParams("education", scedu);
        memList.addParams("smokeYN", scsmoke);
        memList.addParams("drinkYN", scdrink);
        memList.addParams("childcnt", childExist);
        memList.addParams("pictureYN", pictureYN);

        // 페이징
        memList.addParams("pn", String.valueOf(page));

        memList.execute(true, true);
    }

    private boolean checkIsSearch() {
        if (
                StringUtil.isNull(srloc1) &&
                        StringUtil.isNull(srloc1) &&
                        StringUtil.isNull(scgen) &&
                        StringUtil.isNull(scage) &&
                        StringUtil.isNull(scMarriage) &&
                        StringUtil.isNull(screli) &&
                        StringUtil.isNull(scann) &&
                        StringUtil.isNull(scprop) &&
                        StringUtil.isNull(scblood) &&
                        StringUtil.isNull(scedu) &&
                        StringUtil.isNull(scsmoke) &&
                        StringUtil.isNull(scdrink) &&
                        childExist.equalsIgnoreCase("all") &&
                        StringUtil.isNull(pictureYN)
        ) {
            return false;
        } else {
            return true;
        }
    }

    private void inviteKakao() {
        Intent in = new Intent(Intent.ACTION_SEND);
        in.addCategory(Intent.CATEGORY_DEFAULT);
        in.putExtra(Intent.EXTRA_TEXT, "평생의 인연 만들기 앱 [여보자기]\n지금 바로 아래 링크를 통해\n여보자기를 이용해 보세요\nhttps://play.google.com/store/apps/details?id=com.match.honey");
        in.setType("text/plain");
        in.setPackage("com.kakao.talk");
        startActivity(Intent.createChooser(in, "친구초대"));
    }


    private void inviteMessage() {
        String path = Environment.getExternalStorageDirectory() + "/MARRYME/";
        String filename = "marryme.png";

        if (!(new File(path).exists())) {
            new File(path).mkdirs();
        }

        File f = new File(path + filename);
        if (!f.exists()) {

            InputStream fin = getResources().openRawResource(R.raw.sns);
            int size = 0;
            byte[] buffer = null;

            try {
                size = fin.available();
                buffer = new byte[size];
                fin.read(buffer);
                fin.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream fos;

            try {
                fos = new FileOutputStream(path + filename);
                fos.write(buffer);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(act,
                    "com.match.honey.provider", f);
        } else {
            uri = Uri.fromFile(f);
        }

        Intent inviteIntent = new Intent(Intent.ACTION_SEND);
        inviteIntent.putExtra("subject", "[여보자기]");
        inviteIntent.addCategory("android.intent.category.DEFAULT");
        inviteIntent.putExtra("sms_body", "평생의 인연 만들기 앱 [여보자기]!\n지금 바로 아래 링크를 통해\n여보자기를 이용해 보세요\nhttps://play.google.com/store/apps/details?id=com.match.honey");
        inviteIntent.putExtra(Intent.EXTRA_STREAM, uri);
        inviteIntent.setType("image/*");

        startActivity(Intent.createChooser(inviteIntent, "문자초대"));
    }


    private String calcDist(double lat1, double lon1, double lat2, double lon2) {

//        Log.i(StringUtil.TAG, "lat1: "+lat1+" lon1: "+lon1+" lat2: "+lat2+" lon2: "+lon2);
        double EarthR = 6371000.0;
        double Rad = Math.PI / 180;
        double radLat1 = Rad * lat1;
        double radLat2 = Rad * lat2;
        double radDist = Rad * (lon1 - lon2);

        double dist = Math.sin(radLat1) * Math.sin(radLat2);
        dist = dist + Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radDist);

        double ret = EarthR * Math.acos(dist);

        double rslt = Math.round(Math.round(ret) / 1000);

        String result = String.format("%.2f", rslt);
        if (rslt == 0) {
            ret = ret / 1000;
            result = String.format("%.2f", ret);
        }

        if ((lat1 == 0 && lon1 == 0) || (lat2 == 0 && lon2 == 0)) {
            result = "-";
        }

        return result;
    }


    class AscendingObj implements Comparator<MemberData> {
        @Override
        public int compare(MemberData o1, MemberData o2) {

            if (o1.getDist().equalsIgnoreCase("-")) {
                if (o2.getDist().equalsIgnoreCase("-")) {
                    return 0;
                } else {
                    return 1;
                }
            } else if (o2.getDist().equalsIgnoreCase("-")) {
                return -1;
            } else {
                if (Double.parseDouble(o1.getDist()) > Double.parseDouble(o2.getDist())) {
                    return 1;
                } else if (Double.parseDouble(o1.getDist()) < Double.parseDouble(o2.getDist())) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        binding.frameMain.rcvMember.stopScroll();

        if (data == null) {
            Log.i(StringUtil.TAG, "data == null");
            return;
        }

        String result = data.getStringExtra("data");

        switch (requestCode) {
            case DefaultValue.TOTALSEARCH:
                binding.frameMain.totalsearchText.setText(result);

                if (result.equalsIgnoreCase("전체")) {
                    binding.frameMain.totalsearchText.setTextColor(getResources().getColor(R.color.color_2));
                    binding.frameMain.totalArrow.setImageResource(R.drawable.btn_main_categorydrop);
                    scval1 = "";
                } else {
                    binding.frameMain.totalsearchText.setTextColor(getResources().getColor(R.color.color_ea34));
                    binding.frameMain.totalArrow.setImageResource(R.drawable.btn_main_categorydrop_on);
                    if (result.equalsIgnoreCase("결혼")) {
                        scval1 = "married";
                    } else if (result.equalsIgnoreCase("재혼")) {
                        scval1 = "remarry";
                    } else {
                        scval1 = "friend";
                    }
                }

                if (memlist.size() > 0) {
                    memlist.clear();
                }
//                getMemberList(scval1, scval2);
                getListCheck(true);
                break;

            case DefaultValue.GENDERSEARCH:

//                String[] gItem = result.split("#");
//
//                scgen = gItem[0].replace("G:", "");
//                scage = gItem[1].replace("A:", "");
//                sczod = gItem[2].replace("Z:", "");
//
//                if (StringUtil.isNull(scgen) && StringUtil.isNull(scage) && StringUtil.isNull(sczod)) {
//                    binding.frameMain.gendersearchText.setTextColor(getResources().getColor(R.color.color_2));
//                    binding.frameMain.genderArrow.setImageResource(R.drawable.btn_main_categorydrop);
//                } else {
//                    binding.frameMain.gendersearchText.setTextColor(getResources().getColor(R.color.color_ea34));
//                    binding.frameMain.genderArrow.setImageResource(R.drawable.btn_main_categorydrop_on);
//                }
//
//                if (memlist.size() > 0) {
//                    memlist.clear();
//                }
                if (!StringUtil.isNull(result) && result.equalsIgnoreCase("전체")) {
                    binding.frameMain.gendersearchText.setText("성별");
                    scgen = "";
                    binding.frameMain.genderArrow.setSelected(false);
                } else {
                    binding.frameMain.gendersearchText.setText(result);
                    binding.frameMain.genderArrow.setSelected(true);
                    if (result.equalsIgnoreCase("남성")) {
                        scgen = "male";
                    } else {
                        scgen = "female";
                    }
                }

                if (memlist.size() > 0) {
                    memlist.clear();
                }
//                getMemberList(scval1, scval2);
                getListCheck(true);
                break;
            case DefaultValue.LASTNAMESEARCH:
//                lastnames = result;
//                sclastname = result.replaceAll("씨", "");
//                Log.i(StringUtil.TAG, "성씨검색: " + sclastname);
//                if (StringUtil.isNull(result)) {
//                    binding.frameMain.lastnamesearchText.setTextColor(getResources().getColor(R.color.color_2));
//                    binding.frameMain.lastnameArrow.setImageResource(R.drawable.btn_main_categorydrop);
//                } else {
//                    if (lastnames_arr != null && lastnames_arr.size() > 0) {
//                        lastnames_arr.clear();
//                    }
//                    binding.frameMain.lastnamesearchText.setTextColor(getResources().getColor(R.color.color_ea34));
//                    binding.frameMain.lastnameArrow.setImageResource(R.drawable.btn_main_categorydrop_on);
//                }
//
//                if (memlist.size() > 0) {
//                    memlist.clear();
//                }
////                getMemberList(scval1, scval2);


                if (!StringUtil.isNull(result) && result.equalsIgnoreCase("전체")) {
                    binding.frameMain.lastnamesearchText.setText("나이별");
                    binding.frameMain.lastnameArrow.setSelected(false);
                } else {
                    binding.frameMain.lastnamesearchText.setText(result);
                    binding.frameMain.lastnameArrow.setSelected(true);
                }

                String[] ages = getResources().getStringArray(R.array.age);

                Calendar c = Calendar.getInstance();
                int cyear = c.get(Calendar.YEAR) + 1;
                Log.e(TAG, "test: " + cyear);

                int plus = 20;
                for (int i = 0; i < ages.length; i++) {
                    if (ages[i].equalsIgnoreCase(result)) {
                        if (i != 0) {
                            Log.e(TAG, "i, plus: " + i + ", " + plus);
                            scage = String.valueOf(cyear - plus);
                        } else {
                            scage = "";
                        }
                        break;
                    }

                    if (i != 0) {
                        if (plus == 20 || i == 8) {
                            plus += 6;
                        } else {
                            plus += 5;
                        }
                    }
                }
                Log.e(TAG, "scage: " + scage);

                if (memlist.size() > 0) {
                    memlist.clear();
                }
//                getMemberList(scval1, scval2);
                getListCheck(true);
                break;
            case DefaultValue.ADDSEARCH:
                addsearch = result;
                String[] addscItem = result.split("#");
                Log.i(StringUtil.TAG, "ADDSEARCH size: " + addscItem.length);

                if (addscItem[0].replace("AS1:", "").equalsIgnoreCase("전체")) {
                    screli = "";
                } else {
                    screli = addscItem[0].replace("AS1:", "");
                }

                if (addscItem[1].replace("AS2:", "").equalsIgnoreCase("전체")) {
                    scann = "";
                } else {
                    scann = cpv.setAnnParam(addscItem[1].replace("AS2:", ""));
                }

                if (addscItem[2].replace("AS3:", "").equalsIgnoreCase("전체")) {
                    scprop = "";
                } else {
                    scprop = cpv.setPropParam(addscItem[2].replace("AS3:", ""));
                }

                if (addscItem[3].replace("AS4:", "").equalsIgnoreCase("전체")) {
                    scblood = "";
                } else {
                    scblood = addscItem[3].replace("AS4:", "");
                }

                if (addscItem[4].replace("AS5:", "").equalsIgnoreCase("전체")) {
                    scedu = "";
                } else {
                    scedu = addscItem[4].replace("AS5:", "");
                }

                if (addscItem[5].replace("AS6:", "").equalsIgnoreCase("전체")) {
                    scsmoke = "";
                } else {
                    scsmoke = cpv.setSmokeParam(addscItem[5].replace("AS6:", ""));
                }

                if (addscItem[6].replace("AS7:", "").equalsIgnoreCase("전체")) {
                    scdrink = "";
                } else {
                    scdrink = addscItem[6].replace("AS7:", "");
                }

                if (addscItem[7].replace("AS8:", "").equalsIgnoreCase("")) {
                    childExist = "all";
                } else {
                    childExist = cpv.setChild(addscItem[7].replace("AS8:", ""));
                }

                pictureYN = addscItem[8].replace("AS9:", "");

                if (addscItem[9].replace("AS10:", "").equalsIgnoreCase("")) {
                    scMarriage = "";
                } else {
                    scMarriage = addscItem[9].replace("AS10:", "");
                }

                if (memlist.size() > 0) {
                    memlist.clear();
                }
//                getMemberList(scval1, scval2);
                getListCheck(true);
                break;


            case DefaultValue.MAINLOC:
                String midLoc;
                if (result.equalsIgnoreCase("지역")) {
                    midLoc = "상세지역";
                    binding.frameMain.totalArrow.setSelected(false);
                    binding.frameMain.recencyArrow.setSelected(false);
                    srloc1 = "";
                    srloc2 = "";
                } else {
                    midLoc = "전체";
                    binding.frameMain.totalArrow.setSelected(true);
                    binding.frameMain.recencyArrow.setSelected(true);
                    srloc1 = result;
                    srloc2 = "";
                }

                binding.frameMain.totalsearchText.setText(result);
                binding.frameMain.recencyText.setText(midLoc);

                getListCheck(true);
                break;
            case DefaultValue.MIDDLELOC:
                binding.frameMain.recencyText.setText(result);
                if (result.equalsIgnoreCase("전체")) {
                    srloc2 = "";
                } else {
                    srloc2 = result;
                }
//                if (!result.equalsIgnoreCase("상세지역")) {
//                    srloc2 = result;
//                }
                getListCheck(true);
                break;
        }
    }

    private void showDlgRegProf() {
        LayoutInflater dialog = LayoutInflater.from(this);
        View dialogLayout = dialog.inflate(R.layout.dlg_confirm, null);
        final Dialog menuDlg = new Dialog(this);

        menuDlg.setContentView(dialogLayout);

        menuDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        menuDlg.show();

        TextView title = (TextView) dialogLayout.findViewById(R.id.tv_dlgtitle);
        TextView contents = (TextView) dialogLayout.findViewById(R.id.tv_contents);
        TextView btn_cancel = (TextView) dialogLayout.findViewById(R.id.btn_cancel);
        TextView btn_ok = (TextView) dialogLayout.findViewById(R.id.btn_ok);

        title.setText("프로필 등록");
        contents.setText("프로필이 등록 되어있지 않습니다.\n등록하시겠습니까?");

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MyprofileModifyAct.class));
                if (menuDlg.isShowing()) {
                    menuDlg.dismiss();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuDlg.isShowing()) {
                    menuDlg.dismiss();
                }
            }
        });
    }


    private void showDlgoffapp() {
        LayoutInflater dialog = LayoutInflater.from(this);
        View dialogLayout = dialog.inflate(R.layout.dlg_confirm2, null);
        final Dialog menuDlg = new Dialog(this);

        menuDlg.setContentView(dialogLayout);


        menuDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        menuDlg.show();

        TextView btn_cancel = (TextView) dialogLayout.findViewById(R.id.btn_cancel);
        TextView btn_ok = (TextView) dialogLayout.findViewById(R.id.btn_ok);


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuDlg.isShowing()) {
                    menuDlg.dismiss();
                }
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                finishAffinity();
            }
        });
    }

    private void replaceFragment(BaseFrag frag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.replacements, frag)
                .commit();
    }


    public interface onKeyBackPressedListener {
        public void onBack();
    }

    public void setOnKeyBackPressedListener(onKeyBackPressedListener listener) {
        mOnKeyBackPressedListener = listener;
    }

    @Override
    public void onBackPressed() {
        View home = findViewById(R.id.frame_main);
        if (home.getVisibility() == View.VISIBLE) {
            if (binding.frameMain.layoutTotalmenu.getRoot().getVisibility() == View.VISIBLE) {
                binding.frameMain.btnTotalmenu.setSelected(false);
                binding.frameMain.layoutTotalmenu.getRoot().setVisibility(View.GONE);
            } else {
                showDlgoffapp();
            }
        } else {
            switchFrame(true);
        }
    }

    public void fromHomeBtn() {
        View home = findViewById(R.id.frame_main);
        if (home.getVisibility() == View.VISIBLE) {
            if (binding.frameMain.layoutTotalmenu.getRoot().getVisibility() == View.VISIBLE) {
                binding.frameMain.btnTotalmenu.setSelected(false);
                binding.frameMain.layoutTotalmenu.getRoot().setVisibility(View.GONE);
            }
        } else {
            switchFrame(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logOut();
    }

    // 로그아웃(앱 종료시 호출)
    private void logOut() {
        ReqBasic logout = new ReqBasic(this, NetUrls.LOGOUT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            Log.i(StringUtil.TAG, "로그아웃 상태 변경");
                        } else {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {

                }
            }
        };

        logout.addParams("uidx", UserPref.getUidx(this));
        logout.execute(true, false);
    }

    private void initOptions() {
        addsearch = "";
        scval1 = "";
        scval2 = "";
        scloc = "";
        sclat = "";
        srloc1 = "";
        srloc2 = "";
        sclon = "";
        scgen = "";
        scage = "";
        screli = "";
        scann = "";
        scprop = "";
        scblood = "";
        scedu = "";
        scMarriage = "";
        scsmoke = "";
        scdrink = "";
        pictureYN = "";
        childExist = "all";


        // 지역, 상세지역 옵션
        binding.frameMain.totalsearchText.setText("지역");
        binding.frameMain.recencyText.setText("상세지역");

        binding.frameMain.totalArrow.setSelected(false);
        binding.frameMain.recencyArrow.setSelected(false);

        // 나이별 옵션
        binding.frameMain.lastnamesearchText.setText("나이별");
        binding.frameMain.lastnameArrow.setSelected(false);

        // 성별 옵션
        binding.frameMain.gendersearchText.setText("성별");
        binding.frameMain.genderArrow.setSelected(false);
    }
    @Override
    public void onClick(View v) {
        binding.frameMain.rcvMember.stopScroll();

        switch (v.getId()) {
            /* 하단바 */
            case R.id.ll_today_member:
//                setReadProcess("today");

                currentFrag = "other";
                initBottomBtn();
                binding.llTodayMember.setSelected(true);

                replaceFragment(new TodayMemberFrag());
                switchFrame(false);
                break;

            case R.id.ll_view_member:
                setReadProcess("view");

                currentFrag = "other";
                initBottomBtn();
                binding.llViewMember.setSelected(true);

                replaceFragment(new ViewingFrag());
                switchFrame(false);
                break;

            case R.id.ll_message_box:

                currentFrag = "chat";
                initBottomBtn();
                binding.llMessageBox.setSelected(true);

                replaceFragment(new MessageBoxFrag());
                switchFrame(false);
                break;

            case R.id.ll_new_member:
                setReadProcess("new");

                currentFrag = "other";
                initBottomBtn();
                binding.llNewMember.setSelected(true);

                replaceFragment(new NewMemberFrag());
                switchFrame(false);
                break;

            case R.id.ll_like_member:
                setReadProcess("like");

                currentFrag = "other";
                initBottomBtn();
                binding.llLikeMember.setSelected(true);

                replaceFragment(new InterestFrag());
                switchFrame(false);
                break;




            /* 상단바 */
            case R.id.btn_totalmenu:
                if (binding.frameMain.layoutTotalmenu.getRoot().getVisibility() == View.VISIBLE) {
                    binding.frameMain.btnTotalmenu.setSelected(false);
                    binding.frameMain.layoutTotalmenu.getRoot().setVisibility(View.GONE);
                } else {
                    binding.frameMain.btnTotalmenu.setSelected(true);
                    binding.frameMain.layoutTotalmenu.getRoot().setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_myprofile:
                startActivity(new Intent(act, MyProfileAct.class));
                break;
            case R.id.fl_refresh:
                binding.frameMain.btnTotalmenu.setSelected(false);
                binding.frameMain.layoutTotalmenu.getRoot().setVisibility(View.GONE);

                initOptions();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getListCheck(true);
                    }
                }, 100);
                break;

            case R.id.btn_refresh:
                binding.frameMain.btnTotalmenu.setSelected(false);
                binding.frameMain.layoutTotalmenu.getRoot().setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getListCheck(true);
                    }
                }, 100);
                break;

            case R.id.ll_filter_total:
                Intent mainlocIntent = new Intent(this, ListDlgAct.class);
                mainlocIntent.putExtra("subject", "main_loc");
                mainlocIntent.putExtra("select", binding.frameMain.totalsearchText.getText());
                startActivityForResult(mainlocIntent, DefaultValue.MAINLOC);
                break;

            case R.id.ll_filter_recency:
                // 고향 서브
                if (!binding.frameMain.totalsearchText.getText().toString().equalsIgnoreCase("지역")) {
                    Intent middlelocIntent = new Intent(this, ListDlgAct.class);
                    middlelocIntent.putExtra("subject", "middle_loc_search");
                    middlelocIntent.putExtra("mloc", binding.frameMain.totalsearchText.getText());
                    middlelocIntent.putExtra("select", binding.frameMain.recencyText.getText());
                    startActivityForResult(middlelocIntent, DefaultValue.MIDDLELOC);
                } else {
                    Common.showToast(act, "지역을 선택해주세요");
                }
                break;
            case R.id.ll_filter_gender:
                String gender = binding.frameMain.gendersearchText.getText().toString();
                if (gender.equalsIgnoreCase("성별")) {
                    gender = "전체";
                }

                Intent gsearch = new Intent(this, ListDlgAct.class);
                gsearch.putExtra("subject", "gender");
                gsearch.putExtra("select", gender);
                startActivityForResult(gsearch, DefaultValue.GENDERSEARCH);

                break;
            case R.id.ll_filter_lastname:
                String send = binding.frameMain.lastnamesearchText.getText().toString();
                if (send.equalsIgnoreCase("나이별")) {
                    send = "전체";
                }

                Intent lastnamesearchIntent = new Intent(this, ListDlgAct.class);
                lastnamesearchIntent.putExtra("subject", "age");
                lastnamesearchIntent.putExtra("select", send);
                startActivityForResult(lastnamesearchIntent, DefaultValue.LASTNAMESEARCH);

                break;
            case R.id.ll_btn_addsearch:
                Intent addsc = new Intent(act, AddsearchAct.class);
                addsc.putExtra("data", addsearch);
                startActivityForResult(addsc, DefaultValue.ADDSEARCH);
                break;


            /* 메뉴 */
            //공지사항
            case R.id.ll_notice:
                startActivity(new Intent(act, NoticeAct.class));
                break;
            //지인차단
            case R.id.ll_block_know_member:
//                Common.showToastDevelop(act);
                startActivity(new Intent(act, FriendBlockAct.class));
                break;

            //이용후기
            case R.id.ll_review:
                startActivity(new Intent(act, ReviewAct.class));
                break;
            case R.id.ll_profile_revise:
                // 기본정보관리
                startActivity(new Intent(act, BasicinfoSettingAct.class));
                break;
            case R.id.ll_block_member:
                // 차단한회원관리
                startActivity(new Intent(act, BlockmemberAct.class));
                break;
            case R.id.ll_buy_item:
                // 아이템관리
                startActivity(new Intent(act, ItemmanageAct.class).putExtra("from", "main"));
                break;
            case R.id.ll_service_center:
                // 고객센터
                startActivity(new Intent(act, ServiceCenterAct.class));
                break;

            case R.id.ll_report:
//                Common.showToastDevelop(act);
                startActivity(new Intent(act, QnaReportAct.class));
                break;

//            case R.id.ll_talk_board:
//                // 톡톡게시판
//                startActivity(new Intent(act, TokboardAct.class));
//                break;
            case R.id.ll_record:
                // 방문기록설정
                startActivity(new Intent(act, HistorysetAct.class));
                break;
            case R.id.ll_alarm:
                // 알림수신설정
                startActivity(new Intent(act, AlarmsetAct.class));
                break;
            case R.id.ll_invite_message:
                // 문자초대
                inviteMessage();
                break;
            case R.id.ll_invite_kakao:
                // 카톡 친구초대
                inviteKakao();
                break;
            case R.id.ll_dormant:
                // 휴면설정
//                Common.showToast(act, "탈퇴와 휴면설정은 12월1일 이후 가능합니다~\n양해부탁드립니다~");
                startActivity(new Intent(act, PauseAct.class));
                break;
            case R.id.ll_leaving:
                // 탈퇴하기
//                Common.showToast(act, "탈퇴와 휴면설정은 12월1일 이후 가능합니다~\n양해부탁드립니다~");
                startActivity(new Intent(act, SecessionAct.class));
                break;
            case R.id.ll_alliance:
                // 제휴제안
                startActivity(new Intent(act, AllianceAct.class));
                break;
        }
    }
}
