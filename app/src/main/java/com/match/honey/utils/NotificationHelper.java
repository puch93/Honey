package com.match.honey.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.match.honey.R;
import com.match.honey.activity.MainActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;



/**
 * NotificationChannel
 *
 * If on Oreo then notification required a notification channel.
 *
 * IMPORTANCE_DEFAULT       shows everywhere, makes noise, but does not visually intrude
 * IMPORTANCE_HIGH          shows everywhere, makes noise and peeks
 */

/**
 * NotificationCompat.Builder
 *
 * setLargeIcon         큰그림
 * setSmallIcon         큰그림 밑에 작은그림
 * setTicker            알람 발생시 잠깐 나오는 텍스트 (테스트 해보니까 가상 머신에서는 안나오고 실제 디바이스에서는 나오네요)
 * setContentTitle      제목
 * setContentText       내용
 * setWen               알람 시간 (miliSecond 단위로 넣어주면 내부적으로 자동으로 파싱합니다)
 * setDefaults          알람발생시 진동, 사운드등을 설정 (사운드, 진동 둘다 설정할수도 있고 한개 또는 설정하지 않을 수도있음)
 * setContentIntent     알람을 눌렀을 때 실행할작업 인텐트를 설정합니다.
 * setAutoCancel        알람 터치시 자동으로 삭제할 것인지 설정합니다.
 * setNumber            확인하지 않은 알림 갯수를 설정합니다. (999가 초과되면 999+로 나타냅니다.)
 */

public class NotificationHelper {
    private static final String TAG = "TEST_PUSH";

    private Context context;
    private NotificationManager manager;

    private static final String CHANNEL_ID = "default";
    private static final String CHANNEL_NAME = "Default";


    public NotificationHelper(Context context) {
        this.context = context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.enableLights(true);
            manager.createNotificationChannel(channel);
        }
    }


    // notification (default)
    public void showDefaultNotification(String title, String message, String url) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.app_icon);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        notification.flags = notification.flags | notification.FLAG_AUTO_CANCEL;
        manager.notify(1, notification);
    }


    private Bitmap getBitmap(String url) {
        URL imgUrl = null;
        HttpURLConnection connection = null;
        InputStream is = null;

        Bitmap retBitmap = null;

        try {
            imgUrl = new URL(url);
            connection = (HttpURLConnection) imgUrl.openConnection();
            connection.setDoInput(true);
            connection.connect();
            is = connection.getInputStream();
            Bitmap tmp = BitmapFactory.decodeStream(is);
            retBitmap = Bitmap.createScaledBitmap(tmp, tmp.getWidth(), tmp.getHeight(), true);

        } catch (Exception e) {
            Log.e(TAG, "ExceptionExceptionException: ");
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            return retBitmap;
        }
    }


    private String converTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("a hh:mm", java.util.Locale.getDefault());
        String date = dateFormat.format(System.currentTimeMillis());
        Log.e(TAG, "date: " + date);
        return date;
    }
}
