package com.match.marryme.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.match.marryme.R;
import com.match.marryme.adapters.list.MsgboxListAdapter;
import com.match.marryme.databinding.ActivityMoremsgboxBinding;
import com.match.marryme.listDatas.ChatDetailData;
import com.match.marryme.listDatas.MsgboxData;
import com.match.marryme.listDatas.OnlinememData;
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

public class MsgboxAct extends Activity implements View.OnClickListener {

    ActivityMoremsgboxBinding binding;

    ArrayList<MsgboxData> list = new ArrayList<>();
    ArrayList<OnlinememData> loginList = new ArrayList<>();
    MsgboxListAdapter adapter;

    public static Activity act;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_moremsgbox);
        act = this;

        binding.flBack.setOnClickListener(this);
        binding.llWrite.setOnClickListener(this);
        binding.llOnlinemem.setOnClickListener(this);
        binding.llDelmsglist.setOnClickListener(this);

        binding.rcvMsglist.setLayoutManager(new LinearLayoutManager(act));
        adapter = new MsgboxListAdapter(act, list);
        binding.rcvMsglist.setAdapter(adapter);
        binding.rcvMsglist.addItemDecoration(new ItemOffsetDecoration(act));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getChatList();
    }

    private void showDlgConfirm(final ArrayList<String> rooms) {
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


        title.setText("삭제하기");
        contents.setText("선택항목을 삭제 하시겠습니까?");

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < rooms.size(); i++) {
                    chatOut(rooms.get(i), i, rooms.size() - 1);
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

    // for문 처리 AsyncTask??
    private void chatOut(String ridx, final int curr, final int size) {
        ReqBasic chatOut = new ReqBasic(this, NetUrls.CHATOUT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            if (curr == size) {
                                if (list.size() > 0) {
                                    list.clear();
                                }

                                getChatList();
                                Toast.makeText(MsgboxAct.this, "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                                ((MainActivity) MainActivity.act).setMessageCount();
                            }
                        } else {
                            Toast.makeText(MsgboxAct.this, "삭제 되지않았습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MsgboxAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MsgboxAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }

            }
        };

        chatOut.addParams("uidx", UserPref.getUidx(this));
        chatOut.addParams("ridx", ridx);
        chatOut.execute(true, true);
    }

    public void getChatList() {
        ReqBasic chatList = new ReqBasic(act, NetUrls.CHATLIST) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Msgbox List Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            JSONObject job = jo.getJSONObject("data");
                            ArrayList<MsgboxData> tmplist = new ArrayList<>();

                            JSONArray chats = job.getJSONArray("chats");

                            if (chats.length() > 0) {
                                // 카운트 초기화
                                int todaycnt = 0;
                                int totalcnt = 0;
                                String predate = "";


                                for (int i = 0; i < chats.length(); i++) {
                                    JSONObject obj = chats.getJSONObject(i);
                                    Log.e(StringUtil.TAG, "Msgbox List Get Info(" + i + "): " + obj);


                                    // friend 값이 없거나, idx 값이 없으면 continue
                                    if (obj.has("friend")) {
                                        JSONObject friend = obj.getJSONArray("friend").getJSONObject(0);
                                        if (!friend.has("u_idx"))
                                            continue;
                                    } else {
                                        continue;
                                    }


                                    // 전체 카운트 증가
                                    totalcnt++;


                                    /* 날짜 데이터 세팅 */
                                    if (!predate.equalsIgnoreCase(obj.getString("created_at").split(" ")[0])) {
                                        MsgboxData ditem = new MsgboxData();
                                        ditem.setItemtype(StringUtil.TYPEDATE);

                                        SimpleDateFormat predf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                        SimpleDateFormat afterdf = new SimpleDateFormat("yyyy년 MM월 dd일");
                                        try {
                                            Date date = predf.parse(obj.getString("created_at"));
                                            ditem.setCreated_at(afterdf.format(date));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        tmplist.add(ditem);

                                        predate = obj.getString("created_at").split(" ")[0];
                                    }




                                    /* 채팅방 데이터 세팅 */
                                    MsgboxData data = new MsgboxData();
                                    data.setItemtype(StringUtil.TYPEITEM);

                                    SimpleDateFormat orgin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm");
                                    try {
                                        Date old = orgin.parse(obj.getString("created_at"));
                                        data.setCreated_at(sdf.format(old));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    data.setIdx(obj.getString("idx"));
                                    data.setUser_idx(obj.getString("user_idx"));
                                    data.setRoom_idx(obj.getString("room_idx"));
                                    data.setMsg(obj.getString("msg"));
                                    data.setRead_user_idx(obj.getString("read_user_idx"));
                                    data.setRu_bookmark(obj.getString("ru_bookmark"));


                                    // 채팅방별 메시지 카운트
                                    int msgcnt = Integer.parseInt(obj.getString("msgcnt"));
                                    if (msgcnt > 99) {
                                        data.setMsgcnt("99+");
                                    } else {
                                        data.setMsgcnt(obj.getString("msgcnt"));
                                    }


                                    // 오늘온 메시지 카운트
                                    if (StringUtil.isToday(obj.getString("created_at").split(" ")[0])) {
                                        todaycnt++;
                                    }


                                    JSONObject friend = obj.getJSONArray("friend").getJSONObject(0);

                                    ChatDetailData fdata = new ChatDetailData();

                                    fdata.setIdx(friend.getString("u_idx"));
                                    fdata.setPimg(friend.getString("pimg"));
                                    fdata.setPimg_ck(friend.getString("pimg_ck"));
                                    fdata.setCharacter(Common.getCharacterDrawable(friend.getString("character"), friend.getString("gender")));
                                    fdata.setType(friend.getString("type"));
                                    fdata.setId(friend.getString("id"));
                                    fdata.setPw(friend.getString("pw"));
                                    fdata.setName(friend.getString("name"));
                                    fdata.setFamilyName(friend.getString("familyname"));
                                    fdata.setNick(friend.getString("nick"));
                                    fdata.setByear(friend.getString("byear"));
                                    fdata.setBmonth(friend.getString("bmonth"));
                                    fdata.setGender(friend.getString("gender"));
                                    fdata.setAddr1(friend.getString("addr1"));
                                    fdata.setAddr2(friend.getString("addr2"));
                                    fdata.setHopeaddr(friend.getString("hopeaddr"));
                                    fdata.setM_fcm_token(friend.getString("m_fcm_token"));
                                    fdata.setRegdate(friend.getString("regdate"));
                                    fdata.setLat(friend.getString("lat"));
                                    fdata.setLon(friend.getString("lon"));
                                    fdata.setZodiac(friend.getString("zodiac"));
                                    fdata.setLoginYN(friend.getString("loginYN"));
                                    fdata.setInterest_idxs(friend.getString("interest_idxs"));
                                    fdata.setU_cell_num(friend.getString("u_cell_num"));
                                    fdata.setCoin(friend.getString("coin"));
                                    if (friend.has("piimgcnt")) {
                                        fdata.setPiimgcnt(friend.getString("piimgcnt"));
                                    }

                                    fdata.setLastNameYN(friend.getString("lastnameYN"));
                                    fdata.setCoupleType(friend.getString("couple_type"));
                                    fdata.setAge(StringUtil.calcAge(friend.getString("byear")));


                                    data.setFriend(fdata);
                                    tmplist.add(data);
                                }

                                binding.tvTotalnum.setText(StringUtil.setNumComma(totalcnt));
                                binding.tvTodaynum.setText(String.valueOf(todaycnt));

                                if (list.size() > 0) {
                                    list.clear();
                                }

                                list.addAll(tmplist);

                                act.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                    }
                                });

                            } else {
                                binding.tvTotalnum.setText("0");
                                binding.tvTodaynum.setText("0");
                            }

                        } else {
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                } else {
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }

            }
        };

        chatList.addParams("user_idx", UserPref.getUidx(act));
        Log.e("TEST_HOME", "user_idx: " + UserPref.getUidx(act));
        chatList.addParams("limit", "");       // 제한수
        chatList.addParams("offset", "");      // 시작레코드수
        chatList.execute(true, false);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;

            case R.id.ll_write:
                startActivity(new Intent(act, MyProfileAct.class));
                break;

            case R.id.ll_delmsglist:

                ArrayList<String> rooms = new ArrayList<>();
                for (MsgboxData item : list) {
                    if (!StringUtil.isNull(item.getIdx())) {
                        if (item.isCheckState()) {
                            rooms.add(item.getRoom_idx());
                        }
                    }
                }

                if (rooms.size() == 0) {
                    Toast.makeText(act, "항목을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                showDlgConfirm(rooms);
                break;
            case R.id.ll_onlinemem:
                startActivity(new Intent(act, OnlineMemberActivity.class));
                break;
        }
    }
}
