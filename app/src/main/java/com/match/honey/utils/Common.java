package com.match.honey.utils;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.match.honey.R;

public class Common {
    public static final String M1 = "chM01";
    public static final String M2 = "chM02";
    public static final String M3 = "chM03";
    public static final String M4 = "chM04";
    public static final String M5 = "chM05";

    public static final String W1 = "chW01";
    public static final String W2 = "chW02";
    public static final String W3 = "chW03";
    public static final String W4 = "chW04";
    public static final String W5 = "chW05";

    public static void showToast(final Activity act, final String msg) {

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(act, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showToastLong(final Activity act, final String msg) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(act, msg, Toast.LENGTH_LONG).show();
            }
        });
    }



    public static void showToastNet(final Activity act) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(act, "네트워크 상태를 확인해주세요", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showToastDevelop(final Activity act) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(act, "준비중인 기능입니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static int getCharacterDrawable(String c, String gender) {
        if (gender.equalsIgnoreCase("male")) {
            switch (c) {
                case M1:
                    return R.drawable.img_noimg_man;
                case M2:
                    return R.drawable.getmarried_img_pcharacter07_190902;
                case M3:
                    return R.drawable.getmarried_img_pcharacter08_190902;
                case M4:
                    return R.drawable.getmarried_img_pcharacter09_190902;
                case M5:
                    return R.drawable.getmarried_img_pcharacter10_190902;
                default:
                    return 0;
            }
        } else {
            switch (c) {
                case W1:
                    return R.drawable.profile_noimg_woman;
                case W2:
                    return R.drawable.getmarried_img_pcharacter02_190902;
                case W3:
                    return R.drawable.getmarried_img_pcharacter03_190902;
                case W4:
                    return R.drawable.getmarried_img_pcharacter04_190902;
                case W5:
                    return R.drawable.getmarried_img_pcharacter05_190902;
                default:
                    return 0;
            }
        }
    }

    public static void showSnack(final Activity act, final String msg) {

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(act.getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public static void showSnackLong(final Activity act, final String msg) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(act.getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
