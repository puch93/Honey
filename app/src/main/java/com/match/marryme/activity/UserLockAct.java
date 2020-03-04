package com.match.marryme.activity;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.match.marryme.R;
import com.match.marryme.databinding.ActivityUserLockBinding;
import com.match.marryme.utils.Common;

public class UserLockAct extends AppCompatActivity {
    ActivityUserLockBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_lock, null);
        act = this;

        binding.llUserLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.llUserLock.setSelected(!binding.llUserLock.isSelected());
            }
        });

        binding.tvUserLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.showToastDevelop(act);
            }
        });
    }
}
