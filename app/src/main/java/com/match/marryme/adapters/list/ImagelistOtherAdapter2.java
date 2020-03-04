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
import com.match.marryme.sharedPref.SystemPref;
import com.match.marryme.utils.Common;
import com.match.marryme.utils.StringUtil;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ImagelistOtherAdapter2 extends RecyclerView.Adapter<ImagelistOtherAdapter2.ViewHolder> {

    Activity act;
    ArrayList<ImagesData> list;
    boolean isRead = false;

    public ImagelistOtherAdapter2(Activity act, ArrayList<ImagesData> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myprofile_images2, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.iv_image.setImageBitmap(null);

        if (SystemPref.getDeviceWidth(act) == 0) {
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) holder.iv_image.getLayoutParams();
            params.height = 300;
            holder.iv_image.setLayoutParams(params);
        } else {
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) holder.iv_image.getLayoutParams();
            params.height = SystemPref.getDeviceWidth(act) / 3;
            holder.iv_image.setLayoutParams(params);
        }

        if (!StringUtil.isNull(list.get(position).getPiimg())) {
            if (isRead) {
                if (list.get(position).getPiimg_ck().equalsIgnoreCase("Y")) {
                    Glide.with(act)
                            .load(list.get(position).getPiimg())
                            .into(holder.iv_image);

                    holder.iv_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent imgIntent = new Intent(act, MoreimgActivity.class);
                            imgIntent.putExtra("imgurl", list.get(position).getPiimg());
                            act.startActivity(imgIntent);
                        }
                    });

                } else {
                    Glide.with(act)
                            .load(list.get(position).getPiimg())
                            .apply(bitmapTransform(new BlurTransformation(10, 3)))
                            .into(holder.iv_image);

                    holder.iv_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Common.showToast(act, "검수중인 사진입니다");
                        }
                    });
                }
            } else {
                Glide.with(act)
                        .load(list.get(position).getPiimg())
                        .apply(bitmapTransform(new BlurTransformation(10, 3)))
                        .into(holder.iv_image);

                holder.iv_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Common.showToast(act, "열람권 구매 후 이용해주시기 바랍니다");
                    }
                });
            }
        }
    }

    public void setList(ArrayList<ImagesData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setPreadState(boolean isRead) {
        this.isRead = isRead;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_image;

        public ViewHolder(View v) {
            super(v);
            iv_image = v.findViewById(R.id.iv_imgitem);

        }
    }
}
