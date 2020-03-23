package com.match.honey.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.match.honey.R;
import com.match.honey.databinding.ActivitySetbasicinfoBinding;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.Common;
import com.match.honey.utils.DefaultValue;
import com.match.honey.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasicinfoSettingAct extends AppCompatActivity implements View.OnClickListener {

    ActivitySetbasicinfoBinding binding;

    Geocoder geocoder;

    public static AppCompatActivity act;
    boolean isAvailable = true;

    boolean nickCheck = true, locationCheck = true, marriageCheck = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setbasicinfo);
        act = this;

        geocoder = new Geocoder(this);

        setClickListener();

        getMyProfile();

        binding.etNickname.setClickable(false);
        binding.etNickname.setFocusable(false);

//        checkReviseOk(NetUrls.CHECKNICKREVISE);
//        checkReviseOk(NetUrls.CHECKLOCREVISE);
//        checkReviseOk(NetUrls.CHECKMARRYREVISE);
    }

    private void checkReviseOk(final String addr) {
        ReqBasic req = new ReqBasic(this, addr) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Revise Ok Get Info: " + jo);


                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RFAIL)) {
                            switch (addr) {
                                case NetUrls.CHECKNICKREVISE:
                                    Log.e(StringUtil.TAG, addr);
                                    nickCheck = false;
                                    binding.etNickname.setClickable(false);
                                    binding.etNickname.setFocusable(false);
                                    break;
                                case NetUrls.CHECKLOCREVISE:
                                    Log.e(StringUtil.TAG, addr);
                                    locationCheck = false;
                                    binding.tvMainLoc.setClickable(false);
                                    binding.tvMainLoc.setFocusable(false);

                                    binding.tvMiddleLoc.setClickable(false);
                                    binding.tvMiddleLoc.setFocusable(false);
                                    break;
                                case NetUrls.CHECKMARRYREVISE:
                                    Log.e(StringUtil.TAG, addr);
                                    marriageCheck = false;
                                    binding.rdoMarry.setClickable(false);
                                    binding.rdoRemarry.setClickable(false);
//                                    binding.rdoFriend.setClickable(false);
                                    break;
                            }
                        } else {
                            Log.e(StringUtil.TAG, "success: " + addr);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        req.addParams("uidx", UserPref.getUidx(this));
        req.execute(true, false);
    }


    private void setClickListener() {
        binding.flBack.setOnClickListener(this);

        binding.btnLogout.setOnClickListener(this);

        binding.tvBirthYear.setOnClickListener(this);
        binding.tvHopeLoc.setOnClickListener(this);
        binding.tvMainLoc.setOnClickListener(this);
        binding.tvMiddleLoc.setOnClickListener(this);
        binding.tvModify.setOnClickListener(this);

    }

    private void getMyProfileBasic() {
        ReqBasic req = new ReqBasic(this, NetUrls.MYPROFILEBASIC) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Basic Info Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        req.addParams("uidx", UserPref.getUidx(this));
        req.execute(true, true);
    }

    private void getMyProfile() {
        ReqBasic myprofile = new ReqBasic(this, NetUrls.MYPROFILE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "MyInfo List Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            JSONObject obj = new JSONObject(jo.getString("value"));
                            binding.tvEmail.setText(obj.getString("id"));
                            binding.etPw.setText(UserPref.getPw(act));
                            Log.e(StringUtil.TAG, "etPw: " + UserPref.getPw(act));
                            if(!obj.getString("type").equalsIgnoreCase("general") || !obj.getString("id").contains("@")) {
                                binding.etPw.setClickable(false);
                                binding.etPw.setFocusable(false);

                                binding.etNewpw.setFocusable(false);
                                binding.etCheckpw.setFocusable(false);

                                binding.etNewpw.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Common.showToast(act, "SNS 가입회원은 비밀번호를 수정하실 수 없습니다");
                                    }
                                });

                                binding.etCheckpw.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Common.showToast(act, "SNS 가입회원은 비밀번호를 수정하실 수 없습니다");
                                    }
                                });
                            }

                            binding.etNickname.setText(obj.getString("nick"));
                            binding.tvBirthYear.setText(obj.getString("byear"));
                            binding.etName.setText(obj.getString("name"));
                            binding.etFamilyname.setText(obj.getString("familyname"));

                            binding.tvHopeLoc.setText(obj.getString("hopeaddr").replaceAll("\\|", ","));
                            if (obj.getString("couple_type").equalsIgnoreCase("marry")) {
                                binding.rdoMarry.setChecked(true);
                            } else if (obj.getString("couple_type").equalsIgnoreCase("remarry")) {
                                binding.rdoRemarry.setChecked(true);
                            } else if (obj.getString("couple_type").equalsIgnoreCase("friend")) {
                                binding.rdoRemarry.setChecked(true);
                            }
                            if (obj.getString("lastnameYN").equalsIgnoreCase("Y")) {
                                binding.rdoFamilyname.setChecked(true);
                            } else {
                                binding.rdoName.setChecked(true);
                            }

                            binding.tvMainLoc.setText(obj.getString("addr1"));
                            binding.tvMiddleLoc.setText(obj.getString("addr2"));
                        } else {
                            Toast.makeText(BasicinfoSettingAct.this, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(BasicinfoSettingAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BasicinfoSettingAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        myprofile.addParams("uidx", UserPref.getUidx(this));
        myprofile.execute(true, true);
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

    private void logOut() {
        ReqBasic logout = new ReqBasic(this, NetUrls.LOGOUT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            Log.i(StringUtil.TAG, "로그아웃 상태 변경");
                        } else {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {

                }
            }
        };

        logout.addParams("uidx", UserPref.getUidx(this));
        logout.execute(true, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            Log.i(StringUtil.TAG, "data == null");
            return;
        }

        String result = data.getStringExtra("data");

        switch (requestCode) {
            case DefaultValue.HOPELOC:
                if (result.equalsIgnoreCase("")) {
                    binding.tvHopeLoc.setText(null);
                } else {
                    binding.tvHopeLoc.setText(result.replaceAll("\\|", ","));
                }
                break;

            case DefaultValue.MAINLOC:
                String midLoc = "상세지역";
                switch (result) {
                    case "서울":
                        midLoc = getResources().getStringArray(R.array.seoul)[0];
                        break;
                    case "경기":
                        midLoc = getResources().getStringArray(R.array.gyeongi)[0];
                        break;
                    case "인천":
                        midLoc = getResources().getStringArray(R.array.incheon)[0];
                        break;
                    case "대전":
                        midLoc = getResources().getStringArray(R.array.daejeon)[0];
                        break;
                    case "대구":
                        midLoc = getResources().getStringArray(R.array.daegu)[0];
                        break;
                    case "부산":
                        midLoc = getResources().getStringArray(R.array.busan)[0];
                        break;
                    case "광주":
                        midLoc = getResources().getStringArray(R.array.gwangju)[0];
                        break;
                    case "세종":
                        midLoc = getResources().getStringArray(R.array.sejong)[0];
                        break;
                    case "울산":
                        midLoc = getResources().getStringArray(R.array.ulsan)[0];
                        break;
                    case "충북":
                        midLoc = getResources().getStringArray(R.array.chungcheongbuk)[0];
                        break;
                    case "충남":
                        midLoc = getResources().getStringArray(R.array.chungcheongnam)[0];
                        break;
                    case "전북":
                        midLoc = getResources().getStringArray(R.array.jeollabuk)[0];
                        break;
                    case "전남":
                        midLoc = getResources().getStringArray(R.array.jeollanam)[0];
                        break;
                    case "경북":
                        midLoc = getResources().getStringArray(R.array.gyeongsangbuk)[0];
                        break;
                    case "경남":
                        midLoc = getResources().getStringArray(R.array.gyeongsangnam)[0];
                        break;
                    case "강원":
                        midLoc = getResources().getStringArray(R.array.gangwon)[0];
                        break;
                    case "제주":
                        midLoc = getResources().getStringArray(R.array.jeju)[0];
                        break;
                    case "북한":
                        midLoc = getResources().getStringArray(R.array.northk)[0];
                        break;
//                    case "해외":
//                        midLoc = getResources().getStringArray(R.array.gyeongi)[0];
//                        break;
                }

                if(result.equalsIgnoreCase("지역")) {
                    binding.tvMainLoc.setText(null);
                    binding.tvMiddleLoc.setText(null);
                } else {
                    binding.tvMainLoc.setText(result);
                    binding.tvMiddleLoc.setText(midLoc);
                    getLocation(binding.tvMainLoc.getText().toString() + binding.tvMiddleLoc.getText().toString());
                }
                break;
            case DefaultValue.MIDDLELOC:
                getLocation(binding.tvMainLoc.getText() + result);
                binding.tvMiddleLoc.setText(result);
                break;

            case DefaultValue.BIRTHYEAR:
                if(result.equalsIgnoreCase("태어난 년도")) {
                    binding.tvBirthYear.setText(null);
                } else {
                    binding.tvBirthYear.setText(result);
                }
                break;
        }
    }

    private void registProfile() {
        ReqBasic regProf = new ReqBasic(this, NetUrls.PROFBASICMODIFY) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "BasicInfo Reg Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase("success")) {
//                            Toast.makeText(act, "프로필 수정 완료", Toast.LENGTH_SHORT).show();

                            if(binding.etNewpw.length() != 0) {
                                UserPref.setPw(act, binding.etNewpw.getText().toString());
                            } else {
                                UserPref.setPw(act, binding.etPw.getText().toString());
                            }

                            UserPref.setHopeLoc(act, binding.tvHopeLoc.getText().toString().replaceAll(",", "|"));
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(act, "프로필 수정 실패", Toast.LENGTH_SHORT).show();
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

        regProf.addParams("uidx", UserPref.getUidx(this));             //회원
        regProf.addParams("nick", binding.etNickname.getText().toString());
        regProf.addParams("fname", binding.etFamilyname.getText().toString());
        regProf.addParams("name", binding.etName.getText().toString());

        String lastnameYN = "";
        if (binding.rdoFamilyname.isChecked()) {
            lastnameYN = "Y";
        } else {
            lastnameYN = "N";
        }
        regProf.addParams("lastnameYN", "Y");
        regProf.addParams("byear", binding.tvBirthYear.getText().toString());
        String marriage = "";
        if (binding.rdoMarry.isChecked()) {
            marriage = "marry";
        } else if (binding.rdoRemarry.isChecked()) {
            marriage = "remarry";
        }

//        else if (binding.rdoFriend.isChecked()) {
//            marriage = "remarry";
//        }

        regProf.addParams("ct", marriage);
        regProf.addParams("addr1", binding.tvMainLoc.getText().toString());
        regProf.addParams("addr2", binding.tvMiddleLoc.getText().toString());
        regProf.addParams("hopeaddr", binding.tvHopeLoc.getText().toString());
        regProf.addParams("org_pw", binding.etPw.getText().toString());
        regProf.addParams("pw", binding.etNewpw.getText().toString());
        regProf.addParams("pw_confirm", binding.etCheckpw.getText().toString());


        regProf.execute(true, true);

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

    private boolean checkNick(String nick){
        String regex = "^[ㄱ-ㅣ가-힣]*$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(nick);
        boolean isNormal = m.matches();
        return isNormal;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.tv_hope_loc:
                Intent hopelocIntent = new Intent(this, MultipleListDlgAct.class);
                hopelocIntent.putExtra("subject", "hope_loc2");
                hopelocIntent.putExtra("select", binding.tvHopeLoc.getText().toString().replaceAll(",", "|"));
                startActivityForResult(hopelocIntent, DefaultValue.HOPELOC);
                break;

            case R.id.tv_main_loc:
                Intent mainlocIntent = new Intent(this, ListDlgAct.class);
                mainlocIntent.putExtra("subject", "main_loc");
                mainlocIntent.putExtra("select", binding.tvMainLoc.getText());
                startActivityForResult(mainlocIntent, DefaultValue.MAINLOC);
                break;

            case R.id.tv_middle_loc:
                if (binding.tvMainLoc.length() > 0) {
                    Intent middlelocIntent = new Intent(this, ListDlgAct.class);
                    middlelocIntent.putExtra("subject", "middle_loc");
                    middlelocIntent.putExtra("mloc", binding.tvMainLoc.getText());
                    middlelocIntent.putExtra("select", binding.tvMiddleLoc.getText());
                    startActivityForResult(middlelocIntent, DefaultValue.MIDDLELOC);
                } else {
                    Toast.makeText(BasicinfoSettingAct.this, "지역을 선택해주세요", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_logout:
                UserPref.setLoginState(this, false);
                UserPref.setLogoutState(this, true);
                startActivity(new Intent(this, LoginAct.class));
                finishAffinity();
                logOut();
                break;

            case R.id.tv_birth_year:
                Intent birthIntent = new Intent(this, ListDlgAct.class);
                birthIntent.putExtra("subject", "birth");
                birthIntent.putExtra("select", binding.tvBirthYear.getText());
                startActivityForResult(birthIntent, DefaultValue.BIRTHYEAR);
                break;

            case R.id.tv_modify:
                if (StringUtil.isNull(binding.etNickname.getText().toString())) {
                    Toast.makeText(act, "닉네임을 확인해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (binding.etNickname.length() < 2) {
                    Toast.makeText(this, "닉네임은 2자 이상입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (binding.etNickname.length() > 8) {
                    Toast.makeText(this, "닉네임은 8자 이하입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!checkNick(binding.etNickname.getText().toString())) {
                    Toast.makeText(this, "닉네임은 한글만 사용가능합니다", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (StringUtil.isNull(binding.etName.getText().toString())) {
                    Toast.makeText(BasicinfoSettingAct.this, "이름을 확인해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!checkNick(binding.etName.getText().toString())) {
                    Toast.makeText(this, "이름은 한글만 사용가능합니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (StringUtil.isNull(binding.etFamilyname.getText().toString())) {
                    Toast.makeText(BasicinfoSettingAct.this, "성을 확인해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!checkNick(binding.etFamilyname.getText().toString())) {
                    Toast.makeText(this, "성은 한글만 사용가능합니다", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (StringUtil.isNull(binding.tvBirthYear.getText().toString())) {
                    Toast.makeText(BasicinfoSettingAct.this, "나이를 확인해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (StringUtil.isNull(binding.tvMainLoc.getText().toString())) {
                    Toast.makeText(BasicinfoSettingAct.this, "지역을 확인해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (StringUtil.isNull(binding.tvHopeLoc.getText().toString())) {
                    Toast.makeText(BasicinfoSettingAct.this, "배우자 희망지역을 확인해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                //비밀번호
                if (binding.etNewpw.length() != 0){
                    if(binding.etCheckpw.length() == 0) {
                        Toast.makeText(BasicinfoSettingAct.this, "신규 비밀번호를 한번 더 입력해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (binding.etNewpw.length() < 4 || binding.etNewpw.length() > 12) {
                        Toast.makeText(this, "비밀번호는 4~12자로 사용할 수 있습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }


//                    if(!Pattern.matches("^[a-zA-Z0-9]*$", binding.etNewpw.getText().toString())) {
//                        Toast.makeText(this, "비밀번호는 영문, 숫자조합만 사용가능합니다.", Toast.LENGTH_SHORT).show();
//                        return;
//                    }

                    if(!Pattern.matches(".*[a-zA-Z]+.*", binding.etNewpw.getText().toString())) {
                        Toast.makeText(this, "비밀번호에 영어가 포함되어야 합니다", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(!Pattern.matches(".*[0-9]+.*", binding.etNewpw.getText().toString())) {
                        Toast.makeText(this, "비밀번호에 숫자가 포함되어야 합니다", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (binding.etCheckpw.length() != 0) {
                    if(binding.etNewpw.length() == 0) {
                        Toast.makeText(BasicinfoSettingAct.this, "신규 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if(binding.etNewpw.length() != 0 && binding.etCheckpw.length() != 0) {
                    if(!binding.etNewpw.getText().toString().equalsIgnoreCase(binding.etCheckpw.getText().toString())) {
                        Toast.makeText(BasicinfoSettingAct.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                registProfile();
                break;
        }
    }
}
