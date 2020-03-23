package com.match.honey.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.match.honey.R;
import com.match.honey.activity.MainActivity;
import com.match.honey.activity.MyProfileAct;
import com.match.honey.activity.OnlineMemberActivity;
import com.match.honey.adapters.list.InterestMemListAdapter;
import com.match.honey.databinding.ActivityInterestmemBinding;
import com.match.honey.listDatas.InterestMemData;
import com.match.honey.listDatas.InterestPData;
import com.match.honey.listDatas.OnlinememData;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.Common;
import com.match.honey.utils.ItemOffsetDecoration;
import com.match.honey.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class InterestFrag extends BaseFrag implements View.OnClickListener, MainActivity.onKeyBackPressedListener {
    ActivityInterestmemBinding binding;
    AppCompatActivity act;
    View view;

    ArrayList<InterestMemData> list = new ArrayList<>();
    ArrayList<OnlinememData> loginList = new ArrayList<>();
    InterestMemListAdapter adapter;

    String cate = "all";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_interestmem, container, false);
        view = binding.getRoot();
        act = (AppCompatActivity) getActivity();


        binding.llInterest01.setSelected(true);
        setClickListener();

        binding.rcvInterest.setLayoutManager(new LinearLayoutManager(act));
        adapter = new InterestMemListAdapter(act, list);
        binding.rcvInterest.setAdapter(adapter);
        binding.rcvInterest.addItemDecoration(new ItemOffsetDecoration(act));

        getInterList(NetUrls.ALLINTEREST, "all");

        return view;
    }



    private void setClickListener() {
        binding.flBack.setOnClickListener(this);

        binding.llInterest01.setOnClickListener(this);
        binding.llInterest02.setOnClickListener(this);
        binding.llInterest03.setOnClickListener(this);

        binding.llDelmsglist.setOnClickListener(this);
        binding.llOnlinemem.setOnClickListener(this);
        binding.llWrite.setOnClickListener(this);
    }

    private void initTabbtn() {
        binding.llInterest01.setSelected(false);
        binding.llInterest02.setSelected(false);
        binding.llInterest03.setSelected(false);
    }

    private void getInterList(String Url, final String tag) {
        ReqBasic interList = new ReqBasic(act, Url) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Interest List Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {

                            JSONArray ja =jo.getJSONArray("value");
                            ArrayList<InterestMemData> tmplist = new ArrayList<>();
                            String predate = "";
                            if (tag.equalsIgnoreCase("all")) {
                                for (int i = 0; i < ja.length(); i++) {

                                    JSONObject obj = ja.getJSONObject(i);
                                    Log.e(StringUtil.TAG, "obj(" + i + "): " + obj);
                                    InterestMemData data = new InterestMemData();

                                    data.setCate(tag);
                                    data.setIdx(obj.getString("idx"));
                                    data.setMidx(obj.getString("user_idx"));
                                    data.setIptype(obj.getString("ip_type"));
                                    data.setItemtype(StringUtil.TYPEITEM);
                                    data.setRegdate(obj.getString("ip_regdate"));
                                    if(obj.has("piimgcnt")) {
                                        data.setPiimgcnt(obj.getString("piimgcnt"));
                                    }

                                    if (!StringUtil.isNull(obj.getString("ip_regdate"))) {
                                        if (!predate.equalsIgnoreCase(obj.getString("ip_regdate").split(" ")[0])) {
                                            if(!StringUtil.isNull(obj.getString("user_info"))) {
                                                InterestMemData ditem = new InterestMemData();
                                                ditem.setCate(tag);
                                                ditem.setRegdate(obj.getString("ip_regdate"));
                                                ditem.setItemtype(StringUtil.TYPEDATE);
                                                tmplist.add(ditem);
                                                predate = obj.getString("ip_regdate").split(" ")[0];
                                            }
                                        }
                                    }

                                    if (!StringUtil.isNull(obj.getString("user_info"))) {
                                        JSONObject joprof = new JSONObject(obj.getString("user_info"));

                                        InterestPData profile = new InterestPData();


                                        SimpleDateFormat orgin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm");

                                        try {
                                            Date old = orgin.parse(joprof.getString("recentdate"));
                                            profile.setRegdate(sdf.format(old));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        profile.setIdx(joprof.getString("idx"));
//                                    profile.setI_user_idx(joprof.getString("i_user_idx"));
//                                    profile.setU_user_idx(joprof.getString("u_user_idx"));
//                                    profile.setIp_regdate(joprof.getString("ip_regdate"));
                                        profile.setAge(StringUtil.calcAge(joprof.getString("byear")));
                                        profile.setType(joprof.getString("type"));
                                        profile.setId(joprof.getString("id"));
                                        profile.setPw(joprof.getString("pw"));
                                        profile.setName(joprof.getString("name"));
                                        profile.setFamilyname(joprof.getString("familyname"));
                                        profile.setNick(joprof.getString("nick"));
                                        profile.setByear(joprof.getString("byear"));
                                        profile.setBmonth(joprof.getString("bmonth"));
                                        profile.setGender(joprof.getString("gender"));
                                        profile.setAddr1(joprof.getString("addr1"));
                                        profile.setAddr2(joprof.getString("addr2"));
                                        profile.setHopeaddr(joprof.getString("hopeaddr"));
                                        profile.setM_fcm_token(joprof.getString("m_fcm_token"));
                                        profile.setLat(joprof.getString("lat"));
                                        profile.setLon(joprof.getString("lon"));
                                        profile.setZodiac(joprof.getString("zodiac"));
                                        profile.setLoginYN(joprof.getString("loginYN"));
                                        profile.setInterest_idxs(joprof.getString("interest_idxs"));
                                        profile.setU_cell_num(joprof.getString("u_cell_num"));
                                        profile.setCoin(joprof.getString("coin"));
                                        profile.setU_idx(joprof.getString("u_idx"));
                                        profile.setPimg(joprof.getString("pimg"));
                                        profile.setP_wave1(joprof.getString("p_wave1"));
                                        profile.setP_wave2(joprof.getString("p_wave2"));
                                        profile.setP_wave3(joprof.getString("p_wave3"));
                                        profile.setP_movie1(joprof.getString("p_movie1"));
                                        profile.setP_movie2(joprof.getString("p_movie2"));
                                        profile.setP_movie3(joprof.getString("p_movie3"));
                                        profile.setP_gif1(joprof.getString("p_gif1"));
                                        profile.setP_gif2(joprof.getString("p_gif2"));
                                        profile.setP_gif3(joprof.getString("p_gif3"));
                                        profile.setCouple_type(joprof.getString("couple_type"));
                                        profile.setBrotherhood1(joprof.getString("brotherhood1"));
                                        profile.setBrotherhood2(joprof.getString("brotherhood2"));
                                        profile.setBrotherhood3(joprof.getString("brotherhood3"));
                                        profile.setHometown(joprof.getString("hometown"));
                                        profile.setHometown1(joprof.getString("hometown1"));
                                        profile.setJob_group(joprof.getString("job_group"));
                                        profile.setJob_detail(joprof.getString("job_detail"));
                                        profile.setJob_img(joprof.getString("job_img"));
                                        profile.setAnnual(joprof.getString("annual"));
                                        profile.setAnnual_img(joprof.getString("annual_img"));
                                        profile.setEducation(joprof.getString("education"));
                                        profile.setEducation_img(joprof.getString("education_img"));
                                        profile.setAbroad(joprof.getString("abroad"));
                                        profile.setProperty(joprof.getString("property"));
                                        profile.setStyle(joprof.getString("style"));
                                        profile.setPassionstyle1(joprof.getString("passionstyle1"));
                                        profile.setPassionstyle2(joprof.getString("passionstyle2"));
                                        profile.setHeight(joprof.getString("height"));
                                        profile.setWeight(joprof.getString("weight"));
                                        profile.setSmoke(joprof.getString("smoke"));
                                        profile.setDrink(joprof.getString("drink"));
                                        profile.setDrink_detail(joprof.getString("drink_detail"));
                                        profile.setHealth(joprof.getString("health"));
                                        profile.setHealth_detail(joprof.getString("health_detail"));
                                        profile.setBlood(joprof.getString("blood"));
                                        profile.setReligion(joprof.getString("religion"));
                                        profile.setCarinfo(joprof.getString("carinfo"));
                                        profile.setPersonality1(joprof.getString("personality1"));
                                        profile.setPersonality2(joprof.getString("personality2"));
                                        profile.setPersonality3(joprof.getString("personality3"));
                                        profile.setHobby1(joprof.getString("hobby1"));
                                        profile.setHobby2(joprof.getString("hobby2"));
                                        profile.setHobby3(joprof.getString("hobby3"));
                                        profile.setHobby4(joprof.getString("hobby4"));
                                        profile.setHobby5(joprof.getString("hobby5"));
                                        profile.setFood1(joprof.getString("food1"));
                                        profile.setFood2(joprof.getString("food2"));
                                        profile.setFood3(joprof.getString("food3"));
                                        profile.setFood4(joprof.getString("food4"));
                                        profile.setFood5(joprof.getString("food5"));
                                        profile.setFood6(joprof.getString("food6"));
                                        profile.setFood7(joprof.getString("food7"));
                                        profile.setFamily(joprof.getString("family"));
                                        profile.setP_introduce(joprof.getString("p_introduce"));
                                        profile.setFamily_info(joprof.getString("family_info"));
                                        profile.setSelf_score(joprof.getString("self_score"));
                                        profile.setWhen_marry(joprof.getString("when_marry"));
                                        profile.setHope_religion(joprof.getString("hope_religion"));
                                        profile.setHope_style1(joprof.getString("hope_style1"));
                                        profile.setHope_style2(joprof.getString("hope_style2"));
                                        profile.setHope_style3(joprof.getString("hope_style3"));
                                        profile.setHope_style4(joprof.getString("hope_style4"));
                                        profile.setHope_style5(joprof.getString("hope_style5"));
                                        profile.setHope_style6(joprof.getString("hope_style6"));
                                        profile.setHope_style7(joprof.getString("hope_style7"));
                                        profile.setReference(joprof.getString("reference"));
                                        profile.setAuth_num(joprof.getString("auth_num"));
                                        profile.setAuth_ok(joprof.getString("auth_ok"));
                                        profile.setRemarried(joprof.getString("remarried"));
                                        profile.setMarried_paper_img(joprof.getString("married_paper_img"));
                                        profile.setLastnameYN(joprof.getString("lastnameYN"));
                                        profile.setPimg_ck(joprof.getString("pimg_ck"));
                                        profile.setPint_ck(joprof.getString("pint_ck"));

                                        profile.setCharacter(Common.getCharacterDrawable(joprof.getString("character"), joprof.getString("gender")));

                                        data.setCheckState(false);

                                        data.setDatas(profile);
                                        tmplist.add(data);

                                    }
                                }

                            } else {
                                for (int i = 0; i < ja.length(); i++) {

                                    JSONObject obj = ja.getJSONObject(i);
                                    InterestMemData data = new InterestMemData();
                                    data.setIdx(obj.getString("idx"));
                                    data.setItemtype(StringUtil.TYPEITEM);
                                    data.setCate(tag);
                                    if(obj.has("piimgcnt")) {
                                        data.setPiimgcnt(obj.getString("piimgcnt"));
                                    }
                                    InterestPData profile = new InterestPData();

                                    if (StringUtil.isNull(obj.getString("ip_regdate"))) {

                                    } else {
                                        if (!predate.equalsIgnoreCase(obj.getString("ip_regdate").split(" ")[0])) {
                                            InterestMemData ditem = new InterestMemData();
                                            ditem.setCate(tag);
                                            ditem.setRegdate(obj.getString("ip_regdate"));
                                            ditem.setItemtype(StringUtil.TYPEDATE);
                                            tmplist.add(ditem);
                                            predate = obj.getString("ip_regdate").split(" ")[0];
                                        }
                                    }


                                    SimpleDateFormat orgin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm");

                                    try {
                                        Date old = orgin.parse(obj.getString("recentdate"));
                                        profile.setRegdate(sdf.format(old));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    profile.setIdx(obj.getString("idx"));
                                    profile.setI_user_idx(obj.getString("i_user_idx"));
                                    profile.setAge(StringUtil.calcAge(obj.getString("byear")));
                                    profile.setU_user_idx(obj.getString("u_user_idx"));
                                    profile.setIp_regdate(obj.getString("ip_regdate"));
                                    profile.setType(obj.getString("type"));
                                    profile.setId(obj.getString("id"));
                                    profile.setPw(obj.getString("pw"));
                                    profile.setName(obj.getString("name"));
                                    profile.setFamilyname(obj.getString("familyname"));
                                    profile.setNick(obj.getString("nick"));
                                    profile.setByear(obj.getString("byear"));
                                    profile.setBmonth(obj.getString("bmonth"));
                                    profile.setGender(obj.getString("gender"));
                                    profile.setAddr1(obj.getString("addr1"));
                                    profile.setAddr2(obj.getString("addr2"));
                                    profile.setHopeaddr(obj.getString("hopeaddr"));
                                    profile.setM_fcm_token(obj.getString("m_fcm_token"));
//                                    profile.setRegdate(obj.getString("regdate"));
                                    profile.setLat(obj.getString("lat"));
                                    profile.setLon(obj.getString("lon"));
                                    profile.setZodiac(obj.getString("zodiac"));
                                    profile.setLoginYN(obj.getString("loginYN"));
                                    profile.setInterest_idxs(obj.getString("interest_idxs"));
                                    profile.setU_cell_num(obj.getString("u_cell_num"));
                                    profile.setCoin(obj.getString("coin"));
                                    profile.setU_idx(obj.getString("u_idx"));
                                    profile.setPimg(obj.getString("pimg"));
                                    profile.setP_wave1(obj.getString("p_wave1"));
                                    profile.setP_wave2(obj.getString("p_wave2"));
                                    profile.setP_wave3(obj.getString("p_wave3"));
                                    profile.setP_movie1(obj.getString("p_movie1"));
                                    profile.setP_movie2(obj.getString("p_movie2"));
                                    profile.setP_movie3(obj.getString("p_movie3"));
                                    profile.setP_gif1(obj.getString("p_gif1"));
                                    profile.setP_gif2(obj.getString("p_gif2"));
                                    profile.setP_gif3(obj.getString("p_gif3"));
                                    profile.setCouple_type(obj.getString("couple_type"));
                                    profile.setBrotherhood1(obj.getString("brotherhood1"));
                                    profile.setBrotherhood2(obj.getString("brotherhood2"));
                                    profile.setBrotherhood3(obj.getString("brotherhood3"));
                                    profile.setHometown(obj.getString("hometown"));
                                    profile.setHometown1(obj.getString("hometown1"));
                                    profile.setJob_group(obj.getString("job_group"));
                                    profile.setJob_detail(obj.getString("job_detail"));
                                    profile.setJob_img(obj.getString("job_img"));
                                    profile.setAnnual(obj.getString("annual"));
                                    profile.setAnnual_img(obj.getString("annual_img"));
                                    profile.setEducation(obj.getString("education"));
                                    profile.setEducation_img(obj.getString("education_img"));
                                    profile.setAbroad(obj.getString("abroad"));
                                    profile.setProperty(obj.getString("property"));
                                    profile.setStyle(obj.getString("style"));
                                    profile.setPassionstyle1(obj.getString("passionstyle1"));
                                    profile.setPassionstyle2(obj.getString("passionstyle2"));
                                    profile.setHeight(obj.getString("height"));
                                    profile.setWeight(obj.getString("weight"));
                                    profile.setSmoke(obj.getString("smoke"));
                                    profile.setDrink(obj.getString("drink"));
                                    profile.setDrink_detail(obj.getString("drink_detail"));
                                    profile.setHealth(obj.getString("health"));
                                    profile.setHealth_detail(obj.getString("health_detail"));
                                    profile.setBlood(obj.getString("blood"));
                                    profile.setReligion(obj.getString("religion"));
                                    profile.setCarinfo(obj.getString("carinfo"));
                                    profile.setPersonality1(obj.getString("personality1"));
                                    profile.setPersonality2(obj.getString("personality2"));
                                    profile.setPersonality3(obj.getString("personality3"));
                                    profile.setHobby1(obj.getString("hobby1"));
                                    profile.setHobby2(obj.getString("hobby2"));
                                    profile.setHobby3(obj.getString("hobby3"));
                                    profile.setHobby4(obj.getString("hobby4"));
                                    profile.setHobby5(obj.getString("hobby5"));
                                    profile.setFood1(obj.getString("food1"));
                                    profile.setFood2(obj.getString("food2"));
                                    profile.setFood3(obj.getString("food3"));
                                    profile.setFood4(obj.getString("food4"));
                                    profile.setFood5(obj.getString("food5"));
                                    profile.setFood6(obj.getString("food6"));
                                    profile.setFood7(obj.getString("food7"));
                                    profile.setFamily(obj.getString("family"));
                                    profile.setP_introduce(obj.getString("p_introduce"));
                                    profile.setFamily_info(obj.getString("family_info"));
                                    profile.setSelf_score(obj.getString("self_score"));
                                    profile.setWhen_marry(obj.getString("when_marry"));
                                    profile.setHope_religion(obj.getString("hope_religion"));
                                    profile.setHope_style1(obj.getString("hope_style1"));
                                    profile.setHope_style2(obj.getString("hope_style2"));
                                    profile.setHope_style3(obj.getString("hope_style3"));
                                    profile.setHope_style4(obj.getString("hope_style4"));
                                    profile.setHope_style5(obj.getString("hope_style5"));
                                    profile.setHope_style6(obj.getString("hope_style6"));
                                    profile.setHope_style7(obj.getString("hope_style7"));
                                    profile.setReference(obj.getString("reference"));
                                    profile.setAuth_num(obj.getString("auth_num"));
                                    profile.setAuth_ok(obj.getString("auth_ok"));
                                    profile.setRemarried(obj.getString("remarried"));
                                    profile.setMarried_paper_img(obj.getString("married_paper_img"));
                                    profile.setLastnameYN(obj.getString("lastnameYN"));
                                    profile.setPimg_ck(obj.getString("pimg_ck"));
                                    profile.setPint_ck(obj.getString("pint_ck"));
                                    profile.setCharacter(Common.getCharacterDrawable(obj.getString("character"), obj.getString("gender")));

                                    data.setDatas(profile);

                                    data.setCheckState(false);
                                    tmplist.add(data);

                                }
                            }

                            if (list.size() > 0) {
                                list.clear();
                            }
                            list.addAll(tmplist);
                            adapter.notifyDataSetChanged();

                        } else {
                            if (list.size() > 0) {
                                list.clear();
                            }
                            adapter.notifyDataSetChanged();
//                            Toast.makeText(act, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        if (list.size() > 0) {
                            list.clear();
                        }
                        adapter.notifyDataSetChanged();
                        e.printStackTrace();
//                        Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (list.size() > 0) {
                        list.clear();
                    }
                    adapter.notifyDataSetChanged();
//                    Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }

            }
        };

        interList.addParams("u_idx", UserPref.getUidx(act));
        interList.execute(true, true);
    }

    private void getInterIdx() {
        ReqBasic myprofile = new ReqBasic(act, NetUrls.MYPROFILE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            JSONObject obj = new JSONObject(jo.getString("value"));

                            UserPref.setInterIdxs(act, obj.getString("interest_idxs"));

                        } else {
                            Toast.makeText(act, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }


            }
        };

        myprofile.addParams("uidx", UserPref.getUidx(act));
        myprofile.execute(true, false);
    }

    private void deleteData(String idxs) {
        ReqBasic delList = new ReqBasic(act, NetUrls.DELINTER) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        // 리스트 갱신
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
//                            String message = jo.getString("message");
                            Toast.makeText(act, "찜을 해제했습니다", Toast.LENGTH_SHORT).show();
                            if (list.size() > 0) {
                                list.clear();
                            }

                            getInterIdx();
                            if(cate.equalsIgnoreCase("all")) {
                                getInterList(NetUrls.ALLINTEREST, "all");
                            } else if(cate.equalsIgnoreCase("to")) {
                                getInterList(NetUrls.TOINTEREST, "to");
                            } else {
                                getInterList(NetUrls.FROMINTEREST, "from");
                            }

//                            ((MainActivity) MainActivity.act).setMenuCount(NetUrls.LIKECOUNT);
                        } else {
                            Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        delList.setTag("Interest Delete List (" + cate + ")");
        delList.addParams("u_idx", UserPref.getUidx(act)/* 로그인 멤버 인덱스 */);
        delList.addParams("idxs", idxs/*선택한(삭제할)유저인덱스 번호 <문자열 파싱 |>*/);
        delList.addParams("type", cate);
        delList.execute(true, true);
    }

    private void interRelease(String tidx) {
        ReqBasic reqInter = new ReqBasic(act, NetUrls.SETINTER) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Interest Delete Get Info : " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            Toast.makeText(act, "찜을 해제했습니다", Toast.LENGTH_SHORT).show();
                            if (list.size() > 0) {
                                list.clear();
                            }

                            getInterIdx();
                            getInterList(NetUrls.TOINTEREST, "to");
                        } else {
                            Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }

            }
        };

        reqInter.addParams("uidx", UserPref.getUidx(act));
        reqInter.addParams("tidx", tidx);
        reqInter.addParams("stype", "N");
        reqInter.execute(true, true);
    }

    private void showDlgConfirm(final String tidx) {
        LayoutInflater dialog = LayoutInflater.from(act);
        View dialogLayout = dialog.inflate(R.layout.dlg_confirm, null);
        final Dialog menuDlg = new Dialog(act);

        menuDlg.setContentView(dialogLayout);

        menuDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        menuDlg.show();

        TextView title = (TextView) dialogLayout.findViewById(R.id.tv_dlgtitle);
        TextView contents = (TextView) dialogLayout.findViewById(R.id.tv_contents);
        TextView btn_cancel = (TextView) dialogLayout.findViewById(R.id.btn_cancel);
        TextView btn_ok = (TextView) dialogLayout.findViewById(R.id.btn_ok);


        title.setText("찜 해제");
        contents.setText("선택항목을 해제 하시겠습니까?");

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData(tidx);
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

    @Override
    public void onBack() {
        exit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MainActivity activity = (MainActivity) getActivity();
        activity.setOnKeyBackPressedListener(this);
    }

    private void exit() {
        ((MainActivity) act).switchFrame(true);
        ((MainActivity) act).getSupportFragmentManager().beginTransaction().remove(InterestFrag.this).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                exit();
                break;

            case R.id.ll_interest01:
                cate = "all";
                initTabbtn();
                binding.llInterest01.setSelected(true);
                getInterList(NetUrls.ALLINTEREST, "all");
                break;

            case R.id.ll_interest02:
                cate = "to";
                initTabbtn();
                binding.llInterest02.setSelected(true);
                getInterList(NetUrls.TOINTEREST, "to");
                break;

            case R.id.ll_interest03:
                cate = "from";
                initTabbtn();
                binding.llInterest03.setSelected(true);
                getInterList(NetUrls.FROMINTEREST, "from");
                break;

            case R.id.ll_onlinemem:
                startActivity(new Intent(act, OnlineMemberActivity.class));
                break;

            case R.id.ll_write:
                startActivity(new Intent(act, MyProfileAct.class));
                break;

            case R.id.ll_delmsglist:

                String tidx = "";
                for (InterestMemData item : list) {
                    if (!StringUtil.isNull(item.getIdx())) {
                        if (item.isCheckState()) {
                            if (StringUtil.isNull(tidx)) {
                                tidx = item.getIdx();
                            } else {
                                tidx += "|" + item.getIdx();
                            }
                        }
                    }
                }

                if (StringUtil.isNull(tidx)) {
                    Toast.makeText(act, "선택 항목이 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i(StringUtil.TAG, "tidx: " + tidx);
                showDlgConfirm(tidx);

//                String tidx = "";
//                for (InterestMemData item : list) {
//                    if (!StringUtil.isNull(item.getDatas().getU_idx())) {
//                        if (item.isCheckState()) {
//                            if (StringUtil.isNull(tidx)) {
//                                tidx = item.getDatas().getU_idx();
//                            } else {
//                                tidx += "|" + item.getDatas().getU_idx();
//                            }
//                        }
//                    }
//                }
//
//                if (StringUtil.isNull(tidx)) {
//                    Toast.makeText(act, "선택 항목이 없습니다.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                Log.i(StringUtil.TAG, "tidx: " + tidx);
//                showDlgConfirm(tidx);
                break;
        }
    }
}