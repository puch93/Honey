package com.match.honey.adapters.list;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.match.honey.R;
import com.match.honey.activity.ProfileDetailAct;
import com.match.honey.listDatas.BlockmemData;
import com.match.honey.utils.StringUtil;

import java.util.ArrayList;

public class BlocklistAdapter extends RecyclerView.Adapter<BlocklistAdapter.ViewHolder> {

    AppCompatActivity act;
    ArrayList<BlockmemData> list;

    public BlocklistAdapter(AppCompatActivity act, ArrayList<BlockmemData> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_block, parent, false);
        BlocklistAdapter.ViewHolder vh = new BlocklistAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        //닉네임
        holder.tv_nick.setText(list.get(i).getNickname());

        //성혼유형
        if (list.get(i).getMembertype().equalsIgnoreCase("marry")) {
            holder.tv_type.setText("결혼");
            holder.tv_type.setBackgroundResource(R.drawable.marriage_bg);
        } else if (list.get(i).getMembertype().equalsIgnoreCase("remarry")) {
            holder.tv_type.setText("재혼");
            holder.tv_type.setBackgroundResource(R.drawable.remarriage_bg);
        } else if (list.get(i).getMembertype().equalsIgnoreCase("friend")) {
            holder.tv_type.setText("재혼");
            holder.tv_type.setBackgroundResource(R.drawable.remarriage_bg);
        }  else {
            holder.tv_type.setText("-");
            holder.tv_type.setBackgroundResource(R.drawable.marriage_bg);
        }

        //시간
        holder.tv_login_time.setText(list.get(i).getDate());

        if (list.get(i).getGender().equalsIgnoreCase("male")) {
            holder.tv_gender.setText("남성");
            holder.tv_gender.setTextColor(ContextCompat.getColor(act, R.color.man_color));
        } else {
            holder.tv_gender.setText("여성");
            holder.tv_gender.setTextColor(ContextCompat.getColor(act, R.color.women_color));
        }

        //소개글
        if(!StringUtil.isNull(list.get(i).getIntroduce())) {
            holder.tv_content.setText(list.get(i).getIntroduce());
//            if(list.get(i).getPint_ck().equalsIgnoreCase("Y")) {
//                holder.tv_content.setText(list.get(i).getIntroduce());
//            } else {
//                holder.tv_content.setText(R.string.check_introduce);
//            }
        }

//        이름표시
        if (StringUtil.isNull(list.get(i).getLastnameYN())) {
            holder.tv_name.setText("");
        } else {
            if (list.get(i).getLastnameYN().equalsIgnoreCase("Y")) {
                holder.tv_name.setText(list.get(i).getFamilyname());
                holder.tv_hidename.setVisibility(View.VISIBLE);
            } else {
                holder.tv_name.setText(list.get(i).getFamilyname() + list.get(i).getName());
                holder.tv_hidename.setVisibility(View.GONE);
            }
        }

        if (!StringUtil.isNull(list.get(i).getProfimg())) {
            if (list.get(i).getPimg_ck().equalsIgnoreCase("Y")) {
                holder.iv_profimg.setVisibility(View.VISIBLE);
                holder.iv_noprofimg.setVisibility(View.GONE);
                Glide.with(act)
                        .load(list.get(i).getProfimg())
                        .centerCrop()
                        .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(15)))
                        .into(holder.iv_profimg);
            } else {
                holder.iv_profimg.setVisibility(View.GONE);
                holder.iv_noprofimg.setVisibility(View.VISIBLE);
                Glide.with(act)
                        .load(list.get(i).getCharacter())
                        .centerCrop()
                        .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(15)))
                        .into(holder.iv_noprofimg);
            }
        } else {
            holder.iv_profimg.setVisibility(View.GONE);
            holder.iv_noprofimg.setVisibility(View.VISIBLE);

            Glide.with(act)
                    .load(list.get(i).getCharacter())
                    .centerCrop()
                    .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(15)))
                    .into(holder.iv_noprofimg);

        }

        holder.cb_item.setChecked(list.get(i).isCheckState());

        holder.tv_age.setText(list.get(i).getByear() + "세");
        holder.tv_location.setText(list.get(i).getAddr1());
        holder.tv_location_detail.setText(list.get(i).getAddr2());
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout clayout_area;
        ImageView iv_profimg, iv_noprofimg;
        TextView tv_type, tv_nick, tv_name, tv_gender, tv_age, tv_location, tv_content, tv_hidename, tv_location_detail;
        TextView tv_login_time;
        CheckBox cb_item;

        public ViewHolder(View v) {
            super(v);
            clayout_area = v.findViewById(R.id.clayout_area);
            iv_profimg = v.findViewById(R.id.iv_profimg);
            iv_noprofimg = v.findViewById(R.id.iv_noprofimg);
            tv_type = v.findViewById(R.id.tv_membertype);
            tv_nick = v.findViewById(R.id.tv_membernick);
            tv_name = v.findViewById(R.id.tv_name);
            tv_gender = v.findViewById(R.id.tv_gender);
            tv_age = v.findViewById(R.id.tv_age);
            tv_location = v.findViewById(R.id.tv_location);
            tv_content = v.findViewById(R.id.tv_content);
            tv_hidename = v.findViewById(R.id.tv_hidename);
            tv_location_detail = v.findViewById(R.id.tv_location_detail);
            tv_login_time = v.findViewById(R.id.tv_login_time);

            cb_item = v.findViewById(R.id.cb_msgitem);
            cb_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    list.get(getAdapterPosition()).setCheckState(b);
                }
            });


            clayout_area.setOnClickListener(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iv_profimg.setClipToOutline(true);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.clayout_area:
                    Intent profIntent = new Intent(act, ProfileDetailAct.class);
                    profIntent.putExtra("gender", list.get(getAdapterPosition()).getGender());
                    profIntent.putExtra("midx", list.get(getAdapterPosition()).getMidx());
                    act.startActivity(profIntent);
                    break;
            }
        }
    }
}
