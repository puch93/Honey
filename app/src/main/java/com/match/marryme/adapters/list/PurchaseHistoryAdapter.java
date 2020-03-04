package com.match.marryme.adapters.list;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.match.marryme.R;
import com.match.marryme.listDatas.PurchaseData;
import com.match.marryme.utils.StringUtil;

import java.util.ArrayList;

public class PurchaseHistoryAdapter extends RecyclerView.Adapter<PurchaseHistoryAdapter.ViewHolder>{

    Activity act;
    ArrayList<PurchaseData> list;

    public PurchaseHistoryAdapter(Activity act, ArrayList<PurchaseData> list){
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchase_history,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.tv_date.setText(list.get(i).getDate());
        if(list.get(i).getType().equalsIgnoreCase("profile")) {
            holder.tv_type.setText("열람권 " + list.get(i).getName());
        } else {
            holder.tv_type.setText("메세지 " + list.get(i).getName());
        }
        holder.tv_price.setText(StringUtil.setNumComma(Integer.parseInt(list.get(i).getPrice())));
    }

    public void setList(ArrayList<PurchaseData> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_date,tv_type,tv_price;
        public ViewHolder(View v){
            super(v);
            tv_date = v.findViewById(R.id.tv_date);
            tv_type = v.findViewById(R.id.tv_type);
            tv_price = v.findViewById(R.id.tv_price);
        }
    }
}
