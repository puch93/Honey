package com.match.honey.utils;

import android.app.Activity;
import android.os.Build;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.match.honey.R;


public class StatusBarUtil {
    public enum StatusBarColorType {
        WHITE_STATUS_BAR(R.color.white_color),
        GREY_STATUS_BAR(R.color.color_f6f6f6);

        private int backgroundColorId;

        StatusBarColorType(int backgroundColorId) {
            this.backgroundColorId = backgroundColorId;
        }

        public int getBackgroundColorId() {
            return backgroundColorId;
        }
    }

    public static void setStatusBarColor(Activity activity, StatusBarColorType colorType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, colorType.getBackgroundColorId()));
        }
    }
}
