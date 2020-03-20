package com.match.honey.adapters.list;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.match.honey.R;
import com.match.honey.activity.ProfileDetailAct;
import com.match.honey.listDatas.TokboardDetailData;
import com.match.honey.utils.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TokboardDetailAdapter extends RecyclerView.Adapter<TokboardDetailAdapter.ViewHolder>{

    Activity act;
    ArrayList<TokboardDetailData> list;

    public TokboardDetailAdapter(Activity act, ArrayList<TokboardDetailData> list){
        this.act = act;
        this.list = list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        String url = list.get(i).getProfimg();

        if(i < 2) {
            holder.tv_new.setVisibility(View.VISIBLE);
        } else {
            holder.tv_new.setVisibility(View.GONE);
        }

        if (!StringUtil.isNull(list.get(i).getProfimg())) {
            if (list.get(i).getPimg_ck().equalsIgnoreCase("Y")) {
                holder.iv_profimg.setVisibility(View.VISIBLE);
                holder.iv_noprofimg.setVisibility(View.GONE);
                Glide.with(act)
                        .load(list.get(i).getProfimg())
                        .into(holder.iv_profimg);
            } else {
                holder.iv_profimg.setVisibility(View.GONE);
                holder.iv_noprofimg.setVisibility(View.VISIBLE);
                Glide.with(act)
                        .load(list.get(i).getCharacter())
                        .into(holder.iv_noprofimg);
            }
        } else {
            holder.iv_profimg.setVisibility(View.GONE);
            holder.iv_noprofimg.setVisibility(View.VISIBLE);

            Glide.with(act)
                    .load(list.get(i).getCharacter())
                    .into(holder.iv_noprofimg);
        }

        if(list.get(i).getGender().equalsIgnoreCase("male")) {
            holder.tv_nick.setTextColor(ContextCompat.getColor(act, R.color.color_mcolor));
        } else {
            holder.tv_nick.setTextColor(ContextCompat.getColor(act, R.color.color_wcolor));
        }

        holder.tv_nick.setText(list.get(i).getNickname());
        holder.tv_board_contents.setText(list.get(i).getContents());

        SimpleDateFormat predf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd hh:mm");
        try {
            Date date = predf.parse(list.get(i).getRegdate());
            holder.tv_regdate.setText(sdf.format(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        LinearLayout ll_item_area;
        ImageView iv_profimg, iv_noprofimg;
        TextView tv_nick,tv_board_contents,tv_regdate,tv_new;
        public ViewHolder(View v){
            super(v);
            iv_profimg = v.findViewById(R.id.iv_profimg);
            iv_noprofimg = v.findViewById(R.id.iv_noprofimg);
            tv_nick = v.findViewById(R.id.tv_nick);
            tv_new = v.findViewById(R.id.tv_new);
            tv_board_contents = v.findViewById(R.id.tv_board_contents);
            tv_regdate = v.findViewById(R.id.tv_regdate);
            ll_item_area = v.findViewById(R.id.ll_item_area);

            ll_item_area.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ll_item_area:
                    Intent pintent = new Intent(act, ProfileDetailAct.class);
                    pintent.putExtra("gender", list.get(getAdapterPosition()).getGender());
                    pintent.putExtra("midx",list.get(getAdapterPosition()).getUidx());
                    act.startActivity(pintent);
                    break;
            }
        }
    }
}
