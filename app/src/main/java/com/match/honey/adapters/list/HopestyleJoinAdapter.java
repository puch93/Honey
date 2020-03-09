package com.match.honey.adapters.list;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.match.honey.R;
import com.match.honey.dialog.DlgHopestyle;
import com.match.honey.listDatas.ChatMessage;
import com.match.honey.listDatas.HopestyleData;
import com.match.honey.utils.DefaultValue;
import com.match.honey.utils.StringUtil;

import java.util.ArrayList;

public class HopestyleJoinAdapter extends RecyclerView.Adapter<HopestyleJoinAdapter.ViewHolder> {

    Activity act;
    ArrayList<HopestyleData> list;

    private static final int VIEW_ITEM = 0;
    private static final int VIEW_SELECT = 1;

    public HopestyleJoinAdapter(Activity act, ArrayList<HopestyleData> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = null;
        switch (viewType) {
            case VIEW_ITEM:
                view = inflater.inflate(R.layout.item_join_hoplestyle, parent, false);
                return new ViewHolder1(view);
            case VIEW_SELECT:
                view = inflater.inflate(R.layout.item_join_hoplestyle_select, parent, false);
                return new ViewHolder2(view);
            default:
                return null;
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == list.size()) {
            return VIEW_SELECT;
        } else if (position < list.size()) {
            return VIEW_ITEM;
        } else {
            return super.getItemViewType(position);
        }
    }

    public void setList(ArrayList<HopestyleData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        int viewType = getItemViewType(i);

        if (viewType == VIEW_ITEM) {
            Log.e("TEST_HOME", "type: VIEW_ITEM, item: " + list.get(i).getText());
            ViewHolder1 hoder1 = (ViewHolder1) holder;
            hoder1.tv_hopestyle.setText(list.get(i).getText());
        } else if(viewType == VIEW_SELECT) {
            Log.e("TEST_HOME", "type: VIEW_SELECT");
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size() + 1;
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

    public class ViewHolder2 extends ViewHolder implements View.OnClickListener {
        TextView tv_select;

        public ViewHolder2(View v) {
            super(v);
            tv_select = v.findViewById(R.id.tv_select);
            tv_select.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tv_select) {
                Intent hopestyle = new Intent(act, DlgHopestyle.class);
                String hstyle = "";
                for (int i = 0; i < list.size(); i++) {
                    if (i == 0) {
                        hstyle = list.get(i).getText();
                    } else {
                        hstyle += "|" + list.get(i).getText();
                    }
                }

                hopestyle.putExtra("select", hstyle);
                act.startActivityForResult(hopestyle, DefaultValue.HOPESTYLE);
            }
        }
    }
}
