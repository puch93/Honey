package com.match.honey.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class PushAct extends AppCompatActivity {
    AppCompatActivity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;

        finishAffinity();
        startActivity(new Intent(act, SplashAct.class));
        finish();
    }
}
