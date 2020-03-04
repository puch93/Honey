package com.match.marryme.adapters.list;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.match.marryme.R;
import com.match.marryme.activity.ChatAct;
import com.match.marryme.sharedPref.UserPref;
import com.match.marryme.utils.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class InterimgAdapter extends RecyclerView.Adapter<InterimgAdapter.ViewHolder>{

    Activity act;

    ArrayList<Integer> list;

    public InterimgAdapter(Activity act,ArrayList<Integer> list){

        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interimg, parent, false);
        InterimgAdapter.ViewHolder vh = new InterimgAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        int resid = 0;
        switch (list.get(i)){
            case 0:
                resid = R.drawable.img01;
                break;
            case 1:
                resid = R.drawable.img02;
                break;
            case 2:
                resid = R.drawable.img03;
                break;
        }

        holder.ivImg.setImageBitmap(null);
        if (resid != 0) {
            Glide.with(act)
                    .load(resid)
                    .into(holder.ivImg);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView ivImg;
        public ViewHolder(View v){
            super(v);

            ivImg = v.findViewById(R.id.iv_interimg);

            ivImg.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_interimg:
                    Log.i(StringUtil.TAG,"inter img send");
                    ((ChatAct)act).interestUsableCheck(list.get(getAdapterPosition()));
//                    ((ChatAct)act).checkInterest(list.get(getAdapterPosition()));
                    break;
            }
        }
    }


    public File saveBitmap(Bitmap bitmap,String fname){
        String fPath = Environment.getExternalStorageDirectory() + "/MARRYME/";
        File file = new File(fPath);

        if (!file.exists()){
            file.mkdirs();
        }

        File fileCacheItem = new File(fPath,fname);
        OutputStream out = null;

        try{
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);

            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                out.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        return fileCacheItem;
    }

}
