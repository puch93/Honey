package com.match.honey.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.match.honey.R;
import com.match.honey.adapters.list.HopestyleJoinAdapter;
import com.match.honey.adapters.list.ImagelistAdapter;
import com.match.honey.databinding.ActivityMyprofModifyBinding;
import com.match.honey.listDatas.HopestyleData;
import com.match.honey.listDatas.ImagesData;
import com.match.honey.listDatas.ProfileData;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.ChangeProfVal;
import com.match.honey.utils.Common;
import com.match.honey.utils.DefaultValue;
import com.match.honey.utils.ItemOffsetDecorationJoin;
import com.match.honey.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyprofileModifyAct extends Activity implements View.OnClickListener {
    ActivityMyprofModifyBinding binding;

    Geocoder geocoder;
    ChangeProfVal cpv;
    ProfileData pdata;

    List<Uri> mSelected;

    ArrayList<ImagesData> imgpath = new ArrayList<>();
    String chGender;
    ImagelistAdapter imgAdapter;
    public static Activity act;

    HopestyleJoinAdapter adapter;
    ArrayList<HopestyleData> list = new ArrayList<>();

    private static final String TAG = "TEST_HOME";
    private static final int BASIC = 1001;
    private static final int PURCHASE = 1002;

    boolean isMen = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_myprof_modify);
        act = this;

        geocoder = new Geocoder(this);
        cpv = new ChangeProfVal();

        setClickListener();
        getMyProfile();

        if(UserPref.getGender(act).equalsIgnoreCase("male")) {
            isMen = true;
            binding.llMalePurchase.setVisibility(View.VISIBLE);
        } else {
            isMen = false;
            binding.llMalePurchase.setVisibility(View.GONE);
        }

        //배우자 희망지역 세팅
        binding.rcvJoinHoplestyle.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new HopestyleJoinAdapter(this, list);
        binding.rcvJoinHoplestyle.setAdapter(adapter);
        binding.rcvJoinHoplestyle.addItemDecoration(new ItemOffsetDecorationJoin(this,
                getResources().getDimensionPixelSize(R.dimen.dimen_hope01),
                getResources().getDimensionPixelSize(R.dimen.dimen_hope02),
                getResources().getDimensionPixelSize(R.dimen.dimen_10)));
    }

    private void setClickListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.ivProfimg.setClipToOutline(true);
        }

        binding.flBack.setOnClickListener(this);
        binding.btnRegprofile.setOnClickListener(this);
        if(UserPref.getGender(act).equalsIgnoreCase("male")) {
            binding.btnCharge.setOnClickListener(this);
        }

        binding.tvFamilyFemale.setOnClickListener(this);
        binding.tvFamilyMale.setOnClickListener(this);
        binding.tvFamilyOrder.setOnClickListener(this);

        binding.tvJob.setOnClickListener(this);
        binding.flJobClose.setOnClickListener(this);
        binding.llBasicinfoModify.setOnClickListener(this);

        binding.tvAge.setOnClickListener(this);
        binding.tvBloodtype.setOnClickListener(this);
        binding.tvBoy.setOnClickListener(this);
        binding.tvGirl.setOnClickListener(this);
        binding.tvEdu.setOnClickListener(this);
        binding.tvHeight.setOnClickListener(this);
        binding.tvHobby.setOnClickListener(this);
        binding.tvPersonal.setOnClickListener(this);
        binding.tvMaindrinking.setOnClickListener(this);
        binding.tvProperty.setOnClickListener(this);
        binding.tvReligion.setOnClickListener(this);
        binding.tvSalary.setOnClickListener(this);
        binding.tvSmoke.setOnClickListener(this);
        binding.tvStyle.setOnClickListener(this);
    }


    private void setItem() {

        binding.tvFamilyMale.setText(getResources().getStringArray(R.array.brotherhood1)[0]);
        binding.tvFamilyFemale.setText(getResources().getStringArray(R.array.brotherhood2)[0]);
        binding.tvFamilyOrder.setText(getResources().getStringArray(R.array.brotherhood3)[0]);
        binding.tvSalary.setText(getResources().getStringArray(R.array.annual)[0]);
        binding.tvEdu.setText(getResources().getStringArray(R.array.edu)[0]);
        binding.tvStyle.setText(getResources().getStringArray(R.array.style)[0]);
        binding.tvHeight.setText(getResources().getStringArray(R.array.height)[0]);
        binding.tvSmoke.setText(getResources().getStringArray(R.array.smoke)[0]);
        binding.tvMaindrinking.setText(getResources().getStringArray(R.array.drink)[0]);
        binding.tvBloodtype.setText(getResources().getStringArray(R.array.blood)[0]);
        binding.tvReligion.setText(getResources().getStringArray(R.array.religion)[0]);
        binding.tvPersonal.setText(getResources().getStringArray(R.array.personal)[0]);
        binding.tvHobby.setText(getResources().getStringArray(R.array.hobby)[0]);

        binding.tvProperty.setText(getResources().getStringArray(R.array.property)[0]);

        binding.tvBoy.setText(getResources().getStringArray(R.array.children)[0]);
        binding.tvGirl.setText(getResources().getStringArray(R.array.children)[0]);

    }

    private void setImages() {
        ReqBasic setImages = new ReqBasic(this, NetUrls.REGIMAGES) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            JSONArray imgs = new JSONArray(jo.getString("imglist"));

                            for (int i = 0; i < imgs.length(); i++) {
                                JSONObject io = imgs.getJSONObject(i);
                                ImagesData img = new ImagesData();

                                img.setIdx(io.getString("idx"));
                                img.setUidx(io.getString("u_idx"));
                                img.setPiimg(io.getString("pi_img"));
                                img.setRegdate(io.getString("pi_regdate"));

                                imgpath.add(img);
                            }
                            imgAdapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(MyprofileModifyAct.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        };

        setImages.addParams("uidx", UserPref.getUidx(this));
        for (int i = 0; i < mSelected.size(); i++) {
            File img = new File(StringUtil.getPath(this, mSelected.get(i)));
            setImages.addFileParams("piimg[]", img);
        }
        setImages.execute(true, true);
    }

    public String calDateBetweenAandB(String cmpDate) {
//        SimpleDateFormat formatNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String currentDate = formatNow.format(System.currentTimeMillis());

        try { // String Type을 Date Type으로 캐스팅하면서 생기는 예외로 인해 여기서 예외처리 해주지 않으면 컴파일러에서 에러가 발생해서 컴파일을 할 수 없다.
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // date1, date2 두 날짜를 parse()를 통해 Date형으로 변환.
            Date FirstDate = format.parse(cmpDate);
//            Date SecondDate = format.parse(String.valueOf(System.currentTimeMillis()));
            Date SecondDate = new Date();

            // Date로 변환된 두 날짜를 계산한 뒤 그 리턴값으로 long type 변수를 초기화 하고 있다.
            // 연산결과 -950400000. long type 으로 return 된다.
            long calDate = FirstDate.getTime() - SecondDate.getTime();

            // Date.getTime() 은 해당날짜를 기준으로1970년 00:00:00 부터 몇 초가 흘렀는지를 반환해준다.
            // 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
            long calDateDays = calDate / (24 * 60 * 60 * 1000);

            calDateDays = Math.abs(calDateDays);

            return String.valueOf(calDateDays);
        } catch (ParseException e) {
            // 예외 처리
            return "0";
        }
    }

    private void getMyProfile() {
        ReqBasic myprofile = new ReqBasic(this, NetUrls.MYPROFILE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "ModifyAct List Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            JSONObject obj = new JSONObject(jo.getString("value"));
                            pdata = new ProfileData();

                            if(UserPref.getGender(act).equalsIgnoreCase("male")) {
                                // 메시지이용권 체크
                                if (obj.getString("message_expiredate").equalsIgnoreCase("0000-00-00 00:00:00")) {
                                    binding.tvMessageRemain.setText("0");
                                } else {
                                    binding.tvMessageRemain.setText(StringUtil.calDateBetweenAandBSub(obj.getString("message_expiredate")));
                                }

                                // 관심있어요 발송권 체크
                                binding.tvInterestRemain.setText(obj.getString("interest_sendcnt"));

                                // 열람권 남은 일수
                                binding.tvReadRemain.setText(obj.getString("profile_viewcnt"));
                            }

                            pdata.setAbroad(obj.getString("abroad"));
                            pdata.setAddr1(obj.getString("addr1"));
                            pdata.setAddr2(obj.getString("addr2"));
                            pdata.setAnnual(obj.getString("annual"));
                            pdata.setAnnual_img(obj.getString("annual_img"));
                            pdata.setAuth_num(obj.getString("auth_num"));
                            pdata.setAuth_ok(obj.getString("auth_ok"));
                            pdata.setBlood(obj.getString("blood"));
                            pdata.setBmonth(obj.getString("bmonth"));
                            pdata.setBrotherhood1(obj.getString("brotherhood1"));
                            pdata.setBrotherhood2(obj.getString("brotherhood2"));
                            pdata.setBrotherhood3(obj.getString("brotherhood3"));
                            pdata.setByear(obj.getString("byear"));
                            pdata.setCarinfo(obj.getString("carinfo"));
                            pdata.setCouple_type(obj.getString("couple_type"));
                            pdata.setDrink(obj.getString("drink"));
                            pdata.setDrink_detail(obj.getString("drink_detail"));
                            pdata.setEducation(obj.getString("education"));
                            pdata.setEducation_img(obj.getString("education_img"));
                            pdata.setFamily(obj.getString("family"));
                            pdata.setFamily_info(obj.getString("family_info"));
                            pdata.setFood1(obj.getString("food1"));
                            pdata.setFood2(obj.getString("food2"));
                            pdata.setFood3(obj.getString("food3"));
                            pdata.setFood4(obj.getString("food4"));
                            pdata.setFood5(obj.getString("food5"));
                            pdata.setFood6(obj.getString("food6"));
                            pdata.setFood7(obj.getString("food7"));
                            pdata.setGender(obj.getString("gender"));
                            pdata.setHealth(obj.getString("health"));
                            pdata.setHealth_detail(obj.getString("health_detail"));
                            pdata.setHeight(obj.getString("height"));
                            pdata.setHobby1(obj.getString("hobby1"));
                            pdata.setHobby2(obj.getString("hobby2"));
                            pdata.setHobby3(obj.getString("hobby3"));
                            pdata.setHobby4(obj.getString("hobby4"));
                            pdata.setHobby5(obj.getString("hobby5"));
                            pdata.setHometown(obj.getString("hometown"));
                            pdata.setHometown1(obj.getString("hometown1"));
                            pdata.setHopeaddr(obj.getString("hopeaddr"));
                            pdata.setHope_religion(obj.getString("hope_religion"));
                            pdata.setU_cell_num(obj.getString("u_cell_num"));
                            pdata.setChildcnt(obj.getString("childcnt"));

                            pdata.setHope_style1(obj.getString("hope_style1"));
                            pdata.setHope_style2(obj.getString("hope_style2"));
                            pdata.setHope_style3(obj.getString("hope_style3"));
                            pdata.setHope_style4(obj.getString("hope_style4"));
                            pdata.setHope_style5(obj.getString("hope_style5"));
                            pdata.setHope_style6(obj.getString("hope_style6"));
                            pdata.setHope_style7(obj.getString("hope_style7"));
                            pdata.setCharacter(obj.getString("character"));


                            if (!StringUtil.isNull(pdata.getCouple_type())) {
                                if (pdata.getCouple_type().equalsIgnoreCase("marry")) {
                                    binding.tvTopmemtype.setText("결혼");
                                    binding.tvTopmemtype.setBackgroundResource(R.drawable.marriage_bg);
                                } else if (pdata.getCouple_type().equalsIgnoreCase("remarry")) {
                                    binding.tvTopmemtype.setText("재혼");
                                    binding.tvTopmemtype.setBackgroundResource(R.drawable.remarriage_bg);
                                } else if(pdata.getCouple_type().equalsIgnoreCase("friend")) {
                                    binding.tvTopmemtype.setText("재혼");
                                    binding.tvTopmemtype.setBackgroundResource(R.drawable.remarriage_bg);
                                }
                            }

                            binding.tvIntroduce.setText(pdata.getP_introduce());

                            list = new ArrayList<>();

                            //배우자 희망지역 세팅
                            if (!StringUtil.isNull(pdata.getHope_style1())) {
                                HopestyleData hopestyleData = new HopestyleData();
                                hopestyleData.setText(pdata.getHope_style1());
                                list.add(hopestyleData);
                            }
                            if (!StringUtil.isNull(pdata.getHope_style2())) {
                                HopestyleData hopestyleData = new HopestyleData();
                                hopestyleData.setText(pdata.getHope_style2());
                                list.add(hopestyleData);
                            }
                            if (!StringUtil.isNull(pdata.getHope_style3())) {
                                HopestyleData hopestyleData = new HopestyleData();
                                hopestyleData.setText(pdata.getHope_style3());
                                list.add(hopestyleData);
                            }
                            if (!StringUtil.isNull(pdata.getHope_style4())) {
                                HopestyleData hopestyleData = new HopestyleData();
                                hopestyleData.setText(pdata.getHope_style4());
                                list.add(hopestyleData);
                            }
                            if (!StringUtil.isNull(pdata.getHope_style5())) {
                                HopestyleData hopestyleData = new HopestyleData();
                                hopestyleData.setText(pdata.getHope_style5());
                                list.add(hopestyleData);
                            }
                            if (!StringUtil.isNull(pdata.getHope_style6())) {
                                HopestyleData hopestyleData = new HopestyleData();
                                hopestyleData.setText(pdata.getHope_style6());
                                list.add(hopestyleData);
                            }
                            if (!StringUtil.isNull(pdata.getHope_style7())) {
                                HopestyleData hopestyleData = new HopestyleData();
                                hopestyleData.setText(pdata.getHope_style7());
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


                            pdata.setId(obj.getString("id"));
                            pdata.setIdx(obj.getString("idx"));
                            pdata.setInterest_idxs(obj.getString("interest_idxs"));
                            pdata.setJob_detail(obj.getString("job_detail"));
                            pdata.setJob_group(obj.getString("job_group"));
                            pdata.setJob_img(obj.getString("job_img"));
                            pdata.setLastnameYN(obj.getString("lastnameYN"));
                            pdata.setLat(obj.getString("lat"));
                            pdata.setLoginYN(obj.getString("loginYN"));
                            pdata.setLon(obj.getString("lon"));
                            pdata.setMarried_paper_img(obj.getString("married_paper_img"));
                            pdata.setM_fcm_token(obj.getString("m_fcm_token"));
                            pdata.setFamilyname(obj.getString("familyname"));
                            pdata.setName(obj.getString("name"));
                            pdata.setNick(obj.getString("nick"));
                            pdata.setPassionstyle1(obj.getString("passionstyle1"));
                            pdata.setPassionstyle2(obj.getString("passionstyle2"));
                            pdata.setPersonality1(obj.getString("personality1"));
                            pdata.setPersonality2(obj.getString("personality2"));
                            pdata.setPersonality3(obj.getString("personality3"));
                            pdata.setPimg(obj.getString("pimg"));
                            pdata.setPimg_ck(obj.getString("pimg_ck"));
                            pdata.setCharacter_int(Common.getCharacterDrawable(obj.getString("character"), obj.getString("gender")));
                            pdata.setProperty(obj.getString("property"));
                            pdata.setPw(obj.getString("pw"));
                            pdata.setP_gif1(obj.getString("p_gif1"));
                            pdata.setP_gif2(obj.getString("p_gif2"));
                            pdata.setP_gif3(obj.getString("p_gif3"));
                            pdata.setP_introduce(obj.getString("p_introduce"));
                            pdata.setP_movie1(obj.getString("p_movie1"));
                            pdata.setP_movie2(obj.getString("p_movie2"));
                            pdata.setP_movie3(obj.getString("p_movie3"));
                            pdata.setP_movthumb1(obj.getString("p_movthumb1"));
                            pdata.setP_movthumb2(obj.getString("p_movthumb2"));
                            pdata.setP_movthumb3(obj.getString("p_movthumb3"));
                            pdata.setP_wave1(obj.getString("p_wave1"));
                            pdata.setP_wave2(obj.getString("p_wave2"));
                            pdata.setP_wave3(obj.getString("p_wave3"));
                            pdata.setReference(obj.getString("reference"));
                            pdata.setRegdate(obj.getString("regdate"));
                            pdata.setReligion(obj.getString("religion"));
                            pdata.setRemarried(obj.getString("remarried"));
                            pdata.setSelf_score(obj.getString("self_score"));
                            pdata.setSmoke(obj.getString("smoke"));
                            pdata.setSmoke_detail(obj.getString("smoke_detail"));
                            pdata.setStyle(obj.getString("style"));
                            pdata.setType(obj.getString("type"));
                            pdata.setU_idx(obj.getString("u_idx"));
                            pdata.setWeight(obj.getString("weight"));
                            pdata.setWhen_marry(obj.getString("when_marry"));
                            pdata.setChildwho(obj.getString("childwho"));

                            //프로필이미지
                            if (!StringUtil.isNull(pdata.getPimg())) {
                                binding.ivProfimg.setVisibility(View.VISIBLE);
                                binding.ivNoprofimg.setVisibility(View.GONE);
                                Glide.with(act)
                                        .load(pdata.getPimg())
                                        .centerCrop()
                                        .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(15)))
                                        .into(binding.ivProfimg);
                            } else {
                                binding.ivProfimg.setVisibility(View.GONE);
                                binding.ivNoprofimg.setVisibility(View.VISIBLE);
                                Glide.with(act)
                                        .load(pdata.getCharacter_int())
                                        .centerCrop()
                                        .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(15)))
                                        .into(binding.ivNoprofimg);
                            }

//                            // 프로필이미지
//                            if (!StringUtil.isNull(pdata.getPimg())) {
//                                binding.ivProfimg.setVisibility(View.VISIBLE);
//                                binding.ivNoprofimg.setVisibility(View.GONE);
//                                Glide.with(act)
//                                        .load(pdata.getPimg())
//                                        .into(binding.ivProfimg);
//                            } else {
//                                binding.ivProfimg.setVisibility(View.GONE);
//                                binding.ivNoprofimg.setVisibility(View.VISIBLE);
//                                if (pdata.getGender().equalsIgnoreCase("male")) {
//                                    Glide.with(act)
//                                            .load(R.drawable.getmarried_img_main_mnoimg_190918)
//                                            .into(binding.ivNoprofimg);
//                                } else {
//                                    Glide.with(act)
//                                            .load(R.drawable.getmarried_img_main_wnoimg_190918)
//                                            .into(binding.ivNoprofimg);
//                                }
//                            }


                            if (StringUtil.isNull(pdata.getName())) {
                                setItem();
                            }

                            //닉네임
                            if (!StringUtil.isNull(pdata.getNick())) {
                                binding.tvTopnick.setText(pdata.getNick());
                            }

                            //지역, 상세지역
                            if (!StringUtil.isNull(pdata.getAddr1()) && !StringUtil.isNull(pdata.getAddr2())) {
                                binding.tvToploc.setText(pdata.getAddr1() + " " + pdata.getAddr2());
                            } else if (!StringUtil.isNull(pdata.getAddr1())) {
                                binding.tvToploc.setText(pdata.getAddr1());
                            }

                            binding.tvAge.setText(StringUtil.calcAge(pdata.getByear()) + "세");

                            //자녀 누가키우노
                            if (!StringUtil.isNull(pdata.getChildwho())) {
                                if (pdata.getChildwho().equalsIgnoreCase("나")) {
                                    binding.rdoChildMe.setChecked(true);
                                } else if (pdata.getChildwho().equalsIgnoreCase("기타")) {
                                    binding.rdoChildOther.setChecked(true);
                                } else {
                                    binding.rdoChildWife.setChecked(true);
                                }
                            }

                            //성별
                            if (!StringUtil.isNull(pdata.getGender())) {
                                if (pdata.getGender().equalsIgnoreCase("male")) {
                                    binding.tvGender.setText("남성");
                                    binding.tvGender.setTextColor(ContextCompat.getColor(act, R.color.man_color));
                                    chGender = Common.M1;
                                } else {
                                    binding.tvGender.setText("여성");
                                    binding.tvGender.setTextColor(ContextCompat.getColor(act, R.color.women_color));
                                    chGender = Common.W1;
                                }
                            }

                            if (!StringUtil.isNull(pdata.getBrotherhood1())) {
                                binding.tvFamilyMale.setText(pdata.getBrotherhood1());
                            }

                            if (!StringUtil.isNull(pdata.getBrotherhood2())) {
                                binding.tvFamilyFemale.setText(pdata.getBrotherhood2());
                            }

                            if (!StringUtil.isNull(pdata.getBrotherhood3())) {
                                binding.tvFamilyOrder.setText(pdata.getBrotherhood3());
                            }

                            //직업
                            if (!StringUtil.isNull(pdata.getJob_group())) {
                                if (pdata.getJob_group().contains("직접입력")) {
                                    binding.llInputArea.setVisibility(View.VISIBLE);
                                    binding.tvJob.setText("직접입력");
                                    binding.etJob.setText(pdata.getJob_group().replace("직접입력", ""));
                                } else {
                                    binding.llInputArea.setVisibility(View.INVISIBLE);
                                    binding.tvJob.setText(pdata.getJob_group());
                                }
                            }

                            Log.i(StringUtil.TAG, "annul: " + pdata.getAnnual());
                            if (!StringUtil.isNull(pdata.getAnnual())) {
                                binding.tvSalary.setText(cpv.getReannual(pdata.getAnnual()));
                            }

                            if (!StringUtil.isNull(pdata.getEducation())) {
                                binding.tvEdu.setText(pdata.getEducation());
                            }

                            if (!StringUtil.isNull(pdata.getStyle())) {
                                binding.tvStyle.setText(pdata.getStyle());
                            }

                            if (!StringUtil.isNull(pdata.getSmoke())) {
                                binding.tvSmoke.setText(cpv.setMainSmoke(pdata.getSmoke()));
                            }

                            String fashion = "";

                            if (!StringUtil.isNull(pdata.getPassionstyle1())) {
                                fashion = pdata.getPassionstyle1();
                            }
                            if (!StringUtil.isNull(pdata.getPassionstyle2())) {
                                fashion += "," + pdata.getPassionstyle2();
                            }


                            if (!StringUtil.isNull(pdata.getHeight())) {
                                binding.tvHeight.setText(pdata.getHeight());
                            }

                            if (!StringUtil.isNull(pdata.getDrink())) {
                                binding.tvMaindrinking.setText(pdata.getDrink());
                            }


                            if (!StringUtil.isNull(pdata.getBlood())) {
                                binding.tvBloodtype.setText(pdata.getBlood());
                            }

                            if (!StringUtil.isNull(pdata.getReligion())) {
                                binding.tvReligion.setText(pdata.getReligion());
                            }

                            String personal = "";
                            if (!StringUtil.isNull(pdata.getPersonality1())) {
                                personal = pdata.getPersonality1();
                            }
                            if (!StringUtil.isNull(pdata.getPersonality2())) {
                                personal += "," + pdata.getPersonality2();
                            }
                            if (!StringUtil.isNull(pdata.getPersonality3())) {
                                personal += "," + pdata.getPersonality3();
                            }
                            binding.tvPersonal.setText(personal);

                            String hobby = "";
                            if (!StringUtil.isNull(pdata.getHobby1())) {
                                hobby = pdata.getHobby1();
                            }
                            if (!StringUtil.isNull(pdata.getHobby2())) {
                                hobby += "," + pdata.getHobby2();
                            }
                            if (!StringUtil.isNull(pdata.getHobby3())) {
                                hobby += "," + pdata.getHobby3();
                            }
                            if (!StringUtil.isNull(pdata.getHobby4())) {
                                hobby += "," + pdata.getHobby4();
                            }
                            if (!StringUtil.isNull(pdata.getHobby5())) {
                                hobby += "," + pdata.getHobby5();
                            }
                            binding.tvHobby.setText(hobby);


                            if (!StringUtil.isNull(pdata.getProperty())) {
                                binding.tvProperty.setText(cpv.setProperty(pdata.getProperty()));
                            }


                            if (!StringUtil.isNull(pdata.getChildcnt())) {
                                if (!pdata.getChildcnt().equalsIgnoreCase(",")) {
                                    String[] child = pdata.getChildcnt().split(",");
                                    binding.tvBoy.setText(child[0]);
                                    binding.tvGirl.setText(child[1]);
                                }
                            }

                        } else {
                            Toast.makeText(MyprofileModifyAct.this, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MyprofileModifyAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MyprofileModifyAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }

            }
        };

        myprofile.addParams("uidx", UserPref.getUidx(this));
        myprofile.execute(true, true);
    }


    private void registProfile() {
        ReqBasic regProf = new ReqBasic(this, NetUrls.PROFMODIFY) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase("Y")) {
                            Toast.makeText(MyprofileModifyAct.this, "프로필 수정 완료", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(MyprofileModifyAct.this, "프로필 수정 실패", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MyprofileModifyAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MyprofileModifyAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };
        regProf.setTag("MyProfile Modify");

        regProf.addParams("uidx", UserPref.getUidx(this));             //회원
        regProf.addParams("character", chGender);             //회원
        regProf.addParams("lastnameYN", "Y");                                              //형제관계1
        regProf.addParams("bh1", binding.tvFamilyMale.getText().toString());                                              //형제관계1
        regProf.addParams("bh2", binding.tvFamilyFemale.getText().toString());                                              //형제관계2
        regProf.addParams("bh3", binding.tvFamilyOrder.getText().toString());                                                 //직업소분류

        //직업
        if (binding.llInputArea.getVisibility() == View.VISIBLE) {
            if (StringUtil.isNull(binding.etJob.getText().toString()) || binding.etJob.getText().toString().equalsIgnoreCase("")) {
                regProf.addParams("jg", "");
            } else {
                regProf.addParams("jg", binding.tvJob.getText().toString() + binding.etJob.getText().toString());
            }
        } else {
            regProf.addParams("jg", binding.tvJob.getText().toString());
        }

        regProf.addParams("ann", cpv.getAnnual(binding.tvSalary.getText().toString()));                                              //연봉
        regProf.addParams("prp", cpv.getProperty(binding.tvProperty.getText().toString()));                                              //재산
        regProf.addParams("edu", binding.tvEdu.getText().toString());                                                 //유학경험

        regProf.addParams("hg", binding.tvHeight.getText().toString());                                                //신장
        regProf.addParams("sty", binding.tvStyle.getText().toString());                                              //스타일
        regProf.addParams("bld", binding.tvBloodtype.getText().toString());                                              //혈액형
        regProf.addParams("rlg", binding.tvReligion.getText().toString());                                               //차량정보
        String[] pers = binding.tvPersonal.getText().toString().split(",");
        if (pers.length == 1) {
            regProf.addParams("psn1", pers[0]);                                             //성격1
            regProf.addParams("psn2", "");
            regProf.addParams("psn3", "");
        } else if (pers.length == 2) {
            regProf.addParams("psn1", pers[0]);                                             //성격1
            regProf.addParams("psn2", pers[1]);                                             //성격2
            regProf.addParams("psn3", "");
        } else if (pers.length == 3) {
            regProf.addParams("psn1", pers[0]);                                             //성격1
            regProf.addParams("psn2", pers[1]);                                             //성격2
            regProf.addParams("psn3", pers[2]);                                             //성격3
        }

        String[] hobby = binding.tvHobby.getText().toString().split(",");
        if (hobby.length == 1) {
            regProf.addParams("hbb1", hobby[0]);                                             //취미1
            regProf.addParams("hbb2", "");
            regProf.addParams("hbb3", "");
            regProf.addParams("hbb4", "");
            regProf.addParams("hbb5", "");
        } else if (hobby.length == 2) {
            regProf.addParams("hbb1", hobby[0]);                                             //취미1
            regProf.addParams("hbb2", hobby[1]);                                             //취미2
            regProf.addParams("hbb3", "");
            regProf.addParams("hbb4", "");
            regProf.addParams("hbb5", "");
        } else if (hobby.length == 3) {
            regProf.addParams("hbb1", hobby[0]);                                             //취미1
            regProf.addParams("hbb2", hobby[1]);                                             //취미2
            regProf.addParams("hbb3", hobby[2]);                                             //취미3
            regProf.addParams("hbb4", "");
            regProf.addParams("hbb5", "");
        } else if (hobby.length == 4) {
            regProf.addParams("hbb1", hobby[0]);                                             //취미1
            regProf.addParams("hbb2", hobby[1]);                                             //취미2
            regProf.addParams("hbb3", hobby[2]);                                             //취미3
            regProf.addParams("hbb4", hobby[3]);                                             //취미4
            regProf.addParams("hbb5", "");
        } else if (hobby.length == 5) {
            regProf.addParams("hbb1", hobby[0]);                                             //취미1
            regProf.addParams("hbb2", hobby[1]);                                             //취미2
            regProf.addParams("hbb3", hobby[2]);                                             //취미3
            regProf.addParams("hbb4", hobby[3]);                                             //취미4
            regProf.addParams("hbb5", hobby[4]);                                             //취미5
        }

        regProf.addParams("smk", cpv.getMainSmoke(binding.tvSmoke.getText().toString()));                                              //흡연
        regProf.addParams("drk", binding.tvMaindrinking.getText().toString());                                                 //건강관리


        String child = binding.tvBoy.getText().toString() + "," + binding.tvGirl.getText().toString();
        regProf.addParams("childcnt", child);

        String childwho = "";
        if (binding.rdoChildMe.isChecked()) {
            childwho = "나";
        } else if (binding.rdoChildWife.isChecked()) {
            childwho = "전 배우자";
        } else if (binding.rdoChildOther.isChecked()) {
            childwho = "기타";
        }
        regProf.addParams("childwho", childwho);

        //희망 배우자 카테고리
        for (int i = 0; i < 7; i++) {
            if (i < list.size()) {
                regProf.addParams("hst" + (i + 1), list.get(i).getText());
            } else {
                regProf.addParams("hst" + (i + 1), "");
            }
        }

        regProf.execute(true, true);
    }

    private void getLocation(String locStr) {
        List<Address> list = null;

        try {
            list = geocoder.getFromLocationName(locStr, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (list != null) {
            if (list.size() == 0) {
                Log.i(StringUtil.TAG, "주소 정보가 없습니다");
                UserPref.setLocationLat(this, "");
                UserPref.setLocationLon(this, "");
            } else {
                UserPref.setLocationLat(this, String.valueOf(list.get(0).getLatitude()));
                UserPref.setLocationLon(this, String.valueOf(list.get(0).getLongitude()));
            }
        }
    }

    private void initHopestyle() {
        pdata.setHope_style1("");
        pdata.setHope_style2("");
        pdata.setHope_style3("");
        pdata.setHope_style4("");
        pdata.setHope_style5("");
        pdata.setHope_style6("");
        pdata.setHope_style7("");
    }

    private void setHopeStyles(String[] items) {
        switch (items.length) {
            case 0:
                initHopestyle();
                break;
            case 1:
                initHopestyle();
                pdata.setHope_style1(items[0]);
                break;
            case 2:
                initHopestyle();
                pdata.setHope_style1(items[0]);
                pdata.setHope_style2(items[1]);
                break;
            case 3:
                initHopestyle();
                pdata.setHope_style1(items[0]);
                pdata.setHope_style2(items[1]);
                pdata.setHope_style3(items[2]);
                break;
            case 4:
                initHopestyle();
                pdata.setHope_style1(items[0]);
                pdata.setHope_style2(items[1]);
                pdata.setHope_style3(items[2]);
                pdata.setHope_style4(items[3]);
                break;
            case 5:
                initHopestyle();
                pdata.setHope_style1(items[0]);
                pdata.setHope_style2(items[1]);
                pdata.setHope_style3(items[2]);
                pdata.setHope_style4(items[3]);
                pdata.setHope_style5(items[4]);
                break;
            case 6:
                initHopestyle();
                pdata.setHope_style1(items[0]);
                pdata.setHope_style2(items[1]);
                pdata.setHope_style3(items[2]);
                pdata.setHope_style4(items[3]);
                pdata.setHope_style5(items[4]);
                pdata.setHope_style6(items[5]);
                break;
            case 7:
                initHopestyle();
                pdata.setHope_style1(items[0]);
                pdata.setHope_style2(items[1]);
                pdata.setHope_style3(items[2]);
                pdata.setHope_style4(items[3]);
                pdata.setHope_style5(items[4]);
                pdata.setHope_style6(items[5]);
                pdata.setHope_style7(items[6]);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String result = "";
        if (requestCode != BASIC && requestCode != PURCHASE) {
            if (data == null) {
                Log.i(StringUtil.TAG, "data == null");
                return;
            }

            if (!StringUtil.isNull(data.getStringExtra("data"))) {
                result = data.getStringExtra("data");
            }
        }

        switch (requestCode) {
            case PURCHASE:
            case BASIC:
                getMyProfile();
                break;

            case DefaultValue.BHOOD1:
                binding.tvFamilyMale.setText(result);
                binding.tvFamilyOrder.setText("1째");
                break;
            case DefaultValue.BHOOD2:
                binding.tvFamilyFemale.setText(result);
                binding.tvFamilyOrder.setText("1째");
                break;
            case DefaultValue.BHOOD3:
                binding.tvFamilyOrder.setText(result);
                break;
            case DefaultValue.SALARY:
                binding.tvSalary.setText(result);
                break;
            case DefaultValue.EDU:
                binding.tvEdu.setText(result);
                break;
            case DefaultValue.STYLE:
                binding.tvStyle.setText(result);
                break;

            case DefaultValue.MAINJOB:
                if (result.equals("직접입력")) {
                    binding.llInputArea.setVisibility(View.VISIBLE);
                    binding.tvJob.setText(result);
                    binding.etJob.requestFocus();

                    //키보드 보이게 하는 부분
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                } else {
                    binding.llInputArea.setVisibility(View.INVISIBLE);
                    binding.tvJob.setText(result);
                }

                break;

            case DefaultValue.PHEIGHT:
                binding.tvHeight.setText(result);
                break;


            case DefaultValue.SMOKE:
                String smoke = "";
                if (result.equalsIgnoreCase("경험없음")) {
                } else {
                    switch (result) {
                        case "금연":
                            smoke = getResources().getStringArray(R.array.smoked1)[0];
                            break;
                        case "흡연":
                            smoke = getResources().getStringArray(R.array.smoked2)[0];
                            break;
                    }
                }
                binding.tvSmoke.setText(result);
                break;

            case DefaultValue.DRINK:

                binding.tvMaindrinking.setText(result);
                break;


            case DefaultValue.BLOOD:
                binding.tvBloodtype.setText(result);
                break;
            case DefaultValue.RELIGION:
                binding.tvReligion.setText(result);
                break;

            case DefaultValue.PERSONAL:
                binding.tvPersonal.setText(result.replaceAll("\\|", ","));
                break;
            case DefaultValue.HOBBY:
                binding.tvHobby.setText(result.replaceAll("\\|", ","));
                break;


            case DefaultValue.BOY:
                binding.tvBoy.setText(result);
                break;
            case DefaultValue.GIRL:
                binding.tvGirl.setText(result);
                break;

            case DefaultValue.PROPERTY:
                binding.tvProperty.setText(result);
                break;

            case DefaultValue.HOPESTYLE:
                Log.e(TAG, "HOPESTYLE: " + result);

                list = new ArrayList<>();
                if (StringUtil.isNull(result)) {
                    Log.e(TAG, "hope false");
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "hope true");
                    String[] selItem = result.split("\\|");

                    for (int i = 0; i < selItem.length; i++) {
                        HopestyleData hopestyleData = new HopestyleData();
                        hopestyleData.setText(selItem[i]);
                        list.add(hopestyleData);

                        if (i == selItem.length - 1) {
                            adapter.setList(list);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_charge:
                startActivityForResult(new Intent(act, ItemmanageAct.class), PURCHASE);
                break;

            case R.id.ll_basicinfo_modify:
                startActivityForResult(new Intent(this, BasicinfoSettingAct.class), BASIC);
                break;

            case R.id.fl_job_close:
//                binding.tvJob.setText(null);
                binding.etJob.setText(null);
//                binding.llInputArea.setVisibility(View.INVISIBLE);
                break;

            case R.id.fl_back:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.btn_home:
                startActivity(new Intent(this, MainActivity.class));
                finishAffinity();
                break;


            case R.id.btn_regprofile:
                //형제관계
                if (binding.tvFamilyFemale.length() == 0 || binding.tvFamilyOrder.length() == 0 || binding.tvFamilyMale.length() == 0) {
                    Common.showToast(act, "형제관계를 선택해주세요");
                    return;
                }

                //직업
                if (!StringUtil.isNull(binding.tvJob.getText().toString())) {
                    if (binding.llInputArea.getVisibility() == View.VISIBLE) {
                        if (!StringUtil.isNull(binding.etJob.getText().toString())) {
                            if (binding.etJob.length() > 10) {
                                Common.showToast(act, "직업란은 10자 이내입니다");
                                return;
                            }
                        } else {
                            Common.showToast(act, "직업을 입력해주세요");
                            return;
                        }
                    }
                } else {
                    Common.showToast(act, "직업을 선택해주세요");
                    return;
                }

                //연봉
                if (binding.tvSalary.length() == 0) {
                    Common.showToast(act, "연봉을 선택해주세요");
                    return;
                }

                //재산
                if (binding.tvProperty.length() == 0) {
                    Common.showToast(act, "재산을 선택해주세요");
                    return;
                }

                //학력
                if (binding.tvEdu.length() == 0) {
                    Common.showToast(act, "학력을 선택해주세요");
                    return;
                }

                //키
                if (binding.tvHeight.length() == 0) {
                    Common.showToast(act, "키를 선택해주세요");
                    return;
                }

                //스타일
                if (binding.tvStyle.length() == 0) {
                    Common.showToast(act, "스타일을 선택해주세요");
                    return;
                }

                //혈액형
                if (binding.tvBloodtype.length() == 0) {
                    Common.showToast(act, "혈액형을 선택해주세요");
                    return;
                }

                //종교
                if (binding.tvReligion.length() == 0) {
                    Common.showToast(act, "종교를 선택해주세요");
                    return;
                }

                //성격
                if (binding.tvPersonal.length() == 0) {
                    Common.showToast(act, "성격을 선택해주세요");
                    return;
                }

                //취미
                if (binding.tvHobby.length() == 0) {
                    Common.showToast(act, "취미를 선택해주세요");
                    return;
                }

                //흡연여부
                if (binding.tvSmoke.length() == 0) {
                    Common.showToast(act, "흡연여부를 선택해주세요");
                    return;
                }

                //음주량
                if (binding.tvMaindrinking.length() == 0) {
                    Common.showToast(act, "음주량을 선택해주세요");
                    return;
                }

                //희망 배우자 카테고리
                if (list.size() == 0) {
                    Common.showToast(act, "희망 배우자 카테고리를\n선택해주세요");
                    return;
                }

//
//                //희망 배우자 카테고리
//                if (list.size() == 0) {
//                    Common.showToast(act, "희망 배우자 카테고리를\n선택해주세요");
//                    return;
//                }
//
//                //직업
//                if (!StringUtil.isNull(binding.tvJob.getText().toString())) {
//                    if (binding.llInputArea.getVisibility() == View.VISIBLE) {
//                        if (!StringUtil.isNull(binding.etJob.getText().toString())) {
//                            if (binding.etJob.length() > 10) {
//                                Common.showToast(act, "직업란은 10자 이내입니다");
//                                return;
//                            }
//                        } else {
//                            Common.showToast(act, "직업을 입력해주세요");
//                            return;
//                        }
//                    }
//                } else {
//                    Common.showToast(act, "직업을 선택해주세요");
//                    return;
//                }

                registProfile();
                break;

            case R.id.tv_family_male:
                // n남
                Intent bhood1 = new Intent(this, ListDlgAct.class);
                bhood1.putExtra("subject", "bhood1");
                bhood1.putExtra("isMen", isMen);
                bhood1.putExtra("select", binding.tvFamilyMale.getText());
                startActivityForResult(bhood1, DefaultValue.BHOOD1);
                break;
            case R.id.tv_family_female:
                // n녀
                Intent bhood2 = new Intent(this, ListDlgAct.class);
                bhood2.putExtra("subject", "bhood2");
                bhood2.putExtra("isMen", isMen);
                bhood2.putExtra("select", binding.tvFamilyFemale.getText());
                startActivityForResult(bhood2, DefaultValue.BHOOD2);
                break;
            case R.id.tv_family_order:
                // n째
                int men = Integer.valueOf(binding.tvFamilyMale.getText().toString().replace("남", ""));
                int women = Integer.valueOf(binding.tvFamilyFemale.getText().toString().replace("녀", ""));

                int total = men + women;

                Intent bhood3 = new Intent(this, ListDlgAct.class);
                bhood3.putExtra("subject", "bhood3");
                bhood3.putExtra("total", total);
                bhood3.putExtra("select", binding.tvFamilyOrder.getText());
                startActivityForResult(bhood3, DefaultValue.BHOOD3);
                break;

            case R.id.tv_job:
                // 직업
                Intent jobIntent = new Intent(this, ListDlgAct.class);
                jobIntent.putExtra("subject", "mainjob");
                jobIntent.putExtra("select", binding.tvJob.getText());
                startActivityForResult(jobIntent, DefaultValue.MAINJOB);
                break;


            case R.id.tv_property:
                // 재산
                Intent propertyIntent = new Intent(this, ListDlgAct.class);
                propertyIntent.putExtra("subject", "property");
                propertyIntent.putExtra("select", binding.tvProperty.getText());
                startActivityForResult(propertyIntent, DefaultValue.PROPERTY);
                break;
            case R.id.tv_salary:
                // 연봉
                Intent annualIntent = new Intent(this, ListDlgAct.class);
                annualIntent.putExtra("subject", "annual");
                annualIntent.putExtra("select", binding.tvSalary.getText());
                startActivityForResult(annualIntent, DefaultValue.SALARY);
                break;
            case R.id.tv_edu:
                // 학력
                Intent eduIntent = new Intent(this, ListDlgAct.class);
                eduIntent.putExtra("subject", "edu");
                eduIntent.putExtra("select", binding.tvEdu.getText());
                startActivityForResult(eduIntent, DefaultValue.EDU);
                break;
            case R.id.tv_style:
                // 스타일
                Intent stylentent = new Intent(this, ListDlgAct.class);
                stylentent.putExtra("subject", "style");
                stylentent.putExtra("select", binding.tvStyle.getText());
                startActivityForResult(stylentent, DefaultValue.STYLE);
                break;


            case R.id.tv_height:
                // 키
                Intent heightIntent = new Intent(this, ListDlgAct.class);
                heightIntent.putExtra("subject", "height");
                heightIntent.putExtra("select", binding.tvHeight.getText());
                startActivityForResult(heightIntent, DefaultValue.PHEIGHT);
                break;

            case R.id.tv_smoke:
                // 흡연
                Intent smoke = new Intent(this, ListDlgAct.class);
                smoke.putExtra("subject", "smoke");
                smoke.putExtra("select", binding.tvSmoke.getText());
                startActivityForResult(smoke, DefaultValue.SMOKE);
                break;


            case R.id.tv_maindrinking:
                // 음주 횟수
                Intent drink = new Intent(this, ListDlgAct.class);
                drink.putExtra("subject", "drink");
                drink.putExtra("select", binding.tvMaindrinking.getText());
                startActivityForResult(drink, DefaultValue.DRINK);
                break;


            case R.id.tv_bloodtype:
                // 혈액형
                Intent blood = new Intent(this, ListDlgAct.class);
                blood.putExtra("subject", "blood");
                blood.putExtra("select", binding.tvBloodtype.getText());
                startActivityForResult(blood, DefaultValue.BLOOD);
                break;
            case R.id.tv_religion:
                // 종교
                Intent religion = new Intent(this, ListDlgAct.class);
                religion.putExtra("subject", "religion");
                religion.putExtra("select", binding.tvReligion.getText());
                startActivityForResult(religion, DefaultValue.RELIGION);
                break;


            case R.id.tv_personal:
                // 성격
                Intent personal = new Intent(this, MultipleListDlgAct.class);
                personal.putExtra("subject", "personal");
                personal.putExtra("select", binding.tvPersonal.getText().toString().replaceAll(",", "\\|"));
                startActivityForResult(personal, DefaultValue.PERSONAL);
                break;
            case R.id.tv_hobby:
                // 취미
                Intent hobby = new Intent(this, MultipleListDlgAct.class);
                hobby.putExtra("subject", "hobby");
                hobby.putExtra("select", binding.tvHobby.getText().toString().replaceAll(",", "\\|"));
                startActivityForResult(hobby, DefaultValue.HOBBY);
                break;


            case R.id.tv_boy:
                // 남
                Toast.makeText(this, "아들의 숫자를 지정하세요", Toast.LENGTH_SHORT).show();
                Intent boy = new Intent(this, ListDlgAct.class);
                boy.putExtra("subject", "boy");
                boy.putExtra("select", binding.tvBoy.getText());
                startActivityForResult(boy, DefaultValue.BOY);
                break;
            case R.id.tv_girl:
                // 여
                Toast.makeText(this, "딸의 숫자를 지정하세요", Toast.LENGTH_SHORT).show();
                Intent girl = new Intent(this, ListDlgAct.class);
                girl.putExtra("subject", "girl");
                girl.putExtra("select", binding.tvGirl.getText());
                startActivityForResult(girl, DefaultValue.GIRL);
                break;
        }
    }
}
