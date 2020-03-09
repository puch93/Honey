package com.match.honey.adapters.list;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.match.honey.R;
import com.match.honey.activity.MoreimgActivity;
import com.match.honey.activity.MyProfileAct;
import com.match.honey.listDatas.ImagesData;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ImagelistAdapter extends RecyclerView.Adapter<ImagelistAdapter.ViewHolder> {

    Activity act;
    ArrayList<ImagesData> list;

    public ImagelistAdapter(Activity act, ArrayList<ImagesData> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myprofile_images, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.iv_image.setImageBitmap(null);

        if (!StringUtil.isNull(list.get(i).getPiimg())) {
            Glide.with(act)
                    .load(list.get(i).getPiimg())
                    .into(holder.iv_image);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView iv_image, btn_remove;

        public ViewHolder(View v) {
            super(v);
            iv_image = v.findViewById(R.id.iv_imgitem);
            btn_remove = v.findViewById(R.id.btn_remove);

            btn_remove.setOnClickListener(this);
            iv_image.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_remove:
                    delimg(list.get(getAdapterPosition()).getIdx(), getAdapterPosition());
                    break;
                case R.id.iv_imgitem:
                    Intent imgIntent = new Intent(act, MoreimgActivity.class);
                    imgIntent.putExtra("imgurl", list.get(getAdapterPosition()).getPiimg());
                    act.startActivity(imgIntent);
                    break;
            }
        }
    }

    private void delimg(String idx, final int pos) {
        ReqBasic delimg = new ReqBasic(act, NetUrls.DELIMAGE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            list.remove(pos);
                            ((MyProfileAct) act).modifyIntroImageCount();
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(act, act.getString(R.string.err_network), Toast.LENGTH_SHORT).show();
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

        delimg.addParams("piidx", idx);
        delimg.execute(true, true);
    }
}
