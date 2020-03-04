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
import com.match.marryme.activity.TokboardDetailAct;
import com.match.marryme.listDatas.TokboardData;

import java.util.ArrayList;

public class TokboardAdapter extends RecyclerView.Adapter<TokboardAdapter.ViewHolder>{

    Activity act;
    ArrayList<TokboardData> list;

    public TokboardAdapter(Activity act,ArrayList<TokboardData> list){
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_boardmain,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        String url = list.get(i).getBoardimg();
        Glide.with(act)
                .load(url)
                .into(holder.iv_boardimg);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView iv_boardimg;
        public ViewHolder(View v){
            super(v);
            iv_boardimg = v.findViewById(R.id.iv_boarditem);

            iv_boardimg.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_boarditem:
                    Intent boarddetail = new Intent(act, TokboardDetailAct.class);
                    boarddetail.putExtra("idx",list.get(getAdapterPosition()).getIdx());
                    boarddetail.putExtra("title",list.get(getAdapterPosition()).getDetailtitle());
                    boarddetail.putExtra("img",list.get(getAdapterPosition()).getSubimg());
                    boarddetail.putExtra("hit",list.get(getAdapterPosition()).getHit());
                    boarddetail.putExtra("hint",list.get(getAdapterPosition()).getSubject());
                    boarddetail.putExtra("commentCount",list.get(getAdapterPosition()).getComment_count());

                    act.startActivity(boarddetail);

                    break;
            }
        }
    }
}
