package com.match.marryme.adapters.list;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.match.marryme.R;
import com.match.marryme.activity.ReviewDetailAct;
import com.match.marryme.listDatas.ThanksData;
import com.match.marryme.utils.StringUtil;

import java.util.ArrayList;

public class ThanksListAdapter extends RecyclerView.Adapter<ThanksListAdapter.ViewHolder> {

    Activity act;
    ArrayList<ThanksData> list;

    public ThanksListAdapter(Activity act, ArrayList<ThanksData> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thanks, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {
        if (i < 2) {
            holder.tv_new.setVisibility(View.VISIBLE);
        } else {
            holder.tv_new.setVisibility(View.GONE);
        }

        //조회수
        holder.tv_viewhit.setText(list.get(i).getHit());
        //등록일자
        holder.tv_regdate.setText(list.get(i).getRegdate());
        //내용
        holder.tv_contents.setText(list.get(i).getContents());

        //프사
        holder.iv_profimg.setImageBitmap(null);
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

        //상세내용가기
        holder.ll_all_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(act, ReviewDetailAct.class);
                intent.putExtra("review", list.get(i));
                act.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll_all_area;
        TextView tv_new, tv_viewhit, tv_contents, tv_regdate;
        ImageView iv_profimg, iv_noprofimg;


        public ViewHolder(View v) {
            super(v);
            ll_all_area = v.findViewById(R.id.ll_all_area);

            tv_new = v.findViewById(R.id.tv_new);
            tv_viewhit = v.findViewById(R.id.tv_viewhit);
            tv_contents = v.findViewById(R.id.tv_contents);
            tv_regdate = v.findViewById(R.id.tv_regdate);

            iv_profimg = v.findViewById(R.id.iv_profimg);
            iv_noprofimg = v.findViewById(R.id.iv_noprofimg);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iv_profimg.setClipToOutline(true);
            }

        }
    }
}
