package com.match.marryme.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.CompoundButton;

import com.match.marryme.R;
import com.match.marryme.databinding.ActivitySethistoryBinding;
import com.match.marryme.sharedPref.SettingAlarmPref;

public class HistorysetAct extends Activity implements View.OnClickListener{

    ActivitySethistoryBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sethistory);

        binding.flBack.setOnClickListener(this);
        binding.llHome.setOnClickListener(this);

        binding.cbHistoryuse.setChecked(SettingAlarmPref.isSendHistroy(this));


        binding.cbHistoryuse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SettingAlarmPref.setSendHistory(HistorysetAct.this,b);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fl_back:
                finish();
                break;

            case R.id.ll_home:
                ((MainActivity) MainActivity.act).fromHomeBtn();
                finish();
                break;
        }
    }
}
