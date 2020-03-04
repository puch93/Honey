package com.match.marryme.sharedPref;

import android.content.Context;
import android.content.SharedPreferences;

public class SystemPref {
    public static int getDeviceWidth(Context context){
        SharedPreferences pref = context.getSharedPreferences("system", context.MODE_PRIVATE);
        return pref.getInt("width",0);
    }

    public static void setDeviceWidth(Context context,int width){
        SharedPreferences pref = context.getSharedPreferences("system", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("width",width);
        editor.commit();
    }
}
