package com.match.honey.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.match.honey.R;
import com.match.honey.databinding.DlgBuyinterestBinding;
import com.match.honey.utils.StringUtil;

public class DlgBuyinterest extends AppCompatActivity implements View.OnClickListener {

    DlgBuyinterestBinding binding;

    String selectedPrice = "";
    String selectedCode = "";
    String selectedName = "";

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

        binding = DataBindingUtil.setContentView(this, R.layout.dlg_buyinterest);


        binding.llInterest01.setOnClickListener(this);
        binding.llInterest02.setOnClickListener(this);
        binding.llInterest03.setOnClickListener(this);
        binding.llInterest04.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
        binding.tvOk.setOnClickListener(this);

        binding.llInterest01.performClick();
    }

    private void initSelect() {
        binding.rdoInterest01.setChecked(false);
        binding.rdoInterest02.setChecked(false);
        binding.rdoInterest03.setChecked(false);
        binding.rdoInterest04.setChecked(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                if (StringUtil.isNull(selectedCode)) {
                    Toast.makeText(DlgBuyinterest.this, "구매 아이템을 선택 해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra("selectedPrice", selectedPrice);
                intent.putExtra("selectedCode", selectedCode);
                intent.putExtra("selectedName", selectedName);

                setResult(RESULT_OK, intent);
                finish();
                break;

            case R.id.tv_cancel:
                finish();
                break;
            case R.id.ll_interest01:
                selectedPrice = "1000";
                selectedName = "1개";
                selectedCode = StringUtil.INTEREST01;

                initSelect();
                binding.rdoInterest01.setChecked(true);
                break;
            case R.id.ll_interest02:
                selectedPrice = "4900";
                selectedName = "5개";
                selectedCode = StringUtil.INTEREST02;

                initSelect();
                binding.rdoInterest02.setChecked(true);
                break;
            case R.id.ll_interest03:
                selectedPrice = "9000";
                selectedName = "10개";
                selectedCode = StringUtil.INTEREST03;

                initSelect();
                binding.rdoInterest03.setChecked(true);
                break;
            case R.id.ll_interest04:
                selectedPrice = "24900";
                selectedName = "30개";
                selectedCode = StringUtil.INTEREST04;

                initSelect();
                binding.rdoInterest04.setChecked(true);
                break;

        }
    }
}
