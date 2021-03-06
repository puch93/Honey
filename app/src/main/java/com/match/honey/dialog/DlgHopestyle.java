package com.match.honey.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.match.honey.R;
import com.match.honey.adapters.list.HopestyleAdapter;
import com.match.honey.databinding.DlgHopestyleBinding;
import com.match.honey.listDatas.HopestyleData;
import com.match.honey.utils.StringUtil;

import java.util.ArrayList;

public class DlgHopestyle extends Activity implements View.OnClickListener {
    Activity act;
    DlgHopestyleBinding binding;

    ArrayList<HopestyleData> list = new ArrayList<>();
    HopestyleAdapter adapter;

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

        act = this;
        //////////////
        binding = DataBindingUtil.setContentView(this, R.layout.dlg_hopestyle);

        String[] selItem = getIntent().getStringExtra("select").split("\\|");

        binding.btnOk.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);

        for (String item : getResources().getStringArray(R.array.hopestyle)) {
            HopestyleData data = new HopestyleData();
            for (int i = 0; i < selItem.length; i++) {
                if (selItem[i].equalsIgnoreCase(item)) {
                    data.setSelectState(true);
                }
            }
            data.setText(item);
            list.add(data);
        }

        binding.rcvHopestyle.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new HopestyleAdapter(this, list);
        binding.rcvHopestyle.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                finish();

                break;
            case R.id.btn_ok:
                String selItem = "";
                for (HopestyleData item : list) {
                    if (item.isSelectState()) {
                        if (StringUtil.isNull(selItem)) {
                            selItem = item.getText();
                        } else {
                            selItem += "|" + item.getText();
                        }
                        Log.i(StringUtil.TAG, item.getText());
                    }
                }

                Intent res = new Intent();
                res.putExtra("data", selItem);
                setResult(RESULT_OK, res);
                finish();
                break;
        }
    }
}
