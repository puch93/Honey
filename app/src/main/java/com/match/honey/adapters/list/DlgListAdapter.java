package com.match.honey.adapters.list;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.match.honey.R;
import com.match.honey.adapters.RecyclerViewItemClick;
import com.match.honey.listDatas.PopData;

import java.util.ArrayList;

public class DlgListAdapter extends RecyclerView.Adapter<DlgListAdapter.ViewHolder> {

    private static RecyclerViewItemClick myClickListener;
    private int selectedPosition = -1;
    Activity act;
    ArrayList<PopData> list;

    public DlgListAdapter(Activity act, ArrayList<PopData> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listpop, parent, false);
        DlgListAdapter.ViewHolder vh = new DlgListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        holder.tv_itemtext.setText(list.get(pos).getItem());

        holder.btn_rdo.setChecked(list.get(pos).isSelected());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout ll_itemarea;
        TextView tv_itemtext;
        RadioButton btn_rdo;

        public ViewHolder(View v) {
            super(v);

            ll_itemarea = v.findViewById(R.id.ll_pop_itemarea);
            tv_itemtext = v.findViewById(R.id.tv_itemtext);
            btn_rdo = v.findViewById(R.id.btn_rdo);

            ll_itemarea.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
            selectedPosition = getAdapterPosition();
            initListSelectState();
            list.get(getAdapterPosition()).setSelected(true);
            notifyDataSetChanged();
        }
    }

    private void initListSelectState() {
        for (PopData item : list) {
            item.setSelected(false);
        }
    }

    public void onItemClick(RecyclerViewItemClick mclick) {
        this.myClickListener = mclick;
    }
}
