package com.match.honey.callback;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PushTestReceiver extends PushMessageReceiver {
    @Override
    public void onBind(Context context, int errorCode, String appid, String userId, String channelId, String requestId) {
        Log.e(StringUtil.TAG_BAIDU, "baidu onBind()");
        Log.e(StringUtil.TAG_BAIDU, "baidu onBind() errorCode: " + errorCode);
        Log.e(StringUtil.TAG_BAIDU, "baidu onBind() appid: " + appid);
        Log.e(StringUtil.TAG_BAIDU, "baidu onBind() userId: " + userId);
        Log.e(StringUtil.TAG_BAIDU, "baidu onBind() channelId: " + channelId);
        Log.e(StringUtil.TAG_BAIDU, "baidu onBind() requestId: " + requestId);

        UserPref.setBaiduToken(context, channelId);
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
    public void onMessage(Context context, String message, String customContentString) {
        Log.d(StringUtil.TAG_BAIDU, "baidu onMessage()");

        String messageString = "onMessage=\"" + message
                + "\" customContentString=" + customContentString;
        Log.d(StringUtil.TAG_BAIDU, messageString);

        // 自定义内容获取方式，mykey和myvalue对应透传消息推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("mykey")) {
                    myvalue = customJson.getString("mykey");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNotificationClicked(Context context, String title, String description, String customContentString) {
        Log.e(StringUtil.TAG_BAIDU, "baidu onNotificationClicked()");

        String notifyString = "通知点击 onNotificationClicked title=\"" + title + "\" description=\""
                + description + "\" customContent=" + customContentString;
        Log.d(TAG, notifyString);

        // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("mykey")) {
                    myvalue = customJson.getString("mykey");
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNotificationArrived(Context context, String title, String description, String customContentString) {
        Log.e(StringUtil.TAG_BAIDU, "baidu onNotificationArrived()");

        String notifyString = "通知到达 onNotificationArrived  title=\"" + title
                + "\" description=\"" + description + "\" customContent="
                + customContentString;
        Log.d(TAG, notifyString);

        // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("mykey")) {
                    myvalue = customJson.getString("mykey");
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
