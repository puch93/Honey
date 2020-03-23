package com.match.honey.activity;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.match.honey.R;
import com.match.honey.adapters.list.ChatMessageAdapter;
import com.match.honey.adapters.list.InterimgAdapter;
import com.match.honey.databinding.ActivityChatBinding;
import com.match.honey.listDatas.ChatMessage;
import com.match.honey.listDatas.ProfileData;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.ChatValues;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.Common;
import com.match.honey.utils.DefaultValue;
import com.match.honey.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;

public class ChatAct extends AppCompatActivity implements View.OnClickListener {

    static final int REQUEST_TAKE_PHOTO = 2001;
    static final int REQUEST_TAKE_ALBUM = 2002;
    static final int REQUEST_IMAGE_CROP = 2003;

    final int LIMITMSGCNT = 3;

    final int INTERCNT = 3;

    ActivityChatBinding binding;

    Socket mSocket;
    public String pimg, nick, gender;
    public String pimg_ck;
    public int character;

    String midx, roomIdx, bookmarkState;
    ChatMessage message;

    ArrayList<ChatMessage> list = new ArrayList<>();
    ChatMessageAdapter adapter;

    ArrayList<Integer> interimg = new ArrayList<>();
    InterimgAdapter iimgAdapter;

    private Uri photoUri;
    private String mImgFilePath;
    public static AppCompatActivity act;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;


        if (!StringUtil.isNull(getIntent().getStringExtra("gender"))) {
            if (UserPref.getGender(act).equalsIgnoreCase(getIntent().getStringExtra("gender"))) {
                Common.showToast(act, "* 동성간에는 채팅이 불가합니다 *");
                finish();
            }
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);

        Log.e(StringUtil.TAG, "myIdx: " + UserPref.getUidx(act));

        if (!StringUtil.isNull(getIntent().getStringExtra("enter"))) {
            if (getIntent().getStringExtra("enter").equalsIgnoreCase("push")) {
                roomIdx = getIntent().getStringExtra("room_idx");
                midx = getIntent().getStringExtra("msg_from");

                getMyProfile();
            }
        } else {
            roomIdx = getIntent().getStringExtra("room_idx");
            Log.e(StringUtil.TAG, "roomIdx: " + roomIdx);
            midx = getIntent().getStringExtra("midx");
            pimg = getIntent().getStringExtra("pimg");
            nick = getIntent().getStringExtra("nick");
            gender = getIntent().getStringExtra("gender");
            pimg_ck = getIntent().getStringExtra("pimg_ck");
            Log.e(StringUtil.TAG, "pimg_ck: " + pimg_ck);
            character = getIntent().getIntExtra("character", 0);

            String meminfo = "(" + getIntent().getStringExtra("loc") + "/" + getIntent().getStringExtra("age") + "세)";
            binding.tvNick.setText(nick);

            binding.tvMeminfo.setText(meminfo);

            adapter = new ChatMessageAdapter(this, list);
            binding.rcvChat.setLayoutManager(new LinearLayoutManager(this));
            binding.rcvChat.setAdapter(adapter);

            setupSocketClient();
        }

        for (int i = 0; i < INTERCNT; i++) {
            interimg.add(i);
        }

        binding.rcvInterimg.setLayoutManager(new GridLayoutManager(this, 3));
        iimgAdapter = new InterimgAdapter(this, interimg);
        binding.rcvInterimg.setAdapter(iimgAdapter);

        binding.flBack.setOnClickListener(this);
        binding.llFavorite.setOnClickListener(this);
//        binding.tvProfile.setOnClickListener(this);

        binding.btnMsgbox.setOnClickListener(this);
        binding.btnBlock.setOnClickListener(this);
        binding.btnDelete.setOnClickListener(this);

//        binding.btnPlus.setOnClickListener(this);
        binding.btnMsgsend.setOnClickListener(this);

        binding.llBtnInter.setOnClickListener(this);

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
                            ProfileData data = new ProfileData();

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
                            data.setStyle(obj.getString("style"));
                            data.setType(obj.getString("type"));
                            data.setU_idx(obj.getString("u_idx"));
                            data.setWeight(obj.getString("weight"));
                            data.setWhen_marry(obj.getString("when_marry"));
                            data.setZodiac(obj.getString("zodiac"));
                            data.setCoin(obj.getString("coin"));

                            pimg = data.getPimg();
                            pimg_ck = data.getPimg_ck();
                            character = data.getCharacter_int();

                            nick = data.getNick();
                            gender = data.getGender();

                            String meminfo = nick + "(" + data.getAddr1() + "/" + StringUtil.calcAge(data.getByear()) + "세)";

                            binding.tvMeminfo.setText(meminfo);

                            adapter = new ChatMessageAdapter(act, list);
                            binding.rcvChat.setLayoutManager(new LinearLayoutManager(act));
                            binding.rcvChat.setAdapter(adapter);

                            setupSocketClient();

//                            if (UserPref.getGender(act).equalsIgnoreCase("female")) {
//                                if (gender.equalsIgnoreCase("female")){
//                                    if (StringUtil.isNull(UserPref.getMsgExpdate(act))) {
//                                        Intent payIntent = new Intent(act, DlgBuymsg.class);
//                                        payIntent.putExtra("act","chat");
//                                        startActivityForResult(payIntent, DefaultValue.CHATPAY);
//                                    }else {
//                                        if (StringUtil.calcExpire(UserPref.getMsgExpdate(act))) {
//                                            setupSocketClient();
//                                        } else {
//                                            Intent payIntent = new Intent(act, DlgBuymsg.class);
//                                            payIntent.putExtra("act","chat");
//                                            startActivityForResult(payIntent, DefaultValue.CHATPAY);
//                                        }
//                                    }
//                                }else{
//                                    setupSocketClient();
//                                }
//                            }else{
//                                if (StringUtil.isNull(UserPref.getMsgExpdate(act))) {
//                                    Intent payIntent = new Intent(act, DlgBuymsg.class);
//                                    payIntent.putExtra("act","chat");
//                                    startActivityForResult(payIntent, DefaultValue.CHATPAY);
//                                }else {
//                                    if (StringUtil.calcExpire(UserPref.getMsgExpdate(act))) {
//                                        setupSocketClient();
//                                    } else {
//                                        Intent payIntent = new Intent(act, DlgBuymsg.class);
//                                        payIntent.putExtra("act","chat");
//                                        startActivityForResult(payIntent, DefaultValue.CHATPAY);
//                                    }
//                                }
//                            }
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

        myprofile.addParams("uidx", midx);
        myprofile.execute(true, true);
    }


    // todo 소켓 세팅, 채팅 액티비티
    private void setupSocketClient() {
        try {
            Log.i(StringUtil.TAG, "setupSocketClient");
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                    return myTrustedAnchors;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .sslSocketFactory(sc.getSocketFactory()).build();

            // default settings for all sockets
            IO.setDefaultOkHttpWebSocketFactory(okHttpClient);
            IO.setDefaultOkHttpCallFactory(okHttpClient);

            // set as an option
            IO.Options opts = new IO.Options();
            opts.callFactory = okHttpClient;
            opts.webSocketFactory = okHttpClient;

            mSocket = IO.socket(ChatValues.SOCKET_URL);
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(ChatValues.CHATTING_HISTORY, onMessageReceived);
            mSocket.on(ChatValues.CHATTING_SYSTEM_MSG, onChatReceive);
            mSocket.connect();
            System.out.println("socket setup!!! ");
        } catch (URISyntaxException e) {
            Log.i(StringUtil.TAG, "URISyntaxException");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            Log.i(StringUtil.TAG, "NoSuchAlgorithmException");
            e.printStackTrace();
        } catch (KeyManagementException e) {
            Log.i(StringUtil.TAG, "KeyManagementException");
            e.printStackTrace();
        }

    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject sendData = new JSONObject();
            Log.e(StringUtil.TAG_SOCKET, "onConnect");
            System.out.println("socket onConnect : " + sendData);
            try {
                sendData.put(ChatValues.ROOMIDX, roomIdx);
                sendData.put(ChatValues.TALKER, UserPref.getUidx(act));
                mSocket.emit(ChatValues.EVENT_ENTERED, sendData);

                Log.e(StringUtil.TAG, "onConnect Put: " + sendData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    // 채팅 내역(이전 대화 내용)
    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(StringUtil.TAG_SOCKET, "onMessageReceived");
            JSONObject rcvData = (JSONObject) args[0];
            String selectDate = null;

            try {

                JSONObject roomUser = new JSONObject(rcvData.getString("roomUser"));

                JSONArray users = new JSONArray(roomUser.getString("users"));
                for (int j = 0; j < users.length(); j++) {
                    JSONObject obj = users.getJSONObject(j);

                    if (obj.getString("user_idx").equalsIgnoreCase(UserPref.getUidx(act))) {
                        bookmarkState = obj.getString("ru_bookmark");
                    }
                }

                list = new ArrayList<>();

                JSONArray ja = new JSONArray(rcvData.getString("chats"));
                if (ja.length() > 0) {

                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        Log.e(StringUtil.TAG, "chat_list(" + i + "): " + jo);
                        jo.getString("idx");
                        jo.getString("msg");
                        jo.getString("read_user_idx");
                        jo.getString("room_idx");
                        jo.getString("user_idx");
                        jo.getString("created_at");

                        String type = "";
                        if (jo.getString("user_idx").equalsIgnoreCase(UserPref.getUidx(act))) {
                            type = StringUtil.CTYPEMY;
                        } else {
                            type = StringUtil.CTYPEOTHER;
                        }

                        String readnum = "1";
                        if (jo.getString("read_user_idx").contains(",")) {
                            readnum = "0";
                        } else {
                            readnum = "1";
                        }

                        if (type.equalsIgnoreCase(StringUtil.CTYPEMY)) {
                            message = new ChatMessage(type, jo.getString("msg"), jo.getString("created_at"), jo.getString("user_idx"), readnum);
                        } else {
                            message = new ChatMessage(type, jo.getString("msg"), jo.getString("created_at"), jo.getString("user_idx"), "0");
                        }

                        list.add(message);
                    }

                    adapter.setList(list);
//                    list.addAll(tmplist);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(StringUtil.TAG, "data: " + message.toString());
                            adapter.notifyDataSetChanged();
                            binding.rcvChat.scrollToPosition(adapter.getItemCount() - 1);

                            ((MainActivity) MainActivity.act).setMessageCount();
                        }
                    });
                }

            } catch (JSONException e) {
                Log.i(StringUtil.TAG, "JSONException: " + e.toString());
                e.printStackTrace();
            }

        }
    };

    // 실시간 메세지 처리
    private Emitter.Listener onChatReceive = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(StringUtil.TAG_SOCKET, "onChatReceive");
            JSONObject rcvData = (JSONObject) args[0];
            String selectDate = null;

            try {
                JSONObject from = new JSONObject(rcvData.getString("from"));
                Log.e(StringUtil.TAG, "onChatReceive (from): " + from);

                JSONObject chat = new JSONObject(from.getString("chat"));
                Log.e(StringUtil.TAG, "onChatReceive (chat): " + chat);

                chat.getString("idx");
                chat.getString("msg");
                chat.getString("read_user_idx");
                chat.getString("room_idx");
                chat.getString("user_idx");
                chat.getString("created_at");

                String type = "";

                String readnum = "1";
                if (chat.getString("read_user_idx").contains(",")) {
                    readnum = "0";
                } else {
                    readnum = "1";
                }

                if (chat.getString("user_idx").equalsIgnoreCase(UserPref.getUidx(act))) {
//                    myMsgcnt++;
                    type = StringUtil.CTYPEMY;
                    message = new ChatMessage(type, chat.getString("msg"), chat.getString("created_at"), chat.getString("user_idx"), readnum);
                    list.add(message);
                } else {
                    type = StringUtil.CTYPEOTHER;
                    message = new ChatMessage(type, chat.getString("msg"), chat.getString("created_at"), chat.getString("user_idx"), "");
                    list.add(message);
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(StringUtil.TAG, "data: " + message.toString());
                        adapter.setList(list);
                        adapter.notifyDataSetChanged();
                        binding.rcvChat.smoothScrollToPosition(adapter.getItemCount());
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

    private void showImgloadDlg() {
        LayoutInflater dialog = LayoutInflater.from(this);
        View dlgLayout = dialog.inflate(R.layout.dlg_basicimgload, null);
//        View dlgLayout = dialog.inflate(R.layout.dlg_imgload, null);
        final Dialog dlgImgload = new Dialog(this);
        dlgImgload.setContentView(dlgLayout);

        dlgImgload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dlgImgload.show();

        TextView btn_album = (TextView) dlgLayout.findViewById(R.id.btn_album);
        TextView btn_camera = (TextView) dlgLayout.findViewById(R.id.btn_camera);


        btn_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAlbum();
                if (dlgImgload.isShowing()) {
                    dlgImgload.dismiss();
                }
            }
        });

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
                if (dlgImgload.isShowing()) {
                    dlgImgload.dismiss();
                }
            }
        });
    }

    private void sendMsg(final String msg, final String type, final int resid) {
        binding.etChatmsg.setText("");
        ReqBasic sendMsg = new ReqBasic(this, NetUrls.CHATPUSH) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Send Msg Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
//                            if(type.equals("text")) {
//                                long now = System.currentTimeMillis();
//                                Date date = new Date(now);
//                                SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                String adate = dateFormat2.format(date);
//
//                                message = new ChatMessage(StringUtil.CTYPEMY, msg, adate, UserPref.getUidx(act), "1");
//                                list.add(message);
//
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        adapter.notifyDataSetChanged();
//                                        binding.rcvChat.smoothScrollToPosition(adapter.getItemCount());
//                                    }
//                                });
//                            } else {
//                                long now = System.currentTimeMillis();
//                                Date date = new Date(now);
//                                SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                String adate = dateFormat2.format(date);
//
//                                String msg = StringUtil.IMOTICON + resid;
//
//                                message = new ChatMessage(StringUtil.CTYPEMY, msg, adate, UserPref.getUidx(act), "1");
//                                list.add(message);
//                                adapter.notifyDataSetChanged();
//                                binding.rcvChat.smoothScrollToPosition(adapter.getItemCount());
//                            }

                            try {
                                JSONObject sendData = new JSONObject();
                                sendData.put(ChatValues.ROOMIDX, roomIdx);
                                sendData.put(ChatValues.TALKER, UserPref.getUidx(act));
                                sendData.put(ChatValues.MSG, msg);
                                mSocket.emit(ChatValues.SEND_MSG, sendData);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(act, jo.getString("msg"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        // 상대방이 방을 삭제했을 수 있습니다. 채팅방을 나가서 다시 들어와주세요.
                        Toast.makeText(act, "서버 또는 네트워크 연결상태가 좋지 않습니다.\n상대방이 방을 삭제했을 수 있습니다.\n채팅방을 나가서 다시 들어와주세요.", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(act, "서버 또는 네트워크 연결상태가 좋지 않습니다.\n상대방이 방을 삭제했을 수 있습니다.\n채팅방을 나가서 다시 들어와주세요.", Toast.LENGTH_LONG).show();
                }
            }
        };

        sendMsg.addParams("send_idx", UserPref.getUidx(this));
        sendMsg.addParams("msg", msg);
        sendMsg.addParams("room_idx", roomIdx);
        sendMsg.execute(true, true);
    }

    // 삭제하기
    private void chatOut(String ridx) {
        ReqBasic chatOut = new ReqBasic(this, NetUrls.CHATOUT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            Toast.makeText(act, "채팅방 삭제를 완료 했습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(act, "서버 또는 네트워크 연결상태가 좋지 않습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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

        chatOut.addParams("uidx", UserPref.getUidx(this));
        chatOut.addParams("ridx", ridx);
        chatOut.execute(true, true);
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
                            Toast.makeText(act, "차단멤버 리스트에 추가됐습니다", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(act, MainActivity.class));
                            finishAffinity();
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

        block.setTag("Block Result");
        block.addParams("uidx", UserPref.getUidx(this));
        block.addParams("tidx", midx);
        block.execute(true, true);
    }

//    // 이미지 업로드
//    private void sendImg() {
//        ReqBasic sendImg = new ReqBasic(this, NetUrls.CHATIMGUP) {
//            @Override
//            public void onAfter(int resultCode, HttpResult resultData) {
//
//                if (resultData.getResult() != null) {
//
//                    long now = System.currentTimeMillis();
//                    Date date = new Date(now);
//                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    String adate = dateFormat2.format(date);
//
//                    String msg = resultData.getResult().replaceAll("\"", "");
//
//                    message = new ChatMessage(StringUtil.CTYPEMY, msg, adate, UserPref.getUidx(act), "1");
//                    list.add(message);
//                    sendMsg(msg);
//
//                } else {
//                    Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
//                }
//            }
//        };
//
//        File img = new File(mImgFilePath);
//        sendImg.addFileParams("pimg", img);
//        sendImg.execute(true, true);
//    }

    public void sendInterimt(int resid) {
        String msg = StringUtil.IMOTICON + resid;
        sendMsg(msg, "inter", resid);
    }

    // 메시지 사용가능 체크
    public void msgUsableCheck(final String msgType, final String msg) {
//        if(UserPref.getUidx(act).equalsIgnoreCase("40") || UserPref.getGender(act).equalsIgnoreCase("female")) {
        if (UserPref.getGender(act).equalsIgnoreCase("female")) {
            if (msgType.equalsIgnoreCase("img")) {
//                sendImg();
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = binding.etChatmsg.getText().toString();

                        sendMsg(msg, "text", 0);
                    }
                });
            }
        } else {
            ReqBasic check = new ReqBasic(this, NetUrls.ITEMCHECK) {
                @Override
                public void onAfter(int resultCode, HttpResult resultData) {

                    if (resultData.getResult() != null) {
                        try {
                            JSONObject jo = new JSONObject(resultData.getResult());
                            Log.e("TEST_HOME", "Item Check Get Info: " + jo);

                            if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                                if (msgType.equalsIgnoreCase("img")) {
//                                    sendImg();
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String msg = binding.etChatmsg.getText().toString();

                                            sendMsg(msg, "text", 0);
                                        }
                                    });
                                }

                            } else {
                                //이용불가일때
                                Intent intent = new Intent(act, ItemmanageAct.class);
                                intent.putExtra("from", "chat_msg");
                                startActivity(intent);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                    }
                }
            };
            check.addParams("uidx", UserPref.getUidx(this));
            check.addParams("type", "message");
            check.execute(true, false);
        }
    }


    // 관심있어요 사용가능 체크
    public void interestUsableCheck(final int resid) {
//        if(UserPref.getUidx(act).equalsIgnoreCase("40") || UserPref.getGender(act).equalsIgnoreCase("female")) {
        if (UserPref.getGender(act).equalsIgnoreCase("female")) {
            sendInterimt(resid);
        } else {
            ReqBasic check = new ReqBasic(this, NetUrls.ITEMCHECK) {
                @Override
                public void onAfter(int resultCode, HttpResult resultData) {

                    if (resultData.getResult() != null) {
                        try {
                            JSONObject jo = new JSONObject(resultData.getResult());
                            Log.e("TEST_HOME", "Item Check Get Info: " + jo);

                            if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                                sendInterimt(resid);
                            } else {
                                //이용불가일때
                                Intent intent = new Intent(act, ItemmanageAct.class);
                                intent.putExtra("from", "chat_inter");
                                startActivity(intent);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                    }
                }
            };
            check.addParams("uidx", UserPref.getUidx(this));
            check.addParams("type", "interest");
            check.execute(true, false);
        }
    }

    // 관심있어요 사용가능 체크
    public void interestUsableCheckOk() {
        if (UserPref.getGender(act).equalsIgnoreCase("female")) {
            //TODO 사용가능할때 코드
            binding.rcvInterimg.setVisibility(View.VISIBLE);
            binding.etChatmsg.setEnabled(false);
            binding.etChatmsg.setFocusable(false);
            binding.etChatmsg.setClickable(false);
            binding.etChatmsg.setFocusableInTouchMode(false);
        } else {
            ReqBasic check = new ReqBasic(this, NetUrls.ITEMCHECK) {
                @Override
                public void onAfter(int resultCode, HttpResult resultData) {

                    if (resultData.getResult() != null) {
                        try {
                            JSONObject jo = new JSONObject(resultData.getResult());
                            Log.e("TEST_HOME", "Item Check Get Info: " + jo);

                            if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                                //TODO 사용가능할때 코드
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.rcvInterimg.setVisibility(View.VISIBLE);
                                        binding.etChatmsg.setEnabled(false);
                                        binding.etChatmsg.setFocusable(false);
                                        binding.etChatmsg.setClickable(false);
                                        binding.etChatmsg.setFocusableInTouchMode(false);
                                    }
                                });
                            } else {
                                //이용불가일때
                                Intent intent = new Intent(act, ItemmanageAct.class);
                                intent.putExtra("from", "chat_inter");
                                startActivity(intent);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                    }
                }
            };
            check.addParams("uidx", UserPref.getUidx(this));
            check.addParams("type", "interest");
            check.execute(true, false);
        }
    }

    // 즐겨찾기
    private void setBookmark() {
        ReqBasic bookmark = new ReqBasic(this, NetUrls.CHATBOOOKMARK) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            Toast.makeText(act, jo.getString("msg"), Toast.LENGTH_SHORT).show();
                            if (bookmarkState.equalsIgnoreCase("Y")) {
                                bookmarkState = "N";
                            } else {
                                bookmarkState = "Y";
                            }
                        } else {
                            Toast.makeText(act, jo.getString("msg"), Toast.LENGTH_SHORT).show();
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

        bookmark.addParams("uidx", UserPref.getUidx(this));
        bookmark.addParams("ridx", roomIdx);

        // 현재 북마크 상태로 Y/N값 설정 후 전송
        if (bookmarkState.equalsIgnoreCase("Y")) {
            bookmark.addParams("rbookmark", "N");
        } else {
            bookmark.addParams("rbookmark", "Y");
        }

        bookmark.execute(true, true);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "ybyb" + timeStamp;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/MARRYME/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }

        if (photoFile != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                photoUri = FileProvider.getUriForFile(this,
                        "com.match.honey.provider", photoFile);
            } else {
                photoUri = Uri.fromFile(photoFile);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        }
    }

    private void getAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
    }

    private void cropImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.grantUriPermission("com.android.camera", photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);

        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(this, "용량이 큰 사진의 경우 시간이 오래 걸릴 수 있습니다.", Toast.LENGTH_SHORT).show();


            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            File croppedFileName = null;
            try {
                croppedFileName = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            File folder = new File(Environment.getExternalStorageDirectory() + "/MARRYME/");
            File tempFile = new File(folder.toString(), croppedFileName.getName());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                photoUri = FileProvider.getUriForFile(act,
                        "com.match.honey.provider", tempFile);
            } else {
                photoUri = Uri.fromFile(tempFile);
            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);

            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            grantUriPermission(res.activityInfo.packageName, photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, REQUEST_IMAGE_CROP);
        }
    }

    private void showDlgBlock() {
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

        title.setText("차단하기");
        contents.setText("차단 하시겠습니까?");

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reqBlock();
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

    private void showDlgChatout() {
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
        contents.setText("채팅방을 삭제 하시겠습니까?");

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatOut(roomIdx);
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
    protected void onDestroy() {
        super.onDestroy();
        if (mSocket != null && mSocket.connected()) {
            mSocket.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                cropImage();
                MediaScannerConnection.scanFile(act, new String[]{photoUri.getPath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {

                            }
                        });
                break;
            case REQUEST_TAKE_ALBUM:
                if (data == null) {
                    Toast.makeText(this, "사진불러오기 실패", Toast.LENGTH_SHORT).show();
                    return;
                }
                photoUri = data.getData();
                cropImage();
                break;
            case REQUEST_IMAGE_CROP:
                mImgFilePath = photoUri.getPath();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bm = BitmapFactory.decodeFile(mImgFilePath, options);

                Bitmap resize = null;
                try {
                    File resize_file = new File(mImgFilePath);
                    FileOutputStream out = new FileOutputStream(resize_file);

                    int width = bm.getWidth();
                    int height = bm.getHeight();

                    if (width > 1024) {
                        int resizeHeight = 0;
                        if (height > 768) {
                            resizeHeight = 768;
                        } else {
                            resizeHeight = height / (width / 1024);
                        }

                        resize = Bitmap.createScaledBitmap(bm, 1024, resizeHeight, true);
                        resize.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    } else {
                        resize = Bitmap.createScaledBitmap(bm, width, height, true);
                        resize.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    }

//                    sendImg();
                    msgUsableCheck("img", null);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                break;
            case DefaultValue.CHATPAY:
                String res = data.getStringExtra("pay");
                if (res.equalsIgnoreCase("Y")) {
                    setupSocketClient();
                } else {
                    Toast.makeText(this, "메세지이용권 구매후 이용가능합니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.rcvInterimg.getVisibility() == View.VISIBLE) {
            binding.rcvInterimg.setVisibility(View.GONE);
            binding.etChatmsg.setEnabled(true);
            binding.etChatmsg.setFocusable(true);
            binding.etChatmsg.setClickable(true);
            binding.etChatmsg.setFocusableInTouchMode(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;
//            case R.id.tv_profile:
//                Log.e(StringUtil.TAG, "gender: " + gender + ", midx: " + midx);
//                Intent profIntent = new Intent(act, ProfileDetailAct.class);
//                profIntent.putExtra("gender", gender);
//                profIntent.putExtra("midx", midx);
//                startActivity(profIntent);
//                break;
            case R.id.ll_favorite:
                setBookmark();
                break;
            case R.id.btn_msgbox:
                startActivity(new Intent(this, MsgboxAct.class));
                break;
            case R.id.btn_block:
                showDlgBlock();
                break;
            case R.id.btn_delete:
                showDlgChatout();
                break;
            case R.id.iv_profimg:
                Intent pintent = new Intent(this, ProfileDetailAct.class);
                pintent.putExtra("midx", midx);
                startActivity(pintent);
                break;
//            case R.id.btn_plus:
//                if (binding.rcvInterimg.getVisibility() == View.GONE) {
//                    showImgloadDlg();
//                }
//                break;
            case R.id.btn_msgsend:
                if (binding.rcvInterimg.getVisibility() == View.GONE) {
                    if (binding.etChatmsg.length() == 0) {
                        Toast.makeText(act, "메세지를 입력해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }

//                    String msg = binding.etChatmsg.getText().toString();
//
//                    long now = System.currentTimeMillis();
//                    Date date = new Date(now);
//                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    String adate = dateFormat2.format(date);
//
//                    message = new ChatMessage(StringUtil.CTYPEMY, msg, adate, UserPref.getUidx(this), "1");
//                    list.add(message);
//                    adapter.notifyDataSetChanged();
//                    binding.rcvChat.smoothScrollToPosition(adapter.getItemCount());

//                    sendMsg(msg);
                    msgUsableCheck("msg", null);
                }
                break;
            case R.id.ll_btn_inter:
                if (binding.rcvInterimg.getVisibility() == View.VISIBLE) {
                    binding.rcvInterimg.setVisibility(View.GONE);
                    binding.etChatmsg.setEnabled(true);
                    binding.etChatmsg.setFocusable(true);
                    binding.etChatmsg.setClickable(true);
                    binding.etChatmsg.setFocusableInTouchMode(true);
                } else {
                    interestUsableCheckOk();
                }
                break;
        }
    }
}
