package com.match.honey.activity;

import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.match.honey.R;
import com.match.honey.adapters.list.FriendBlockAdapter;
import com.match.honey.databinding.ActivityFriendBlockBinding;
import com.match.honey.listDatas.FriendBlockData;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.Common;
import com.match.honey.utils.StatusBarUtil;
import com.match.honey.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FriendBlockAct extends AppCompatActivity {
    ActivityFriendBlockBinding binding;
    AppCompatActivity act;
    ArrayList<FriendBlockData> list = new ArrayList<>();
    FriendBlockAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_friend_block, null);
        act = this;

        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.llHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) MainActivity.act).fromHomeBtn();
                finish();
            }
        });

        //차단
        binding.llAddressOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etAddress.length() == 0) {
                    Toast.makeText(act, "휴대폰번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!checkCellnum(binding.etAddress.getText().toString())) {
                    Toast.makeText(act, "휴대폰번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

//                for (int i = 0; i < list.size(); i++) {
//                    if (list.get(i).getAddress().equalsIgnoreCase(binding.etAddress.getText().toString())) {
//                        Common.showToast(act, "이미 차단한 전화번호 입니다");
//                        return;
//                    }
//                }

                setBlockNumber(binding.etAddress.getText().toString());
                binding.etAddress.setText(null);
            }
        });

        //차단해제
        binding.tvUnblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String unblock = "";
                for (int i = 0; i < list.size(); i++) {
                    Log.e(StringUtil.TAG, i + " : " + list.get(i).isChecked());
                    if (list.get(i).isChecked()) {
                        if (i == 0) {
                            unblock = list.get(i).getAddress();
                        } else {
                            unblock += "," + list.get(i).getAddress();
                        }
                    }
                }

                if(StringUtil.isNull(unblock)) {
                    Common.showToast(act, "해제할 번호를 선택해주세요");
                } else {
                    setFriendUnblock(unblock);
                }
            }
        });

        //차단리스트
        binding.rcvOnline.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FriendBlockAdapter(this, list);
        binding.rcvOnline.setAdapter(adapter);

        //차단리스트 가져오기
        getFriendBlockList();
    }

    private void setBlockNumber(String number) {
        ReqBasic block = new ReqBasic(this, NetUrls.FRIENDBLOCK) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        final String message = jo.getString("value");

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            getFriendBlockList();
                        } else {
                            Common.showToast(act, message);
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

        block.setTag("Set Friend Block");
        block.addParams("uidx", UserPref.getUidx(this));
        block.addParams("phoneNum", number);
        block.execute(true, false);
    }

    private void getFriendBlockList() {
        ReqBasic block = new ReqBasic(this, NetUrls.FRIENDBLOCKLIST) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Friend Block List Get Info: " + jo);
                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            JSONArray ja = jo.getJSONArray("value");

                            list = new ArrayList<>();
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);
                                FriendBlockData data = new FriendBlockData();
                                data.setAddress(job.getString("phoneNum"));
                                data.setChecked(false);
                                list.add(data);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(list);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    list = new ArrayList<>();
                                    adapter.setList(list);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        };

        block.addParams("uidx", UserPref.getUidx(this));
        block.execute(true, true);
    }

    private void setFriendUnblock(String unblock) {
        ReqBasic block = new ReqBasic(this, NetUrls.FRIENDBLOCKCANCEL) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Friend UnBlock Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            Common.showToast(act, "차단해제가 완료되었습니다");
                            getFriendBlockList();
                        } else {
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

        block.addParams("uidx", UserPref.getUidx(this));
        block.addParams("phoneNum", unblock);
        block.execute(true, true);
    }

    private boolean checkCellnum(String cellnum) {
        cellnum = PhoneNumberUtils.formatNumber(cellnum);

        boolean returnValue = false;
        try {
            String regex = "^\\s*(010|011|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$";

            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(cellnum);
            if (m.matches()) {
                returnValue = true;
            }

            if (returnValue && cellnum != null
                    && cellnum.length() > 0
                    && cellnum.startsWith("010")) {
                cellnum = cellnum.replace("-", "");
                if (cellnum.length() < 10) {
                    returnValue = false;
                }
            }
            return returnValue;
        } catch (Exception e) {
            return false;
        }
    }

}
