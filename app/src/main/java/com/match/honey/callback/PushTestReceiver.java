package com.match.honey.callback;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.match.honey.utils.StringUtil;

import java.util.List;

public class PushTestReceiver extends PushMessageReceiver {
    @Override
    public void onBind(Context context, int i, String s, String s1, String s2, String s3) {
        Log.e(StringUtil.TAG_BAIDU, "baidu onBind()");
        Log.e(StringUtil.TAG_BAIDU, "baidu onBind() errorCode: " + i);
        Log.e(StringUtil.TAG_BAIDU, "baidu onBind() appid: " + s);
        Log.e(StringUtil.TAG_BAIDU, "baidu onBind() userId: " + s1);
        Log.e(StringUtil.TAG_BAIDU, "baidu onBind() channelId: " + s2);
        Log.e(StringUtil.TAG_BAIDU, "baidu onBind() requestId: " + s3);

    }

    @Override
    public void onUnbind(Context context, int i, String s) {
        Log.e(StringUtil.TAG_BAIDU, "baidu onUnbind()");
        Log.e(StringUtil.TAG_BAIDU, "baidu onUnbind() errorCode: " + i);
    }

    @Override
    public void onSetTags(Context context, int i, List<String> list, List<String> list1, String s) {
        Log.e(StringUtil.TAG_BAIDU, "baidu onSetTags()");
        Log.e(StringUtil.TAG_BAIDU, "baidu onSetTags() errorCode: " + i);
    }

    @Override
    public void onDelTags(Context context, int i, List<String> list, List<String> list1, String s) {
        Log.e(StringUtil.TAG_BAIDU, "baidu onDelTags()");
        Log.e(StringUtil.TAG_BAIDU, "baidu onDelTags() errorCode: " + i);
    }

    @Override
    public void onListTags(Context context, int i, List<String> list, String s) {
        Log.e(StringUtil.TAG_BAIDU, "baidu onListTags()");
        Log.e(StringUtil.TAG_BAIDU, "baidu onListTags() errorCode: " + i);
    }

    @Override
    public void onMessage(Context context, String s, String s1) {
        Log.e(StringUtil.TAG_BAIDU, "baidu onMessage()");
    }

    @Override
    public void onNotificationClicked(Context context, String s, String s1, String s2) {
        Log.e(StringUtil.TAG_BAIDU, "baidu onNotificationClicked()");
    }

    @Override
    public void onNotificationArrived(Context context, String s, String s1, String s2) {
        Log.e(StringUtil.TAG_BAIDU, "baidu onNotificationArrived()");
    }
}
