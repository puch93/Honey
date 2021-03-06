package com.match.honey.adapters.list;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.match.honey.R;
import com.match.honey.activity.MoreimgActivity;
import com.match.honey.listDatas.ImagesData;
import com.match.honey.utils.Common;
import com.match.honey.utils.StringUtil;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ImagelistOtherAdapter extends RecyclerView.Adapter<ImagelistOtherAdapter.ViewHolder> {

    AppCompatActivity act;
    ArrayList<ImagesData> list;

    public ImagelistOtherAdapter(AppCompatActivity act, ArrayList<ImagesData> list) {
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
