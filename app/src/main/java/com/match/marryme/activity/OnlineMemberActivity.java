package com.match.marryme.activity;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.match.marryme.R;
import com.match.marryme.adapters.list.MemberListAdapter;
import com.match.marryme.databinding.ActivityOnlineMemberBinding;
import com.match.marryme.listDatas.MemberData;
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

public class OnlineMemberActivity extends AppCompatActivity {
    ActivityOnlineMemberBinding binding;
    Activity act;

    ArrayList<MemberData> list = new ArrayList<>();
    MemberListAdapter adapter;
    private static final String TAG = "TEST_HOME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_online_member, null);
        act = this;

        binding.rcvOnline.setLayoutManager(new LinearLayoutManager(act));
        adapter = new MemberListAdapter(act, list);
        binding.rcvOnline.setAdapter(adapter);
        binding.rcvOnline.addItemDecoration(new ItemOffsetDecoration(act));

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        getList();
    }

    private void getList() {
        ReqBasic newuser = new ReqBasic(act, NetUrls.ONLINEUSER) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Online List Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {

                            JSONArray ja = new JSONArray(jo.getString("value"));
                            ArrayList<MemberData> tmplist = new ArrayList<>();

                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject obj = ja.getJSONObject(i);

                                if (i == 0) {
                                    Log.e(TAG, "obj: " + obj);
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
                                data.setLocation(obj.getString("addr1"));
                                data.setLocation2(obj.getString("addr2"));
                                data.setProfimg(obj.getString("pimg"));
                                data.setZodiac(obj.getString("zodiac"));
                                data.setPimg_ck(obj.getString("pimg_ck"));
                                data.setPint_ck(obj.getString("pint_ck"));
                                data.setCharacter(Common.getCharacterDrawable(obj.getString("character"), obj.getString("gender")));

                                data.setFamilyname(obj.getString("familyname"));
                                data.setName(obj.getString("name"));
                                data.setLastnameState(obj.getString("lastnameYN"));

                                data.setLat(obj.getString("lat"));
                                data.setLon(obj.getString("lon"));

                                if(obj.has("piimgcnt")) {
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

        newuser.addParams("uidx", UserPref.getUidx(act));
        newuser.execute(true, true);
    }
}
