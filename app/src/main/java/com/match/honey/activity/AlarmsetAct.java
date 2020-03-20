package com.match.honey.activity;

import android.app.Activity;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;

import com.match.honey.R;
import com.match.honey.databinding.ActivityAlarmsettingBinding;
import com.match.honey.sharedPref.SettingAlarmPref;

public class AlarmsetAct extends Activity implements View.OnClickListener{

    ActivityAlarmsettingBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarmsetting);

        binding.flBack.setOnClickListener(this);
        binding.llHome.setOnClickListener(this);

        binding.cbMsg.setChecked(SettingAlarmPref.isAlarmNewmsg(this));
        binding.cbNewuser.setChecked(SettingAlarmPref.isAlarmNewuser(this));
        binding.cbNotice.setChecked(SettingAlarmPref.isAlarmNotice(this));
//        binding.cbProfupdate.setChecked(SettingAlarmPref.isAlarmProfupdate(this));
        binding.cbProfread.setChecked(SettingAlarmPref.isAlarmProfread(this));
        binding.cbLike.setChecked(SettingAlarmPref.isAlarmLike(this));


        binding.cbMsg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SettingAlarmPref.setAlarmNewmsg(AlarmsetAct.this,b);
            }
        });

        binding.cbNewuser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SettingAlarmPref.setAlarmNewuser(AlarmsetAct.this,b);
            }
        });

        binding.cbNotice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SettingAlarmPref.setAlarmNotice(AlarmsetAct.this,b);
            }
        });


//        binding.cbProfupdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                SettingAlarmPref.setAlarmProfupdate(AlarmsetAct.this,b);
//            }
//        });

        binding.cbProfread.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SettingAlarmPref.setAlarmProfread(AlarmsetAct.this,b);
            }
        });

        binding.cbLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SettingAlarmPref.setAlarmLike(AlarmsetAct.this,b);
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
