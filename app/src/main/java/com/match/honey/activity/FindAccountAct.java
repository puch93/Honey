package com.match.honey.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.match.honey.R;
import com.match.honey.databinding.ActivityFindaccountBinding;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.utils.Common;
import com.match.honey.utils.StatusBarUtil;
import com.match.honey.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindAccountAct extends AppCompatActivity implements View.OnClickListener {

    ActivityFindaccountBinding binding;
    AppCompatActivity act;

    String fId = "", fPw = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_findaccount);
        act = this;

        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);

//        binding.btnFindid.setOnClickListener(this);
        binding.btnFindpw.setOnClickListener(this);
        binding.btnLogin.setOnClickListener(this);
        binding.flBack.setOnClickListener(this);

    }

    private void showDlgResult(String ftype, String res) {
        LayoutInflater dialog = LayoutInflater.from(this);
        View dialogLayout = dialog.inflate(R.layout.dlg_findres, null);
        final Dialog resDlg = new Dialog(this);

        resDlg.setContentView(dialogLayout);

        resDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        resDlg.show();

        TextView title = (TextView) dialogLayout.findViewById(R.id.tv_tile);
        TextView type = (TextView) dialogLayout.findViewById(R.id.tv_ftype);
        TextView restext = (TextView) dialogLayout.findViewById(R.id.tv_fres);
        TextView btn_ok = (TextView) dialogLayout.findViewById(R.id.btn_ok);

        if (ftype.equals("id")) {
            title.setText("아이디 찾기");
            type.setText("아이디는");

            fId = res;
        } else if (ftype.equals("pw")) {
            title.setText("비밀번호 찾기");
            type.setText("비밀번호는");

            fPw = res;
        }

        restext.setText(res);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resDlg.isShowing()) {
                    resDlg.dismiss();
                }
            }
        });

    }

    private void showDlgReg() {
        LayoutInflater dialog = LayoutInflater.from(this);
        View dialogLayout = dialog.inflate(R.layout.dlg_password_reg, null);
        final Dialog resDlg = new Dialog(this);

        resDlg.setContentView(dialogLayout);
        resDlg.setCancelable(false);

        resDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        resDlg.show();

        final EditText et_pw01 = (EditText) dialogLayout.findViewById(R.id.et_pw01);
        final EditText et_pw02 = (EditText) dialogLayout.findViewById(R.id.et_pw02);

        TextView btn_ok = (TextView) dialogLayout.findViewById(R.id.btn_ok);
        TextView btn_cancel = (TextView) dialogLayout.findViewById(R.id.btn_cancel);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (StringUtil.isNull(et_pw01.getText().toString())) {
                    Common.showToast(act, "신규비밀번호를 입력해주세요");
                    return;
                }

                if (StringUtil.isNull(et_pw02.getText().toString())) {
                    Common.showToast(act, "비밀번호확인을 입력해주세요");
                    return;
                }


                if (et_pw01.length() < 4 || et_pw01.length() > 12) {
                    Toast.makeText(act, "비밀번호는 4~12자로 사용할 수 있습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!Pattern.matches(".*[a-zA-Z]+.*", et_pw01.getText().toString())) {
                    Toast.makeText(act, "비밀번호에 영어가 포함되어야 합니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!Pattern.matches(".*[0-9]+.*", et_pw01.getText().toString())) {
                    Toast.makeText(act, "비밀번호에 숫자가 포함되어야 합니다", Toast.LENGTH_SHORT).show();
                    return;
                }


                if(!et_pw01.getText().toString().equalsIgnoreCase(et_pw02.getText().toString())) {
                    Common.showToast(act, "비밀번호가 일치하지 않습니다");
                    return;
                }



                findPwReg(et_pw01.getText().toString());
                if (resDlg.isShowing()) {
                    resDlg.dismiss();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resDlg.isShowing()) {
                    resDlg.dismiss();
                }
            }
        });
    }

    private void findId() {
        ReqBasic findId = new ReqBasic(this, NetUrls.FINDID) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                Log.i(StringUtil.TAG, "findId: " + resultData.getResult() + "\ncode: " + resultCode);
                if (resultData.getResult() != null) {

                    try {

                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Find ID Get Info: " + jo);


                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            showDlgResult("id", jo.getString("value"));
                        } else {
                            Toast.makeText(FindAccountAct.this, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(FindAccountAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FindAccountAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }

            }
        };

        findId.addParams("cell", binding.etFindidCellnum.getText().toString());
        findId.execute(true, true);
    }

    private void findPw() {
        ReqBasic findPw = new ReqBasic(this, NetUrls.FINDPW) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                Log.i(StringUtil.TAG, "findPw: " + resultData.getResult() + "\ncode: " + resultCode);
                if (resultData.getResult() != null) {

                    try {

                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Find PW Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            Common.showToast(act, "계정이 확인되었습니다\n새로운 비밀번호를 등록해주세요");
                            showDlgReg();
                        } else {
                            Toast.makeText(FindAccountAct.this, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(FindAccountAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(FindAccountAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

//        findPw.addParams("cell", binding.etFindpwCellnum.getText().toString());
        findPw.addParams("id", binding.etFindpwEmail.getText().toString());
        findPw.execute(true, true);
    }

    private void findPwReg(String newPw) {
        ReqBasic findPw = new ReqBasic(this, NetUrls.FINDPWREG) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                Log.i(StringUtil.TAG, "findPw: " + resultData.getResult() + "\ncode: " + resultCode);
                if (resultData.getResult() != null) {

                    try {

                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Password Reg Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            Common.showToast(act, "비밀번호가 변경되었습니다");
                        } else {
                            Toast.makeText(FindAccountAct.this, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(FindAccountAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(FindAccountAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

//        findPw.addParams("cell", binding.etFindpwCellnum.getText().toString());
        findPw.addParams("id", binding.etFindpwEmail.getText().toString());
        findPw.addParams("pw", newPw);
        findPw.execute(true, true);
    }


    private boolean checkEmail(String email) {
        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        boolean isNormal = m.matches();
        return isNormal;
    }

    private boolean checkCellnum(String cellnum) {
        cellnum = PhoneNumberUtils.formatNumber(cellnum);

        boolean returnValue = false;
        try {
            String regex = "^\\s*(010|011|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$";

            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(cellnum);
            if (m.matches()) {
                returnValue = true;
            }

            if (returnValue && cellnum != null
                    && cellnum.length() > 0
                    && cellnum.startsWith("010")) {
                cellnum = cellnum.replace("-", "");
                if (cellnum.length() < 10) {
                    returnValue = false;
                }
            }
            return returnValue;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_findid:

                if (binding.etFindidCellnum.length() == 0) {
                    Toast.makeText(this, "휴대폰번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!checkCellnum(binding.etFindidCellnum.getText().toString())) {
                    Toast.makeText(this, "휴대폰번호를 확인하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                findId();

                break;
            case R.id.btn_findpw:

                if (binding.etFindpwEmail.length() == 0) {
                    Toast.makeText(this, "아이디(이메일)을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!checkEmail(binding.etFindpwEmail.getText().toString())) {
                    Toast.makeText(this, "아이디(이메일)을 확인해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

//                if (binding.etFindpwCellnum.length() == 0) {
//                    Toast.makeText(this, "휴대폰번호를 입력하세요.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (!checkCellnum(binding.etFindpwCellnum.getText().toString())) {
//                    Toast.makeText(this, "휴대폰번호를 확인하세요.", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                findPw();

                break;
            case R.id.btn_login:
                Intent res = new Intent();
                String data = fId + "#" + fPw;
                res.putExtra("data", data);
                setResult(RESULT_OK, res);
                finish();
                break;
            case R.id.fl_back:
                finish();
                break;
        }
    }
}
