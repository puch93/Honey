package com.match.honey.customWidgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatDialog;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.match.honey.R;

public class CustomLoading {

    AppCompatDialog progressDialog;
    Context act;

    public CustomLoading(Context act){
        this.act = act;
    }


    public void progressON(String msg){

        if (act == null || ((Activity)act).isFinishing()){
            return;
        }

        if (progressDialog != null && progressDialog.isShowing()){
        }else{
            progressDialog = new AppCompatDialog(act);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dlg_loading);
            progressDialog.show();
        }

        ImageView aniImg = (ImageView) progressDialog.findViewById(R.id.iv_loading);
//        AnimationUtils.loadAnimation(act,R.anim.loading);
        aniImg.setAnimation(AnimationUtils.loadAnimation(act,R.anim.blink_anim));

        // 애니메이션? gif?

//        Animation anim = AnimationUtils.loadAnimation(act,R.anim.blink_anim);
//        TextView message = (TextView)progressDialog.findViewById(R.id.tv_loading);
//        if (!StringUtil.isNull(msg)){
////            message.setAnimation(anim);
//            message.setText(msg);
//        }
    }

//    public void progressSET(String msg){
//        if (progressDialog == null || !progressDialog.isShowing()){
//            return;
//        }
//
//        TextView message = (TextView)progressDialog.findViewById(R.id.tv_loading);
//        if (!StringUtil.isNull(msg)){
//            message.setText(msg);
//        }
//
//    }

    public void progressOFF(){
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}
