package com.match.honey.sharedPref;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPref {
    public static boolean isLogout(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getBoolean("islogout", false);
    }

    public static void setLogoutState(Context context, boolean islogin){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("islogout",islogin);
        editor.commit();
    }

    public static boolean isPause(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getBoolean("pause", false);
    }

    public static void setPause(Context context, boolean islogin){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("pause",islogin);
        editor.commit();
    }


    public static boolean isLogin(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getBoolean("islogin", false);
    }

    public static void setLoginState(Context context, boolean islogin){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("islogin",islogin);
        editor.commit();
    }

    public static boolean isRegProf(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getBoolean("isregprof", false);
    }

    public static void setRegProf(Context context, boolean isregprof){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isregprof",isregprof);
        editor.commit();
    }

    public static String getUidx(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getString("uidx","");
    }

    public static void setUidx(Context context,String uidx){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("uidx",uidx);
        editor.commit();
    }

    public static String getId(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getString("id","");
    }

    public static void setId(Context context,String id){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id",id);
        editor.commit();
    }

    public static String getPw(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getString("pw","");
    }

    public static void setPw(Context context,String pw){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("pw",pw);
        editor.commit();
    }

    public static String getGender(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getString("gender","");
    }

    public static void setGender(Context context,String gender){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("gender",gender);
        editor.commit();
    }

    public static String getJtype(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getString("jtype","");
    }

    public static void setJtype(Context context,String jtype){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("jtype",jtype);
        editor.commit();
    }


    public static String getInterIdxs(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getString("interidxs","");
    }

    public static void setInterIdxs(Context context,String interidxs){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("interidxs",interidxs);
        editor.commit();
    }

    public static String getBaiduToken(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getString("baidu","");
    }

    public static void setBaiduToken(Context context,String fcm){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("baidu",fcm);
        editor.commit();
    }

    public static String getLat(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getString("lat","");
    }

    public static void setLat(Context context,String lat){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("lat",lat);
        editor.commit();
    }

    public static String getLon(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getString("lon","");
    }

    public static void setLon(Context context,String lon){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("lon",lon);
        editor.commit();
    }

    public static String getLocationLat(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getString("loclat","");
    }

    public static void setLocationLat(Context context,String loclat){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("loclat",loclat);
        editor.commit();
    }

    public static String getLocationLon(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getString("loclon","");
    }

    public static void setLocationLon(Context context,String loclon){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("loclon",loclon);
        editor.commit();
    }

    public static String getHopeLoc(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getString("hopeloc","");
    }

    public static void setHopeLoc(Context context,String hopeloc){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("hopeloc",hopeloc);
        editor.commit();
    }

    public static int getCoin(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getInt("coin",0);
    }

    public static void setCoin(Context context,int coin){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("coin",coin);
        editor.commit();
    }

    public static String getMsgtState(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getString("msgtstate","");
    }

    public static void setMsgtState(Context context,String msgtstate){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("msgtstate",msgtstate);
        editor.commit();
    }

    public static String getMsgExpdate(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getString("msgexpdate","");
    }

    public static void setMsgExpdate(Context context,String msgexpdate){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("msgexpdate",msgexpdate);
        editor.commit();
    }

    public static String getProfrtState(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getString("profrtstate","");
    }

    public static void setProfrtState(Context context,String profrtstate){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("profrtstate",profrtstate);
        editor.commit();
    }



    /* 푸시 preference */

    //프로필 업데이트 푸시 - 1~100
    public static int getProfileNotiPref(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getInt("profile_id",1);
    }

    public static void setProfileNotiPref(Context context,int noti_id){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("profile_id",noti_id);
        editor.commit();
    }

    //공지 푸시 - 101~200
    public static int getNoticeNotiPref(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getInt("notice_id",101);
    }

    public static void setNoticeNotiPref(Context context,int noti_id){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("notice_id",noti_id);
        editor.commit();
    }

    //관리자 푸시 - 201~300
    public static int getTopNotiPref(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getInt("top_id",201);
    }

    public static void setTopNotiPref(Context context,int noti_id){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("top_id",noti_id);
        editor.commit();
    }


    //열람회원 푸시 - 301~400
    public static int getViewNotiPref(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getInt("view_id",301);
    }

    public static void setViewNotiPref(Context context,int noti_id){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("view_id",noti_id);
        editor.commit();
    }

    //채팅 푸시 - 401~500
    public static int getChatNotiPref(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getInt("chat_id",401);
    }

    public static void setChatNotiPref(Context context,int noti_id){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("chat_id",noti_id);
        editor.commit();
    }

    //신규가입 푸시 - 501~600
    public static int getNewNotiPref(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getInt("new_id",501);
    }

    public static void setNewNotiPref(Context context,int noti_id){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("new_id",noti_id);
        editor.commit();
    }

    //찜회원 푸시 - 601~700
    public static int getLikeNotiPref(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getInt("like_id",601);
    }

    public static void setLikeNotiPref(Context context,int noti_id){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("like_id",noti_id);
        editor.commit();
    }




    // 위챗페이 테스트
    public static String getPayNum(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getString("pay","");
    }

    public static void setPayNum(Context context,String uidx){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("pay",uidx);
        editor.commit();
    }





    // 나라
    public static String getCountry(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        return pref.getString("country","international");
    }

    public static void setCountry(Context context,String country){
        SharedPreferences pref = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("country",country);
        editor.commit();
    }
}
