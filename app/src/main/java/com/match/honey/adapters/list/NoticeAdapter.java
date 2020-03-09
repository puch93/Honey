package com.match.honey.adapters.list;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.match.honey.R;
import com.match.honey.activity.NoticeDetailAct;
import com.match.honey.listDatas.NoticeData;

import java.util.ArrayList;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {

    Activity act;
    ArrayList<NoticeData> list;

    public NoticeAdapter(Activity act, ArrayList<NoticeData> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {
        holder.tv_title.setText(list.get(i).getTitle());
        holder.tv_regdate.setText(list.get(i).getRegdate());
        holder.tv_viewcount.setText(list.get(i).getHit());
        holder.tv_order.setText("0" + (i + 1));

        holder.ll_item_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TEST_HOME", "onClick");
                Intent intent = new Intent(act, NoticeDetailAct.class);
                intent.putExtra("idx", list.get(i).getIdx());
                intent.putExtra("title", list.get(i).getTitle());
                intent.putExtra("contents", list.get(i).getContents());
                intent.putExtra("regdate", list.get(i).getRegdate());
                intent.putExtra("viewcount", list.get(i).getHit());

                act.startActivity(intent);
            }
        });
    }

    public void setList(ArrayList<NoticeData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title, tv_regdate, tv_viewcount, tv_order;
        LinearLayout ll_item_area;

        public ViewHolder(View v) {
            super(v);
            tv_title = v.findViewById(R.id.tv_notice_title);
            tv_regdate = v.findViewById(R.id.tv_regdate);
            tv_viewcount = v.findViewById(R.id.tv_viewcount);
            tv_order = v.findViewById(R.id.tv_order);

            ll_item_area = v.findViewById(R.id.ll_item_area);
        }
    }
}
