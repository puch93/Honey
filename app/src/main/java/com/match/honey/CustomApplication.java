package com.match.honey;

import android.app.Application;
import android.util.Log;


public class CustomApplication extends Application {
    public static final String TAG = Application.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application is onCreated!");
    }
}
