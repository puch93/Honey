package com.match.marryme.adapters.list;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.match.marryme.R;
import com.match.marryme.activity.MoreimgActivity;
import com.match.marryme.listDatas.ImagesData;
import com.match.marryme.utils.Common;
import com.match.marryme.utils.StringUtil;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ImagelistOtherAdapter extends RecyclerView.Adapter<ImagelistOtherAdapter.ViewHolder> {

    Activity act;
    ArrayList<ImagesData> list;

    public ImagelistOtherAdapter(Activity act, ArrayList<ImagesData> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myprofile_images, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {
        holder.iv_image.setImageBitmap(null);
        holder.btn_remove.setVisibility(View.GONE);

        if (!StringUtil.isNull(list.get(i).getPiimg())) {
            if(!StringUtil.isNull(list.get(i).getPiimg_ck())) {
                if(list.get(i).getPiimg_ck().equalsIgnoreCase("Y")) {
                    Glide.with(act)
                            .load(list.get(i).getPiimg())
                            .into(holder.iv_image);

                    holder.iv_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent imgIntent = new Intent(act, MoreimgActivity.class);
                            imgIntent.putExtra("imgurl", list.get(i).getPiimg());
                            act.startActivity(imgIntent);
                        }
                    });
                } else {
                    Glide.with(act)
                            .load(list.get(i).getPiimg())
                            .apply(bitmapTransform(new BlurTransformation(10, 3)))
                            .into(holder.iv_image);

                    holder.iv_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Common.showToast(act, "검수중인 사진입니다");
                        }
                    });
                }
            }
        }
    }

    public void setList(ArrayList<ImagesData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        ImageView iv_image, btn_remove;

        public ViewHolder(View v) {
            super(v);
            iv_image = v.findViewById(R.id.iv_imgitem);
            btn_remove = v.findViewById(R.id.btn_remove);

//            iv_image.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.iv_imgitem:
//                    Intent imgIntent = new Intent(act, MoreimgActivity.class);
//                    imgIntent.putExtra("imgurl", list.get(getAdapterPosition()).getPiimg());
//                    act.startActivity(imgIntent);
//                    break;
//            }
//        }
    }
}
