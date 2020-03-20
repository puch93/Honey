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
import com.match.honey.databinding.DlgUserlockBinding;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class DlgUserlock extends Activity implements View.OnClickListener{

    DlgUserlockBinding binding;

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
        lpWindow.height = (int)(deviceHeight*0.85f);
        getWindow().setAttributes(lpWindow);

        binding = DataBindingUtil.setContentView(this, R.layout.dlg_userlock);

        binding.btnOk.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);

    }

    private void reqUserLock(){
        ReqBasic userLock = new ReqBasic(this, NetUrls.MEMPAUSE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null){
                    try{
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)){
                            Toast.makeText(DlgUserlock.this, jo.getString("value"), Toast.LENGTH_SHORT).show();
                            finishAffinity();
                        }else{
                            Toast.makeText(DlgUserlock.this, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                        Toast.makeText(DlgUserlock.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(DlgUserlock.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }

            }
        };

        userLock.addParams("uidx", UserPref.getUidx(this));
        userLock.execute(true,true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_ok:
                reqUserLock();
                break;
        }
    }
}
