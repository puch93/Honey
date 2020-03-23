package com.match.honey.adapters.list;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.match.honey.R;
import com.match.honey.activity.ChatAct;
import com.match.honey.activity.MyProfileAct;
import com.match.honey.activity.MyprofileModifyAct;
import com.match.honey.activity.ProfileDetailAct;
import com.match.honey.listDatas.MemberData;
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

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.ViewHolder> {

    AppCompatActivity act;
    ArrayList<MemberData> list;

    private final int DATE_VIEW = 0;
    private final int NEWUSERLIST_VIEW = 1;

    public MemberListAdapter(AppCompatActivity act, ArrayList<MemberData> list) {
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
            case NEWUSERLIST_VIEW:
                view = inflater.inflate(R.layout.item_member_main, parent, false);
                return new ItemHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        int viewType = getItemViewType(i);

        if (viewType == DATE_VIEW) {
            DateHolder dateHolder = (DateHolder) holder;

            SimpleDateFormat predf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");

            try {
                Date date = predf.parse(list.get(i).getRegDate());
                dateHolder.tv_date.setText(sdf.format(date));

            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else if (viewType == NEWUSERLIST_VIEW) {
            ItemHolder itemHolder = (ItemHolder) holder;
            if (list.get(i).getMembertype().equalsIgnoreCase("marry")) {
                itemHolder.tv_type.setText("결혼");
                itemHolder.tv_type.setBackgroundResource(R.drawable.marriage_bg);
            } else if (list.get(i).getMembertype().equalsIgnoreCase("remarry")) {
                itemHolder.tv_type.setText("재혼");
                itemHolder.tv_type.setBackgroundResource(R.drawable.remarriage_bg);
            } else if (list.get(i).getMembertype().equalsIgnoreCase("friend")) {
                itemHolder.tv_type.setText("재혼");
                itemHolder.tv_type.setBackgroundResource(R.drawable.remarriage_bg);
            } else {
                itemHolder.tv_type.setText("-");
                itemHolder.tv_type.setBackgroundResource(R.drawable.marriage_bg);
            }

            if (list.get(i).getGender().equalsIgnoreCase("male")) {
                itemHolder.tv_gender.setText("남성");
                itemHolder.tv_gender.setTextColor(ContextCompat.getColor(act, R.color.man_color));
            } else {
                itemHolder.tv_gender.setText("여성");
                itemHolder.tv_gender.setTextColor(ContextCompat.getColor(act, R.color.women_color));
            }

            itemHolder.tv_nick.setText(list.get(i).getNickname());
            if (StringUtil.isNull(list.get(i).getLastnameState())) {
                itemHolder.tv_name.setText("");
            } else {
                if (list.get(i).getLastnameState().equalsIgnoreCase("Y")) {
                    itemHolder.tv_name.setText(list.get(i).getFamilyname());
                    itemHolder.tv_hidename.setVisibility(View.VISIBLE);
                } else {
                    itemHolder.tv_name.setText(list.get(i).getFamilyname() + list.get(i).getName());
                    itemHolder.tv_hidename.setVisibility(View.GONE);
                }
            }

            itemHolder.iv_profimg.setImageBitmap(null);

            if (!StringUtil.isNull(list.get(i).getProfimg())) {
                if (list.get(i).getPimg_ck().equalsIgnoreCase("Y")) {
                    itemHolder.iv_profimg.setVisibility(View.VISIBLE);
                    itemHolder.iv_noprofimg.setVisibility(View.GONE);
                    Glide.with(act)
                            .load(list.get(i).getProfimg())
                            .centerCrop()
                            .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(15)))
                            .into(itemHolder.iv_profimg);
                } else {
                    itemHolder.iv_profimg.setVisibility(View.GONE);
                    itemHolder.iv_noprofimg.setVisibility(View.VISIBLE);
                    Glide.with(act)
                            .load(list.get(i).getCharacter())
                            .centerCrop()
                            .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(15)))
                            .into(itemHolder.iv_noprofimg);
                }
            } else {
                itemHolder.iv_profimg.setVisibility(View.GONE);
                itemHolder.iv_noprofimg.setVisibility(View.VISIBLE);

                Glide.with(act)
                        .load(list.get(i).getCharacter())
                        .centerCrop()
                        .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(15)))
                        .into(itemHolder.iv_noprofimg);

            }

            itemHolder.tv_age.setText(list.get(i).getAge() + "세");
            itemHolder.tv_location.setText(list.get(i).getLocation());
            itemHolder.tv_location_detail.setText(list.get(i).getLocation2());


            if (StringUtil.isNull(list.get(i).getContent())) {
                itemHolder.tv_content.setText("");
            } else {
                itemHolder.tv_content.setText(list.get(i).getContent());
//                if(list.get(i).getPint_ck().equalsIgnoreCase("Y")) {
//                    itemHolder.tv_content.setText(list.get(i).getContent());
//                } else {
//                    itemHolder.tv_content.setText(R.string.check_introduce);
//                }
            }
            itemHolder.tv_login_time.setText(list.get(i).getRegDate());


            if(list.get(i).isMe()) {
                itemHolder.tv_chat.setText("수정");
                itemHolder.iv_chat.setVisibility(View.GONE);
            } else {
                itemHolder.tv_chat.setText("연락하기");
                itemHolder.iv_chat.setVisibility(View.VISIBLE);
            }


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

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {

                            JSONObject obj = new JSONObject(jo.getString("data"));

                            obj.getString("room_idx");

                            Intent chatIntent = new Intent(act, ChatAct.class);
                            chatIntent.putExtra("room_idx", obj.getString("room_idx"));
                            chatIntent.putExtra("midx", list.get(pos).getMidx());
                            chatIntent.putExtra("pimg", list.get(pos).getProfimg());
                            chatIntent.putExtra("pimg_ck", list.get(pos).getPimg_ck());
                            chatIntent.putExtra("character", list.get(pos).getCharacter());
                            chatIntent.putExtra("gender", list.get(pos).getGender());
                            chatIntent.putExtra("nick", list.get(pos).getNickname());
                            chatIntent.putExtra("loc", list.get(pos).getLocation());
                            chatIntent.putExtra("age", list.get(pos).getAge());
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

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).ll_chatstart.startShimmerAnimation();
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).ll_chatstart.stopShimmerAnimation();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        MemberData item = list.get(position);
        if (!StringUtil.isNull(item.getItemtype())) {
            if (item.getItemtype().equalsIgnoreCase(StringUtil.TYPEDATE)) {
                return DATE_VIEW;
            } else if (item.getItemtype().equalsIgnoreCase(StringUtil.TYPEITEM)) {
                return NEWUSERLIST_VIEW;
            }
        } else {
            return NEWUSERLIST_VIEW;
        }
        return super.getItemViewType(position);
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


    public class ItemHolder extends ViewHolder implements View.OnClickListener {
        LinearLayout clayout_area;
        ShimmerLayout ll_chatstart;
        LinearLayout btn_chat;
        ImageView iv_profimg, iv_noprofimg;
        ImageView iv_chat;
        TextView tv_type, tv_nick, tv_name, tv_gender, tv_age, tv_location, tv_content, tv_hidename, tv_location_detail;
        TextView tv_login_time;
        TextView tv_chat, tv_intro_count;

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
            tv_hidename = v.findViewById(R.id.tv_hidename);
            tv_location_detail = v.findViewById(R.id.tv_location_detail);
            tv_login_time = v.findViewById(R.id.tv_login_time);
            iv_chat = v.findViewById(R.id.iv_chat);
            tv_chat = v.findViewById(R.id.tv_chat);
            tv_intro_count = v.findViewById(R.id.tv_intro_count);

            ll_chatstart = v.findViewById(R.id.ll_chat_start);

            clayout_area.setOnClickListener(this);
            btn_chat.setOnClickListener(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iv_profimg.setClipToOutline(true);
            }
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.clayout_area:
                    if(list.get(getAdapterPosition()).isMe()) {
                        act.startActivity(new Intent(act, MyProfileAct.class));
                    } else {
                        Intent profIntent = new Intent(act, ProfileDetailAct.class);
                        profIntent.putExtra("gender", list.get(getAdapterPosition()).getGender());
                        profIntent.putExtra("midx", list.get(getAdapterPosition()).getMidx());
                        act.startActivity(profIntent);
                    }
                    break;
                case R.id.btn_chat:
//                    int pos = getAdapterPosition();
//
//                    Intent chatIntent = new Intent(act, ChatAct.class);
//                    chatIntent.putExtra("midx", list.get(pos).getMidx());
//                    chatIntent.putExtra("pimg", list.get(pos).getProfimg());
//                    chatIntent.putExtra("gender", list.get(pos).getGender());
//                    chatIntent.putExtra("nick", list.get(pos).getNickname());
//                    chatIntent.putExtra("loc", list.get(pos).getLocation());
//                    chatIntent.putExtra("age", list.get(pos).getAge());
//                    act.startActivity(chatIntent);

                    if(list.get(getAdapterPosition()).isMe()) {
                        act.startActivity(new Intent(act, MyprofileModifyAct.class));
                    } else {
                        reqChat(list.get(getAdapterPosition()).getMidx(), getAdapterPosition());
                    }
                    break;
            }
        }
    }

}
