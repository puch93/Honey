package com.match.marryme.adapters.list;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.match.marryme.R;
import com.match.marryme.listDatas.MemberData;
import com.match.marryme.utils.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class FirstAdapter extends RecyclerView.Adapter<FirstAdapter.ViewHolder> {

    Activity act;
    ArrayList<MemberData> list;

    private final int DATE_VIEW = 0;
    private final int NEWUSERLIST_VIEW = 1;

    public FirstAdapter(Activity act, ArrayList<MemberData> list) {
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
                view = inflater.inflate(R.layout.item_member2, parent, false);
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
                itemHolder.tv_type.setTextColor(ContextCompat.getColor(act, R.color.color_407ff3));
                itemHolder.tv_type.setBackgroundResource(R.drawable.getmarried_bg_categorybg02_190923);
            } else if (list.get(i).getMembertype().equalsIgnoreCase("remarry")) {
                itemHolder.tv_type.setText("재혼");
                itemHolder.tv_type.setTextColor(ContextCompat.getColor(act, R.color.color_f34075));
                itemHolder.tv_type.setBackgroundResource(R.drawable.getmarried_bg_categorybg01_190923);
            } else if (list.get(i).getMembertype().equalsIgnoreCase("friend")) {
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
                itemHolder.tv_nick.setTextColor(ContextCompat.getColor(act, R.color.color_mcolor));
                itemHolder.tv_gender.setTextColor(ContextCompat.getColor(act, R.color.color_407ff3));
            } else {
                itemHolder.tv_gender.setText("여성");
                itemHolder.tv_nick.setTextColor(ContextCompat.getColor(act, R.color.color_wcolor));
                itemHolder.tv_gender.setTextColor(ContextCompat.getColor(act, R.color.color_f34075));
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
        }
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


    public class ItemHolder extends ViewHolder {
        LinearLayout clayout_area;
        ShimmerLayout ll_chatstart;
        LinearLayout btn_chat;
        ImageView iv_profimg, iv_noprofimg;
        TextView tv_type, tv_nick, tv_name, tv_gender, tv_age, tv_location, tv_content, tv_hidename, tv_location_detail;
        TextView tv_login_time;

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

            ll_chatstart = v.findViewById(R.id.ll_chat_start);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iv_profimg.setClipToOutline(true);
            }
        }
    }

}
