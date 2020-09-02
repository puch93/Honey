package com.match.honey.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.match.honey.R;
import com.match.honey.adapters.list.FirstAdapter;
import com.match.honey.databinding.ActivityFirstBinding;
import com.match.honey.listDatas.MemberData;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
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

public class FirstAct extends AppCompatActivity {
    ActivityFirstBinding binding;
    AppCompatActivity act;

    FirstAdapter adapter;
    ArrayList<MemberData> memlist = new ArrayList<>();

    String enter, msg_from, room_idx;
    boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_first, null);
        act = this;

        enter = getIntent().getStringExtra("enter");
        msg_from = getIntent().getStringExtra("msg_from");
        room_idx = getIntent().getStringExtra("room_idx");

        binding.frameMain.rcvMember.setLayoutManager(new LinearLayoutManager(act));
        adapter = new FirstAdapter(act, memlist);
        binding.frameMain.rcvMember.setAdapter(adapter);
        binding.frameMain.rcvMember.addItemDecoration(new ItemOffsetDecoration(act));

        getMemberList2();

        binding.touchArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.isNull(enter)) {
                    startActivity(new Intent(act, LoginAct.class));
                    finish();
                } else {
                    if (enter.equalsIgnoreCase("push")) {
                        Intent login = new Intent(act, LoginAct.class);
                        login.putExtra("enter", enter);
                        login.putExtra("msg_from", msg_from);
                        login.putExtra("room_idx", room_idx);
                        startActivity(login);
                        finish();
                    } else {
                        startActivity(new Intent(act, LoginAct.class));
                        finish();
                    }
                }
            }
        });

    }


    private void getMemberList2() {
        ReqBasic memList = new ReqBasic(act, NetUrls.MEMBERLIST) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            Log.e(StringUtil.TAG, "First List Get Info: " + jo);

                            JSONArray ja = jo.getJSONArray("value");
                            ArrayList<MemberData> tmplist = new ArrayList<>();

                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject obj = ja.getJSONObject(i);

                                if (i == 0) {
                                    Log.e(StringUtil.TAG, "obj: " + obj);
                                }
                                MemberData data = new MemberData();

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
//                                data.setIntroduce(obj.getString("p_introduce"));
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

                                data.setDist("-");

                                tmplist.add(data);
                            }

                            if (memlist.size() > 0) {
                                memlist.clear();
                            }

                            memlist.addAll(tmplist);
                            adapter.notifyDataSetChanged();

                        } else {
                            if (memlist.size() > 0) {
                                memlist.clear();
                            }
                            adapter.notifyDataSetChanged();
                            Toast.makeText(act, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        if (memlist.size() > 0) {
                            memlist.clear();
                        }
                        adapter.notifyDataSetChanged();
                        e.printStackTrace();
//                        Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                        Common.showToast(act, e.getMessage());
                    }
                } else {
                    if (memlist.size() > 0) {
                        memlist.clear();
                    }
                    adapter.notifyDataSetChanged();
//                    Common.showToast(act, "resultData.getResult() == null");
//                    Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }

            }
        };

        memList.addParams("uidx", "");
        memList.addParams("srloc1", "");
        memList.addParams("srloc2", "");
        memList.addParams("newYN", "Y");
        memList.addParams("gender", "");
        memList.addParams("byear", "");

        memList.addParams("religion", "");
        memList.addParams("annual", "");
        memList.addParams("property", "");
        memList.addParams("blood", "");
        memList.addParams("education", "");
        memList.addParams("smokeYN", "");
        memList.addParams("drinkYN", "");
        memList.addParams("childcnt", "all");
        memList.addParams("pictureYN", "");

        memList.execute(true, true);
    }
}
