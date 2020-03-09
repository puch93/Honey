package com.match.honey.adapters.list;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.match.honey.R;
import com.match.honey.listDatas.HopestyleData;

import java.util.ArrayList;

public class HopestyleAdapter extends RecyclerView.Adapter<HopestyleAdapter.ViewHolder> {

    Activity act;
    ArrayList<HopestyleData> list;

    public HopestyleAdapter(Activity act, ArrayList<HopestyleData> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hopestyle, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        if (list.get(i).isSelectState()) {
//            holder.tv_hopestyle.setTextColor(act.getResources().getColor(R.color.color_8345f3));
            holder.tv_hopestyle.setSelected(true);
        } else {
//            holder.tv_hopestyle.setTextColor(act.getResources().getColor(R.color.color_222222));
            holder.tv_hopestyle.setSelected(false);
        }
        holder.tv_hopestyle.setText(list.get(i).getText());
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_hopestyle;

        public ViewHolder(View v) {
            super(v);
            tv_hopestyle = v.findViewById(R.id.tv_hopestyle);
            tv_hopestyle.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_hopestyle:
                    if (getCheckNum() >= 7 && !list.get(getAdapterPosition()).isSelectState()) {
                        Toast.makeText(act, "7개만 선택가능 합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        list.get(getAdapterPosition()).setSelectState(!list.get(getAdapterPosition()).isSelectState());
                        tv_hopestyle.setSelected(list.get(getAdapterPosition()).isSelectState());
//                        notifyItemChanged(getAdapterPosition());
                    }
                    break;
            }
        }
    }

    private int getCheckNum() {
        int cnt = 0;
        for (HopestyleData item : list) {
            if (item.isSelectState()) {
                cnt++;
            }
        }
        return cnt;
    }

}
