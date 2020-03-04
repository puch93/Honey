package com.match.marryme.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.match.marryme.R;
import com.match.marryme.activity.MainActivity;
import com.match.marryme.activity.MyProfileAct;
import com.match.marryme.activity.OnlineMemberActivity;
import com.match.marryme.adapters.list.ProfReadingListAdapter;
import com.match.marryme.databinding.ActivityProfreadingBinding;
import com.match.marryme.listDatas.ProfreadData;
import com.match.marryme.network.ReqBasic;
import com.match.marryme.network.netUtil.HttpResult;
import com.match.marryme.network.netUtil.NetUrls;
import com.match.marryme.sharedPref.UserPref;
import com.match.marryme.utils.Common;
import com.match.marryme.utils.ItemOffsetDecoration;
import com.match.marryme.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ViewingFrag extends BaseFrag implements View.OnClickListener, MainActivity.onKeyBackPressedListener {
    ActivityProfreadingBinding binding;
    Activity act;
    View view;

    ArrayList<ProfreadData> profreadList = new ArrayList<>();

    ProfReadingListAdapter adapter;
    String cate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_profreading, container, false);
        view = binding.getRoot();
        act = getActivity();
        binding.llRead01.setSelected(true);

        setClickListener();

        binding.rcvProfreading.setLayoutManager(new LinearLayoutManager(act));
        adapter = new ProfReadingListAdapter(act, profreadList);
        binding.rcvProfreading.setAdapter(adapter);
        binding.rcvProfreading.addItemDecoration(new ItemOffsetDecoration(act));

        getReadList(NetUrls.PROFREADALL, "all");
        cate = "all";

        return view;
    }

    private void initTab() {
        binding.llRead01.setSelected(false);
        binding.llRead02.setSelected(false);
        binding.llRead03.setSelected(false);
    }

    private void setClickListener() {
        binding.flBack.setOnClickListener(this);

        binding.llDelmsglist.setOnClickListener(this);
        binding.llOnlinemem.setOnClickListener(this);
        binding.llWrite.setOnClickListener(this);

        binding.llRead01.setOnClickListener(this);
        binding.llRead02.setOnClickListener(this);
        binding.llRead03.setOnClickListener(this);
    }

    private void exit() {
        ((MainActivity) act).switchFrame(true);
        ((MainActivity) act).getSupportFragmentManager().beginTransaction().remove(ViewingFrag.this).commit();
    }

    private void deleteData(String idxs) {
        ReqBasic delList = new ReqBasic(act, NetUrls.MYPROFREADDEL) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        // 리스트 갱신
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Viewing Delete List Get Info (" + cate + "): " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            Toast.makeText(act, "열람내역을 삭제했습니다", Toast.LENGTH_SHORT).show();
                            if (profreadList.size() > 0) {
                                profreadList.clear();
                            }

                            if(cate.equalsIgnoreCase("all")) {
                                getReadList(NetUrls.PROFREADALL, "all");
                            } else if(cate.equalsIgnoreCase("to")) {
                                getReadList(NetUrls.MYPROFREAD, "to");
                            } else {
                                getReadList(NetUrls.PROFREADME, "from");
                            }

//                            ((MainActivity) MainActivity.act).setMenuCount(NetUrls.VIEWCOUNT);
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

        delList.addParams("u_idx", UserPref.getUidx(act)/* 로그인 멤버 인덱스 */);
        delList.addParams("idxs", idxs/*선택한(삭제할)유저인덱스 번호 <문자열 파싱 |>*/);
        delList.addParams("type", cate);
        delList.execute(true, true);
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


        title.setText("열람내역 삭제");
        contents.setText("열람내역을 삭제 하시겠습니까?");

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                exit();
                break;
            case R.id.ll_read01:
                cate = "all";
                getReadList(NetUrls.PROFREADALL, "all");
                initTab();
                binding.llRead01.setSelected(true);
                break;
            case R.id.ll_read02:
                cate = "to";
                getReadList(NetUrls.MYPROFREAD, "to");
                initTab();
                binding.llRead02.setSelected(true);
                break;
            case R.id.ll_read03:
                cate = "from";
                getReadList(NetUrls.PROFREADME, "from");
                initTab();
                binding.llRead03.setSelected(true);
                break;

            case R.id.ll_onlinemem:
                startActivity(new Intent(act, OnlineMemberActivity.class));
                break;

            case R.id.ll_write:
                startActivity(new Intent(act, MyProfileAct.class));
                break;

            case R.id.ll_delmsglist:
                String delitem = "";
                for (ProfreadData item : profreadList) {
                    if (!StringUtil.isNull(item.getIdx())) {
                        if (item.isCheckState()) {
                            if (StringUtil.isNull(delitem)) {
                                delitem = item.getIdx();
                            } else {
                                delitem += "|" + item.getIdx();
                            }
                        }
                    }
                }

                if (StringUtil.isNull(delitem)) {
                    Common.showToast(act, "선택 항목이 없습니다.");
                } else {
                    showDlgConfirm(delitem);
                }

                break;
        }
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

    /**
     * 프로필 열람 전체 리스트(내가본/내프로필을 본)
     * 전체에 담아서 각 리스트 별로 다시 담아서 보여주기
     */
    private void getReadList(String Url, final String cate) {
        final ReqBasic readList = new ReqBasic(act, Url) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {

                            JSONArray ja = new JSONArray(jo.getString("value"));
                            ArrayList<ProfreadData> tmplist = new ArrayList<>();

                            Log.i(StringUtil.TAG, "ja length: " + ja.length());

                            int todaycnt = 0;
                            String predate = "";
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject obj = ja.getJSONObject(i);
                                Log.e(StringUtil.TAG, "obj (" + i + "): " + obj);
                                ProfreadData data = new ProfreadData();

                                data.setItemtype(StringUtil.TYPEITEM);
                                data.setIdx(obj.getString("rp_idx"));
                                if(obj.has("piimgcnt")) {
                                    data.setPiimgcnt(obj.getString("piimgcnt"));
                                }

                                if (cate.equalsIgnoreCase("all")) {
                                    data.setCate(cate);
                                    data.setP_user_idx(obj.getString("p_user_idx"));
                                    data.setR_user_idx(obj.getString("r_user_idx"));

                                    if (!StringUtil.isNull(obj.getString("userinfo"))) {
                                        JSONObject userinfo = new JSONObject(obj.getString("userinfo"));

                                        Log.i(StringUtil.TAG, "ck: " + StringUtil.isNull(userinfo.toString()));

                                        if (StringUtil.isToday(obj.getString("rp_regdate").split(" ")[0])) {
                                            todaycnt++;
                                        }

//                                        data.setIdx(userinfo.getString("idx"));

                                        if (!StringUtil.isNull(data.getIdx())) {
                                            if (StringUtil.isNull(obj.getString("rp_regdate"))) {

                                            } else {
                                                if (!predate.equalsIgnoreCase(obj.getString("rp_regdate").split(" ")[0])) {
                                                    ProfreadData ditem = new ProfreadData();
                                                    ditem.setRegdate(obj.getString("rp_regdate"));
                                                    ditem.setItemtype(StringUtil.TYPEDATE);
                                                    tmplist.add(ditem);
                                                    predate = obj.getString("rp_regdate").split(" ")[0];
                                                }
                                            }
                                        }

                                        SimpleDateFormat orgin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm");

                                        try {
                                            Date old = orgin.parse(obj.getString("rp_regdate"));
                                            data.setRegdate(sdf.format(old));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        data.setU_idx(userinfo.getString("u_idx"));
                                        data.setPimg(userinfo.getString("pimg"));
                                        data.setP_wave1(userinfo.getString("p_wave1"));
                                        data.setP_wave2(userinfo.getString("p_wave2"));
                                        data.setP_wave3(userinfo.getString("p_wave3"));
                                        data.setP_movie1(userinfo.getString("p_movie1"));
                                        data.setP_movie2(userinfo.getString("p_movie2"));
                                        data.setP_movie3(userinfo.getString("p_movie3"));
                                        data.setP_gif1(userinfo.getString("p_gif1"));
                                        data.setP_gif2(userinfo.getString("p_gif2"));
                                        data.setP_gif3(userinfo.getString("p_gif3"));
                                        data.setCouple_type(userinfo.getString("couple_type"));
                                        data.setBrotherhood1(userinfo.getString("brotherhood1"));
                                        data.setBrotherhood2(userinfo.getString("brotherhood2"));
                                        data.setBrotherhood3(userinfo.getString("brotherhood3"));
                                        data.setHometown(userinfo.getString("hometown"));
                                        data.setHometown1(userinfo.getString("hometown1"));
                                        data.setJob_group(userinfo.getString("job_group"));
                                        data.setJob_detail(userinfo.getString("job_detail"));
                                        data.setJob_img(userinfo.getString("job_img"));
                                        data.setAnnual(userinfo.getString("annual"));
                                        data.setAnnual_img(userinfo.getString("annual_img"));
                                        data.setEducation(userinfo.getString("education"));
                                        data.setEducation_img(userinfo.getString("education_img"));
                                        data.setAbroad(userinfo.getString("abroad"));
                                        data.setProperty(userinfo.getString("property"));
                                        data.setStyle(userinfo.getString("style"));
                                        data.setPassionstyle1(userinfo.getString("passionstyle1"));
                                        data.setPassionstyle2(userinfo.getString("passionstyle2"));
                                        data.setHeight(userinfo.getString("height"));
                                        data.setWeight(userinfo.getString("weight"));
                                        data.setSmoke(userinfo.getString("smoke"));
                                        data.setDrink(userinfo.getString("drink"));
                                        data.setDrink_detail(userinfo.getString("drink_detail"));
                                        data.setHealth(userinfo.getString("health"));
                                        data.setHealth_detail(userinfo.getString("health_detail"));
                                        data.setBlood(userinfo.getString("blood"));
                                        data.setReligion(userinfo.getString("religion"));
                                        data.setCarinfo(userinfo.getString("carinfo"));
                                        data.setPersonality1(userinfo.getString("personality1"));
                                        data.setPersonality2(userinfo.getString("personality2"));
                                        data.setPersonality3(userinfo.getString("personality3"));
                                        data.setHobby1(userinfo.getString("hobby1"));
                                        data.setHobby2(userinfo.getString("hobby2"));
                                        data.setHobby3(userinfo.getString("hobby3"));
                                        data.setHobby4(userinfo.getString("hobby4"));
                                        data.setHobby5(userinfo.getString("hobby5"));
                                        data.setFood1(userinfo.getString("food1"));
                                        data.setFood2(userinfo.getString("food2"));
                                        data.setFood3(userinfo.getString("food3"));
                                        data.setFood4(userinfo.getString("food4"));
                                        data.setFood5(userinfo.getString("food5"));
                                        data.setFood6(userinfo.getString("food6"));
                                        data.setFood7(userinfo.getString("food7"));
                                        data.setFamily(userinfo.getString("family"));
                                        data.setP_introduce(userinfo.getString("p_introduce"));
                                        data.setFamily_info(userinfo.getString("family_info"));
                                        data.setSelf_score(userinfo.getString("self_score"));
                                        data.setWhen_marry(userinfo.getString("when_marry"));
                                        data.setHope_religion(userinfo.getString("hope_religion"));
                                        data.setHope_style1(userinfo.getString("hope_style1"));
                                        data.setHope_style2(userinfo.getString("hope_style2"));
                                        data.setHope_style3(userinfo.getString("hope_style3"));
                                        data.setHope_style4(userinfo.getString("hope_style4"));
                                        data.setHope_style5(userinfo.getString("hope_style5"));
                                        data.setHope_style6(userinfo.getString("hope_style6"));
                                        data.setHope_style7(userinfo.getString("hope_style7"));
                                        data.setReference(userinfo.getString("reference"));
                                        data.setAuth_num(userinfo.getString("auth_num"));
                                        data.setAuth_ok(userinfo.getString("auth_ok"));
                                        data.setRemarried(userinfo.getString("remarried"));
                                        data.setMarried_paper_img(userinfo.getString("married_paper_img"));
                                        data.setLastnameYN(userinfo.getString("lastnameYN"));
                                        data.setType(userinfo.getString("type"));
                                        data.setId(userinfo.getString("id"));
                                        data.setPw(userinfo.getString("pw"));
                                        data.setName(userinfo.getString("name"));
                                        data.setFamilyname(userinfo.getString("familyname"));
                                        data.setNick(userinfo.getString("nick"));
                                        data.setByear(userinfo.getString("byear"));
                                        data.setBmonth(userinfo.getString("bmonth"));
                                        data.setGender(userinfo.getString("gender"));
                                        data.setAddr1(userinfo.getString("addr1"));
                                        data.setAddr2(userinfo.getString("addr2"));
                                        data.setHopeaddr(userinfo.getString("hopeaddr"));
                                        data.setM_fcm_token(userinfo.getString("m_fcm_token"));
                                        data.setLat(userinfo.getString("lat"));
                                        data.setLon(userinfo.getString("lon"));
                                        data.setZodiac(userinfo.getString("zodiac"));
                                        data.setLoginYN(userinfo.getString("loginYN"));
                                        data.setInterest_idxs(userinfo.getString("interest_idxs"));
                                        data.setU_cell_num(userinfo.getString("u_cell_num"));
                                        data.setCoin(userinfo.getString("coin"));
                                        data.setPimg_ck(userinfo.getString("pimg_ck"));
                                        data.setPint_ck(userinfo.getString("pint_ck"));
                                        data.setCharacter(Common.getCharacterDrawable(userinfo.getString("character"), userinfo.getString("gender")));

                                        data.setCheckState(false);
                                    }
                                } else {
                                    data.setCate(cate);

                                    if (StringUtil.isToday(obj.getString("rp_regdate").split(" ")[0])) {
                                        todaycnt++;
                                    }

//                                    data.setIdx(obj.getString("idx"));

                                    if (!StringUtil.isNull(data.getIdx())) {
                                        if (StringUtil.isNull(obj.getString("rp_regdate"))) {

                                        } else {
                                            if (!predate.equalsIgnoreCase(obj.getString("rp_regdate").split(" ")[0])) {
                                                ProfreadData ditem = new ProfreadData();
                                                ditem.setRegdate(obj.getString("rp_regdate"));
                                                ditem.setItemtype(StringUtil.TYPEDATE);
                                                tmplist.add(ditem);
                                                predate = obj.getString("rp_regdate").split(" ")[0];
                                            }
                                        }
                                    }

                                    SimpleDateFormat orgin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm");

                                    try {
                                        Date old = orgin.parse(obj.getString("rp_regdate"));
                                        data.setRegdate(sdf.format(old));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    data.setP_user_idx(obj.getString("p_user_idx"));
                                    data.setR_user_idx(obj.getString("r_user_idx"));
//                                    data.setRegdate(obj.getString("regdate"));
                                    data.setU_idx(obj.getString("u_idx"));
                                    data.setPimg(obj.getString("pimg"));
                                    data.setP_wave1(obj.getString("p_wave1"));
                                    data.setP_wave2(obj.getString("p_wave2"));
                                    data.setP_wave3(obj.getString("p_wave3"));
                                    data.setP_movie1(obj.getString("p_movie1"));
                                    data.setP_movie2(obj.getString("p_movie2"));
                                    data.setP_movie3(obj.getString("p_movie3"));
                                    data.setP_gif1(obj.getString("p_gif1"));
                                    data.setP_gif2(obj.getString("p_gif2"));
                                    data.setP_gif3(obj.getString("p_gif3"));
                                    data.setCouple_type(obj.getString("couple_type"));
                                    data.setBrotherhood1(obj.getString("brotherhood1"));
                                    data.setBrotherhood2(obj.getString("brotherhood2"));
                                    data.setBrotherhood3(obj.getString("brotherhood3"));
                                    data.setHometown(obj.getString("hometown"));
                                    data.setHometown1(obj.getString("hometown1"));
                                    data.setJob_group(obj.getString("job_group"));
                                    data.setJob_detail(obj.getString("job_detail"));
                                    data.setJob_img(obj.getString("job_img"));
                                    data.setAnnual(obj.getString("annual"));
                                    data.setAnnual_img(obj.getString("annual_img"));
                                    data.setEducation(obj.getString("education"));
                                    data.setEducation_img(obj.getString("education_img"));
                                    data.setAbroad(obj.getString("abroad"));
                                    data.setProperty(obj.getString("property"));
                                    data.setStyle(obj.getString("style"));
                                    data.setPassionstyle1(obj.getString("passionstyle1"));
                                    data.setPassionstyle2(obj.getString("passionstyle2"));
                                    data.setHeight(obj.getString("height"));
                                    data.setWeight(obj.getString("weight"));
                                    data.setSmoke(obj.getString("smoke"));
                                    data.setDrink(obj.getString("drink"));
                                    data.setDrink_detail(obj.getString("drink_detail"));
                                    data.setHealth(obj.getString("health"));
                                    data.setHealth_detail(obj.getString("health_detail"));
                                    data.setBlood(obj.getString("blood"));
                                    data.setReligion(obj.getString("religion"));
                                    data.setCarinfo(obj.getString("carinfo"));
                                    data.setPersonality1(obj.getString("personality1"));
                                    data.setPersonality2(obj.getString("personality2"));
                                    data.setPersonality3(obj.getString("personality3"));
                                    data.setHobby1(obj.getString("hobby1"));
                                    data.setHobby2(obj.getString("hobby2"));
                                    data.setHobby3(obj.getString("hobby3"));
                                    data.setHobby4(obj.getString("hobby4"));
                                    data.setHobby5(obj.getString("hobby5"));
                                    data.setFood1(obj.getString("food1"));
                                    data.setFood2(obj.getString("food2"));
                                    data.setFood3(obj.getString("food3"));
                                    data.setFood4(obj.getString("food4"));
                                    data.setFood5(obj.getString("food5"));
                                    data.setFood6(obj.getString("food6"));
                                    data.setFood7(obj.getString("food7"));
                                    data.setFamily(obj.getString("family"));
                                    data.setP_introduce(obj.getString("p_introduce"));
                                    data.setFamily_info(obj.getString("family_info"));
                                    data.setSelf_score(obj.getString("self_score"));
                                    data.setWhen_marry(obj.getString("when_marry"));
                                    data.setHope_religion(obj.getString("hope_religion"));
                                    data.setHope_style1(obj.getString("hope_style1"));
                                    data.setHope_style2(obj.getString("hope_style2"));
                                    data.setHope_style3(obj.getString("hope_style3"));
                                    data.setHope_style4(obj.getString("hope_style4"));
                                    data.setHope_style5(obj.getString("hope_style5"));
                                    data.setHope_style6(obj.getString("hope_style6"));
                                    data.setHope_style7(obj.getString("hope_style7"));
                                    data.setReference(obj.getString("reference"));
                                    data.setAuth_num(obj.getString("auth_num"));
                                    data.setAuth_ok(obj.getString("auth_ok"));
                                    data.setRemarried(obj.getString("remarried"));
                                    data.setMarried_paper_img(obj.getString("married_paper_img"));
                                    data.setLastnameYN(obj.getString("lastnameYN"));
                                    data.setType(obj.getString("type"));
                                    data.setId(obj.getString("id"));
                                    data.setPw(obj.getString("pw"));
                                    data.setName(obj.getString("name"));
                                    data.setFamilyname(obj.getString("familyname"));
                                    data.setNick(obj.getString("nick"));
                                    data.setByear(obj.getString("byear"));
                                    data.setBmonth(obj.getString("bmonth"));
                                    data.setGender(obj.getString("gender"));
                                    data.setAddr1(obj.getString("addr1"));
                                    data.setAddr2(obj.getString("addr2"));
                                    data.setHopeaddr(obj.getString("hopeaddr"));
                                    data.setM_fcm_token(obj.getString("m_fcm_token"));
                                    data.setLat(obj.getString("lat"));
                                    data.setLon(obj.getString("lon"));
                                    data.setZodiac(obj.getString("zodiac"));
                                    data.setLoginYN(obj.getString("loginYN"));
                                    data.setInterest_idxs(obj.getString("interest_idxs"));
                                    data.setU_cell_num(obj.getString("u_cell_num"));
                                    data.setCoin(obj.getString("coin"));
                                    data.setPimg_ck(obj.getString("pimg_ck"));
                                    data.setPint_ck(obj.getString("pint_ck"));
                                    data.setCharacter(Common.getCharacterDrawable(obj.getString("character"), obj.getString("gender")));

                                    data.setCheckState(false);
                                }

                                if (cate.equalsIgnoreCase("all")) {
                                    if(!StringUtil.isNull(obj.getString("userinfo"))) {
                                        if (!StringUtil.isNull(data.getIdx())) {
                                            tmplist.add(data);
                                        }
                                    }
                                } else {
                                    if (!StringUtil.isNull(data.getIdx())) {
                                        tmplist.add(data);
                                    }
                                }
                            }


                            if (profreadList.size() > 0) {
                                profreadList.clear();
                            }

                            profreadList.addAll(tmplist);
                            adapter.notifyDataSetChanged();

                        } else {
                            if (profreadList.size() > 0) {
                                profreadList.clear();
                            }
                            adapter.notifyDataSetChanged();
                            Toast.makeText(act, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (profreadList.size() > 0) {
                            profreadList.clear();
                        }
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    if (profreadList.size() > 0) {
                        profreadList.clear();
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        };
        readList.setTag("Profread List (" + cate + ")");
        readList.addParams("u_idx", UserPref.getUidx(act)/* 로그인 멤버 인덱스 */);
        readList.execute(true, true);

    }
}