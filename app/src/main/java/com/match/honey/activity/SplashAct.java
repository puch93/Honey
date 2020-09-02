package com.match.honey.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.android.pushservice.BasicPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.android.pushservice.PushService;
import com.baidu.android.pushservice.PushSettings;
import com.bumptech.glide.Glide;
import com.match.honey.R;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.SystemPref;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SplashAct extends BaseActivity {
    final static int GPS_CHECK = 20;
    final static int PERMISSION = 21;

    private Handler handler = new Handler();
    private Handler handler_baidu = new Handler();
    private Timer tokenTimer = new Timer();
    private Timer tokenTimer_baidu = new Timer();

    String enter, msg_from, room_idx;
    String device_version;

    TextView tv_bottom;

    AppCompatActivity act;

    Geocoder geocoder;
    private boolean isPushOk = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        act = this;

        tv_bottom = (TextView) findViewById(R.id.tv_bottom);

        // 스플래시 gif이미지
        Glide.with(act)
                .load(R.raw.splash_1440x2560_gif)
                .into((ImageView) findViewById(R.id.splash));

        // gps정보 가져오기위함
        geocoder = new Geocoder(this);

        // 푸시관련
        enter = getIntent().getStringExtra("enter");
        msg_from = getIntent().getStringExtra("msg_from");
        room_idx = getIntent().getStringExtra("room_idx");


        // 버전 가져오기
        try {
            device_version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            Log.e(TAG, "device_version: " + device_version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // 디바이스 높이 계산
        if (SystemPref.getDeviceWidth(act) == 0) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels - 130;
            SystemPref.setDeviceWidth(act, width);

            Log.e(StringUtil.TAG, "device width: " + width);
        }

        Locale systemLocale = getApplicationContext().getResources().getConfiguration().locale;
        String strCountry = systemLocale.getCountry(); // KR
        Log.i(TAG, "strCountry: " + strCountry);
        if(!StringUtil.isNull(strCountry)) {
            if(strCountry.equalsIgnoreCase("CN")) {
                UserPref.setCountry(act, "country");
            } else {
                UserPref.setCountry(act, "international");
            }
        }

        checkVersion();
    }

    private void checkVersion() {
        ReqBasic buyItem = new ReqBasic(this, NetUrls.TERMS) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                final String res = resultData.getResult();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!StringUtil.isNull(res)) {
                                JSONObject jo = new JSONObject(res);

                                String[] version = jo.getString("app_version").split("\\.");
                                String[] version_me = device_version.split("\\.");


                                for (int i = 0; i < 3; i++) {
                                    int tmp1 = Integer.parseInt(version[i]);
                                    int tmp2 = Integer.parseInt(version_me[i]);

                                    if (tmp2 < tmp1) {
                                        AlertDialog.Builder alertDialogBuilder =
                                                new AlertDialog.Builder(new ContextThemeWrapper(act, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert));
                                        alertDialogBuilder.setTitle("업데이트");
                                        alertDialogBuilder.setMessage("새로운 버전이 있습니다.")
                                                .setPositiveButton("업데이트 바로가기", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.match.honey"));
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.setCanceledOnTouchOutside(false);
                                        alertDialog.show();

                                        return;
                                    }
                                }
                                startProgram();
                            } else {
                                startProgram();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        buyItem.execute(true, true);
    }

    public void startProgram() {
        if (isReqPermission()) {
            startActivityForResult(new Intent(SplashAct.this, PermissionActivity.class), PERMISSION);
        } else {
            handler.post(mrun_gps);

            startLocationService();
            gpsStatusCheck();
        }
    }

    private void checkState(boolean isGps) {
        if (isGps) {
            tokenTimer.cancel();
            handler.removeCallbacks(mrun_gps);

            handler_baidu.post(mrun_baidu);
        } else {
            //TODO
//            if (!StringUtil.isNull(UserPref.getBaiduToken(act)) && !isPushOk) {
            if (!isPushOk) {
                isPushOk = true;

                tokenTimer_baidu.cancel();
                handler_baidu.removeCallbacks(mrun_baidu);

                // 다음 프로세스
                checkAutoLogin();
            }
        }
    }

    private boolean isReqPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            ) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    Runnable mrun_gps = new Runnable() {
        @Override
        public void run() {
            TimerTask tokenTask = new TimerTask() {
                @Override
                public void run() {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tv_bottom.setText("위치정보 받는중");
                        }
                    }, 0);


                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tv_bottom.setText("위치정보 받는중.");
                        }
                    }, 250);

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tv_bottom.setText("위치정보 받는중..");
                        }
                    }, 500);

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tv_bottom.setText("위치정보 받는중...");
                        }
                    }, 750);
                }
            };
            tokenTimer.schedule(tokenTask, 1000, 1000);
        }
    };

    Runnable mrun_baidu = new Runnable() {
        @Override
        public void run() {
            TimerTask tokenTask = new TimerTask() {
                @Override
                public void run() {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tv_bottom.setText("푸시정보 받는중");
                            checkState(false);
                        }
                    }, 0);


                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tv_bottom.setText("푸시정보 받는중.");
                            checkState(false);
                        }
                    }, 250);

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tv_bottom.setText("푸시정보 받는중..");
                            checkState(false);
                        }
                    }, 500);

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tv_bottom.setText("푸시정보 받는중...");
                            checkState(false);
                        }
                    }, 750);
                }
            };
            tokenTimer_baidu.schedule(tokenTask, 1000, 1000);
        }
    };

    private void checkAutoLogin() {
        //원래위치 (onResume)
        if (UserPref.isLogin(act)) {
            reqAutoLogin();
        } else {
            if (StringUtil.isNull(enter)) {
                startActivity(new Intent(SplashAct.this, FirstAct.class));
                finish();
            } else {
                if (enter.equalsIgnoreCase("push")) {
                    Intent login = new Intent(SplashAct.this, FirstAct.class);
                    login.putExtra("enter", enter);
                    login.putExtra("msg_from", msg_from);
                    login.putExtra("room_idx", room_idx);
                    startActivity(login);
                    finish();
                } else {
                    startActivity(new Intent(SplashAct.this, FirstAct.class));
                    finish();
                }
            }
        }
    }

    private void reqAutoLogin() {
//        if (StringUtil.isNull(token)) {
//            token = FirebaseInstanceId.getInstance().getToken();
//            Toast.makeText(act, "푸시토큰을 가져오는 중입니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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

                            UserPref.setUidx(act, obj.getString("idx"));
                            UserPref.setId(act, obj.getString("id"));
                            UserPref.setPw(act, UserPref.getPw(act));
                            UserPref.setHopeLoc(act, obj.getString("hopeaddr"));
                            UserPref.setCoin(act, Integer.parseInt(obj.getString("coin")));
                            UserPref.setJtype(act, obj.getString("type"));
                            UserPref.setGender(act, obj.getString("gender"));
                            getLocation(obj.getString("addr1") + obj.getString("addr2"));

                            UserPref.setInterIdxs(act, obj.getString("interest_idxs"));

                            if (!StringUtil.isNull(obj.getString("familyname"))) {
                                UserPref.setRegProf(act, true);
                            }

                            setUserPauseCheck();

                        } else {
                            UserPref.setLoginState(act, false);
                            UserPref.setLogoutState(act, false);
                            UserPref.setId(act, "");
                            UserPref.setPw(act, "");
                            Toast.makeText(act, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("TEST_HOME", "error4: " + e.getMessage() + "\n" + e.getLocalizedMessage());
                        Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("TEST_HOME", "error5");
                    Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
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
        autologin.execute(true, false);

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
                            UserPref.setLoginState(act, true);
                            if (StringUtil.isNull(enter)) {
                                startActivity(new Intent(act, MainActivity.class));
                                finish();
                            } else {
                                if (enter.equalsIgnoreCase("push")) {
                                    Intent main = new Intent(act, MainActivity.class);
                                    main.putExtra("enter", enter);
                                    main.putExtra("msg_from", msg_from);
                                    main.putExtra("room_idx", room_idx);
                                    startActivity(main);
                                    finish();
                                } else {
                                    startActivity(new Intent(act, MainActivity.class));
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
                UserPref.setLoginState(act, true);

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
                                startActivity(new Intent(act, MainActivity.class));
                                finish();
                            } else {
                                if (enter.equalsIgnoreCase("push")) {
                                    Intent main = new Intent(act, MainActivity.class);
                                    main.putExtra("enter", enter);
                                    main.putExtra("msg_from", msg_from);
                                    main.putExtra("room_idx", room_idx);
                                    startActivity(main);
                                    finish();
                                } else {
                                    startActivity(new Intent(act, MainActivity.class));
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

    public void gpsStatusCheck() {
        //GPS가 켜져있는지 체크
        if (!locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            AlertDialog.Builder alertDialogBuilder =
                    new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("GPS 설정");
            alertDialogBuilder.setMessage("GPS가 꺼져있습니다. GPS를 켜시겠습니까?")
                    .setPositiveButton("GPS 켜기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            startActivityForResult(intent, GPS_CHECK);
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checkState(true);
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            if (!this.isFinishing()) {
                alertDialog.show();
            }
        } else {
            checkState(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopLocationService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Log.e(StringUtil.TAG, "gps check ok");
            if (requestCode == GPS_CHECK) {
                startLocationService();
                gpsStatusCheck();
            } else if (requestCode == PERMISSION) {
                startProgram();
            }
        } else {
            Log.e(StringUtil.TAG, "gps check cancel");
            checkState(true);
        }
    }
}
