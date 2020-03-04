package com.match.marryme.adapters.list;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.match.marryme.R;
import com.match.marryme.activity.LastnameSearchAct;
import com.match.marryme.adapters.RecyclerViewItemClick;
import com.match.marryme.listDatas.LastnameData;

import java.util.ArrayList;

public class LastnameListAdapter extends RecyclerView.Adapter<LastnameListAdapter.ViewHolder> {

    Activity act;
    ArrayList<LastnameData> list;

    public LastnameListAdapter(Activity act, ArrayList<LastnameData> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lastnames, parent, false);
        LastnameListAdapter.ViewHolder vh = new LastnameListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.tv_lastname.setText(list.get(i).getLastnameText());

        if (i % 2 == 0) {
            holder.iv_divider.setVisibility(View.VISIBLE);
        } else {
            holder.iv_divider.setVisibility(View.GONE);
        }

        holder.cb_lastname.setChecked(list.get(i).isCheckstate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        LinearLayout ll_lastnamearea;
        TextView tv_lastname;
        ImageView iv_divider;
        CheckBox cb_lastname;

        public ViewHolder(View v) {
            super(v);
            ll_lastnamearea = v.findViewById(R.id.ll_lastname_area);
            tv_lastname = v.findViewById(R.id.tv_lastname);
            iv_divider = v.findViewById(R.id.iv_hdivider);
            cb_lastname = v.findViewById(R.id.cb_lastname);

            ll_lastnamearea.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_lastname_area:
                    if (((LastnameSearchAct) act).selectlastname.size() < 5 || list.get(getAdapterPosition()).isCheckstate()) {
                        list.get(getAdapterPosition()).setCheckstate(!list.get(getAdapterPosition()).isCheckstate());

                        if (list.get(getAdapterPosition()).isCheckstate()) {
                            ((LastnameSearchAct) act).addSelectList(list.get(getAdapterPosition()));
                        } else {
                            ((LastnameSearchAct) act).removeSelectList(list.get(getAdapterPosition()));
                        }

                        notifyItemChanged(getAdapterPosition());
                    }else{
                        Toast.makeText(act,"성씨선택 5개까지 가능",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

}
