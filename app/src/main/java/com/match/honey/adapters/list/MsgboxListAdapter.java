package com.match.honey.adapters.list;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.match.honey.R;
import com.match.honey.activity.ChatAct;
import com.match.honey.listDatas.MsgboxData;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MsgboxListAdapter extends RecyclerView.Adapter<MsgboxListAdapter.ViewHolder> {

    private final int DATE_VIEW = 0;
    private final int MSGLIST_VIEW = 1;

    Activity act;
    ArrayList<MsgboxData> list;

    public MsgboxListAdapter(Activity act, ArrayList<MsgboxData> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;
        switch (viewType) {
            case DATE_VIEW:
                view = inflater.inflate(R.layout.item_msgbox_date, parent, false);
                return new DateHolder(view);
            case MSGLIST_VIEW:
                view = inflater.inflate(R.layout.item_msgbox2, parent, false);
                return new ItemHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        int viewType = getItemViewType(i);

        if (viewType == DATE_VIEW){
            DateHolder dateHolder = (DateHolder)holder;
            dateHolder.tv_date.setText(list.get(i).getCreated_at());

        } else if (viewType == MSGLIST_VIEW){
            ItemHolder itemHolder = (ItemHolder)holder;

            if (list.get(i).getFriend().getCoupleType().equalsIgnoreCase("marry")) {
                itemHolder.tv_type.setText("결혼");
                itemHolder.tv_type.setBackgroundResource(R.drawable.marriage_bg);
            } else if (list.get(i).getFriend().getCoupleType().equalsIgnoreCase("remarry")) {
                itemHolder.tv_type.setText("재혼");
                itemHolder.tv_type.setBackgroundResource(R.drawable.remarriage_bg);
            } else if (list.get(i).getFriend().getCoupleType().equalsIgnoreCase("friend")) {
                itemHolder.tv_type.setText("재혼");
                itemHolder.tv_type.setBackgroundResource(R.drawable.remarriage_bg);
            } else {
                itemHolder.tv_type.setText("-");
                itemHolder.tv_type.setBackgroundResource(R.drawable.marriage_bg);
            }

            if (list.get(i).getFriend().getGender().equalsIgnoreCase("male")) {
                itemHolder.tv_gender.setText("남성");
                itemHolder.tv_gender.setTextColor(ContextCompat.getColor(act, R.color.man_color));
            } else {
                itemHolder.tv_gender.setText("여성");
                itemHolder.tv_gender.setTextColor(ContextCompat.getColor(act, R.color.women_color));
            }

            if (!StringUtil.isNull(list.get(i).getFriend().getNick())) {
                itemHolder.tv_nick.setText(list.get(i).getFriend().getNick());
            }

            if (StringUtil.isNull(list.get(i).getFriend().getLastNameYN())) {
                itemHolder.tv_name.setText("");
            } else {
                if (list.get(i).getFriend().getLastNameYN().equalsIgnoreCase("Y")) {
                    itemHolder.tv_name.setText(list.get(i).getFriend().getFamilyName());
                    itemHolder.tv_hidename.setVisibility(View.VISIBLE);
                } else {
                    itemHolder.tv_name.setText(list.get(i).getFriend().getFamilyName() + list.get(i).getFriend().getName());
                    itemHolder.tv_hidename.setVisibility(View.GONE);
                }
            }

            if (!StringUtil.isNull(list.get(i).getFriend().getPimg())) {
                if (list.get(i).getFriend().getPimg_ck().equalsIgnoreCase("Y")) {
                    itemHolder.iv_profimg.setVisibility(View.VISIBLE);
                    itemHolder.iv_noprofimg.setVisibility(View.GONE);
                    Glide.with(act)
                            .load(list.get(i).getFriend().getPimg())
                            .centerCrop()
                            .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(15)))
                            .into(itemHolder.iv_profimg);
                } else {
                    itemHolder.iv_profimg.setVisibility(View.GONE);
                    itemHolder.iv_noprofimg.setVisibility(View.VISIBLE);
                    Glide.with(act)
                            .load(list.get(i).getFriend().getCharacter())
                            .centerCrop()
                            .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(15)))
                            .into(itemHolder.iv_noprofimg);
                }
            } else {
                itemHolder.iv_profimg.setVisibility(View.GONE);
                itemHolder.iv_noprofimg.setVisibility(View.VISIBLE);

                Glide.with(act)
                        .load(list.get(i).getFriend().getCharacter())
                        .centerCrop()
                        .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(15)))
                        .into(itemHolder.iv_noprofimg);

            }


            if (list.get(i).getMsg().contains("http")){
                itemHolder.tv_content.setText("사진");
            }else if (list.get(i).getMsg().contains(StringUtil.IMOTICON)){
                itemHolder.tv_content.setText("\'관심있어요\'");
            }else {
                itemHolder.tv_content.setText(list.get(i).getMsg());
            }

            if (!StringUtil.isNull(list.get(i).getCreated_at())) {
                itemHolder.tv_login_time.setText(list.get(i).getCreated_at().split(" ")[1]);
            }

            itemHolder.tv_chatcount.setText(list.get(i).getMsgcnt());
            itemHolder.cb_item.setChecked(list.get(i).isCheckState());
            itemHolder.tv_age.setText(list.get(i).getFriend().getAge() + "세");
            itemHolder.tv_location.setText(list.get(i).getFriend().getAddr1());
            itemHolder.tv_location_detail.setText(list.get(i).getFriend().getAddr2());

            if(!StringUtil.isNull(list.get(i).getFriend().getPiimgcnt())) {
                if(list.get(i).getFriend().getPiimgcnt().equalsIgnoreCase("0")) {
                    itemHolder.tv_intro_count.setVisibility(View.GONE);
                } else {
                    itemHolder.tv_intro_count.setVisibility(View.VISIBLE);
                    itemHolder.tv_intro_count.setText(list.get(i).getFriend().getPiimgcnt());
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        MsgboxData item = list.get(position);
        if (item.getItemtype().equalsIgnoreCase(StringUtil.TYPEDATE)){
            return DATE_VIEW;
        }else if (item.getItemtype().equalsIgnoreCase(StringUtil.TYPEITEM)){
            return MSGLIST_VIEW;
        }
        return super.getItemViewType(position);
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

    public class DateHolder extends ViewHolder{
        TextView tv_date;
        public DateHolder(View v){
            super(v);
            tv_date = v.findViewById(R.id.tv_itemdate);
        }
    }

    public class ItemHolder extends ViewHolder implements View.OnClickListener{
        LinearLayout clayout_area;
        ImageView iv_profimg, iv_noprofimg;
        TextView tv_type, tv_nick, tv_name, tv_gender, tv_age, tv_location, tv_content, tv_hidename, tv_location_detail;
        TextView tv_login_time, tv_chatcount, tv_intro_count;
        CheckBox cb_item;

        public ItemHolder(View v){
            super(v);
            clayout_area = v.findViewById(R.id.clayout_area);
            iv_profimg = v.findViewById(R.id.iv_profimg);
            iv_noprofimg = v.findViewById(R.id.iv_noprofimg);
            tv_type = v.findViewById(R.id.tv_membertype);
            tv_nick = v.findViewById(R.id.tv_membernick);
            tv_name = v.findViewById(R.id.tv_name);
            tv_gender = v.findViewById(R.id.tv_gender);
            tv_age = v.findViewById(R.id.tv_age);
            tv_location = v.findViewById(R.id.tv_location);
            tv_content = v.findViewById(R.id.tv_content);
            tv_hidename = v.findViewById(R.id.tv_hidename);
            tv_location_detail = v.findViewById(R.id.tv_location_detail);
            tv_login_time = v.findViewById(R.id.tv_login_time);
            tv_chatcount = v.findViewById(R.id.tv_chatcount);
            tv_intro_count = v.findViewById(R.id.tv_intro_count);

            cb_item = v.findViewById(R.id.cb_msgitem);
            cb_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    list.get(getAdapterPosition()).setCheckState(b);
                }
            });


            clayout_area.setOnClickListener(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iv_profimg.setClipToOutline(true);
            }

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.clayout_area:
//                    reqChat();
                    Intent chatIntent = new Intent(act, ChatAct.class);

                    chatIntent.putExtra("room_idx", list.get(getAdapterPosition()).getRoom_idx());
                    chatIntent.putExtra("midx", list.get(getAdapterPosition()).getFriend().getIdx());
                    chatIntent.putExtra("pimg", list.get(getAdapterPosition()).getFriend().getPimg());
                    chatIntent.putExtra("pimg_ck", list.get(getAdapterPosition()).getFriend().getPimg_ck());
                    chatIntent.putExtra("character", list.get(getAdapterPosition()).getFriend().getCharacter());
                    chatIntent.putExtra("gender", list.get(getAdapterPosition()).getFriend().getGender());
                    chatIntent.putExtra("nick", list.get(getAdapterPosition()).getFriend().getNick());
                    chatIntent.putExtra("loc", list.get(getAdapterPosition()).getFriend().getAddr1());
                    chatIntent.putExtra("age", StringUtil.calcAge(list.get(getAdapterPosition()).getFriend().getByear()));

                    act.startActivity(chatIntent);
                    break;
            }
        }

        private void reqChat() {
            ReqBasic reqChat = new ReqBasic(act, NetUrls.REQCHAT) {
                @Override
                public void onAfter(int resultCode, HttpResult resultData) {

                    if (resultData.getResult() != null) {
                        try {
                            JSONObject jo = new JSONObject(resultData.getResult());

                            if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {

                                JSONObject obj = new JSONObject(jo.getString("data"));

                                obj.getString("room_idx");
                                Intent chatIntent = new Intent(act, ChatAct.class);
                                chatIntent.putExtra("room_idx", list.get(getAdapterPosition()).getRoom_idx());
                                chatIntent.putExtra("midx", list.get(getAdapterPosition()).getFriend().getIdx());
                                chatIntent.putExtra("pimg", list.get(getAdapterPosition()).getFriend().getPimg());
                                chatIntent.putExtra("pimg_ck", list.get(getAdapterPosition()).getFriend().getPimg_ck());
                                chatIntent.putExtra("character", list.get(getAdapterPosition()).getFriend().getCharacter());
                                chatIntent.putExtra("gender", list.get(getAdapterPosition()).getFriend().getGender());
                                chatIntent.putExtra("nick", list.get(getAdapterPosition()).getFriend().getNick());
                                chatIntent.putExtra("loc", list.get(getAdapterPosition()).getFriend().getAddr1());
                                chatIntent.putExtra("age", StringUtil.calcAge(list.get(getAdapterPosition()).getFriend().getByear()));

                                act.startActivity(chatIntent);

                            } else {
                                Toast.makeText(act, jo.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(act, act.getResources().getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(act, act.getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                }
            };
            reqChat.setTag("Chat Create Room");
            reqChat.addParams("type", "common");
            reqChat.addParams("uidx", UserPref.getUidx(act));
            reqChat.addParams("tidx", list.get(getAdapterPosition()).getFriend().getIdx());
            reqChat.execute(true, true);
        }
    }


    public void setAllCheck(boolean state){
        for (MsgboxData item : list){
            item.setCheckState(state);
            notifyDataSetChanged();
        }
    }
}
