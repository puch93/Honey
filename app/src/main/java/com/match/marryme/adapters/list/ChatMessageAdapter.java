package com.match.marryme.adapters.list;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.match.marryme.R;
import com.match.marryme.activity.ChatAct;
import com.match.marryme.activity.MoreimgActivity;
import com.match.marryme.listDatas.ChatMessage;
import com.match.marryme.utils.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {

    private static final int SYSTEM_VIEW = 0;
    private static final int SELF_VIEW = 1;
    private static final int OTHERS_VIEW = 2;

    private ArrayList<ChatMessage> chatMessages;
    private Activity act;

    public ChatMessageAdapter(Activity act, ArrayList<ChatMessage> chatMessages) {
        this.act = act;
        this.chatMessages = chatMessages;
    }

    public void addItems(ArrayList<ChatMessage> item) {
        if (chatMessages == null) {
            chatMessages = new ArrayList<>();
        }
        chatMessages.addAll(item);
        notifyDataSetChanged();
    }

    public void setItems(int pos, ChatMessage items) {
        chatMessages.set(pos, items);
        notifyDataSetChanged();
    }



    public void setList(ArrayList<ChatMessage> list) {
        this.chatMessages = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;
        switch (viewType) {
            case SYSTEM_VIEW:
                view = inflater.inflate(R.layout.item_msgbox_date, parent, false);
                return new SystemHolder(view);
            case SELF_VIEW:
                view = inflater.inflate(R.layout.item_mymsg, parent, false);
                return new MymsgHolder(view);
            case OTHERS_VIEW:
                view = inflater.inflate(R.layout.item_othermsg, parent, false);
                return new OtherHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        final ChatMessage item = chatMessages.get(position);

        if (viewType == SYSTEM_VIEW) {
            SystemHolder systemHolder = (SystemHolder) holder;


        } else if (viewType == SELF_VIEW) {
            MymsgHolder selfHolder = (MymsgHolder) holder;

            selfHolder.iv_sendimg.setImageBitmap(null);
            if (!StringUtil.isNull(item.getMsg())) {
//                if (item.getMsg().contains("http")) {
                if (StringUtil.isImage(item.getMsg())) {
                    selfHolder.iv_sendimg.setVisibility(View.VISIBLE);
                    selfHolder.tv_contents.setVisibility(View.GONE);
                    Glide.with(act)
                            .load(item.getMsg().replaceAll("\"", ""))
                            .into(selfHolder.iv_sendimg);
                } else if (item.getMsg().contains(StringUtil.IMOTICON)) {
                    selfHolder.iv_sendimg.setVisibility(View.VISIBLE);
                    selfHolder.tv_contents.setVisibility(View.GONE);
                    int rid = Integer.parseInt(item.getMsg().replace(StringUtil.IMOTICON, ""));

                    int resid = 0;
                    switch (rid) {
                        case 0:
                            resid = R.drawable.img01;
                            break;
                        case 1:
                            resid = R.drawable.img02;
                            break;
                        case 2:
                            resid = R.drawable.img03;
                            break;
                    }
                    selfHolder.iv_sendimg.setImageResource(resid);
                } else {
                    selfHolder.tv_contents.setVisibility(View.VISIBLE);
                    selfHolder.iv_sendimg.setVisibility(View.GONE);
                    selfHolder.tv_contents.setText(item.getMsg());
                }

                // 가입일
                SimpleDateFormat orgin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm");
                try {
                    Date old = orgin.parse(item.getRegdate());
                    selfHolder.tv_sendtime.setText(sdf.format(old));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(!StringUtil.isNull(item.getReadnum())) {
                    if(item.getReadnum().equalsIgnoreCase("1")) {
                        selfHolder.tv_read_count.setVisibility(View.VISIBLE);
                    } else {
                        selfHolder.tv_read_count.setVisibility(View.GONE);
                    }
                } else {
                    selfHolder.tv_read_count.setVisibility(View.GONE);
                }
            }

            //상대상대상대상대
        } else if (viewType == OTHERS_VIEW) {
            OtherHolder othersHolder = (OtherHolder) holder;

            othersHolder.tv_nick.setText(((ChatAct) act).nick);

            othersHolder.iv_sendimg.setImageBitmap(null);
            if (!StringUtil.isNull(item.getMsg())) {
                othersHolder.tv_sendtime.setText(item.getRegdate());

                if (StringUtil.isImage(item.getMsg())) {
                    othersHolder.tv_msg.setVisibility(View.GONE);
                    othersHolder.iv_sendimg.setVisibility(View.VISIBLE);
                    Glide.with(act)
                            .load(item.getMsg().replaceAll("\"", ""))
                            .into(othersHolder.iv_sendimg);
                } else if (item.getMsg().contains(StringUtil.IMOTICON)) {
                    othersHolder.tv_msg.setVisibility(View.GONE);
                    othersHolder.iv_sendimg.setVisibility(View.VISIBLE);
                    int rid = Integer.parseInt(item.getMsg().replace(StringUtil.IMOTICON, ""));

                    int resid = 0;
                    switch (rid) {
                        case 0:
                            resid = R.drawable.img_img01;
                            break;
                        case 1:
                            resid = R.drawable.img_img02;
                            break;
                        case 2:
                            resid = R.drawable.img_img03;
                            break;
                    }
                    othersHolder.iv_sendimg.setImageResource(resid);
                } else {
//                    othersHolder.tv_msg.setBackground(null);
                    othersHolder.tv_msg.setVisibility(View.VISIBLE);
//                    othersHolder.tv_msg.setBackgroundResource(R.drawable.getmarried_bg_chat_chat01_190902);
                    othersHolder.iv_sendimg.setVisibility(View.GONE);
                    othersHolder.tv_msg.setText(item.getMsg());
                }
            }


            //프사
            othersHolder.iv_profimg.setImageBitmap(null);

            if (!StringUtil.isNull(((ChatAct) act).pimg)) {
                if (((ChatAct) act).pimg_ck.equalsIgnoreCase("Y")) {
                    othersHolder.iv_profimg.setVisibility(View.VISIBLE);
                    othersHolder.iv_noprofimg.setVisibility(View.GONE);

                    Glide.with(act)
                            .load(((ChatAct) act).pimg)
                            .into(othersHolder.iv_profimg);
                } else {
                    othersHolder.iv_profimg.setVisibility(View.GONE);
                    othersHolder.iv_noprofimg.setVisibility(View.VISIBLE);

                    Glide.with(act)
                            .load(((ChatAct) act).character)
                            .into(othersHolder.iv_noprofimg);
                }
            } else {
                othersHolder.iv_profimg.setVisibility(View.GONE);
                othersHolder.iv_noprofimg.setVisibility(View.VISIBLE);

                Glide.with(act)
                        .load(((ChatAct) act).character)
                        .into(othersHolder.iv_noprofimg);
            }

            // 가입일
            SimpleDateFormat orgin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm");
            try {
                Date old = orgin.parse(item.getRegdate());
                othersHolder.tv_sendtime.setText(sdf.format(old));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public int getItemCount() {
        return chatMessages == null ? 0 : chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage currentItem = chatMessages.get(position);
        if (currentItem.getType().equals(StringUtil.CTYPESYS)) {
            return SYSTEM_VIEW;
        } else if (currentItem.getType().equals(StringUtil.CTYPEMY)) {
            return SELF_VIEW;
        } else if (currentItem.getType().equals(StringUtil.CTYPEOTHER)) {
            return OTHERS_VIEW;
        }
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    public class SystemHolder extends ViewHolder {
        TextView tv_itemdate;

        public SystemHolder(View v) {
            super(v);
            tv_itemdate = v.findViewById(R.id.tv_itemdate);
        }
    }

    public class MymsgHolder extends ViewHolder implements View.OnClickListener {
        TextView tv_sendtime, tv_contents, tv_read_count;
        ImageView iv_sendimg;

        public MymsgHolder(View v) {
            super(v);

            tv_contents = v.findViewById(R.id.tv_contents);
            tv_sendtime = v.findViewById(R.id.tv_sendtime);
            iv_sendimg = v.findViewById(R.id.iv_sendimg);
            tv_read_count = v.findViewById(R.id.tv_read_count);

            iv_sendimg.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_sendimg:
                    if (!chatMessages.get(getAdapterPosition()).getMsg().contains(StringUtil.IMOTICON)) {
                        Intent imgIntent = new Intent(act, MoreimgActivity.class);
                        imgIntent.putExtra("imgurl", chatMessages.get(getAdapterPosition()).getMsg());
                        act.startActivity(imgIntent);
                    }
                    break;
            }
        }
    }

    public class OtherHolder extends ViewHolder implements View.OnClickListener {
        TextView tv_nick, tv_msg, tv_sendtime;
        ImageView iv_profimg, iv_noprofimg, iv_sendimg;

        public OtherHolder(View v) {
            super(v);

            tv_nick = v.findViewById(R.id.tv_nick);
            tv_msg = v.findViewById(R.id.tv_msg);
            tv_sendtime = v.findViewById(R.id.tv_sendtime);
            iv_profimg = v.findViewById(R.id.iv_profimg);
            iv_noprofimg = v.findViewById(R.id.iv_noprofimg);
            iv_sendimg = v.findViewById(R.id.iv_sendimg);

            iv_profimg.setOnClickListener(this);
            iv_sendimg.setOnClickListener(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iv_profimg.setClipToOutline(true);
            }

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_profimg:
                    if (StringUtil.isNull(((ChatAct) act).pimg)) {
                        Toast.makeText(act, "프로필 사진이 등록되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent pimgIntent = new Intent(act, MoreimgActivity.class);
                        pimgIntent.putExtra("imgurl", ((ChatAct) act).pimg);
                        act.startActivity(pimgIntent);
                    }
                    break;
                case R.id.iv_sendimg:
                    if (!chatMessages.get(getAdapterPosition()).getMsg().contains(StringUtil.IMOTICON)) {
                        Intent imgIntent = new Intent(act, MoreimgActivity.class);
                        imgIntent.putExtra("imgurl", chatMessages.get(getAdapterPosition()).getMsg());
                        act.startActivity(imgIntent);
                    }
                    break;
            }
        }
    }


}
