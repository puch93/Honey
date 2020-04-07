package com.match.honey;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.baidu.android.pushservice.BasicPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.match.honey.sharedPref.UserPref;


public class CustomApplication extends Application {
    public static final String TAG = Application.class.getSimpleName();
    public static Context ctx;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application is onCreated!");
        ctx = getApplicationContext();
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, getString(R.string.baidu_api_key));

        BasicPushNotificationBuilder bBuilder = new BasicPushNotificationBuilder();
        bBuilder.setChannelId("여보자기기본");
        bBuilder.setChannelName("여보자기기본이름");
        PushManager.setDefaultNotificationBuilder(getApplicationContext(), bBuilder);
    }
}
