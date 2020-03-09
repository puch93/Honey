package com.match.honey.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.match.honey.R;
import com.match.honey.activity.MainActivity;
import com.match.honey.activity.MultipleListDlgAct;
import com.match.honey.activity.OnlineMemberActivity;
import com.match.honey.adapters.list.MemberListAdapter;
import com.match.honey.databinding.ActivityNewuserBinding;
import com.match.honey.listDatas.MemberData;
import com.match.honey.listDatas.OnlinememData;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.Common;
import com.match.honey.utils.DefaultValue;
import com.match.honey.utils.ItemOffsetDecoration;
import com.match.honey.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewMemberFrag extends BaseFrag implements View.OnClickListener, MainActivity.onKeyBackPressedListener {
    ActivityNewuserBinding binding;
    Activity act;
    View view;

    ArrayList<MemberData> list = new ArrayList<>();
    ArrayList<OnlinememData> loginList = new ArrayList<>();

    MemberListAdapter adapter;
    private static final String TAG = "TEST_HOME";
    String hopeLoc = "";

    String[] myHopeLoc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_newuser, container, false);
        view = binding.getRoot();
        act = getActivity();

        setClickListener();

        String preferenceLoc = UserPref.getHopeLoc(act);
        myHopeLoc = preferenceLoc.split("\\|");
        Log.e(TAG, "myHopeLoc: " + myHopeLoc);

        binding.rcvHopeloc.setLayoutManager(new LinearLayoutManager(act));
        adapter = new MemberListAdapter(act, list);
        binding.rcvHopeloc.setAdapter(adapter);
        binding.rcvHopeloc.addItemDecoration(new ItemOffsetDecoration(act));

        getNewMemberPush();
        getList("");

        return view;
    }


    private void setClickListener() {
        binding.flBack.setOnClickListener(this);
        binding.llHopelocArea.setOnClickListener(this);
        binding.llOnlinemem.setOnClickListener(this);
    }

    private void setNewMemberPush(String locs) {
        ReqBasic newuser = new ReqBasic(act, NetUrls.SETNEWUSERPUSH) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "NewMember Push Set Info: " + jo);

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

        newuser.addParams("u_idx", UserPref.getUidx(act));
        if (locs.equalsIgnoreCase("전체")) {
            newuser.addParams("hope_addr_new", "all");
        } else {
            newuser.addParams("hope_addr_new", locs);
        }
        newuser.execute(true, false);
    }

    private void getNewMemberPush() {
        ReqBasic newuser = new ReqBasic(act, NetUrls.GETNEWUSERPUSH) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "NewMember Push Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase("Y")) {
                            String hope_addr_new = jo.getString("hope_Addr_new");
                            if(hope_addr_new.equalsIgnoreCase("서울,경기,인천,대전,대구,부산,광주,세종,울산,충북,충남,전북,전남,경북,경남,강원,제주,북한")) {
                                binding.tvHopeloc.setText("전체");
                            } else {
                                binding.tvHopeloc.setText(hope_addr_new);
                            }
                        } else {
                            binding.tvHopeloc.setText("전체");
                            setNewMemberPush("전체");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        };

        newuser.addParams("u_idx", UserPref.getUidx(act));
        newuser.execute(true, false);
    }


    private void getList(String locs) {
        if (!locs.equalsIgnoreCase("")) {
            for (int i = 0; i < myHopeLoc.length; i++) {
                if (!locs.contains(myHopeLoc[i])) {
                    locs += "|" + myHopeLoc[i];
                }
            }
        }

        ReqBasic newuser = new ReqBasic(act, NetUrls.NEWUSER) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "NewMember List Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {

                            JSONArray ja = new JSONArray(jo.getString("value"));
                            ArrayList<MemberData> tmplist = new ArrayList<>();

                            String predate = "";
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject obj = ja.getJSONObject(i);
                                Log.e(StringUtil.TAG, "obj(" + i + "): " + obj);


                                Log.e(StringUtil.TAG_ERROR, "regdate(" + i + "): " + obj.getString("regdate"));


                                MemberData data = new MemberData();

                                if (StringUtil.isNull(obj.getString("regdate"))) {

                                } else {
                                    if (!predate.equalsIgnoreCase(obj.getString("regdate").split(" ")[0])) {
                                        MemberData ditem = new MemberData();
                                        ditem.setRegDate(obj.getString("regdate"));
                                        ditem.setItemtype(StringUtil.TYPEDATE);
                                        tmplist.add(ditem);
                                        predate = obj.getString("regdate").split(" ")[0];
                                    }
                                }
                                data.setItemtype(StringUtil.TYPEITEM);
                                SimpleDateFormat orgin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm");

                                try {
                                    Date old = orgin.parse(obj.getString("regdate"));
                                    data.setRegDate(sdf.format(old));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                data.setMidx(obj.getString("u_idx"));
                                data.setContent(obj.getString("p_introduce"));
                                data.setMembertype(obj.getString("couple_type"));
                                data.setNickname(obj.getString("nick"));

                                data.setAge(StringUtil.calcAge(obj.getString("byear")));
                                data.setFashion01(obj.getString("passionstyle1"));
                                data.setFashion02(obj.getString("passionstyle2"));
                                data.setGender(obj.getString("gender"));
                                data.setLocation(obj.getString("addr1"));
                                data.setLocation2(obj.getString("addr2"));
                                data.setProfimg(obj.getString("pimg"));
                                data.setPimg_ck(obj.getString("pimg_ck"));
                                data.setPint_ck(obj.getString("pint_ck"));
                                data.setCharacter(Common.getCharacterDrawable(obj.getString("character"), obj.getString("gender")));
                                data.setZodiac(obj.getString("zodiac"));

                                data.setFamilyname(obj.getString("familyname"));
                                data.setName(obj.getString("name"));
                                data.setLastnameState(obj.getString("lastnameYN"));

                                data.setLat(obj.getString("lat"));
                                data.setLon(obj.getString("lon"));

                                if (obj.has("piimgcnt")) {
                                    data.setPiimgcnt(obj.getString("piimgcnt"));
                                }

                                tmplist.add(data);
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
                            Toast.makeText(act, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
//                        Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        newuser.addParams("u_idx", UserPref.getUidx(act));
        newuser.addParams("hope_addrs", locs);
        newuser.execute(true, true);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            Log.i(StringUtil.TAG, "data == null");
            return;
        }

        String res = data.getStringExtra("data");

        switch (requestCode) {
            case DefaultValue.HOPELOC:
                if (res.equalsIgnoreCase("전체")) {
                    binding.tvHopeloc.setText("전체");
                    setNewMemberPush("전체");
                } else {
                    binding.tvHopeloc.setText(res);
                    setNewMemberPush(res);
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

    private void exit() {
        ((MainActivity) act).switchFrame(true);
        ((MainActivity) act).getSupportFragmentManager().beginTransaction().remove(NewMemberFrag.this).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                exit();
                break;

            case R.id.ll_hopeloc_area:
                String location = binding.tvHopeloc.getText().toString();

                Intent hopeIntent = new Intent(act, MultipleListDlgAct.class);
                hopeIntent.putExtra("subject", "hope_loc2");
                hopeIntent.putExtra("select", location);
                startActivityForResult(hopeIntent, DefaultValue.HOPELOC);
                break;
            case R.id.ll_onlinemem:
                startActivity(new Intent(act, OnlineMemberActivity.class));

                break;
        }
    }
}
