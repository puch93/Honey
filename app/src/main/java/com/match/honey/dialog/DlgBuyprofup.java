package com.match.honey.dialog;

import android.app.Activity;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.match.honey.R;
import com.match.honey.databinding.DlgBuyprofileupBinding;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class DlgBuyprofup extends Activity implements View.OnClickListener{

    DlgBuyprofileupBinding binding;

    int itemprice = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        // 아래에 원하는 투명도를 넣으면 된다.
        lpWindow.dimAmount = 0.6f;
        lpWindow.height = (int) (deviceHeight * 0.85f);
        getWindow().setAttributes(lpWindow);

        binding = DataBindingUtil.setContentView(this,R.layout.dlg_buyprofileup);

        binding.flProfup01.setOnClickListener(this);
        binding.flProfup02.setOnClickListener(this);
        binding.flProfup03.setOnClickListener(this);
        binding.flProfup04.setOnClickListener(this);
        binding.flProfup05.setOnClickListener(this);
        binding.flProfup06.setOnClickListener(this);

        binding.btnCancel.setOnClickListener(this);
        binding.btnOk.setOnClickListener(this);

        binding.tvHavecoin.setText(StringUtil.setNumComma(UserPref.getCoin(this)));

    }

    private void initSelect(){
        binding.rdoProfup01.setChecked(false);
        binding.rdoProfup02.setChecked(false);
        binding.rdoProfup03.setChecked(false);
        binding.rdoProfup04.setChecked(false);
        binding.rdoProfup05.setChecked(false);
        binding.rdoProfup06.setChecked(false);
    }

    /**
     * 아이템 구매
     * @param icode 아이템코드(m_01_4900,m_01_5900,m_01_6900,m_05_21900,m_10_39500,m_15_49900,m_30_89500,p_03_3000,p_05_5000,p_15_15000,p_30_25000,i_01_1000,i_05_4900,i_10_9000,i_30_24900,up_30_3000,up_50_5000,up_100_10000,up_200_19000,up_400_36000,up_600_50000)
     * @param itype 아이템타입(메세지이용권 : message, 프로필 열람권 : profile, 관심있어요 : interest, 프로필 자동 위로 올리기 : profileup)
     * @param isubject 아이템 명 (ex: 메세지이용권 1일 [전체내용])
     * @param ucoin 사용 코인금액
     */
    private void buyItem(String icode, String itype,String isubject, String ucoin){
        ReqBasic buyItem = new ReqBasic(this, NetUrls.BUYITEM) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null){
                    try{

                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)){
                            int coin = UserPref.getCoin(DlgBuyprofup.this) - itemprice;
                            UserPref.setCoin(DlgBuyprofup.this,coin);
                            finish();
                        }else{
                            Toast.makeText(DlgBuyprofup.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                        Toast.makeText(DlgBuyprofup.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(DlgBuyprofup.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        buyItem.addParams("uidx", UserPref.getUidx(this));
        buyItem.addParams("icode",icode);
        buyItem.addParams("itype",itype);
        buyItem.addParams("isubject",isubject);
        buyItem.addParams("ucoin",ucoin);
        buyItem.execute(true,true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
                String code = "", subject = "프로필 자동 위로 올리기";
                if (binding.rdoProfup01.isChecked()){
                    code = StringUtil.UITEM01;
                    subject += " 30회";
                }else if (binding.rdoProfup02.isChecked()){
                    code = StringUtil.UITEM02;
                    subject += " 50회";
                }else if (binding.rdoProfup03.isChecked()){
                    code = StringUtil.UITEM03;
                    subject += " 100회";
                }else if (binding.rdoProfup04.isChecked()){
                    code = StringUtil.UITEM04;
                    subject += " 200회";
                }else if (binding.rdoProfup05.isChecked()){
                    code = StringUtil.UITEM05;
                    subject += " 400회";
                }else if (binding.rdoProfup06.isChecked()){
                    code = StringUtil.UITEM06;
                    subject += " 600회";
                }else{
                    // 토스트 메세지 선택항목 없음
                }

                buyItem(code,StringUtil.TYPE_P,subject,String.valueOf(itemprice));
                break;
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.fl_profup01:
                itemprice = 3000;
                initSelect();
                binding.rdoProfup01.setChecked(true);
                binding.tvNeedcoin.setText(StringUtil.setNumComma(StringUtil.calcCoin(UserPref.getCoin(this),itemprice)));
                break;
            case R.id.fl_profup02:
                itemprice = 5000;
                initSelect();
                binding.rdoProfup02.setChecked(true);
                binding.tvNeedcoin.setText(StringUtil.setNumComma(StringUtil.calcCoin(UserPref.getCoin(this),itemprice)));
                break;
            case R.id.fl_profup03:
                itemprice = 10000;
                initSelect();
                binding.rdoProfup03.setChecked(true);
                binding.tvNeedcoin.setText(StringUtil.setNumComma(StringUtil.calcCoin(UserPref.getCoin(this),itemprice)));
                break;
            case R.id.fl_profup04:
                itemprice = 19000;
                initSelect();
                binding.rdoProfup04.setChecked(true);
                binding.tvNeedcoin.setText(StringUtil.setNumComma(StringUtil.calcCoin(UserPref.getCoin(this),itemprice)));
                break;
            case R.id.fl_profup05:
                itemprice = 36000;
                initSelect();
                binding.rdoProfup05.setChecked(true);
                binding.tvNeedcoin.setText(StringUtil.setNumComma(StringUtil.calcCoin(UserPref.getCoin(this),itemprice)));
                break;
            case R.id.fl_profup06:
                itemprice = 50000;
                initSelect();
                binding.rdoProfup06.setChecked(true);
                binding.tvNeedcoin.setText(StringUtil.setNumComma(StringUtil.calcCoin(UserPref.getCoin(this),itemprice)));
                break;
        }
    }
}
