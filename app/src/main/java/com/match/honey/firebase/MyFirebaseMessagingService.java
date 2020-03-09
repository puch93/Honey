package com.match.honey.firebase;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.match.honey.R;
import com.match.honey.activity.ChatAct;
import com.match.honey.activity.MainActivity;
import com.match.honey.activity.PushAct;
import com.match.honey.activity.SplashAct;
import com.match.honey.dialog.DlgAdviewFullScreen;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.SettingAlarmPref;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.NotificationHelper;
import com.match.honey.utils.StringUtil;

import java.util.List;
import java.util.Map;


public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    Context ctx;
    static final int ALARM_NITI = 1;


    @Override
    public void onNewToken(String token) {
        Log.d("TEST_HOME", "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        ctx = getApplicationContext();

//        type : 푸시 타입 값

//        board_idx : 해당 게시물 idx 값
//        board_contents : 해당 게시물 내용 값

//        sender_idx : 댓글 작성한 사용자 idx 값
//        sender_name : 댓글 작성한 사용자 닉네임 값

        Log.e(StringUtil.TAG, "push: " + remoteMessage);
        Log.e(StringUtil.TAG, "push data: " + remoteMessage.getData());

//        {board_idx=6, user_idx=1, btype=notice, title=여보자기공지사항2, content=테스트내용입니다.}
//        {msg_from=40, type=chatting, message=ㅎㅇ}
//        {msg_from=40, room_idx=33, type=chatting, message=로오}

        String type = "";

        if (!StringUtil.isNull(remoteMessage.getData().get("type"))) {
            type = remoteMessage.getData().get("type");
        } else if (!StringUtil.isNull(remoteMessage.getData().get("btype"))) {
            type = remoteMessage.getData().get("btype");
        }

        //type != null / 휴면계정아닐때
        if (!StringUtil.isNull(type) && !UserPref.isPause(ctx)) {
            //프로필 업데이트
//            if (type.equalsIgnoreCase("profileupdate")) {
//                if (SettingAlarmPref.isAlarmProfupdate(getApplicationContext())) {
//                    if (!remoteMessage.getData().get("user_idx").equalsIgnoreCase(UserPref.getUidx(ctx))) {
//                        if (UserPref.getProfileNotiPref(ctx) == 101) {
//                            UserPref.setProfileNotiPref(ctx, 1);
//                        } else {
//                            UserPref.setProfileNotiPref(ctx, UserPref.getNoticeNotiPref(ctx) + 1);
//                        }
//
//                        noticeNoti(getApplicationContext(), remoteMessage.getData(), UserPref.getProfileNotiPref(ctx));
//                    }
//                }
//                //공지
            if (type.equalsIgnoreCase("notice")) {
                if (SettingAlarmPref.isAlarmNotice(getApplicationContext())) {
                    if (UserPref.getNoticeNotiPref(ctx) == 201) {
                        UserPref.setNoticeNotiPref(ctx, 101);
                    } else {
                        UserPref.setNoticeNotiPref(ctx, UserPref.getNoticeNotiPref(ctx) + 1);
                    }

                    noticeNoti(getApplicationContext(), remoteMessage.getData(), UserPref.getNoticeNotiPref(ctx));
                }

                //신규회원가입(관심지역)
            } else if (type.equalsIgnoreCase("newcomer")) {
                if (!remoteMessage.getData().get("user_idx").equalsIgnoreCase(UserPref.getUidx(ctx))) {
                    if (MainActivity.act != null) {
                        ((MainActivity) MainActivity.act).setMenuCount(NetUrls.NEWCOUNT);
                    }

                    if (SettingAlarmPref.isAlarmNewuser(getApplicationContext())) {
                        if (UserPref.getNewNotiPref(ctx) == 601) {
                            UserPref.setNewNotiPref(ctx, 501);
                        } else {
                            UserPref.setNewNotiPref(ctx, UserPref.getNewNotiPref(ctx) + 1);
                        }

                        noticeNoti(getApplicationContext(), remoteMessage.getData(), UserPref.getNewNotiPref(ctx));
                    }
                }

                //채팅
            } else if (type.equalsIgnoreCase("chatting")) {
                if (SettingAlarmPref.isAlarmNewmsg(getApplicationContext())) {
                    if (UserPref.getChatNotiPref(ctx) == 501) {
                        UserPref.setChatNotiPref(ctx, 401);
                    } else {
                        UserPref.setChatNotiPref(ctx, UserPref.getChatNotiPref(ctx) + 1);
                    }

                    chatNoti(getApplicationContext(), remoteMessage.getData(), UserPref.getChatNotiPref(ctx));
                }

                //전면광고
            } else if (type.equalsIgnoreCase("notification_front")) {
                frontadminpush(getApplicationContext(), remoteMessage.getData());

                //노티
            } else if (type.equalsIgnoreCase("notification_top")) {
                topadminpush(getApplicationContext(), remoteMessage.getData());

                //열람회원
            } else if (type.equalsIgnoreCase("view")) {
                if (!remoteMessage.getData().get("user_idx").equalsIgnoreCase(UserPref.getUidx(ctx))) {
                    if (MainActivity.act != null) {
                        ((MainActivity) MainActivity.act).setMenuCount(NetUrls.VIEWCOUNT);
                    }

                    if (SettingAlarmPref.isAlarmProfread(getApplicationContext())) {
                        if (UserPref.getViewNotiPref(ctx) == 401) {
                            UserPref.setViewNotiPref(ctx, 301);
                        } else {
                            UserPref.setViewNotiPref(ctx, UserPref.getViewNotiPref(ctx) + 1);
                        }

                        noticeNoti(getApplicationContext(), remoteMessage.getData(), UserPref.getViewNotiPref(ctx));
                    }
                }

                //찜회원
            } else if (type.equalsIgnoreCase("like")) {
                if (!remoteMessage.getData().get("user_idx").equalsIgnoreCase(UserPref.getUidx(ctx))) {
                    if (MainActivity.act != null) {
                        ((MainActivity) MainActivity.act).setMenuCount(NetUrls.LIKECOUNT);
                    }
                    if (SettingAlarmPref.isAlarmLike(getApplicationContext())) {
                        if (UserPref.getLikeNotiPref(ctx) == 701) {
                            UserPref.setLikeNotiPref(ctx, 601);
                        } else {
                            UserPref.setLikeNotiPref(ctx, UserPref.getLikeNotiPref(ctx) + 1);
                        }

                        noticeNoti(getApplicationContext(), remoteMessage.getData(), UserPref.getNoticeNotiPref(ctx));
                    }
                }
            }
        } else {
            Log.e(StringUtil.TAG, "푸시 타입 없음");
        }
    }

    private void frontadminpush(final Context context, Map<String, String> extras) {
        String type = extras.get("type");
        String title = extras.get("title");
//        String msg = extras.get("content");
        String img = extras.get("filename");
        String url = extras.get("url");

        Log.e("TEST", "title: " + title + " img: " + img + " url: " + url);

        Intent intent = new Intent(this, DlgAdviewFullScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

//        Intent intent = new Intent(this, DlgPopUpAd.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.putExtra("type", type);
        intent.putExtra("imgurl", img);
        intent.putExtra("targeturl", url);

        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void topadminpush(final Context context, Map<String, String> extras) {
        String type = extras.get("type");
        String title = extras.get("title");
        String msg = extras.get("content");
        String img = extras.get("filename");
        String url = extras.get("url");

        NotificationHelper helper = new NotificationHelper(getBaseContext());
        helper.showNotification(title, msg, img, url);

    }

    public void noticeNoti(Context context, Map<String, String> data, int id) {

        NotificationCompat.Builder notificationBuilder;

        String title = data.get("title");
        String content = data.get("content");

        /* 팝업삭제 */
//        Intent dlg = new Intent(context, DlgPush.class);
//        dlg.putExtra("title", title);
//        dlg.putExtra("contents", content);
//        dlg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent pendingIntent_dlg = PendingIntent.getActivity(ctx, 1, dlg,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        try {
//            pendingIntent_dlg.send();
//        } catch (PendingIntent.CanceledException e) {
//            e.printStackTrace();
//        }

        Intent intent = new Intent(context, PushAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    "여보자기", "여보자기", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(mChannel);
            notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), mChannel.getId());
        } else {
            notificationBuilder = new NotificationCompat.Builder(getApplicationContext());
        }

        notificationBuilder.setAutoCancel(true)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent);

        notificationManager.notify(id, notificationBuilder.build());
    }

    public void chatNoti(Context context, Map<String, String> data, int id) {
        if (MainActivity.act != null) {
            ((MainActivity) MainActivity.act).setMessageCount();
        }

        NotificationCompat.Builder notificationBuilder;

        String msg = data.get("message");
        String msg_from = data.get("msg_from");
        String room_idx = data.get("room_idx");
        String gender = data.get("gender");

//        if (msg.contains("http:\\/\\/fileupload.coreplanet.kr\\/ybyb\\/")){
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

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    "여보자기", "여보자기", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), "여보자기");
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


//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel("0", "여보자기", NotificationManager.IMPORTANCE_DEFAULT);
//            channel.setDescription("여보자기");
//            channel.enableVibration(true);
//            channel.enableLights(false);
//
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        NotificationCompat.Builder builder = null;
//        builder = new NotificationCompat.Builder(context, "0")
//                .setSmallIcon(R.drawable.app_icon)
//                .setAutoCancel(true)
//                .setContentTitle("여보자기")
//                .setContentText(msg);
//
//
//        Notification notification = builder.build();
//
//        notification.flags = notification.flags | notification.FLAG_AUTO_CANCEL;
//
//        PendingIntent pendingIn = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//        builder.setContentIntent(pendingIn);
//        notification = builder.build();
//
//        notificationManager.notify(MyFirebaseMessagingService.ALARM_NITI, notification);


    }

    public String getClassname(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> services = activityManager.getRunningTasks(1);

        return services.get(0).topActivity.getClassName();
    }

    public boolean isAppRun(Context context) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(9999);

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).topActivity.getPackageName().equalsIgnoreCase(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

}
