package com.match.honey.fragment;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;

public class BaseFrag extends Fragment {
    public static WeakHandler handler;

    public static class WeakHandler extends Handler {
        private final WeakReference<AppCompatActivity> mWeakActivity;

        public WeakHandler(AppCompatActivity activity) {
            mWeakActivity = new WeakReference<AppCompatActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Activity _activity = mWeakActivity.get();
            if (_activity != null) {
                //activity Method Call
            }
        }
    }
}
