package com.match.marryme.sharedPref;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingAlarmPref {

    public static boolean isAlarmNewmsg(Context context){
        SharedPreferences pref = context.getSharedPreferences("setting", context.MODE_PRIVATE);
        return pref.getBoolean("isnewmsg", true);
    }

    /**
     * 신규메세지 / 관심있어요 알림
     * @param context
     * @param isnewmsg 신규메세지/관심있어요 알림 설정 상태 true/false
     */
    public static void setAlarmNewmsg(Context context, boolean isnewmsg){
        SharedPreferences pref = context.getSharedPreferences("setting", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isnewmsg",isnewmsg);
        editor.commit();
    }

    public static boolean isAlarmNotice(Context context){
        SharedPreferences pref = context.getSharedPreferences("setting", context.MODE_PRIVATE);
        return pref.getBoolean("isnotice", true);
    }

    /**
     * 공지사항 알림
     * @param context
     * @param isnotice 공지사항 알림 설정 상태 true/false
     */
    public static void setAlarmNotice(Context context, boolean isnotice){
        SharedPreferences pref = context.getSharedPreferences("setting", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isnotice",isnotice);
        editor.commit();
    }

    public static boolean isAlarmNewuser(Context context){
        SharedPreferences pref = context.getSharedPreferences("setting", context.MODE_PRIVATE);
        return pref.getBoolean("isnewuser", true);
    }

    /**
     * 신규가입회원 실시간 알림
     * @param context
     * @param isnewuser 신규가입회원 알림 설정 상태 true/false
     */
    public static void setAlarmNewuser(Context context, boolean isnewuser){
        SharedPreferences pref = context.getSharedPreferences("setting", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isnewuser",isnewuser);
        editor.commit();
    }

    public static boolean isAlarmProfread(Context context){
        SharedPreferences pref = context.getSharedPreferences("setting", context.MODE_PRIVATE);
        return pref.getBoolean("isprofread", true);
    }

    /**
     * 프로필 열람상승 알림
     * @param context
     * @param isprofread 프로필열람 상승 알림 설정 상태 true/false
     */
    public static void setAlarmProfread(Context context, boolean isprofread){
        SharedPreferences pref = context.getSharedPreferences("setting", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isprofread",isprofread);
        editor.commit();
    }


    public static boolean isAlarmLike(Context context){
        SharedPreferences pref = context.getSharedPreferences("setting", context.MODE_PRIVATE);
        return pref.getBoolean("like", true);
    }

    /**
     * 찜 알림
     * @param context
     * @param like 찜 알림 설정 상태 true/false
     */
    public static void setAlarmLike(Context context, boolean like){
        SharedPreferences pref = context.getSharedPreferences("setting", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("like",like);
        editor.commit();
    }

    public static boolean isAlarmProfupdate(Context context){
        SharedPreferences pref = context.getSharedPreferences("setting", context.MODE_PRIVATE);
        return pref.getBoolean("isprofupdate", true);
    }

    /**
     * 프로필 업데이트 알림
     * @param context
     * @param isprofupdate 프로필 업데이트 알림 설정 상태 true/false
     */
    public static void setAlarmProfupdate(Context context, boolean isprofupdate){
        SharedPreferences pref = context.getSharedPreferences("setting", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isprofupdate",isprofupdate);
        editor.commit();
    }



    public static boolean isSendHistroy(Context context){
        SharedPreferences pref = context.getSharedPreferences("setting", context.MODE_PRIVATE);
        return pref.getBoolean("ishistory", true);
    }

    /**
     * 방문기록 남기기
     * @param context
     * @param ishistory 방문기록 남기기 상태 설정 true/false
     */
    public static void setSendHistory(Context context, boolean ishistory){
        SharedPreferences pref = context.getSharedPreferences("setting", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("ishistory",ishistory);
        editor.commit();
    }



}
