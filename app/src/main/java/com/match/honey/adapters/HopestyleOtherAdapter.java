package com.match.honey.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.match.honey.R;
import com.match.honey.listDatas.HopestyleData;

import java.util.ArrayList;

public class HopestyleOtherAdapter extends RecyclerView.Adapter<HopestyleOtherAdapter.ViewHolder> {

    AppCompatActivity act;
    ArrayList<HopestyleData> list;

    public HopestyleOtherAdapter(AppCompatActivity act, ArrayList<HopestyleData> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.item_join_hoplestyle, parent, false);

        return new ViewHolder1(view);
    }

    public void setList(ArrayList<HopestyleData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        ViewHolder1 hoder1 = (ViewHolder1) holder;
        hoder1.tv_hopestyle.setText(list.get(i).getText());

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    public class ViewHolder1 extends ViewHolder {
        TextView tv_hopestyle;

        public ViewHolder1(View v) {
            super(v);
            tv_hopestyle = v.findViewById(R.id.tv_hopestyle);
        }
    }
}
