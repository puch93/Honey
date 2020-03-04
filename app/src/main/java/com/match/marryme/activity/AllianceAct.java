package com.match.marryme.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.match.marryme.R;
import com.match.marryme.databinding.ActivityAllianceBinding;
import com.match.marryme.network.ReqBasic;
import com.match.marryme.network.netUtil.HttpResult;
import com.match.marryme.network.netUtil.NetUrls;
import com.match.marryme.utils.DefaultValue;
import com.match.marryme.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 제휴제안 Activity
 */
public class AllianceAct extends Activity implements View.OnClickListener {

    ActivityAllianceBinding binding;
    File sendFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_alliance);

        binding.flBack.setOnClickListener(this);
        binding.llHome.setOnClickListener(this);

        binding.tvProposetype.setOnClickListener(this);
        binding.btnFindfile.setOnClickListener(this);
        binding.btnPropose.setOnClickListener(this);

        binding.rdoItem3.setChecked(true);
    }

    /**
     * 제휴제안 전송
     *
     * @param type 제휴구분(기업:inheritance,개인:personal,기타:etc)
     * @param sugg 제안유형(서비스:service,광고:advertise)
     */
    private void reqSuggest(String type, String sugg) {
        ReqBasic suggest = new ReqBasic(this, NetUrls.SUGGEST) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            Toast.makeText(AllianceAct.this, jo.getString("comment"), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AllianceAct.this, jo.getString("comment"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AllianceAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AllianceAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        suggest.addParams("atype", type);
        suggest.addParams("suggest", sugg);
        suggest.addParams("cname", binding.etCompname.getText().toString());
        suggest.addParams("email", binding.etEmail.getText().toString());
        suggest.addParams("cnum", binding.etCellnum.getText().toString());
        suggest.addParams("scontents", binding.etContents.getText().toString());
        if (sendFile != null) {
            suggest.addFileParams("sfile", sendFile);
        }
        suggest.execute(true, true);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            Log.i(StringUtil.TAG, "data == null");
            return;
        }

        switch (requestCode) {
            case DefaultValue.SUGGEST:
                String result = data.getStringExtra("data");
                Log.i(StringUtil.TAG, "res: " + result);
                binding.tvProposetype.setText(result);
                binding.tvProposetype.setTextColor(getResources().getColor(R.color.color_2));
                break;
            case DefaultValue.ATTACHFILE:
                sendFile = new File(StringUtil.getPath(this, data.getData()));
                binding.tvAttachfile.setText(sendFile.getName());
                break;
        }

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
            case R.id.fl_back:
                finish();
                break;
            case R.id.ll_home:
                if(ServiceCenterAct.act != null) {
                    ServiceCenterAct.act.finish();
                }

                ((MainActivity) MainActivity.act).fromHomeBtn();

                finish();
                break;

            case R.id.tv_proposetype:

                Intent suggest = new Intent(this, ListDlgAct.class);
                suggest.putExtra("subject", "suggest");
                suggest.putExtra("select", binding.tvProposetype.getText());
                startActivityForResult(suggest, DefaultValue.SUGGEST);
                break;
            case R.id.btn_findfile:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, DefaultValue.ATTACHFILE);
                break;
            case R.id.btn_propose:

                if (!binding.rdoItem1.isChecked() && !binding.rdoItem2.isChecked() && !binding.rdoItem3.isChecked()) {
                    Toast.makeText(AllianceAct.this, "구분 항목을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (binding.tvProposetype.getText().toString().equalsIgnoreCase("제안유형을 선택해주세요")) {
                    Toast.makeText(AllianceAct.this, "제안유형을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (binding.etCompname.length() == 0) {
                    Toast.makeText(AllianceAct.this, "회사(기관)명을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (binding.etEmail.length() == 0) {
                    Toast.makeText(AllianceAct.this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!checkEmail(binding.etEmail.getText().toString())) {
                    Toast.makeText(AllianceAct.this, "이메일을 확인해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (binding.etCellnum.length() == 0) {
                    Toast.makeText(AllianceAct.this, "연락처를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!checkCellnum(binding.etCellnum.getText().toString())) {
                    Toast.makeText(AllianceAct.this, "연락처를 확인해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (binding.etContents.length() == 0) {
                    Toast.makeText(AllianceAct.this, "제안내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }


                String type = "";
                if (binding.rdoItem1.isChecked()) {
                    type = "inheritance";
                } else if (binding.rdoItem2.isChecked()) {
                    type = "personal";
                } else if (binding.rdoItem3.isChecked()) {
                    type = "etc";
                }

                String propose = "";
                if (binding.tvProposetype.getText().toString().equalsIgnoreCase("서비스")) {
                    propose = "service";
                } else {
                    propose = "advertise";
                }

                Log.i(StringUtil.TAG, "propose: " + propose);
                reqSuggest(type, propose);
                break;
        }
    }
}
