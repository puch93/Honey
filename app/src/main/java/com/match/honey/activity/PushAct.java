package com.match.honey.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class PushAct extends AppCompatActivity {
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;

        finishAffinity();
        startActivity(new Intent(act, SplashAct.class));
        finish();
    }
}
