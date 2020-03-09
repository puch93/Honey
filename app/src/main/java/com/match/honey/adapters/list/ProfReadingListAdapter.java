package com.match.honey.adapters.list;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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

import com.bumptech.glide.Glide;
import com.match.honey.R;
import com.match.honey.activity.ChatAct;
import com.match.honey.activity.ProfileDetailAct;
import com.match.honey.listDatas.ProfreadData;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class ProfReadingListAdapter extends RecyclerView.Adapter<ProfReadingListAdapter.ViewHolder> {

    private final int DATE_VIEW = 0;
    private final int READLIST_VIEW = 1;

    Activity act;
    ArrayList<ProfreadData> list;

    public ProfReadingListAdapter(Activity act, ArrayList<ProfreadData> list) {
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
                view = inflater.inflate(R.layout.item_msgbox_date,parent,false);
                return new DateHolder(view);
            case READLIST_VIEW:
                view = inflater.inflate(R.layout.item_member_main,parent,false);
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

            SimpleDateFormat predf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");

            try {
                Date date = predf.parse(list.get(i).getRegdate());

                dateHolder.tv_date.setText(sdf.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }else if (viewType == READLIST_VIEW){
            ItemHolder itemHolder = (ItemHolder)holder;

            if (list.get(i).getCouple_type().equalsIgnoreCase("marry")) {
                itemHolder.tv_type.setText("결혼");
                itemHolder.tv_type.setTextColor(ContextCompat.getColor(act, R.color.color_407ff3));
                itemHolder.tv_type.setBackgroundResource(R.drawable.getmarried_bg_categorybg02_190923);
            } else if (list.get(i).getCouple_type().equalsIgnoreCase("remarry")) {
                itemHolder.tv_type.setText("재혼");
                itemHolder.tv_type.setTextColor(ContextCompat.getColor(act, R.color.color_f34075));
                itemHolder.tv_type.setBackgroundResource(R.drawable.getmarried_bg_categorybg01_190923);
            } else if (list.get(i).getCouple_type().equalsIgnoreCase("friend")) {
                itemHolder.tv_type.setText("재혼");
                itemHolder.tv_type.setTextColor(ContextCompat.getColor(act, R.color.color_adapter_friend));
                itemHolder.tv_type.setBackgroundResource(R.drawable.getmarried_bg_categorybg01_190923);
            } else {
                itemHolder.tv_type.setText("-");
                itemHolder.tv_type.setTextColor(ContextCompat.getColor(act, R.color.color_407ff3));
                itemHolder.tv_type.setBackgroundResource(R.drawable.getmarried_bg_categorybg02_190923);
            }

            if (list.get(i).getGender().equalsIgnoreCase("male")) {
                itemHolder.tv_gender.setText("남성");
                itemHolder.tv_gender.setTextColor(ContextCompat.getColor(act, R.color.color_407ff3));
                itemHolder.tv_nick.setTextColor(ContextCompat.getColor(act, R.color.color_mcolor));
            } else {
                itemHolder.tv_gender.setText("여성");
                itemHolder.tv_gender.setTextColor(ContextCompat.getColor(act, R.color.color_f34075));
                itemHolder.tv_nick.setTextColor(ContextCompat.getColor(act, R.color.color_wcolor));
            }

            itemHolder.tv_nick.setText(list.get(i).getNick());

            if (StringUtil.isNull(list.get(i).getLastnameYN())) {
                itemHolder.tv_name.setText("");
            } else {
                if (list.get(i).getLastnameYN().equalsIgnoreCase("Y")) {
                    itemHolder.tv_name.setText(list.get(i).getFamilyname() + "○○");
                } else {
                    itemHolder.tv_name.setText(list.get(i).getFamilyname() + list.get(i).getName());
                }
            }

            if (!StringUtil.isNull(list.get(i).getPimg())) {
                if (list.get(i).getPimg_ck().equalsIgnoreCase("Y")) {
                    itemHolder.iv_profimg.setVisibility(View.VISIBLE);
                    itemHolder.iv_noprofimg.setVisibility(View.GONE);
                    Glide.with(act)
                            .load(list.get(i).getPimg())
                            .into(itemHolder.iv_profimg);
                } else {
                    itemHolder.iv_profimg.setVisibility(View.GONE);
                    itemHolder.iv_noprofimg.setVisibility(View.VISIBLE);
                    Glide.with(act)
                            .load(list.get(i).getCharacter())
                            .into(itemHolder.iv_noprofimg);
                }
            } else {
                itemHolder.iv_profimg.setVisibility(View.GONE);
                itemHolder.iv_noprofimg.setVisibility(View.VISIBLE);

                Glide.with(act)
                        .load(list.get(i).getCharacter())
                        .into(itemHolder.iv_noprofimg);

            }



            if (StringUtil.isNull(list.get(i).getP_introduce())) {
                itemHolder.tv_content.setText("");
            } else {
                itemHolder.tv_content.setText(list.get(i).getP_introduce());
//                if(list.get(i).getPint_ck().equalsIgnoreCase("Y")) {
//                    itemHolder.tv_content.setText(list.get(i).getP_introduce());
//                } else {
//                    itemHolder.tv_content.setText(R.string.check_introduce);
//                }
            }

            itemHolder.tv_age.setText(StringUtil.calcAge(list.get(i).getByear()) + "세");
            itemHolder.tv_location.setText(list.get(i).getAddr1());
            itemHolder.tv_location_detail.setText(list.get(i).getAddr2());
            itemHolder.tv_login_time.setText(list.get(i).getRegdate());

            //선택삭제가능하게
            itemHolder.cb_item.setVisibility(View.VISIBLE);
            itemHolder.cb_item.setChecked(list.get(i).isCheckState());


            if(!StringUtil.isNull(list.get(i).getPiimgcnt())) {
                if(list.get(i).getPiimgcnt().equalsIgnoreCase("0")) {
                    itemHolder.tv_intro_count.setVisibility(View.GONE);
                } else {
                    itemHolder.tv_intro_count.setVisibility(View.VISIBLE);
                    itemHolder.tv_intro_count.setText(list.get(i).getPiimgcnt());
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        ProfreadData item = list.get(position);

        if (item.getItemtype().equalsIgnoreCase(StringUtil.TYPEDATE)) {
            return DATE_VIEW;
        } else if (item.getItemtype().equalsIgnoreCase(StringUtil.TYPEITEM)) {
            return READLIST_VIEW;
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

    public class DateHolder extends ViewHolder {
        TextView tv_date;

        public DateHolder(View v) {
            super(v);
            tv_date = v.findViewById(R.id.tv_itemdate);
        }
    }

    public class ItemHolder extends ViewHolder implements View.OnClickListener{
        LinearLayout clayout_area;
        ShimmerLayout ll_chatstart;
        LinearLayout btn_chat;
        ImageView iv_profimg, iv_noprofimg;
        TextView tv_type, tv_nick, tv_name, tv_gender, tv_age, tv_location, tv_content, tv_location_detail;
        TextView tv_login_time, tv_intro_count;
        CheckBox cb_item;

        public ItemHolder(View v) {
            super(v);

            clayout_area = v.findViewById(R.id.clayout_area);
            btn_chat = v.findViewById(R.id.btn_chat);
            iv_profimg = v.findViewById(R.id.iv_profimg);
            iv_noprofimg = v.findViewById(R.id.iv_noprofimg);
            tv_type = v.findViewById(R.id.tv_membertype);
            tv_nick = v.findViewById(R.id.tv_membernick);
            tv_name = v.findViewById(R.id.tv_name);
            tv_gender = v.findViewById(R.id.tv_gender);
            tv_age = v.findViewById(R.id.tv_age);
            tv_location = v.findViewById(R.id.tv_location);
            tv_content = v.findViewById(R.id.tv_content);
            tv_location_detail = v.findViewById(R.id.tv_location_detail);
            tv_login_time = v.findViewById(R.id.tv_login_time);
            tv_intro_count = v.findViewById(R.id.tv_intro_count);

            ll_chatstart = v.findViewById(R.id.ll_chat_start);

            clayout_area.setOnClickListener(this);
            btn_chat.setOnClickListener(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iv_profimg.setClipToOutline(true);
            }

            cb_item = v.findViewById(R.id.cb_msgitem);
            cb_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    list.get(getAdapterPosition()).setCheckState(b);
                }
            });
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.clayout_area:
                    Intent profIntent = new Intent(act, ProfileDetailAct.class);
                    profIntent.putExtra("gender", list.get(getAdapterPosition()).getGender());
                    profIntent.putExtra("midx", list.get(getAdapterPosition()).getU_idx());
                    act.startActivity(profIntent);
                    break;

                case R.id.btn_chat:
                    reqChat(list.get(getAdapterPosition()).getU_idx(), getAdapterPosition());
//                    Common.showToastDevelop(act);
            }
        }
    }

    /**
     * 채팅요청
     * type common(1:1)
     * uidx 자신의 멤버 인덱스
     *
     * @param tidx 상대방 멤버 인덱스
     */
    private void reqChat(String tidx, final int pos) {

        ReqBasic reqChat = new ReqBasic(act, NetUrls.REQCHAT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                Log.i(StringUtil.TAG, "reqChat:  " + resultData.getResult() + "\ncode: " + resultCode);

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Chat Check Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {

                            JSONObject obj = new JSONObject(jo.getString("data"));

                            obj.getString("room_idx");

                            Intent chatIntent = new Intent(act, ChatAct.class);
                            chatIntent.putExtra("room_idx", obj.getString("room_idx"));
                            chatIntent.putExtra("midx", list.get(pos).getU_idx());
                            chatIntent.putExtra("pimg", list.get(pos).getPimg());
                            chatIntent.putExtra("pimg_ck", list.get(pos).getPimg_ck());
                            chatIntent.putExtra("character", list.get(pos).getCharacter());
                            chatIntent.putExtra("gender", list.get(pos).getGender());
                            chatIntent.putExtra("nick", list.get(pos).getNick());
                            chatIntent.putExtra("loc", list.get(pos).getAddr1());
                            chatIntent.putExtra("age", list.get(pos).getByear());

                            act.startActivity(chatIntent);

                        } else {
                            Toast.makeText(act, jo.getString("msg"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(act, act.getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(act, act.getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };
        reqChat.setTag("Chat Create Room");
        reqChat.addParams("type", "common");
        reqChat.addParams("uidx", UserPref.getUidx(act));
        reqChat.addParams("tidx", tidx);
        reqChat.execute(true, true);
    }
}
