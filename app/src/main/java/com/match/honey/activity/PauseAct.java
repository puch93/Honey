package com.match.honey.activity;

import android.app.Activity;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.match.honey.R;
import com.match.honey.databinding.ActivityPauseBinding;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.SettingAlarmPref;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.*;

import org.json.JSONException;
import org.json.JSONObject;

public class PauseAct extends AppCompatActivity {
    ActivityPauseBinding binding;
    Activity act;

    boolean isPossible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pause, null);
        act = this;

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.llHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) MainActivity.act).fromHomeBtn();
                finish();
            }
        });

        binding.cbPause.setChecked(false);
//        binding.cbPause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                binding.cbPause.setChecked(!binding.cbPause.isChecked());
//            }
//        });

        binding.tvPauseOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.cbPause.isChecked()) {
                    if(isPossible) {
                        showDlgPause();
                    } else {
                        Common.showToast(act, "아이템 구매회원은 휴면처리가 불가합니다");
                    }
                } else {
                    Common.showToast(act, "휴면처리 여부를 선택해주세요");
                }
            }
        });

        getMyProfile();
    }

    private void getMyProfile() {
        ReqBasic myprofile = new ReqBasic(this, NetUrls.MYPROFILE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "ModifyAct List Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            JSONObject obj = new JSONObject(jo.getString("value"));

                            if(UserPref.getGender(act).equalsIgnoreCase("male")) {
                                // 메시지이용권 체크
                                if (!StringUtil.calDateBetweenAandBSub(obj.getString("message_expiredate")).equalsIgnoreCase("0")) {
                                    isPossible = false;
                                }

                                // 관심있어요 발송권 체크
                                if(!obj.getString("interest_sendcnt").equalsIgnoreCase("0")) {
                                    isPossible = false;
                                }

                                // 프로필열람권 체크
                                if(!obj.getString("profile_viewcnt").equalsIgnoreCase("0")) {
                                    isPossible = false;
                                }

                            }
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

    //휴면회원여부
    private void showDlgPause() {
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

        title.setText("휴면설정");
        contents.setText("휴면중에는 모든서비스 이용이 불가합니다.\n해제하시겠습니까?");

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reqUserLock();
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


    private void reqUserLock() {
        ReqBasic userLock = new ReqBasic(this, NetUrls.MEMPAUSE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            Toast.makeText(act, jo.getString("value"), Toast.LENGTH_SHORT).show();
                            UserPref.setLoginState(act, false);
                            UserPref.setPause(act, true);
                            logOut();
                            finishAffinity();
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

        userLock.addParams("uidx", UserPref.getUidx(this));
        userLock.execute(true, true);
    }
}
