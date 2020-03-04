package com.match.marryme.adapters.list;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.match.marryme.R;
import com.match.marryme.adapters.RecyclerViewItemClick;
import com.match.marryme.listDatas.PopData;
import com.match.marryme.utils.StringUtil;

import java.util.ArrayList;

public class DlgMultipleListAdapter extends RecyclerView.Adapter<DlgMultipleListAdapter.ViewHolder> {

    private static RecyclerViewItemClick myClickListener;
    private int selectedPosition = -1;
    Activity act;
    ArrayList<PopData> list;
    int limitnum = 0;
    boolean isLimit = false;
    public int count = 0;

    public DlgMultipleListAdapter(Activity act, ArrayList<PopData> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listpop, parent, false);
        DlgMultipleListAdapter.ViewHolder vh = new DlgMultipleListAdapter.ViewHolder(v);
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

            if (isLimit) {

                if (limitnum <= getCheckNum() && !list.get(getAdapterPosition()).isSelected()) {
                    Toast.makeText(act, limitnum + "개만 선택가능 합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    list.get(getAdapterPosition()).setSelected(!list.get(getAdapterPosition()).isSelected());
                    notifyDataSetChanged();
                }

            } else {
                if (list.get(getAdapterPosition()).getItem().equalsIgnoreCase("전체")) {
                    initListSelectState(false);
                    list.get(getAdapterPosition()).setSelected(true);
                    count = 0;

                } else {
                    list.get(getAdapterPosition()).setSelected(!list.get(getAdapterPosition()).isSelected());
                    if (list.get(0).getItem().equalsIgnoreCase("전체") && (getAdapterPosition() != 0)) {
                        list.get(0).setSelected(false);
                    }

                    if (list.get(getAdapterPosition()).isSelected()) {
                        count++;
                    } else {
                        count--;
                    }
                    Log.e(StringUtil.TAG, "count: " + count);
                    if (count <= 0) {
                        Log.e(StringUtil.TAG, "execute");
                        initListSelectState(false);
                        list.get(0).setSelected(true);
                    }
                }
                notifyDataSetChanged();
            }
        }
    }

    private int getCheckNum() {
        int cnt = 0;
        for (PopData item : list) {
            if (item.isSelected()) {
                cnt++;
            }
        }
        return cnt;
    }

    public void setLimit(boolean isLimit, int limitnum) {
        this.isLimit = isLimit;
        this.limitnum = limitnum;
    }

    private void initListSelectState(boolean state) {
        count = 0;

        for (PopData item : list) {
            item.setSelected(state);
        }
    }

    public void onItemClick(RecyclerViewItemClick mclick) {
        this.myClickListener = mclick;
    }
}
