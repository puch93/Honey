package com.match.honey.adapters.list;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.match.honey.R;
import com.match.honey.listDatas.FriendBlockData;

import java.util.ArrayList;

public class FriendBlockAdapter extends RecyclerView.Adapter<FriendBlockAdapter.ViewHolder> {

    Activity act;
    ArrayList<FriendBlockData> list;

    public FriendBlockAdapter(Activity act, ArrayList<FriendBlockData> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_block, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {
        holder.cb_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                list.get(i).setChecked(isChecked);
            }
        });
        String address = PhoneNumberUtils.formatNumber(list.get(i).getAddress());
        holder.cb_address.setText(address);
        holder.cb_address.setChecked(list.get(i).isChecked());
    }

    public void setList(ArrayList<FriendBlockData> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cb_address;

        public ViewHolder(View v) {
            super(v);
            cb_address = v.findViewById(R.id.cb_address);
        }
    }

}
