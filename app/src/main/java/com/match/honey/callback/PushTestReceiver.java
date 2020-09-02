package com.match.honey.callback;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.match.honey.R;
import com.match.honey.activity.ChatAct;
import com.match.honey.activity.MainActivity;
import com.match.honey.activity.PushAct;
import com.match.honey.activity.SplashAct;
import com.match.honey.dialog.DlgAdviewFullScreen;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.SettingAlarmPref;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.Common;
import com.match.honey.utils.NotificationHelper;
import com.match.honey.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import static android.content.Context.ACTIVITY_SERVICE;

public class PushTestReceiver extends PushMessageReceiver {
    @Override
    public void onBind(Context context, int errorCode, String appid, String userId, String channelId, String requestId) {
        Log.e(StringUtil.TAG_BAIDU, "baidu onBind()");
        Log.e(StringUtil.TAG_BAIDU, "baidu onBind() errorCode: " + errorCode);
        Log.e(StringUtil.TAG_BAIDU, "baidu onBind() appid: " + appid);
        Log.e(StringUtil.TAG_BAIDU, "baidu onBind() userId: " + userId);
        Log.e(StringUtil.TAG_BAIDU, "baidu onBind() channelId: " + channelId);
        Log.e(StringUtil.TAG_BAIDU, "baidu onBind() requestId: " + requestId);

        Common.showToastLongContext(context,
                "errorCode: " + errorCode + "\n" +
                        "appid: " + appid + "\n" +
                        "userId: " + userId + "\n" +
                        "channelId: " + channelId + "\n" +
                        "requestId: " + requestId
        );

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
//        Log.d(StringUtil.TAG_BAIDU, "baidu onMessage()");
//
//        String messageString = "onMessage=\"" + message
//                + "\" customContentString=" + customContentString;
//        Log.d(StringUtil.TAG_BAIDU, messageString);
//
//        // 自定义内容获取方式，mykey和myvalue对应透传消息推送时自定义内容中设置的键和值
//        if (!TextUtils.isEmpty(customContentString)) {
//            JSONObject customJson = null;
//            try {
//                customJson = new JSONObject(customContentString);
//                String myvalue = null;
//                if (!customJson.isNull("mykey")) {
//                    myvalue = customJson.getString("mykey");
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public void onNotificationClicked(Context context, String title, String description, String customContentString) {
        Log.e(StringUtil.TAG_BAIDU, "baidu onNotificationClicked()");

        String notifyString = "onNotificationClicked title=\"" + title + "\" description=\""
                + description + "\" customContent=" + customContentString;
        Log.d(TAG, notifyString);

        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);

                String type = StringUtil.getStr(customJson, "type");

                Intent intent = null;
                if (!StringUtil.isNull(type) && !UserPref.isPause(context)) {
                    switch (type) {
                        case "notice":
                        case "like":
                        case "newcomer":
                        case "view":

                            intent = new Intent(context, PushAct.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            break;


                        case "chatting":
                            String msg_from = customJson.getString("user_idx");
                            String room_idx = customJson.getString("room_idx");

                            if (isAppRun(context)) {
                                intent = new Intent(context, ChatAct.class);
                                intent.putExtra("enter", "push");
                                intent.putExtra("msg_from", msg_from);
                                intent.putExtra("room_idx", room_idx);
                            } else {
                                intent = new Intent(context, SplashAct.class);
                                intent.putExtra("enter", "push");
                                intent.putExtra("msg_from", msg_from);
                                intent.putExtra("room_idx", room_idx);
                            }

                            context.startActivity(intent);
                            break;


                        case "notification_front":
                            frontadminpush(context, title, description, customJson);
                            break;

                        case "notification_top":
                            topadminpush(context, title, description, customJson);
                            break;
                    }
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
        Log.d(StringUtil.TAG_BAIDU, notifyString);


        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);

                String type = StringUtil.getStr(customJson, "type");

                if (!StringUtil.isNull(type) && !UserPref.isPause(context)) {
                    switch (type) {
                        case "chatting":
                            if (MainActivity.act != null) {
                                ((MainActivity) MainActivity.act).setMessageCount();
                            }

                            break;

                        case "like":
                            if (MainActivity.act != null) {
                                ((MainActivity) MainActivity.act).setMenuCount(NetUrls.LIKECOUNT);
                            }

                            break;

                        case "newcomer":
                            if (MainActivity.act != null) {
                                ((MainActivity) MainActivity.act).setMenuCount(NetUrls.NEWCOUNT);
                            }
                            break;

                        case "view":
                            if (MainActivity.act != null) {
                                ((MainActivity) MainActivity.act).setMenuCount(NetUrls.VIEWCOUNT);
                            }
                            break;
                    }
                }


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    public void noticeNoti(Context context, String title, String description, JSONObject data, int id) throws JSONException {
        NotificationCompat.Builder notificationBuilder;

        Intent intent = new Intent(context, PushAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    "여보자기", "여보자기", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(mChannel);
            notificationBuilder = new NotificationCompat.Builder(context, mChannel.getId());
        } else {
            notificationBuilder = new NotificationCompat.Builder(context);
        }

        notificationBuilder.setAutoCancel(true)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(title)
                .setContentText(description)
                .setContentIntent(pendingIntent);

        notificationManager.notify(id, notificationBuilder.build());
    }


    public void chatNoti(Context context, String title, String description, JSONObject data, int id) throws JSONException {
        if (MainActivity.act != null) {
            ((MainActivity) MainActivity.act).setMessageCount();
        }

        NotificationCompat.Builder notificationBuilder;

        String msg = description;
        String msg_from = data.getString("user_idx");
        String room_idx = data.getString("room_idx");

        if (StringUtil.isImage(msg)) {
            msg = "사진을 받았습니다.";
        } else if (msg.contains(StringUtil.IMOTICON)) {
            msg = "\'찜♡\'을 받았습니다.";
        }

        Intent intent = null;

        if (isAppRun(context)) {
            intent = new Intent(context, ChatAct.class);
            intent.putExtra("enter", "push");
            intent.putExtra("msg_from", msg_from);
            intent.putExtra("room_idx", room_idx);
        } else {
            intent = new Intent(context, SplashAct.class);
            intent.putExtra("enter", "push");
            intent.putExtra("msg_from", msg_from);
            intent.putExtra("room_idx", room_idx);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    "여보자기", "여보자기", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationBuilder = new NotificationCompat.Builder(context, "여보자기");
        notificationBuilder.setAutoCancel(true)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("여보자기")
                .setContentText(msg)
                .setContentIntent(pendingIntent);

        Notification notification = notificationBuilder.build();
        notification.flags = notification.flags | notification.FLAG_AUTO_CANCEL;


        if (!UserPref.getUidx(context).equalsIgnoreCase(msg_from)) {
            if (!getClassname(context).equalsIgnoreCase("com.match.honey.activity.ChatAct")) {
                /* id */
                notificationManager.notify(id, notification);
            }
        }
    }

    private void frontadminpush(final Context context, String title, String description, JSONObject extras) throws JSONException {
        String type = extras.getString("type");
//        String msg = extras.getString("content");
        String url = extras.getString("url");

        Log.e("TEST", "title: " + title + " img: " + description + " url: " + url);

        Intent intent = new Intent(context, DlgAdviewFullScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

//        Intent intent = new Intent(this, DlgPopUpAd.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.putExtra("type", type);
        intent.putExtra("imgurl", description);
        intent.putExtra("targeturl", url);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void topadminpush(final Context context, String title, String description, JSONObject extras) throws JSONException {
        String type = extras.getString("type");
        String img = extras.getString("filename");
        String url = extras.getString("url");

        NotificationHelper helper = new NotificationHelper(context);
        helper.showNotification(title, description, img, url);

    }

    public String getClassname(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> services = activityManager.getRunningTasks(1);

        return services.get(0).topActivity.getClassName();
    }

    public boolean isAppRun(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(9999);

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).topActivity.getPackageName().equalsIgnoreCase(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}
