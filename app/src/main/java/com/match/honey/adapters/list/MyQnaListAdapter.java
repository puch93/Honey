package com.match.honey.adapters.list;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.match.honey.R;
import com.match.honey.activity.QnaDetailAct;
import com.match.honey.activity.QnaReportDetailAct;
import com.match.honey.listDatas.MyQnaData;
import com.match.honey.utils.StringUtil;

import java.util.ArrayList;

public class MyQnaListAdapter extends RecyclerView.Adapter<MyQnaListAdapter.ViewHolder> {

    Activity act;
    ArrayList<MyQnaData> list;
    String type;

    public MyQnaListAdapter(Activity act, ArrayList<MyQnaData> list, String type) {
        this.act = act;
        this.list = list;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myqna, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {

        if (list.get(i).isReplystate()) {
            holder.tv_qnastate.setText("답변완료");
        } else {
            holder.tv_qnastate.setText("답변대기");
        }

        holder.tv_regdate.setText(list.get(i).getRegdate());
        holder.tv_qnatype.setText(list.get(i).getQnatype());
        holder.tv_qnacontents.setText(list.get(i).getQtext());

        holder.ll_all_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                if(type.equalsIgnoreCase("qna")) {
                    intent = new Intent(act, QnaDetailAct.class);
                } else {
                    intent = new Intent(act, QnaReportDetailAct.class);
                }
                intent.putExtra("type", list.get(i).getQnatype());
                intent.putExtra("state", list.get(i).isReplystate());
                intent.putExtra("regdate", list.get(i).getRegdate());
                intent.putExtra("question", list.get(i).getQtext());
                intent.putExtra("answer", list.get(i).getAnswer());
                act.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_all_area;
        TextView tv_qnastate, tv_regdate, tv_qnatype, tv_qnacontents;

        public ViewHolder(View v) {
            super(v);
            ll_all_area = v.findViewById(R.id.ll_all_area);

            tv_qnastate = v.findViewById(R.id.tv_qnastate);
            tv_regdate = v.findViewById(R.id.tv_regdate);
            tv_qnatype = v.findViewById(R.id.tv_qnatype);
            tv_qnacontents = v.findViewById(R.id.tv_qnacontents);

        }
    }

}
