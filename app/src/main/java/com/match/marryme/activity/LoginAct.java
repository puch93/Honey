package com.match.marryme.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import com.match.marryme.R;
import com.match.marryme.databinding.ActivityLoginBinding;
import com.match.marryme.network.ReqBasic;
import com.match.marryme.network.netUtil.HttpResult;
import com.match.marryme.network.netUtil.NetUrls;
import com.match.marryme.sharedPref.UserPref;
import com.match.marryme.utils.DefaultValue;
import com.match.marryme.utils.StringUtil;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginAct extends BaseActivity implements View.OnClickListener {

    ActivityLoginBinding binding;

    Session session;

    OAuthLogin mOAuthLoginModule;
    Context mContext;

    FacebookLoginCallback mLoginCallback;
    CallbackManager mCallbackManager;

    Geocoder geocoder;

    String token;

    Activity act;
    String enter, msg_from, room_idx;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        act = this;

        getHashKey();

        //fcm 토큰 저장
        token = FirebaseInstanceId.getInstance().getToken();
        Log.e("TEST_HOME", "fcm_token: " + token);
        UserPref.setFcmToken(this, token);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCallbackManager = CallbackManager.Factory.create();
    }

    private void getHashKey() {
        try {                                                        // 패키지이름을 입력해줍니다.
            PackageInfo info = getPackageManager().getPackageInfo("com.match.marryme", PackageManager.GET_SIGNATURES);
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
        binding.llBtnKakao.setOnClickListener(this);
        binding.llBtnNaver.setOnClickListener(this);
        binding.llBtnFacebook.setOnClickListener(this);

        binding.btnFindaccount.setOnClickListener(this);
        binding.tvNomemberQna.setOnClickListener(this);
    }

    private void reqLogin() {
        if (StringUtil.isNull(token)) {
            token = FirebaseInstanceId.getInstance().getToken();
            Toast.makeText(LoginAct.this, "푸시토큰을 가져오는 중입니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            UserPref.setFcmToken(this, token);
        }

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
        login.addParams("fcm", UserPref.getFcmToken(this));

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
        if (StringUtil.isNull(token)) {
            token = FirebaseInstanceId.getInstance().getToken();
            Toast.makeText(LoginAct.this, "푸시토큰을 가져오는 중입니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            UserPref.setFcmToken(this, token);
        }

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
        autologin.addParams("fcm", UserPref.getFcmToken(this));

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

    private void reqJoinCheck(final String snsType, final String snsRes) {
        Log.e(TAG, "snsRes: " + snsRes);
        ReqBasic joinCheck = new ReqBasic(this, NetUrls.LOGIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                Log.i(StringUtil.TAG, "reqJoinCheck:  " + resultData.getResult() + "\ncode: " + resultCode);
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        // sns로 가입 이력이 있을경우
                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            JSONObject obj = new JSONObject(jo.getString("value"));

                            UserPref.setUidx(LoginAct.this, obj.getString("idx"));
                            UserPref.setId(LoginAct.this, obj.getString("id"));
                            UserPref.setPw(LoginAct.this, obj.getString("id"));
                            UserPref.setHopeLoc(LoginAct.this, obj.getString("hopeaddr"));
                            UserPref.setCoin(LoginAct.this, Integer.parseInt(obj.getString("coin")));
                            UserPref.setJtype(LoginAct.this, obj.getString("type"));
                            UserPref.setGender(LoginAct.this, obj.getString("gender"));

                            UserPref.setInterIdxs(LoginAct.this, obj.getString("interest_idxs"));

                            getLocation(obj.getString("addr1") + obj.getString("addr2"));

                            if (!StringUtil.isNull(obj.getString("familyname"))) {
                                UserPref.setRegProf(LoginAct.this, true);
                            }

                            setUserPauseCheck();

                        } else {
                            Log.e(TAG, "message: " + jo.getString("value"));
                            if (snsType.equalsIgnoreCase("kakaotalk")) {
                                Intent kakao = new Intent(LoginAct.this, JoinAct.class);
                                kakao.putExtra("snstype", snsType);
                                kakao.putExtra("uniqvalue", snsRes);
                                startActivity(kakao);
                            } else if (snsType.equalsIgnoreCase("facebook")) {
                                Intent facebook = new Intent(LoginAct.this, JoinAct.class);
                                facebook.putExtra("snstype", snsType);
                                facebook.putExtra("uniqvalue", snsRes);
                                startActivity(facebook);
                            } else if (snsType.equalsIgnoreCase("naver")) {
                                Intent naver = new Intent(LoginAct.this, JoinAct.class);
                                naver.putExtra("snstype", snsType);
                                naver.putExtra("uniqvalue", snsRes);
                                startActivity(naver);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("TEST_HOME", "error6");
                        Toast.makeText(LoginAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("TEST_HOME", "error7");
                    Toast.makeText(LoginAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }

            }
        };

        joinCheck.addParams("id", snsRes);
        joinCheck.addParams("pw", snsRes);
        joinCheck.addParams("fcm", UserPref.getFcmToken(this));

        if (!locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            joinCheck.addParams("lat", UserPref.getLocationLat(this));
            joinCheck.addParams("lon", UserPref.getLocationLon(this));
        } else {
            joinCheck.addParams("lat", UserPref.getLat(this));
            joinCheck.addParams("lon", UserPref.getLon(this));
        }
        joinCheck.execute(true, true);

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
                startActivity(new Intent(LoginAct.this, JoinAct.class));
                break;

            case R.id.ll_btn_kakao:
                session = Session.getCurrentSession();
                session.addCallback(new SessionCallback());
                session.open(AuthType.KAKAO_ACCOUNT, LoginAct.this);
                break;

            // 기존 키: AiLkSu_Lwc3Mg_n_rtwp
            // 기존 비밀 키: wlXmK6Q8mM
            case R.id.ll_btn_naver:
                mOAuthLoginModule = OAuthLogin.getInstance();
                mOAuthLoginModule.init(
                        LoginAct.this
                        , "m5934I76xqwUE2Jm3TyH"
                        , "Dm8tBpbT4P"
                        , getResources().getString(R.string.app_name)
                );
                mOAuthLoginModule.startOauthLoginActivity(LoginAct.this, mOAuthLoginHandler);
                break;

            case R.id.ll_btn_facebook:
                mLoginCallback = new FacebookLoginCallback();

                LoginManager loginManager = LoginManager.getInstance();
                loginManager.logInWithReadPermissions(LoginAct.this, Arrays.asList("public_profile", "email"));
                loginManager.registerCallback(mCallbackManager, mLoginCallback);
                break;



            case R.id.btn_findaccount:
                Intent findacc = new Intent(this, FindAccountAct.class);
                startActivityForResult(findacc, DefaultValue.FINDACC);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
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

    public class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            requestMe();

            Log.i(StringUtil.TAG, "onSessionOpened");
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.i(StringUtil.TAG, "onSessionOpenFailed" + exception.toString());
        }

        public void requestMe() {
            List<String> keys = new ArrayList<>();
            keys.add("properties.nickname");
            keys.add("properties.profile_image");
            keys.add("kakao_account.email");
            UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {

                @Override
                public void onFailure(ErrorResult errorResult) {
                    super.onFailure(errorResult);
                    Log.i(StringUtil.TAG, "onFailure" + errorResult.toString());
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Log.i(StringUtil.TAG, "onSessionClosed" + errorResult.toString());
                }

                @Override
                public void onSuccess(MeV2Response result) {

                    Log.i(StringUtil.TAG, "onSuccess: " + result.toString());
                    Log.i(StringUtil.TAG, "getId: " + result.getId());
                    result.getId();
                    result.getNickname();

                    //TODO 로그아웃
//                    UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
//                        @Override
//                        public void onCompleteLogout() {
//                            Log.e(StringUtil.TAG, "onCompleteLogout: 로그아웃 완료");
//                        }
//                    });

                    reqJoinCheck("kakaotalk", String.valueOf(result.getId()));
                    session.clearCallbacks();
                }
            });
        }
    }


    public class FacebookLoginCallback implements FacebookCallback<LoginResult> {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.e("Callback :: ", "onSuccess");
            requestMe(loginResult.getAccessToken());
        }

        @Override
        public void onCancel() {
            Log.e("Callback :: ", "onCancel");
        }

        @Override
        public void onError(FacebookException error) {
            Log.e("Callback :: ", "onError : " + error.getMessage());
        }

        public void requestMe(AccessToken token) {
            final GraphRequest graphRequest = GraphRequest.newMeRequest(token,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.i(StringUtil.TAG, object.toString());
                            try {
                                //TODO 로그아웃
//                                LoginManager.getInstance().logOut();

                                Log.i(StringUtil.TAG, "face id: " + object.getString("id"));
                                reqJoinCheck("facebook", object.getString("id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender,birthday");
            graphRequest.setParameters(parameters);
            graphRequest.executeAsync();

        }
    }

    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                final String accessToken = mOAuthLoginModule.getAccessToken(mContext);
                String refreshToken = mOAuthLoginModule.getRefreshToken(mContext);
                long expiresAt = mOAuthLoginModule.getExpiresAt(mContext);
                String tokenType = mOAuthLoginModule.getTokenType(mContext);

                Log.i(StringUtil.TAG, "accessToken: " + accessToken);
                Log.i(StringUtil.TAG, "refreshToken: " + refreshToken);
                Log.i(StringUtil.TAG, "expiresAt: " + expiresAt);
                Log.i(StringUtil.TAG, "tokenType: " + tokenType);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String header = "Bearer " + accessToken;
                        try {
                            String apiURL = "https://openapi.naver.com/v1/nid/me";
                            URL url = new URL(apiURL);
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                            con.setRequestMethod("GET");
                            con.setRequestProperty("Authorization", header);
                            int responseCode = con.getResponseCode();
                            BufferedReader br;
                            if (responseCode == 200) { // 정상 호출
                                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            } else {  // 에러 발생
                                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                            }
                            String inputLine;
                            StringBuffer response = new StringBuffer();
                            while ((inputLine = br.readLine()) != null) {
                                response.append(inputLine);
                            }
                            br.close();
                            System.out.println(response.toString());
                            Log.i(StringUtil.TAG, "naver res: " + response.toString());

                            JSONObject naverData = new JSONObject(response.toString());
                            if (StringUtil.getStr(naverData, "message").equalsIgnoreCase("success")) {
                                JSONObject naverResponse = naverData.getJSONObject("response");
                                //TODO 로그아웃
//                                OAuthLogin.getInstance().logout(act);
                                reqJoinCheck("naver", naverResponse.getString("id"));
                            }
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }).start();

            } else {
                String errorCode = mOAuthLoginModule.getLastErrorCode(mContext).getCode();
                String errorDesc = mOAuthLoginModule.getLastErrorDesc(mContext);
                Log.i(StringUtil.TAG, "errorCode:" + errorCode + ", errorDesc:" + errorDesc);
//                Toast.makeText(DlgActivityLogin.this, "errorCode:" + errorCode
//                        + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
            }
        }

        ;
    };
}
