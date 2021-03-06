package com.match.honey.activity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.match.honey.R;
import com.match.honey.databinding.ActivityThanksdetailBinding;
import com.match.honey.listDatas.ThanksData;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.utils.StatusBarUtil;
import com.match.honey.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class ReviewDetailAct extends AppCompatActivity implements View.OnClickListener {

    ActivityThanksdetailBinding binding;
    AppCompatActivity act;
    String bidx;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_thanksdetail);
        act = this;
        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);
        binding.flBack.setOnClickListener(this);

//        bidx = getIntent().getStringExtra("bidx");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.ivProfimg.setClipToOutline(true);
        }

        setReview((ThanksData) getIntent().getSerializableExtra("review"));
    }

    private void setReview(ThanksData data) {
        setPlusCount(data.getBidx());

        binding.tvNick.setText(data.getNick());
        binding.tvLocation.setText(data.getAddr1() + " " + data.getAddr2());
        if(data.getMarriage().equalsIgnoreCase("marry")) {
            binding.tvMarriage.setText("결혼");
        } else if(data.getMarriage().equalsIgnoreCase("remarry")){
            binding.tvMarriage.setText("재혼");
        } else {
            binding.tvMarriage.setText("재혼");
        }

        if (data.getGender().equalsIgnoreCase("male")) {
            binding.tvGender.setTextColor(getResources().getColor(R.color.man_color));
            binding.tvGender.setText("(남성)");
        } else {
            binding.tvGender.setTextColor(getResources().getColor(R.color.women_color));
            binding.tvGender.setText("(여성)");
        }

        if (!StringUtil.isNull(data.getProfimg())) {
            if (data.getPimg_ck().equalsIgnoreCase("Y")) {
                binding.ivProfimg.setVisibility(View.VISIBLE);
                binding.ivNoprofimg.setVisibility(View.GONE);
                Glide.with(act)
                        .load(data.getProfimg())
                        .centerCrop()
                        .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(15)))
                        .into(binding.ivProfimg);
            } else {
                binding.ivProfimg.setVisibility(View.GONE);
                binding.ivNoprofimg.setVisibility(View.VISIBLE);
                Glide.with(act)
                        .load(data.getCharacter())
                        .centerCrop()
                        .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(15)))
                        .into(binding.ivNoprofimg);
            }
        } else {
            binding.ivProfimg.setVisibility(View.GONE);
            binding.ivNoprofimg.setVisibility(View.VISIBLE);

            Glide.with(act)
                    .load(data.getCharacter())
                    .centerCrop()
                    .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(15)))
                    .into(binding.ivNoprofimg);

        }

        binding.tvContents.setText(data.getContents());

        if(!StringUtil.isNull(data.getPint())) {
            binding.tvIntroduce.setText(data.getPint());
//            if(data.getPint_ck().equalsIgnoreCase("Y")) {
//                binding.tvIntroduce.setText(data.getPint());
//            } else {
//                binding.tvIntroduce.setText(R.string.check_introduce);
//            }
        }
    }

    private void setPlusCount(String idx) {
        ReqBasic getNotice = new ReqBasic(this, NetUrls.REVIEWCOUNT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Review Plus Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {

                        } else {
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        };

        getNotice.addParams("idx", idx);
        getNotice.execute(true, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;
        }
    }
}
