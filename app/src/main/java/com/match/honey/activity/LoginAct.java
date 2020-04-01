package com.match.honey.activity;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.baidu.android.pushservice.BasicPushNotificationBuilder;
import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.android.pushservice.PushSettings;
import com.match.honey.R;
import com.match.honey.databinding.ActivityLoginBinding;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.DefaultValue;
import com.match.honey.utils.StatusBarUtil;
import com.match.honey.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class LoginAct extends BaseActivity implements View.OnClickListener {
    ActivityLoginBinding binding;
    Context mContext;

    Geocoder geocoder;
    String token;

    AppCompatActivity act;
    String enter, msg_from, room_idx;

    private NotificationManager manager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        act = this;

        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.GREY_STATUS_BAR);

        getHashKey();

        mContext = this;
        startLocationService();
        geocoder = new Geocoder(this);

        enter = getIntent().getStringExtra("enter");
        msg_from = getIntent().getStringExtra("msg_from");
        room_idx = getIntent().getStringExtra("room_idx");

        setClick();

        //원래위치 (onResume)
        if (UserPref.isLogin(this)) {
            reqAutoLogin();
        } else {
            if (UserPref.isLogout(this)) {
                binding.etEmail.setText(UserPref.getId(this));
                binding.etPw.setText(UserPref.getPw(this));
            }
        }


        /* 바이두 */
//        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, getString(R.string.baidu_api_key));
//
//        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
//                R.layout.notification_custom_builder,
//                R.id.notification_icon,
//                R.id.notification_title,
//                R.id.notification_text);
//
//
//        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
//        cBuilder.setNotificationDefaults(Notification.DEFAULT_VIBRATE);
//        cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
//        cBuilder.setLayoutDrawable(R.drawable.app_icon);
//        cBuilder.setNotificationSound(Uri.withAppendedPath(
//                MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "6").toString());
//        cBuilder.setChannelId("testId");
//        cBuilder.setChannelName("testName");
//        PushManager.setNotificationBuilder(this, 1, cBuilder);


        /* 텐센트 테스트 */
        String appId = "1400340865";
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void setClick() {
        binding.btnLogin.setOnClickListener(this);
        binding.btnJoin.setOnClickListener(this);

        binding.btnFindaccount.setOnClickListener(this);
        binding.tvNomemberQna.setOnClickListener(this);
    }

    private void reqLogin() {
//        if (StringUtil.isNull(token)) {
//            token = FirebaseInstanceId.getInstance().getToken();
//            Toast.makeText(LoginAct.this, "푸시토큰을 가져오는 중입니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
//            return;
//        } else {
//            UserPref.setBaiduToken(this, token);
//        }

        ReqBasic login = new ReqBasic(this, NetUrls.LOGIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Login Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            JSONObject obj = new JSONObject(jo.getString("value"));

                            UserPref.setUidx(LoginAct.this, obj.getString("idx"));
                            UserPref.setId(LoginAct.this, obj.getString("id"));
                            UserPref.setPw(act, binding.etPw.getText().toString());
                            UserPref.setHopeLoc(LoginAct.this, obj.getString("hopeaddr"));
                            UserPref.setCoin(LoginAct.this, Integer.parseInt(obj.getString("coin")));
                            UserPref.setJtype(LoginAct.this, obj.getString("type"));
                            UserPref.setGender(LoginAct.this, obj.getString("gender"));

                            UserPref.setInterIdxs(LoginAct.this, obj.getString("interest_idxs"));

                            if (!StringUtil.isNull(obj.getString("familyname"))) {
                                UserPref.setRegProf(LoginAct.this, true);
                            }

                            getLocation(obj.getString("addr1") + obj.getString("addr2"));

                            setUserPauseCheck();

                        } else {
                            Log.e("TEST_HOME", "error1");
                            Toast.makeText(LoginAct.this, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("TEST_HOME", "error2");
                        Toast.makeText(LoginAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("TEST_HOME", "error3");
                    Toast.makeText(LoginAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        login.addParams("id", binding.etEmail.getText().toString());
        login.addParams("pw", binding.etPw.getText().toString());
        login.addParams("fcm", UserPref.getBaiduToken(this));

        if (!locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            login.addParams("lat", UserPref.getLocationLat(this));
            login.addParams("lon", UserPref.getLocationLon(this));
        } else {
            login.addParams("lat", UserPref.getLat(this));
            login.addParams("lon", UserPref.getLon(this));
        }
        login.execute(true, true);
    }

    private void reqAutoLogin() {
//        if (StringUtil.isNull(token)) {
//            token = FirebaseInstanceId.getInstance().getToken();
//            Toast.makeText(LoginAct.this, "푸시토큰을 가져오는 중입니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
//            return;
//        } else {
//            UserPref.setBaiduToken(this, token);
//        }

        ReqBasic autologin = new ReqBasic(this, NetUrls.LOGIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Auto Login Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            JSONObject obj = new JSONObject(jo.getString("value"));

                            UserPref.setUidx(LoginAct.this, obj.getString("idx"));
                            UserPref.setId(LoginAct.this, obj.getString("id"));
                            UserPref.setPw(act, UserPref.getPw(act));
                            UserPref.setHopeLoc(LoginAct.this, obj.getString("hopeaddr"));
                            UserPref.setCoin(LoginAct.this, Integer.parseInt(obj.getString("coin")));
                            UserPref.setJtype(LoginAct.this, obj.getString("type"));
                            UserPref.setGender(LoginAct.this, obj.getString("gender"));
                            getLocation(obj.getString("addr1") + obj.getString("addr2"));

                            UserPref.setInterIdxs(LoginAct.this, obj.getString("interest_idxs"));

                            if (!StringUtil.isNull(obj.getString("familyname"))) {
                                UserPref.setRegProf(LoginAct.this, true);
                            }

                            setUserPauseCheck();

                        } else {
                            UserPref.setLoginState(act, false);
                            UserPref.setLogoutState(act, false);
                            UserPref.setId(act, "");
                            UserPref.setPw(act, "");
                            Toast.makeText(LoginAct.this, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("TEST_HOME", "error4");
                        Toast.makeText(LoginAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("TEST_HOME", "error5");
                    Toast.makeText(LoginAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }

            }
        };

        autologin.addParams("id", UserPref.getId(this));
        autologin.addParams("pw", UserPref.getPw(this));
        Log.e(TAG, "password: " + UserPref.getPw(this));
        autologin.addParams("fcm", UserPref.getBaiduToken(this));

        if (!locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            autologin.addParams("lat", UserPref.getLocationLat(this));
            autologin.addParams("lon", UserPref.getLocationLon(this));
        } else {
            autologin.addParams("lat", UserPref.getLat(this));
            autologin.addParams("lon", UserPref.getLon(this));
        }
        autologin.execute(true, true);

    }

    private void showDlgPause() {
        LayoutInflater dialog = LayoutInflater.from(this);
        View dialogLayout = dialog.inflate(R.layout.dlg_confirm, null);
        final Dialog menuDlg = new Dialog(this);

        menuDlg.setContentView(dialogLayout);

        menuDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        menuDlg.setCancelable(false);

        menuDlg.show();

        TextView title = (TextView) dialogLayout.findViewById(R.id.tv_dlgtitle);
        TextView contents = (TextView) dialogLayout.findViewById(R.id.tv_contents);
        TextView btn_cancel = (TextView) dialogLayout.findViewById(R.id.btn_cancel);
        TextView btn_ok = (TextView) dialogLayout.findViewById(R.id.btn_ok);

        title.setText("휴면해제");
        contents.setText("휴면중인 회원입니다\n휴면상태를 해제하시겠습니까?");

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserPref.setLoginState(LoginAct.this, true);

                setUserPause();
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
                    finish();
                    finishAffinity();
                }
            }
        });
    }

    private void setUserPause() {
        ReqBasic block = new ReqBasic(this, NetUrls.MEMUNPAUSE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "UnPause Get Info: " + jo);
                        UserPref.setPause(act, false);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            if (StringUtil.isNull(enter)) {
                                startActivity(new Intent(LoginAct.this, MainActivity.class));
                                finish();
                            } else {
                                if (enter.equalsIgnoreCase("push")) {
                                    Intent main = new Intent(LoginAct.this, MainActivity.class);
                                    main.putExtra("enter", enter);
                                    main.putExtra("msg_from", msg_from);
                                    main.putExtra("room_idx", room_idx);
                                    startActivity(main);
                                    finish();
                                } else {
                                    startActivity(new Intent(LoginAct.this, MainActivity.class));
                                    finish();
                                }
                            }
                        } else {
                            finish();
                            finishAffinity();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        };

        block.addParams("uidx", UserPref.getUidx(this));
        block.execute(true, true);
    }

    private void setUserPauseCheck() {
        ReqBasic block = new ReqBasic(this, NetUrls.MEMPAUSECHECK) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Pause Check Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase("Y")) {
                            showDlgPause();
                        } else {
                            UserPref.setLoginState(LoginAct.this, true);
                            if (StringUtil.isNull(enter)) {
                                startActivity(new Intent(LoginAct.this, MainActivity.class));
                                finish();
                            } else {
                                if (enter.equalsIgnoreCase("push")) {
                                    Intent main = new Intent(LoginAct.this, MainActivity.class);
                                    main.putExtra("enter", enter);
                                    main.putExtra("msg_from", msg_from);
                                    main.putExtra("room_idx", room_idx);
                                    startActivity(main);
                                    finish();
                                } else {
                                    startActivity(new Intent(LoginAct.this, MainActivity.class));
                                    finish();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        };

        block.addParams("uidx", UserPref.getUidx(this));
        block.execute(true, false);
    }

    private void getLocation(String locStr) {
        List<Address> list = null;

        try {
            list = geocoder.getFromLocationName(locStr, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (list != null) {
            if (list.size() == 0) {
                Log.i(StringUtil.TAG, "주소 정보가 없습니다");
                UserPref.setLocationLat(this, "");
                UserPref.setLocationLon(this, "");
            } else {
                UserPref.setLocationLat(this, String.valueOf(list.get(0).getLatitude()));
                UserPref.setLocationLon(this, String.valueOf(list.get(0).getLongitude()));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationService();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_nomember_qna:
                Intent intent = new Intent(act, ServiceCenterAct.class);
                intent.putExtra("from", "login");
                startActivity(intent);
                break;

            case R.id.btn_login:

                if (binding.etEmail.length() == 0) {
                    Toast.makeText(this, getString(R.string.input_email), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (binding.etPw.length() == 0) {
                    Toast.makeText(this, getString(R.string.input_pw), Toast.LENGTH_SHORT).show();
                    return;
                }

//                // 비밀번호 4 - 12자
                if (binding.etPw.length() < 4) {
                    Toast.makeText(this, "비밀번호는 최소 4자 입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                reqLogin();
                break;

            case R.id.btn_join:
                //TODO
//                startActivity(new Intent(act, WeChatPayAct.class));
                startActivity(new Intent(LoginAct.this, JoinAct.class));
                break;


            case R.id.btn_findaccount:
                Intent findacc = new Intent(this, FindAccountAct.class);
                startActivityForResult(findacc, DefaultValue.FINDACC);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            Log.i(StringUtil.TAG, "data == null");
            return;
        }

        String result = data.getStringExtra("data");

        switch (requestCode) {
            case DefaultValue.FINDACC:
                Log.i(StringUtil.TAG, "res: " + result);
                String[] account = result.split("#");

                if (account.length != 0) {
                    if (!StringUtil.isNull(account[0])) {
                        binding.etEmail.setText(account[0]);
                    }

                    if (account.length == 2) {
                        if (!StringUtil.isNull(account[1])) {
                            binding.etPw.setText(account[1]);
                        }
                    }
                }
                break;
        }
    }

}
