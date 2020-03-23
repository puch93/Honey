package com.match.honey.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.match.honey.R;
import com.match.honey.adapters.HopestyleOtherAdapter;
import com.match.honey.adapters.list.ImagelistOtherAdapter2;
import com.match.honey.databinding.ActivityProfiledetailBinding;
import com.match.honey.listDatas.HopestyleData;
import com.match.honey.listDatas.ImagesData;
import com.match.honey.listDatas.ProfileData;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.SettingAlarmPref;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.ChangeProfVal;
import com.match.honey.utils.Common;
import com.match.honey.utils.ItemOffsetDecorationJoin;
import com.match.honey.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileDetailAct extends AppCompatActivity implements View.OnClickListener {

    ActivityProfiledetailBinding binding;

    String midx, gender;
    public ProfileData data;
    ChangeProfVal cpv;

    public boolean isReadOk = false;
    AppCompatActivity act;


    HopestyleOtherAdapter adapter;
    ArrayList<HopestyleData> list = new ArrayList<>();
    ArrayList<ImagesData> imgpath = new ArrayList<>();

    ImagelistOtherAdapter2 imgAdapter;

    String otherNick;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        gender = getIntent().getStringExtra("gender");
        midx = getIntent().getStringExtra("midx");

        if (!StringUtil.isNull(gender) && gender.equalsIgnoreCase(UserPref.getGender(act))) {
            Common.showToast(act, "* 동성간에는 프로필 열람이 불가합니다 *");
            finish();
        } else {
            binding = DataBindingUtil.setContentView(this, R.layout.activity_profiledetail);

            cpv = new ChangeProfVal();

            binding.ivProfimg.setOnClickListener(this);
            binding.flBack.setOnClickListener(this);
            binding.llInterest.setOnClickListener(this);
            binding.llChat.setOnClickListener(this);
            binding.tvCall.setOnClickListener(this);
            binding.tvReport.setOnClickListener(this);


            String interIdx[] = UserPref.getInterIdxs(this).split(",");
            if (interIdx.length > 0) {
                for (String idx : interIdx) {
                    if (midx.equalsIgnoreCase(idx)) {
                        binding.llInterest.setSelected(true);
                    }
                }
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.ivProfimg.setClipToOutline(true);
            }

            //배우자 희망지역 세팅
            binding.rcvHoplestyle.setLayoutManager(new GridLayoutManager(this, 3));
            adapter = new HopestyleOtherAdapter(this, list);
            binding.rcvHoplestyle.setAdapter(adapter);
            binding.rcvHoplestyle.addItemDecoration(new ItemOffsetDecorationJoin(this,
                    getResources().getDimensionPixelSize(R.dimen.dimen_hope01),
                    getResources().getDimensionPixelSize(R.dimen.dimen_hope02),
                    getResources().getDimensionPixelSize(R.dimen.dimen_10)));

            //하단 이미지 리스트
            binding.rcvImages.setLayoutManager(new GridLayoutManager(this, 3));
            imgAdapter = new ImagelistOtherAdapter2(this, imgpath);
            binding.rcvImages.setAdapter(imgAdapter);
            binding.rcvImages.addItemDecoration(new ItemOffsetDecorationJoin(this,
                    getResources().getDimensionPixelSize(R.dimen.dimen_hope01),
                    getResources().getDimensionPixelSize(R.dimen.dimen_hope02),
                    getResources().getDimensionPixelSize(R.dimen.dimen_8)));

            if (UserPref.getGender(act).equalsIgnoreCase("female")) {
                isReadOk = true;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showLayout();
                    }
                });

                // getProfile : 방문기록 남기기, getMyProfile : 방문기록 남기지 않기
                if (SettingAlarmPref.isSendHistroy(act)) {
                    getProfile();
                } else {
                    getMyProfile();
                }
            } else {
                checkProfileIsRead();
            }
        }
    }

    private void hideLayout() {
        binding.tvRelation.setVisibility(View.GONE);
        binding.tvSalary.setVisibility(View.GONE);
        binding.tvProperty.setVisibility(View.GONE);
        binding.tvEdu.setVisibility(View.GONE);
        binding.tvSmoke.setVisibility(View.GONE);
        binding.tvDrinking.setVisibility(View.GONE);
        binding.tvChild.setVisibility(View.GONE);
        binding.tvChildWho.setVisibility(View.GONE);
        binding.llBloodReligionArea.setVisibility(View.GONE);
        binding.tvHopeaddr.setVisibility(View.GONE);

        binding.llRead01.setVisibility(View.VISIBLE);
        binding.llRead02.setVisibility(View.VISIBLE);
        binding.llRead03.setVisibility(View.VISIBLE);
        binding.llRead04.setVisibility(View.VISIBLE);
        binding.llRead05.setVisibility(View.VISIBLE);
        binding.llRead06.setVisibility(View.VISIBLE);
        binding.llRead07.setVisibility(View.VISIBLE);
        binding.llRead08.setVisibility(View.VISIBLE);
        binding.llRead09.setVisibility(View.VISIBLE);
        binding.llRead10.setVisibility(View.VISIBLE);

        binding.btnRead01.setOnClickListener(this);
        binding.btnRead02.setOnClickListener(this);
        binding.btnRead03.setOnClickListener(this);
        binding.btnRead04.setOnClickListener(this);
        binding.btnRead05.setOnClickListener(this);
        binding.btnRead06.setOnClickListener(this);
        binding.btnRead07.setOnClickListener(this);
        binding.btnRead08.setOnClickListener(this);
        binding.btnRead09.setOnClickListener(this);
        binding.btnRead10.setOnClickListener(this);
    }

    private void showLayout() {
        imgpath = new ArrayList<>();
        list = new ArrayList<>();

        binding.llRead01.setVisibility(View.GONE);
        binding.llRead02.setVisibility(View.GONE);
        binding.llRead03.setVisibility(View.GONE);
        binding.llRead04.setVisibility(View.GONE);
        binding.llRead05.setVisibility(View.GONE);
        binding.llRead06.setVisibility(View.GONE);
        binding.llRead07.setVisibility(View.GONE);
        binding.llRead08.setVisibility(View.GONE);
        binding.llRead09.setVisibility(View.GONE);
        binding.llRead10.setVisibility(View.GONE);

        binding.tvRelation.setVisibility(View.VISIBLE);
        binding.tvSalary.setVisibility(View.VISIBLE);
        binding.tvProperty.setVisibility(View.VISIBLE);
        binding.tvEdu.setVisibility(View.VISIBLE);
        binding.tvSmoke.setVisibility(View.VISIBLE);
        binding.tvDrinking.setVisibility(View.VISIBLE);
        binding.tvChild.setVisibility(View.VISIBLE);
        binding.tvChildWho.setVisibility(View.VISIBLE);
        binding.llBloodReligionArea.setVisibility(View.VISIBLE);
        binding.tvHopeaddr.setVisibility(View.VISIBLE);
    }

    private void showWarning() {
        if (StringUtil.isNull(otherNick)) {
            return;
        }

        LayoutInflater dialog = LayoutInflater.from(this);
        View dialogLayout = dialog.inflate(R.layout.dlg_confirm3, null);
        final Dialog menuDlg = new Dialog(this);

        menuDlg.setContentView(dialogLayout);
        menuDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        menuDlg.show();

        TextView btn_cancel = (TextView) dialogLayout.findViewById(R.id.btn_cancel);
        TextView tv_contents = (TextView) dialogLayout.findViewById(R.id.tv_contents);
        TextView btn_ok = (TextView) dialogLayout.findViewById(R.id.btn_ok);

        tv_contents.setText(otherNick + "님의 모든 가려진 정보와 사진을 확인하시겠습니까?");

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuDlg.isShowing()) {
                    menuDlg.dismiss();
                }
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkProfileVoucher();

                if (menuDlg.isShowing()) {
                    menuDlg.dismiss();
                }
            }
        });
    }

    private void checkProfileVoucher() {
        ReqBasic buyItem = new ReqBasic(act, NetUrls.PROFREADCHECK) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Check Profile Voucher Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            isReadOk = true;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showLayout();
                                }
                            });

                            // getProfile : 방문기록 남기기, getMyProfile : 방문기록 남기지 않기
                            if (SettingAlarmPref.isSendHistroy(act)) {
                                getProfile();
                            } else {
                                getMyProfile();
                            }
                        } else {
                            Common.showToast(act, "열람권 구매 후 이용바랍니다");
                            Intent intent2 = new Intent(act, ItemmanageAct.class);
                            intent2.putExtra("from", "read");
                            startActivityForResult(intent2, 1000);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        };

        buyItem.addParams("u_idx", UserPref.getUidx(act));
        buyItem.addParams("t_idx", midx);

        buyItem.execute(true, false);
    }

    private void checkProfileIsRead() {
        ReqBasic buyItem = new ReqBasic(act, NetUrls.PROFREADCHECK2) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            isReadOk = true;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showLayout();
                                }
                            });

                            // getProfile : 방문기록 남기기, getMyProfile : 방문기록 남기지 않기
                            if (SettingAlarmPref.isSendHistroy(act)) {
                                getProfile();
                            } else {
                                getMyProfile();
                            }
                        } else {
                            isReadOk = false;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideLayout();
                                }
                            });

                            // getProfile : 방문기록 남기기, getMyProfile : 방문기록 남기지 않기
                            if (SettingAlarmPref.isSendHistroy(act)) {
                                getProfile();
                            } else {
                                getMyProfile();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        };

        buyItem.setTag("Is Read");
        buyItem.addParams("u_idx", UserPref.getUidx(act));
        buyItem.addParams("t_idx", midx);

        buyItem.execute(true, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            showWarning();
        }
    }

    private void reqChat() {
        ReqBasic reqChat = new ReqBasic(this, NetUrls.REQCHAT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {

                            JSONObject obj = new JSONObject(jo.getString("data"));

                            obj.getString("room_idx");

                            Intent chatIntent = new Intent(ProfileDetailAct.this, ChatAct.class);
                            chatIntent.putExtra("room_idx", obj.getString("room_idx"));
                            chatIntent.putExtra("midx", data.getU_idx());
                            chatIntent.putExtra("pimg", data.getPimg());
                            chatIntent.putExtra("pimg_ck", data.getPimg_ck());
                            chatIntent.putExtra("character", data.getCharacter_int());
                            chatIntent.putExtra("gender", data.getGender());
                            chatIntent.putExtra("nick", data.getNick());
                            chatIntent.putExtra("loc", data.getAddr1());
                            chatIntent.putExtra("age", StringUtil.calcAge(data.getByear()));
                            startActivity(chatIntent);

                        } else {
                            Toast.makeText(act, jo.getString("msg"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ProfileDetailAct.this, getResources().getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileDetailAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };
        reqChat.setTag("Chat Create Room");
        reqChat.addParams("type", "common");
        reqChat.addParams("uidx", UserPref.getUidx(this));
        reqChat.addParams("tidx", data.getU_idx());
        reqChat.execute(true, true);
    }

    private void getProfile() {
        ReqBasic myprofile = new ReqBasic(this, NetUrls.OTHERPROFILE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Other Profile01 Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            JSONObject value = new JSONObject(jo.getString("value"));
                            JSONObject obj = new JSONObject(value.getString("0"));

                            data = new ProfileData();

                            data.setNick(obj.getString("abroad"));
                            data.setAbroad(obj.getString("abroad"));
                            data.setAddr1(obj.getString("addr1"));
                            data.setAddr2(obj.getString("addr2"));
                            data.setAnnual(obj.getString("annual"));
                            data.setAnnual_img(obj.getString("annual_img"));
                            data.setAuth_num(obj.getString("auth_num"));
                            data.setAuth_ok(obj.getString("auth_ok"));
                            data.setBlood(obj.getString("blood"));
                            data.setBmonth(obj.getString("bmonth"));
                            data.setBrotherhood1(obj.getString("brotherhood1"));
                            data.setBrotherhood2(obj.getString("brotherhood2"));
                            data.setBrotherhood3(obj.getString("brotherhood3"));
                            data.setByear(obj.getString("byear"));
                            data.setCarinfo(obj.getString("carinfo"));
                            data.setCouple_type(obj.getString("couple_type"));
                            data.setDrink(obj.getString("drink"));
                            data.setDrink_detail(obj.getString("drink_detail"));
                            data.setEducation(obj.getString("education"));
                            data.setEducation_img(obj.getString("education_img"));
                            data.setFamily(obj.getString("family"));
                            data.setFamily_info(obj.getString("family_info"));
                            data.setFood1(obj.getString("food1"));
                            data.setFood2(obj.getString("food2"));
                            data.setFood3(obj.getString("food3"));
                            data.setFood4(obj.getString("food4"));
                            data.setFood5(obj.getString("food5"));
                            data.setFood6(obj.getString("food6"));
                            data.setFood7(obj.getString("food7"));
                            data.setGender(obj.getString("gender"));
                            data.setHealth(obj.getString("health"));
                            data.setHealth_detail(obj.getString("health_detail"));
                            data.setHeight(obj.getString("height"));
                            data.setHobby1(obj.getString("hobby1"));
                            data.setHobby2(obj.getString("hobby2"));
                            data.setHobby3(obj.getString("hobby3"));
                            data.setHobby4(obj.getString("hobby4"));
                            data.setHobby5(obj.getString("hobby5"));
                            data.setHometown(obj.getString("hometown"));
                            data.setHometown1(obj.getString("hometown1"));
                            data.setHopeaddr(obj.getString("hopeaddr"));
                            data.setHope_religion(obj.getString("hope_religion"));
                            data.setHope_style1(obj.getString("hope_style1"));
                            data.setHope_style2(obj.getString("hope_style2"));
                            data.setHope_style3(obj.getString("hope_style3"));
                            data.setHope_style4(obj.getString("hope_style4"));
                            data.setHope_style5(obj.getString("hope_style5"));
                            data.setHope_style6(obj.getString("hope_style6"));
                            data.setHope_style7(obj.getString("hope_style7"));
                            data.setId(obj.getString("id"));
                            data.setIdx(obj.getString("idx"));
                            data.setInterest_idxs(obj.getString("interest_idxs"));
                            data.setJob_detail(obj.getString("job_detail"));
                            data.setJob_group(obj.getString("job_group"));
                            data.setJob_img(obj.getString("job_img"));
                            data.setLastnameYN(obj.getString("lastnameYN"));
                            data.setLat(obj.getString("lat"));
                            data.setLoginYN(obj.getString("loginYN"));
                            data.setLon(obj.getString("lon"));
                            data.setMarried_paper_img(obj.getString("married_paper_img"));
                            data.setM_fcm_token(obj.getString("m_fcm_token"));
                            data.setName(obj.getString("name"));
                            data.setFamilyname(obj.getString("familyname"));
                            data.setNick(obj.getString("nick"));
                            data.setPassionstyle1(obj.getString("passionstyle1"));
                            data.setPassionstyle2(obj.getString("passionstyle2"));
                            data.setPersonality1(obj.getString("personality1"));
                            data.setPersonality2(obj.getString("personality2"));
                            data.setPersonality3(obj.getString("personality3"));
                            data.setPimg(obj.getString("pimg"));
                            data.setPimg_ck(obj.getString("pimg_ck"));
                            data.setPint_ck(obj.getString("pint_ck"));
                            data.setCharacter_int(Common.getCharacterDrawable(obj.getString("character"), obj.getString("gender")));
                            data.setProperty(obj.getString("property"));
                            data.setPw(obj.getString("pw"));
                            data.setP_gif1(obj.getString("p_gif1"));
                            data.setP_gif2(obj.getString("p_gif2"));
                            data.setP_gif3(obj.getString("p_gif3"));
                            data.setP_introduce(obj.getString("p_introduce"));
                            data.setP_movie1(obj.getString("p_movie1"));
                            data.setP_movie2(obj.getString("p_movie2"));
                            data.setP_movie3(obj.getString("p_movie3"));
                            data.setP_movthumb1(obj.getString("p_movthumb1"));
                            data.setP_movthumb2(obj.getString("p_movthumb2"));
                            data.setP_movthumb3(obj.getString("p_movthumb3"));
                            data.setP_wave1(obj.getString("p_wave1"));
                            data.setP_wave2(obj.getString("p_wave2"));
                            data.setP_wave3(obj.getString("p_wave3"));
                            data.setReference(obj.getString("reference"));
                            data.setRegdate(obj.getString("regdate"));
                            data.setReligion(obj.getString("religion"));
                            data.setRemarried(obj.getString("remarried"));
                            data.setSelf_score(obj.getString("self_score"));
                            data.setSmoke(obj.getString("smoke"));
                            data.setSmoke_detail(obj.getString("smoke_detail"));
                            data.setStyle(obj.getString("style"));
                            data.setType(obj.getString("type"));
                            data.setU_idx(obj.getString("u_idx"));
                            data.setWeight(obj.getString("weight"));
                            data.setWhen_marry(obj.getString("when_marry"));
                            data.setZodiac(obj.getString("zodiac"));
                            data.setChildcnt(obj.getString("childcnt"));
                            data.setChildwho(obj.getString("childwho"));

                            int pic_all_count = 0;
                            int pic_profile_count = 0;
                            int pic_intro_count = 0;


                            //프로필이미지
                            if (!StringUtil.isNull(data.getPimg())) {
                                if (data.getPimg_ck().equalsIgnoreCase("Y")) {
                                    binding.ivProfimg.setVisibility(View.VISIBLE);
                                    binding.ivNoprofimg.setVisibility(View.GONE);
                                    Glide.with(ProfileDetailAct.this)
                                            .load(data.getPimg())
                                            .centerCrop()
                                            .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(15)))
                                            .into(binding.ivProfimg);
                                } else {
                                    binding.ivProfimg.setVisibility(View.GONE);
                                    binding.ivNoprofimg.setVisibility(View.VISIBLE);
                                    Glide.with(ProfileDetailAct.this)
                                            .load(data.getCharacter_int())
                                            .centerCrop()
                                            .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(15)))
                                            .into(binding.ivNoprofimg);
                                }

                                ++pic_all_count;
                                ++pic_profile_count;
                            } else {
                                binding.ivProfimg.setVisibility(View.GONE);
                                binding.ivNoprofimg.setVisibility(View.VISIBLE);
                                Glide.with(ProfileDetailAct.this)
                                        .load(data.getCharacter_int())
                                        .centerCrop()
                                        .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(15)))
                                        .into(binding.ivNoprofimg);
                            }

                            binding.tvTitlenick.setText(data.getNick());
                            binding.tvNick.setText(data.getNick());

                            otherNick = data.getNick();

                            if (data.getCouple_type().equalsIgnoreCase("marry")) {
                                binding.tvMembertype.setText("결혼");
                                binding.tvTopmemtype.setText("결혼");
                                binding.tvTopmemtype.setBackgroundResource(R.drawable.marriage_bg);
                                binding.tvMembertype.setTextColor(ContextCompat.getColor(ProfileDetailAct.this, R.color.man_color));
                            } else if (data.getCouple_type().equalsIgnoreCase("remarry")) {
                                binding.tvMembertype.setTextColor(ContextCompat.getColor(ProfileDetailAct.this, R.color.women_color));
                                binding.tvMembertype.setText("재혼");
                                binding.tvTopmemtype.setText("재혼");
                                binding.tvTopmemtype.setBackgroundResource(R.drawable.remarriage_bg);
                            } else if (data.getCouple_type().equalsIgnoreCase("friend")) {
                                binding.tvMembertype.setTextColor(ContextCompat.getColor(ProfileDetailAct.this, R.color.color_adapter_friend));
                                binding.tvMembertype.setText("재혼");
                                binding.tvTopmemtype.setText("재혼");
                                binding.tvTopmemtype.setBackgroundResource(R.drawable.remarriage_bg);
                            }

                            binding.tvIntroduceTop.setText(data.getP_introduce());

                            binding.tvTopnick.setText(data.getNick());

                            binding.tvToploc.setText(data.getAddr1() + " " + data.getAddr2());

                            if (data.getGender().equalsIgnoreCase("male")) {
                                binding.tvGender.setText("남성");
                                binding.tvGender.setTextColor(getResources().getColor(R.color.man_color));
                            } else {
                                binding.tvGender.setText("여성");
                                binding.tvGender.setTextColor(getResources().getColor(R.color.women_color));
                            }

                            if (!StringUtil.isNull(data.getFamilyname())) {
                                binding.tvName.setTextColor(getResources().getColor(R.color.color_2));
                                if (data.getLastnameYN().equalsIgnoreCase("Y")) {
                                    binding.tvName.setText(data.getFamilyname() + "○○");
                                } else {
                                    binding.tvName.setText(data.getFamilyname() + data.getName());
                                }
                            }

//                            String ageinfo = StringUtil.calcAge(data.getByear()) + "세(" + data.getBmonth() + "월생/" + data.getZodiac() + "띠)";
                            String ageinfo = StringUtil.calcAge(data.getByear()) + "세(" + StringUtil.calcZodiac(Integer.parseInt(data.getByear())) + "띠)";
                            binding.tvAge.setText(ageinfo);


                            //직업
                            if (!StringUtil.isNull(data.getJob_group())) {
                                if (data.getJob_group().contains("직접입력")) {
                                    binding.tvJob.setText(data.getJob_group().replace("직접입력", ""));
                                } else {
                                    binding.tvJob.setText(data.getJob_group());
                                }
                            } else {
                                binding.tvJob.setText("없음");
                            }


                            //키
                            if (!StringUtil.isNull(data.getHeight())) {
                                binding.tvHeight.setTextColor(getResources().getColor(R.color.color_2));
                                binding.tvHeight.setText(data.getHeight());
                            } else {
                                binding.tvHeight.setTextColor(getResources().getColor(R.color.color_2));
                                binding.tvHeight.setText("없음");
                            }
                            //스타일
                            if (!StringUtil.isNull(data.getStyle())) {
                                binding.tvStyle.setTextColor(getResources().getColor(R.color.color_2));
                                binding.tvStyle.setText(data.getStyle());
                            } else {
                                binding.tvStyle.setTextColor(getResources().getColor(R.color.color_2));
                                binding.tvStyle.setText("없음");
                            }

                            //성격
                            String personal = "";
                            if (!StringUtil.isNull(data.getPersonality1())) {
                                personal = data.getPersonality1();
                            }
                            if (!StringUtil.isNull(data.getPersonality2())) {
                                personal += "," + data.getPersonality2();
                            }
                            if (!StringUtil.isNull(data.getPersonality3())) {
                                personal += "," + data.getPersonality3();
                            }

                            if (StringUtil.isNull(personal)) {
                                binding.tvCharater.setText("없음");
                            } else {
                                binding.tvCharater.setText(personal);
                            }

                            //취미
                            String hobby = "";
                            if (!StringUtil.isNull(data.getHobby1())) {
                                hobby = data.getHobby1();
                            }
                            if (!StringUtil.isNull(data.getHobby2())) {
                                hobby += "," + data.getHobby2();
                            }
                            if (!StringUtil.isNull(data.getHobby3())) {
                                hobby += "," + data.getHobby3();
                            }
                            if (!StringUtil.isNull(data.getHobby4())) {
                                hobby += "," + data.getHobby4();
                            }
                            if (!StringUtil.isNull(data.getHobby5())) {
                                hobby += "," + data.getHobby5();
                            }
                            if (!StringUtil.isNull(hobby)) {
                                binding.tvHobby.setTextColor(getResources().getColor(R.color.color_2));
                                binding.tvHobby.setText(hobby);
                            } else {
                                binding.tvHobby.setTextColor(getResources().getColor(R.color.color_2));
                                binding.tvHobby.setText("없음");
                            }


                            //배우자 희망지역 세팅
                            if (!StringUtil.isNull(data.getHope_style1())) {
                                HopestyleData hopestyleData = new HopestyleData();
                                hopestyleData.setText(data.getHope_style1());
                                list.add(hopestyleData);
                            }
                            if (!StringUtil.isNull(data.getHope_style2())) {
                                HopestyleData hopestyleData = new HopestyleData();
                                hopestyleData.setText(data.getHope_style2());
                                list.add(hopestyleData);
                            }
                            if (!StringUtil.isNull(data.getHope_style3())) {
                                HopestyleData hopestyleData = new HopestyleData();
                                hopestyleData.setText(data.getHope_style3());
                                list.add(hopestyleData);
                            }
                            if (!StringUtil.isNull(data.getHope_style4())) {
                                HopestyleData hopestyleData = new HopestyleData();
                                hopestyleData.setText(data.getHope_style4());
                                list.add(hopestyleData);
                            }
                            if (!StringUtil.isNull(data.getHope_style5())) {
                                HopestyleData hopestyleData = new HopestyleData();
                                hopestyleData.setText(data.getHope_style5());
                                list.add(hopestyleData);
                            }
                            if (!StringUtil.isNull(data.getHope_style6())) {
                                HopestyleData hopestyleData = new HopestyleData();
                                hopestyleData.setText(data.getHope_style6());
                                list.add(hopestyleData);
                            }
                            if (!StringUtil.isNull(data.getHope_style7())) {
                                HopestyleData hopestyleData = new HopestyleData();
                                hopestyleData.setText(data.getHope_style7());
                                list.add(hopestyleData);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (adapter != null) {
                                        adapter.setList(list);
                                    }
                                }
                            });

                            //자기소개
                            if (!StringUtil.isNull(data.getP_introduce())) {
                                binding.tvIntroduce.setTextColor(getResources().getColor(R.color.color_2));
                                binding.tvIntroduce.setText(data.getP_introduce());
//                                if(data.getPint_ck().equalsIgnoreCase("Y")) {
//                                    binding.tvIntroduce.setText(data.getP_introduce());
//                                } else {
//                                    binding.tvIntroduce.setText(R.string.check_introduce);
//                                }
                            }

                            if (isReadOk) {

                                //배우자 희망지역
                                String hopeaddr;
                                if(!StringUtil.isNull(data.getHopeaddr())) {
                                    hopeaddr = data.getHopeaddr();
                                } else {
                                    hopeaddr = "전체";
                                }

                                if (StringUtil.isNull(hopeaddr)) {
                                    binding.tvHopeaddr.setText("없음");
                                } else {
                                    binding.tvHopeaddr.setText(hopeaddr);
                                }

                                //형제관계
                                String relation = "";
                                if (!StringUtil.isNull(data.getBrotherhood1())) {
                                    relation = data.getBrotherhood1();
                                }

                                if (!StringUtil.isNull(data.getBrotherhood2())) {
                                    relation += data.getBrotherhood2();
                                }

                                if (!StringUtil.isNull(data.getBrotherhood3())) {
                                    relation += data.getBrotherhood3();
                                }

                                if (StringUtil.isNull(relation)) {
                                    binding.tvRelation.setText("없음");
                                } else {
                                    binding.tvRelation.setText(relation);
                                }


                                //혈액형
                                if (!StringUtil.isNull(data.getBlood())) {
                                    binding.tvBloodtype.setText(data.getBlood());
                                } else {
                                    binding.tvBloodtype.setText("없음");
                                }
                                //종교
                                if (!StringUtil.isNull(data.getReligion())) {
                                    binding.tvReligion.setText(data.getReligion());
                                } else {
                                    binding.tvReligion.setText("없음");
                                }


                                //연봉
                                if (!StringUtil.isNull(data.getAnnual())) {
                                    binding.tvSalary.setTextColor(getResources().getColor(R.color.color_2));
                                    binding.tvSalary.setText(cpv.getReannual(data.getAnnual()));
                                } else {
                                    binding.tvSalary.setTextColor(getResources().getColor(R.color.color_2));
                                    binding.tvSalary.setText("없음");
                                }

                                //재산
                                if (!StringUtil.isNull(data.getProperty())) {
                                    binding.tvProperty.setTextColor(getResources().getColor(R.color.color_2));
                                    binding.tvProperty.setText(cpv.setProperty(data.getProperty()));
                                } else {
                                    binding.tvProperty.setTextColor(getResources().getColor(R.color.color_2));
                                    binding.tvProperty.setText("없음");
                                }

                                //학력
                                if (!StringUtil.isNull(data.getProperty())) {
                                    binding.tvEdu.setTextColor(getResources().getColor(R.color.color_2));
                                    binding.tvEdu.setText(data.getEducation());
                                } else {
                                    binding.tvEdu.setTextColor(getResources().getColor(R.color.color_2));
                                    binding.tvEdu.setText("없음");
                                }


                                //흡연여부
                                if (!StringUtil.isNull(data.getSmoke())) {
                                    binding.tvSmoke.setText(cpv.setMainSmoke(data.getSmoke()));
                                } else {
                                    binding.tvSmoke.setText("없음");
                                }

                                //음주량
                                if (!StringUtil.isNull(data.getDrink())) {
                                    binding.tvDrinking.setText(data.getDrink());
                                } else {
                                    binding.tvDrinking.setText("없음");
                                }

                                //자녀수
                                String[] child = data.getChildcnt().split(",");
                                if (child.length == 2) {
                                    binding.tvChild.setText("아들 : " + child[0] + ", 딸 : " + child[1]);
                                }

                                //자녀누가
                                binding.tvChildWho.setText(data.getChildwho());
                            }


                            //하단 이미지들
                            if (value.has("piimg")) {
                                JSONArray imgs = new JSONArray(value.getString("piimg"));
                                if (imgpath.size() > 0) {
                                    imgpath.clear();
                                }

                                pic_all_count += imgs.length();
                                pic_intro_count += imgs.length();


                                for (int k = 0; k < imgs.length(); k++) {
                                    JSONObject io = imgs.getJSONObject(k);
                                    ImagesData img = new ImagesData();

                                    img.setIdx(io.getString("idx"));
                                    img.setUidx(io.getString("u_idx"));
                                    img.setPiimg(io.getString("pi_img"));
                                    img.setPiimg_ck(io.getString("pi_img_ck"));
                                    img.setRegdate(io.getString("pi_regdate"));

                                    imgpath.add(img);
                                }

                                if (isReadOk) {
                                    imgAdapter.setPreadState(isReadOk);
                                }

                                if (pic_intro_count == 0) {
                                    binding.llNophotoArea.setVisibility(View.VISIBLE);
                                    binding.rcvImages.setVisibility(View.GONE);
                                } else {
                                    binding.llNophotoArea.setVisibility(View.GONE);
                                    binding.rcvImages.setVisibility(View.VISIBLE);
                                    imgAdapter.setList(imgpath);
                                }
                            }

                            //이미지 개수 세팅
                            binding.tvAllCount.setText(String.valueOf(pic_all_count));
                            binding.tvProfileimgCount.setText(String.valueOf(pic_profile_count));
                            binding.tvIntroimgCount.setText(String.valueOf(pic_intro_count));

//                            binding.scrollView.scrollTo(0, 0);
//                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    binding.scrollView.fullScroll(ScrollView.FOCUS_UP);
//                                }
//                            }, 50);

                        } else {
//                            Common.showToast(act, "접근할수 없는 회원입니다.");
                            Toast.makeText(ProfileDetailAct.this, jo.getString("value"), Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
//                        Toast.makeText(ProfileDetailAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    Toast.makeText(ProfileDetailAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        myprofile.addParams("puidx", UserPref.getUidx(this));
        myprofile.addParams("uidx", midx);
//        myprofile.addParams("yidx", midx);
        myprofile.execute(true, true);
    }

    private void getMyProfile() {
        ReqBasic myprofile = new ReqBasic(this, NetUrls.MYPROFILE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            JSONObject obj = new JSONObject(jo.getString("value"));
                            data = new ProfileData();

                            data.setAbroad(obj.getString("abroad"));
                            data.setAddr1(obj.getString("addr1"));
                            data.setAddr2(obj.getString("addr2"));
                            data.setAnnual(obj.getString("annual"));
                            data.setAnnual_img(obj.getString("annual_img"));
                            data.setAuth_num(obj.getString("auth_num"));
                            data.setAuth_ok(obj.getString("auth_ok"));
                            data.setBlood(obj.getString("blood"));
                            data.setBmonth(obj.getString("bmonth"));
                            data.setBrotherhood1(obj.getString("brotherhood1"));
                            data.setBrotherhood2(obj.getString("brotherhood2"));
                            data.setBrotherhood3(obj.getString("brotherhood3"));
                            data.setByear(obj.getString("byear"));
                            data.setCarinfo(obj.getString("carinfo"));
                            data.setCouple_type(obj.getString("couple_type"));
                            data.setDrink(obj.getString("drink"));
                            data.setDrink_detail(obj.getString("drink_detail"));
                            data.setEducation(obj.getString("education"));
                            data.setEducation_img(obj.getString("education_img"));
                            data.setFamily(obj.getString("family"));
                            data.setFamily_info(obj.getString("family_info"));
                            data.setFood1(obj.getString("food1"));
                            data.setFood2(obj.getString("food2"));
                            data.setFood3(obj.getString("food3"));
                            data.setFood4(obj.getString("food4"));
                            data.setFood5(obj.getString("food5"));
                            data.setFood6(obj.getString("food6"));
                            data.setFood7(obj.getString("food7"));
                            data.setGender(obj.getString("gender"));
                            data.setHealth(obj.getString("health"));
                            data.setHealth_detail(obj.getString("health_detail"));
                            data.setHeight(obj.getString("height"));
                            data.setHobby1(obj.getString("hobby1"));
                            data.setHobby2(obj.getString("hobby2"));
                            data.setHobby3(obj.getString("hobby3"));
                            data.setHobby4(obj.getString("hobby4"));
                            data.setHobby5(obj.getString("hobby5"));
                            data.setHometown(obj.getString("hometown"));
                            data.setHometown1(obj.getString("hometown1"));
                            data.setHopeaddr(obj.getString("hopeaddr"));
                            data.setHope_religion(obj.getString("hope_religion"));
                            data.setHope_style1(obj.getString("hope_style1"));
                            data.setHope_style2(obj.getString("hope_style2"));
                            data.setHope_style3(obj.getString("hope_style3"));
                            data.setHope_style4(obj.getString("hope_style4"));
                            data.setHope_style5(obj.getString("hope_style5"));
                            data.setHope_style6(obj.getString("hope_style6"));
                            data.setHope_style7(obj.getString("hope_style7"));
                            data.setId(obj.getString("id"));
                            data.setIdx(obj.getString("idx"));
                            data.setInterest_idxs(obj.getString("interest_idxs"));
                            data.setJob_detail(obj.getString("job_detail"));
                            data.setJob_group(obj.getString("job_group"));
                            data.setJob_img(obj.getString("job_img"));
                            data.setLastnameYN(obj.getString("lastnameYN"));
                            data.setLat(obj.getString("lat"));
                            data.setLoginYN(obj.getString("loginYN"));
                            data.setLon(obj.getString("lon"));
                            data.setMarried_paper_img(obj.getString("married_paper_img"));
                            data.setM_fcm_token(obj.getString("m_fcm_token"));
                            data.setName(obj.getString("name"));
                            data.setFamilyname(obj.getString("familyname"));
                            data.setNick(obj.getString("nick"));
                            data.setPassionstyle1(obj.getString("passionstyle1"));
                            data.setPassionstyle2(obj.getString("passionstyle2"));
                            data.setPersonality1(obj.getString("personality1"));
                            data.setPersonality2(obj.getString("personality2"));
                            data.setPersonality3(obj.getString("personality3"));
                            data.setPimg(obj.getString("pimg"));
                            data.setPimg_ck(obj.getString("pimg_ck"));
                            data.setPint_ck(obj.getString("pint_ck"));
                            data.setCharacter_int(Common.getCharacterDrawable(obj.getString("character"), obj.getString("gender")));
                            data.setProperty(obj.getString("property"));
                            data.setPw(obj.getString("pw"));
                            data.setP_gif1(obj.getString("p_gif1"));
                            data.setP_gif2(obj.getString("p_gif2"));
                            data.setP_gif3(obj.getString("p_gif3"));
                            data.setP_introduce(obj.getString("p_introduce"));
                            data.setP_movie1(obj.getString("p_movie1"));
                            data.setP_movie2(obj.getString("p_movie2"));
                            data.setP_movie3(obj.getString("p_movie3"));
                            data.setP_movthumb1(obj.getString("p_movthumb1"));
                            data.setP_movthumb2(obj.getString("p_movthumb2"));
                            data.setP_movthumb3(obj.getString("p_movthumb3"));
                            data.setP_wave1(obj.getString("p_wave1"));
                            data.setP_wave2(obj.getString("p_wave2"));
                            data.setP_wave3(obj.getString("p_wave3"));
                            data.setReference(obj.getString("reference"));
                            data.setRegdate(obj.getString("regdate"));
                            data.setReligion(obj.getString("religion"));
                            data.setRemarried(obj.getString("remarried"));
                            data.setSelf_score(obj.getString("self_score"));
                            data.setSmoke(obj.getString("smoke"));
                            data.setSmoke_detail(obj.getString("smoke_detail"));
                            data.setStyle(obj.getString("style"));
                            data.setType(obj.getString("type"));
                            data.setU_idx(obj.getString("u_idx"));
                            data.setWeight(obj.getString("weight"));
                            data.setWhen_marry(obj.getString("when_marry"));
                            data.setZodiac(obj.getString("zodiac"));
                            data.setChildcnt(obj.getString("childcnt"));
                            data.setChildwho(obj.getString("childwho"));

                            int pic_all_count = 0;
                            int pic_profile_count = 0;
                            int pic_intro_count = 0;


                            //프로필이미지
                            if (!StringUtil.isNull(data.getPimg())) {
                                if (data.getPimg_ck().equalsIgnoreCase("Y")) {
                                    binding.ivProfimg.setVisibility(View.VISIBLE);
                                    binding.ivNoprofimg.setVisibility(View.GONE);
                                    Glide.with(ProfileDetailAct.this)
                                            .load(data.getPimg())
                                            .centerCrop()
                                            .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(15)))
                                            .into(binding.ivProfimg);
                                } else {
                                    binding.ivProfimg.setVisibility(View.GONE);
                                    binding.ivNoprofimg.setVisibility(View.VISIBLE);
                                    Glide.with(ProfileDetailAct.this)
                                            .load(data.getCharacter_int())
                                            .centerCrop()
                                            .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(15)))
                                            .into(binding.ivNoprofimg);
                                }

                                ++pic_all_count;
                                ++pic_profile_count;
                            } else {
                                binding.ivProfimg.setVisibility(View.GONE);
                                binding.ivNoprofimg.setVisibility(View.VISIBLE);
                                Glide.with(ProfileDetailAct.this)
                                        .load(data.getCharacter_int())
                                        .centerCrop()
                                        .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(15)))
                                        .into(binding.ivNoprofimg);
                            }

                            binding.tvTitlenick.setText(data.getNick());
                            binding.tvNick.setText(data.getNick());

                            binding.tvIntroduceTop.setText(data.getP_introduce());

                            otherNick = data.getNick();

                            if (data.getCouple_type().equalsIgnoreCase("marry")) {
                                binding.tvMembertype.setText("결혼");
                                binding.tvTopmemtype.setText("결혼");
                                binding.tvTopmemtype.setBackgroundResource(R.drawable.marriage_bg);
                                binding.tvMembertype.setTextColor(ContextCompat.getColor(ProfileDetailAct.this, R.color.man_color));
                            } else if (data.getCouple_type().equalsIgnoreCase("remarry")) {
                                binding.tvMembertype.setTextColor(ContextCompat.getColor(ProfileDetailAct.this, R.color.women_color));
                                binding.tvMembertype.setText("재혼");
                                binding.tvTopmemtype.setText("재혼");
                                binding.tvTopmemtype.setBackgroundResource(R.drawable.remarriage_bg);
                            } else if (data.getCouple_type().equalsIgnoreCase("friend")) {
                                binding.tvMembertype.setTextColor(ContextCompat.getColor(ProfileDetailAct.this, R.color.color_adapter_friend));
                                binding.tvMembertype.setText("재혼");
                                binding.tvTopmemtype.setText("재혼");
                                binding.tvTopmemtype.setBackgroundResource(R.drawable.remarriage_bg);
                            }

                            binding.tvTopnick.setText(data.getNick());

                            binding.tvToploc.setText(data.getAddr1() + " " + data.getAddr2());

                            if (data.getGender().equalsIgnoreCase("male")) {
                                binding.tvGender.setText("남성");
                                binding.tvGender.setTextColor(getResources().getColor(R.color.man_color));
                            } else {
                                binding.tvGender.setText("여성");
                                binding.tvGender.setTextColor(getResources().getColor(R.color.women_color));
                            }

                            if (!StringUtil.isNull(data.getFamilyname())) {
                                binding.tvName.setTextColor(getResources().getColor(R.color.color_2));
                                if (data.getLastnameYN().equalsIgnoreCase("Y")) {
                                    binding.tvName.setText(data.getFamilyname() + "○○");
                                } else {
                                    binding.tvName.setText(data.getFamilyname() + data.getName());
                                }
                            }

//                            String ageinfo = StringUtil.calcAge(data.getByear()) + "세(" + data.getBmonth() + "월생/" + data.getZodiac() + "띠)";
                            String ageinfo = StringUtil.calcAge(data.getByear()) + "세(" + StringUtil.calcZodiac(Integer.parseInt(data.getByear())) + "띠)";
                            binding.tvAge.setText(ageinfo);

                            //키
                            if (!StringUtil.isNull(data.getHeight())) {
                                binding.tvHeight.setTextColor(getResources().getColor(R.color.color_2));
                                binding.tvHeight.setText(data.getHeight());
                            }
                            //스타일
                            if (!StringUtil.isNull(data.getStyle())) {
                                binding.tvStyle.setTextColor(getResources().getColor(R.color.color_2));
                                binding.tvStyle.setText(data.getStyle());
                            }


                            //성격
                            String personal = "";
                            if (!StringUtil.isNull(data.getPersonality1())) {
                                personal = data.getPersonality1();
                            }
                            if (!StringUtil.isNull(data.getPersonality2())) {
                                personal += "," + data.getPersonality2();
                            }
                            if (!StringUtil.isNull(data.getPersonality3())) {
                                personal += "," + data.getPersonality3();
                            }
                            binding.tvCharater.setText(personal);


                            //직업
                            if (!StringUtil.isNull(data.getJob_group())) {
                                if (data.getJob_group().contains("직접입력")) {
                                    binding.tvJob.setText(data.getJob_group().replace("직접입력", ""));
                                } else {
                                    binding.tvJob.setText(data.getJob_group());
                                }
                            } else {
                                binding.tvJob.setText("없음");
                            }

                            //취미
                            String hobby = "";
                            if (!StringUtil.isNull(data.getHobby1())) {
                                hobby = data.getHobby1();
                            }
                            if (!StringUtil.isNull(data.getHobby2())) {
                                hobby += "," + data.getHobby2();
                            }
                            if (!StringUtil.isNull(data.getHobby3())) {
                                hobby += "," + data.getHobby3();
                            }
                            if (!StringUtil.isNull(data.getHobby4())) {
                                hobby += "," + data.getHobby4();
                            }
                            if (!StringUtil.isNull(data.getHobby5())) {
                                hobby += "," + data.getHobby5();
                            }
                            if (!StringUtil.isNull(hobby)) {
                                binding.tvHobby.setTextColor(getResources().getColor(R.color.color_2));
                                binding.tvHobby.setText(hobby);
                            }


                            //배우자 희망지역 세팅
                            if (!StringUtil.isNull(data.getHope_style1())) {
                                HopestyleData hopestyleData = new HopestyleData();
                                hopestyleData.setText(data.getHope_style1());
                                list.add(hopestyleData);
                            }
                            if (!StringUtil.isNull(data.getHope_style2())) {
                                HopestyleData hopestyleData = new HopestyleData();
                                hopestyleData.setText(data.getHope_style2());
                                list.add(hopestyleData);
                            }
                            if (!StringUtil.isNull(data.getHope_style3())) {
                                HopestyleData hopestyleData = new HopestyleData();
                                hopestyleData.setText(data.getHope_style3());
                                list.add(hopestyleData);
                            }
                            if (!StringUtil.isNull(data.getHope_style4())) {
                                HopestyleData hopestyleData = new HopestyleData();
                                hopestyleData.setText(data.getHope_style4());
                                list.add(hopestyleData);
                            }
                            if (!StringUtil.isNull(data.getHope_style5())) {
                                HopestyleData hopestyleData = new HopestyleData();
                                hopestyleData.setText(data.getHope_style5());
                                list.add(hopestyleData);
                            }
                            if (!StringUtil.isNull(data.getHope_style6())) {
                                HopestyleData hopestyleData = new HopestyleData();
                                hopestyleData.setText(data.getHope_style6());
                                list.add(hopestyleData);
                            }
                            if (!StringUtil.isNull(data.getHope_style7())) {
                                HopestyleData hopestyleData = new HopestyleData();
                                hopestyleData.setText(data.getHope_style7());
                                list.add(hopestyleData);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (adapter != null) {
                                        adapter.setList(list);
                                    }
                                }
                            });

                            //자기소개
                            if (!StringUtil.isNull(data.getP_introduce())) {
                                binding.tvIntroduce.setTextColor(getResources().getColor(R.color.color_2));
                                binding.tvIntroduce.setText(data.getP_introduce());
//                                if(data.getPint_ck().equalsIgnoreCase("Y")) {
//                                    binding.tvIntroduce.setText(data.getP_introduce());
//                                } else {
//                                    binding.tvIntroduce.setText(R.string.check_introduce);
//                                }
                            }


                            if (isReadOk) {

                                //배우자 희망지역
                                String hopeaddr;
                                if(!StringUtil.isNull(data.getHopeaddr())) {
                                    hopeaddr = data.getHopeaddr();
                                } else {
                                    hopeaddr = "전체";
                                }

                                if (StringUtil.isNull(hopeaddr)) {
                                    binding.tvHopeaddr.setText("없음");
                                } else {
                                    binding.tvHopeaddr.setText(hopeaddr);
                                }

                                //형제관계
                                String relation = "";
                                if (!StringUtil.isNull(data.getBrotherhood1())) {
                                    relation = data.getBrotherhood1();
                                }

                                if (!StringUtil.isNull(data.getBrotherhood2())) {
                                    relation += data.getBrotherhood2();
                                }

                                if (!StringUtil.isNull(data.getBrotherhood3())) {
                                    relation += data.getBrotherhood3();
                                }
                                binding.tvRelation.setText(relation);

                                //혈액형
                                if (!StringUtil.isNull(data.getBlood())) {
                                    binding.tvBloodtype.setText(data.getBlood());
                                } else {
                                    binding.tvBloodtype.setText("없음");
                                }
                                //종교
                                if (!StringUtil.isNull(data.getReligion())) {
                                    binding.tvReligion.setText(data.getReligion());
                                } else {
                                    binding.tvReligion.setText("없음");
                                }

                                //연봉
                                if (!StringUtil.isNull(data.getAnnual())) {
                                    binding.tvSalary.setTextColor(getResources().getColor(R.color.color_2));
                                    binding.tvSalary.setText(cpv.getReannual(data.getAnnual()));
                                }

                                //재산
                                if (!StringUtil.isNull(data.getProperty())) {
                                    binding.tvProperty.setTextColor(getResources().getColor(R.color.color_2));
                                    binding.tvProperty.setText(cpv.setProperty(data.getProperty()));
                                }

                                //학력
                                if (!StringUtil.isNull(data.getProperty())) {
                                    binding.tvEdu.setTextColor(getResources().getColor(R.color.color_2));
                                    binding.tvEdu.setText(data.getEducation());
                                }


                                //흡연여부
                                if (!StringUtil.isNull(data.getSmoke())) {
                                    if (!StringUtil.isNull(data.getSmoke_detail())) {
                                        if (data.getSmoke().equalsIgnoreCase("smoking")) {
                                            binding.tvSmoke.setText(cpv.setMainSmoke(data.getSmoke()) + " " + cpv.setSmoketype2(data.getSmoke_detail()));
                                        } else {
                                            binding.tvSmoke.setText(cpv.setMainSmoke(data.getSmoke()) + " " + cpv.setSmoketype1(data.getSmoke_detail()));
                                        }
                                    } else {
                                        binding.tvSmoke.setText(cpv.setMainSmoke(data.getSmoke()));
                                    }
                                }

                                //음주량
                                String drink = "";
                                if (!StringUtil.isNull(data.getDrink())) {
                                    drink = data.getDrink();
                                }
                                binding.tvDrinking.setText(drink);

                                //자녀수
                                String[] child = data.getChildcnt().split(",");
                                if (child.length == 2) {
                                    binding.tvChild.setText("아들 : " + child[0] + ", 딸 : " + child[1]);
                                }

                                //자녀누가
                                binding.tvChildWho.setText(data.getChildwho());

                            }

                            //하단 이미지들
                            if (obj.has("piimg")) {
                                JSONArray imgs = new JSONArray(obj.getString("piimg"));
                                if (imgpath.size() > 0) {
                                    imgpath.clear();
                                }

                                pic_all_count += imgs.length();
                                pic_intro_count += imgs.length();


                                for (int k = 0; k < imgs.length(); k++) {
                                    JSONObject io = imgs.getJSONObject(k);
                                    ImagesData img = new ImagesData();

                                    img.setIdx(io.getString("idx"));
                                    img.setUidx(io.getString("u_idx"));
                                    img.setPiimg(io.getString("pi_img"));
                                    img.setPiimg_ck(io.getString("pi_img_ck"));
                                    img.setRegdate(io.getString("pi_regdate"));

                                    imgpath.add(img);
                                }

                                if (isReadOk) {
                                    imgAdapter.setPreadState(isReadOk);
                                }

                                if (pic_intro_count == 0) {
                                    binding.llNophotoArea.setVisibility(View.VISIBLE);
                                    binding.rcvImages.setVisibility(View.GONE);
                                } else {
                                    binding.llNophotoArea.setVisibility(View.GONE);
                                    binding.rcvImages.setVisibility(View.VISIBLE);
                                    imgAdapter.setList(imgpath);
                                }
                            }

                            //이미지 개수 세팅
                            binding.tvAllCount.setText(String.valueOf(pic_all_count));
                            binding.tvProfileimgCount.setText(String.valueOf(pic_profile_count));
                            binding.tvIntroimgCount.setText(String.valueOf(pic_intro_count));

//                            binding.scrollView.scrollTo(0, 0);
//                            binding.scrollView.fullScroll(ScrollView.FOCUS_UP);
                        } else {
//                            Common.showToast(act, "접근할수 없는 회원입니다.");
                            Toast.makeText(ProfileDetailAct.this, jo.getString("value"), Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
//                        Toast.makeText(ProfileDetailAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    Toast.makeText(ProfileDetailAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        myprofile.addParams("uidx", UserPref.getUidx(act));
        myprofile.addParams("yidx", midx);
        myprofile.execute(true, true);
    }

    private void declareMem() {
        ReqBasic declare = new ReqBasic(this, NetUrls.DECLARE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            Toast.makeText(ProfileDetailAct.this, "신고 되었습니다", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileDetailAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ProfileDetailAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(ProfileDetailAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        declare.addParams("uidx", UserPref.getUidx(this));
        declare.addParams("dcidx", midx);
        declare.execute(true, true);
    }

    private void showDlgConfirm(final String type) {
        LayoutInflater dialog = LayoutInflater.from(this);
        View dialogLayout = dialog.inflate(R.layout.dlg_confirm, null);
        final Dialog menuDlg = new Dialog(this);

        menuDlg.setContentView(dialogLayout);

        menuDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        menuDlg.show();

        TextView title = (TextView) dialogLayout.findViewById(R.id.tv_dlgtitle);
        TextView contents = (TextView) dialogLayout.findViewById(R.id.tv_contents);
        TextView btn_cancel = (TextView) dialogLayout.findViewById(R.id.btn_cancel);
        TextView btn_ok = (TextView) dialogLayout.findViewById(R.id.btn_ok);

        if (type.equalsIgnoreCase("decl")) {
            title.setText("신고하기");
            contents.setText("신고 하시겠습니까?");
        } else if (type.equalsIgnoreCase("block")) {
            title.setText("차단하기");
            contents.setText("차단 하시겠습니까?");
        }

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equalsIgnoreCase("block")) {
                    reqBlock();
                }

                if (menuDlg.isShowing()) {
                    menuDlg.dismiss();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuDlg.isShowing()) {
                    menuDlg.dismiss();
                }
            }
        });
    }

    // 차단하기
    private void reqBlock() {
        ReqBasic block = new ReqBasic(this, NetUrls.SETBLOCK) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            Toast.makeText(ProfileDetailAct.this, "차단멤버 리스트에 추가됐습니다", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ProfileDetailAct.this, MainActivity.class));
                            finishAffinity();
                        } else {
                            Toast.makeText(ProfileDetailAct.this, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ProfileDetailAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileDetailAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        block.addParams("uidx", UserPref.getUidx(this));
        block.addParams("tidx", midx);
        block.execute(true, true);
    }

    private void reqInterest(String tidx) {
        ReqBasic reqInter = new ReqBasic(this, NetUrls.SETINTER) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            Toast.makeText(ProfileDetailAct.this, "찜 했습니다.", Toast.LENGTH_SHORT).show();
                            binding.llInterest.setSelected(true);

                            if (StringUtil.isNull(UserPref.getInterIdxs(ProfileDetailAct.this))) {
                                UserPref.setInterIdxs(ProfileDetailAct.this, midx);
                            } else {
                                UserPref.setInterIdxs(ProfileDetailAct.this, UserPref.getInterIdxs(ProfileDetailAct.this) + "," + midx);
                            }

                        } else {
                            Toast.makeText(ProfileDetailAct.this, "이미 " + data.getNick() + "님을 찜하였습니다", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ProfileDetailAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileDetailAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }

        };

        reqInter.addParams("uidx", UserPref.getUidx(this));
        reqInter.addParams("tidx", tidx);
        reqInter.addParams("stype", "Y");
        reqInter.execute(true, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.ll_interest:
                if (UserPref.getGender(ProfileDetailAct.this).equalsIgnoreCase(data.getGender())) {
                    Toast.makeText(ProfileDetailAct.this, "동성간에는 찜을 할 수 없습니다", Toast.LENGTH_SHORT).show();
                } else {
                    reqInterest(midx);
                }
                break;
            case R.id.tv_call:
            case R.id.ll_chat:
                reqChat();
                break;

            case R.id.tv_report:
                Intent intent = new Intent(act, QnaReportRegAct.class);
                intent.putExtra("yidx", midx);
                startActivity(intent);
                break;

            case R.id.iv_profimg:
                if (!StringUtil.isNull(data.getPimg())) {
                    Intent imgIntent = new Intent(this, MoreimgActivity.class);
                    imgIntent.putExtra("imgurl", data.getPimg());
                    startActivity(imgIntent);
                } else {
                    Toast.makeText(ProfileDetailAct.this, "프로필 사진이 없습니다.", Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.btn_read_01:
            case R.id.btn_read_02:
            case R.id.btn_read_03:
            case R.id.btn_read_04:
            case R.id.btn_read_05:
            case R.id.btn_read_06:
            case R.id.btn_read_07:
            case R.id.btn_read_08:
            case R.id.btn_read_09:
            case R.id.btn_read_10:
                showWarning();
        }
    }
}
