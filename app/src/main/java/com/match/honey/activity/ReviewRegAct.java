package com.match.honey.activity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.match.honey.R;
import com.match.honey.databinding.ActivityThanksregBinding;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.Common;
import com.match.honey.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReviewRegAct extends AppCompatActivity {
    ActivityThanksregBinding binding;
    AppCompatActivity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_thanksreg, null);
        act = this;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.ivProfimg.setClipToOutline(true);
        }

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.tvReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //자기소개서
                if (StringUtil.isNull(binding.etReviewContents.getText().toString())) {
                    Common.showToast(act, "후기글을 작성해주세요");
                    return;
                } else {
                    if (binding.etReviewContents.length() < 50) {
                        Toast.makeText(act, "후기글은 50자 이상입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (binding.etReviewContents.length() > 300) {
                        Toast.makeText(act, "후기글은 300자 이하입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                registReview();
            }
        });


        //자기소개 글자수 세팅
        binding.etReviewContents.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = binding.etReviewContents.getText().toString();
                binding.tvCountText.setText(String.valueOf(input.length()));
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        getMyProfileBasic();
    }

    private void getMyProfileBasic() {
        ReqBasic req = new ReqBasic(this, NetUrls.MYPROFILEBASIC) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Basic Info Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            JSONArray ja = jo.getJSONArray("value");
                            JSONObject job = ja.getJSONObject(0);

                            binding.tvNick.setText(job.getString("nick"));
                            binding.tvLocation.setText(job.getString("addr1") + " " + job.getString("addr2"));
                            if(job.getString("couple_type").equalsIgnoreCase("marry")) {
                                binding.tvMarriage.setText("결혼");
                            } else if(job.getString("couple_type").equalsIgnoreCase("remarry")){
                                binding.tvMarriage.setText("재혼");
                            } else {
                                binding.tvMarriage.setText("재혼");
                            }

                            if (job.getString("gender").equalsIgnoreCase("male")) {
                                binding.tvGender.setTextColor(getResources().getColor(R.color.man_color));
                                binding.tvGender.setText("(남성)");
                            } else {
                                binding.tvGender.setTextColor(getResources().getColor(R.color.women_color));
                                binding.tvGender.setText("(여성)");
                            }

                            //프사
                           if (!StringUtil.isNull(job.getString("pimg"))) {
                                    binding.ivProfimg.setVisibility(View.VISIBLE);
                                    binding.ivNoprofimg.setVisibility(View.GONE);
                                    Glide.with(act)
                                            .load(job.getString("pimg"))
                                            .centerCrop()
                                            .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(15)))
                                            .into(binding.ivProfimg);

                            } else {
                                binding.ivProfimg.setVisibility(View.GONE);
                                binding.ivNoprofimg.setVisibility(View.VISIBLE);

                                Glide.with(act)
                                        .load(Common.getCharacterDrawable(job.getString("character"), job.getString("gender")))
                                        .centerCrop()
                                        .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(15)))
                                        .into(binding.ivNoprofimg);

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        req.addParams("uidx", UserPref.getUidx(this));
        req.execute(true, true);
    }

    private void registReview() {
        ReqBasic req = new ReqBasic(this, NetUrls.REGISTREVIEW) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Reg Review Get Info: " + jo);
                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            Common.showToast(act, "이용후기 등록완료");
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        req.addParams("uidx", UserPref.getUidx(this));
        req.addParams("content", binding.etReviewContents.getText().toString());
        req.execute(true, true);
    }
}
