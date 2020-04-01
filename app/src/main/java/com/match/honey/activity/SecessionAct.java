package com.match.honey.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.match.honey.R;
import com.match.honey.databinding.ActivitySecessionBinding;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.StatusBarUtil;
import com.match.honey.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class SecessionAct extends AppCompatActivity implements View.OnClickListener {

    ActivitySecessionBinding binding;
    AppCompatActivity act;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_secession);
        act = this;
        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);
        binding.flBack.setOnClickListener(this);
        binding.llHome.setOnClickListener(this);
        binding.btnOutmember.setOnClickListener(this);

        binding.rdoAnswer1.setChecked(true);
    }

    private void dropMember(String reason) {
        ReqBasic dropMem = new ReqBasic(this, NetUrls.DROPMEMBER) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Leave Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            Toast.makeText(SecessionAct.this, "탈퇴 되었습니다.", Toast.LENGTH_SHORT).show();
                            UserPref.setLoginState(act, false);
                            UserPref.setLogoutState(act, false);
                            UserPref.setId(act, "");
                            UserPref.setPw(act, "");
                            finishAffinity();
                        } else {
                            Toast.makeText(SecessionAct.this, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SecessionAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SecessionAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        dropMem.addParams("uidx", UserPref.getUidx(this));
        dropMem.addParams("upw", UserPref.getPw(this));
        dropMem.addParams("dreq", reason);
        dropMem.execute(true, true);
    }

    private void writeReview() {
        ReqBasic writeReview = new ReqBasic(this, NetUrls.WRITEBOARD) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            Toast.makeText(SecessionAct.this, jo.getString("comment"), Toast.LENGTH_SHORT).show();
                            finishAffinity();
                        } else {
                            Toast.makeText(SecessionAct.this, jo.getString("comment"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SecessionAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SecessionAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }

            }
        };

        writeReview.addParams("uidx", UserPref.getUidx(this));
        writeReview.addParams("btype", "review");
//        writeReview.addParams("title", binding.etReviewTitle.getText().toString());
//        writeReview.addParams("content", binding.etReview.getText().toString());
        writeReview.execute(true, true);
    }

    private void showLeaveDlg(final String reason) {
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

        title.setText("탈퇴하기");
        contents.setText("여보자기를 탈퇴하시겠습니까?");

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dropMember(reason);
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
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_outmember:
                if (!binding.rdoAnswer1.isChecked() && !binding.rdoAnswer2.isChecked() && !binding.rdoAnswer3.isChecked() && !binding.rdoAnswer4.isChecked() && !binding.rdoAnswer5.isChecked()) {
                    Toast.makeText(this, "탈퇴 이유를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String reason = "";
                if (binding.rdoAnswer1.isChecked()) {
                    reason = binding.rdoAnswer1.getText().toString();
                } else if (binding.rdoAnswer2.isChecked()) {
                    reason = binding.rdoAnswer2.getText().toString();
                } else if (binding.rdoAnswer3.isChecked()) {
                    reason = binding.rdoAnswer3.getText().toString();
                } else if (binding.rdoAnswer4.isChecked()) {
                    reason = binding.rdoAnswer4.getText().toString();
                } else if (binding.rdoAnswer5.isChecked()) {
                    reason = binding.rdoAnswer4.getText().toString();
                }


                showLeaveDlg(reason);

                break;
        }
    }
}
