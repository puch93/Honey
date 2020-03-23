package com.match.honey.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.match.honey.R;
import com.match.honey.adapters.list.HopestyleJoinAdapter;
import com.match.honey.databinding.ActivityJoinBinding;
import com.match.honey.listDatas.HopestyleData;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinAct extends BaseActivity implements View.OnClickListener {

    ActivityJoinBinding binding;
    AppCompatActivity act;

    boolean isAvailable = true;

    Geocoder geocoder;
    String token;

    ChangeProfVal cpv;
    String chGender = Common.M1, cellNum = "";

    HopestyleJoinAdapter adapter;
    ArrayList<HopestyleData> list = new ArrayList<>();

    boolean isMen = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_join);
        act = this;
        cpv = new ChangeProfVal();

        token = null;
        Log.d(StringUtil.TAG, "jtoken: " + token);

        geocoder = new Geocoder(this);
        startLocationService();

        setClickListener();

        //초기세팅
        binding.rdoMale.setChecked(true);
        binding.tvFamilyMale.setText("1남");
        binding.tvFamilyFemale.setText("0녀");
        binding.tvFamilyOrder.setText("1째");

        binding.rdoMarry.setChecked(true);
        binding.rdoChildOther.setChecked(true);
        binding.rdoFamilyname.setChecked(true);

        binding.rdoMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chGender = Common.M1;

                    binding.tvFamilyMale.setText("1남");
                    binding.tvFamilyFemale.setText("0녀");
                    binding.tvFamilyOrder.setText("1째");

                    isMen = true;
                }
            }
        });

        binding.rdoFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chGender = Common.W1;

                    binding.tvFamilyMale.setText("0남");
                    binding.tvFamilyFemale.setText("1녀");
                    binding.tvFamilyOrder.setText("1째");

                    isMen = false;
                }
            }
        });
        //자기소개 글자수 세팅
        binding.etIntroduce.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = binding.etIntroduce.getText().toString();
                binding.tvCountText.setText(String.valueOf(input.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //배우자 희망지역 세팅
        binding.rcvJoinHoplestyle.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new HopestyleJoinAdapter(this, list);
        binding.rcvJoinHoplestyle.setAdapter(adapter);
        binding.rcvJoinHoplestyle.addItemDecoration(new ItemOffsetDecorationJoin(this,
                getResources().getDimensionPixelSize(R.dimen.dimen_hope01),
                getResources().getDimensionPixelSize(R.dimen.dimen_hope02),
                getResources().getDimensionPixelSize(R.dimen.dimen_10)));
    }

    private boolean checkNick(String nick) {
        String regex = "^[ㄱ-ㅣ가-힣]*$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(nick);
        boolean isNormal = m.matches();
        return isNormal;
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


    private void modifyJoin() {
        ReqBasic reqJoin = new ReqBasic(this, NetUrls.REGMEM) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "onAfterJoin: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            JSONArray ja = jo.getJSONArray("value");
                            JSONObject obj = ja.getJSONObject(0);
                            UserPref.setLoginState(JoinAct.this, true);
                            UserPref.setUidx(JoinAct.this, obj.getString("idx"));
                            UserPref.setId(JoinAct.this, obj.getString("id"));
                            UserPref.setPw(act, binding.etPw.getText().toString());
                            UserPref.setHopeLoc(JoinAct.this, obj.getString("hopeaddr"));
                            UserPref.setCoin(JoinAct.this, Integer.parseInt(obj.getString("coin")));
                            UserPref.setJtype(JoinAct.this, obj.getString("type"));
                            UserPref.setGender(JoinAct.this, obj.getString("gender"));

                            UserPref.setInterIdxs(JoinAct.this, obj.getString("interest_idxs"));

                            if (!StringUtil.isNull(obj.getString("familyname"))) {
                                UserPref.setRegProf(JoinAct.this, true);
                            }

                            getLocation(obj.getString("addr1") + obj.getString("addr2"));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(JoinAct.this, MainActivity.class));
                                    finishAffinity();
                                }
                            });

                        } else {
                            Toast.makeText(JoinAct.this, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(JoinAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // result가 null일 경우
                    Toast.makeText(JoinAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        //
        reqJoin.addParams("type", "general");        // 회원가입형태(general,kakaotalk,naver,facebook)
        reqJoin.addParams("fcm_token", UserPref.getFcmToken(this));
        if (!locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.i(StringUtil.TAG, "loclat: " + UserPref.getLocationLat(this) + " loclon: " + UserPref.getLocationLon(this));
            reqJoin.addParams("lat", UserPref.getLocationLat(this));
            reqJoin.addParams("lon", UserPref.getLocationLon(this));
        } else {
            Log.i(StringUtil.TAG, "lat: " + UserPref.getLat(this) + " lon: " + UserPref.getLon(this));
            reqJoin.addParams("lat", UserPref.getLat(this));
            reqJoin.addParams("lon", UserPref.getLon(this));
        }

        String email = "";
        String pw = "";
        String pw_check = "";

        if (binding.llPortalInput.getVisibility() == View.VISIBLE) {
            email = binding.etEmail.getText().toString() + "@" + binding.etPortal.getText().toString();
        } else {
            email = binding.etEmail.getText().toString() + "@" + binding.tvPortal.getText().toString();
        }
        pw = binding.etPw.getText().toString();
        pw_check = binding.etPwcheck.getText().toString();

        reqJoin.addParams("id", email);
        reqJoin.addParams("pw", pw);
        reqJoin.addParams("pw_confirm", pw_check);
        reqJoin.addParams("name", binding.etName.getText().toString());
        reqJoin.addParams("familyname", binding.etFamilyname.getText().toString());

        String lastnameYN = "";
        if (binding.rdoFamilyname.isChecked()) {
            lastnameYN = "Y";
        } else if (binding.rdoName.isChecked()) {
            lastnameYN = "N";
        }

        reqJoin.addParams("lastnameYN", "Y");
        reqJoin.addParams("nick", binding.etMemid.getText().toString());
        reqJoin.addParams("byear", binding.tvBirthYear.getText().toString());

        String gender = "";
        if (binding.rdoFemale.isChecked()) {
            gender = "female";
        } else if (binding.rdoMale.isChecked()) {
            gender = "male";
        }
        reqJoin.addParams("gender", gender);

        String marriage = "";
        if (binding.rdoMarry.isChecked()) {
            marriage = "marry";
        } else if (binding.rdoRemarry.isChecked()) {
            marriage = "remarry";
        }

        reqJoin.addParams("ct", marriage);
        reqJoin.addParams("addr1", binding.tvMainLoc.getText().toString());
        reqJoin.addParams("addr2", binding.tvMiddleLoc.getText().toString());

        reqJoin.addParams("hopeaddr", binding.tvHopeLoc.getText().toString());

        reqJoin.addParams("character", chGender);

        reqJoin.addParams("bh1", binding.tvFamilyMale.getText().toString());
        reqJoin.addParams("bh2", binding.tvFamilyFemale.getText().toString());
        reqJoin.addParams("bh3", binding.tvFamilyOrder.getText().toString());

        if (binding.llInputArea.getVisibility() == View.VISIBLE) {
            Log.e(TAG, "job value: " + binding.tvJob.getText().toString() + binding.etJob.getText().toString());
            if (StringUtil.isNull(binding.etJob.getText().toString()) || binding.etJob.getText().toString().equalsIgnoreCase("")) {
                reqJoin.addParams("jg", "");
            } else {
                reqJoin.addParams("jg", binding.tvJob.getText().toString() + binding.etJob.getText().toString());
            }
        } else {
            Log.e(TAG, "job value: " + binding.tvJob.getText().toString());
            reqJoin.addParams("jg", binding.tvJob.getText().toString());
        }

        reqJoin.addParams("ann", cpv.getAnnual(binding.tvSalary.getText().toString()));
        reqJoin.addParams("prp", cpv.getProperty(binding.tvProperty.getText().toString()));
        reqJoin.addParams("edu", binding.tvEdu.getText().toString());

        reqJoin.addParams("hg", binding.tvHeight.getText().toString());
        reqJoin.addParams("sty", binding.tvStyle.getText().toString());
        reqJoin.addParams("bld", binding.tvBloodtype.getText().toString());
        reqJoin.addParams("rlg", binding.tvReligion.getText().toString());
        String[] pers = binding.tvPersonal.getText().toString().split(",");
        if (pers.length == 1) {
            reqJoin.addParams("psn1", pers[0]);                                             //성격1
            reqJoin.addParams("psn2", "");
            reqJoin.addParams("psn3", "");
        } else if (pers.length == 2) {
            reqJoin.addParams("psn1", pers[0]);                                             //성격1
            reqJoin.addParams("psn2", pers[1]);                                             //성격2
            reqJoin.addParams("psn3", "");
        } else if (pers.length == 3) {
            reqJoin.addParams("psn1", pers[0]);                                             //성격1
            reqJoin.addParams("psn2", pers[1]);                                             //성격2
            reqJoin.addParams("psn3", pers[2]);                                             //성격3
        }

        String[] hobby = binding.tvHobby.getText().toString().split(",");
        if (hobby.length == 1) {
            reqJoin.addParams("hbb1", hobby[0]);                                             //취미1
            reqJoin.addParams("hbb2", "");
            reqJoin.addParams("hbb3", "");
            reqJoin.addParams("hbb4", "");
            reqJoin.addParams("hbb5", "");
        } else if (hobby.length == 2) {
            reqJoin.addParams("hbb1", hobby[0]);                                             //취미1
            reqJoin.addParams("hbb2", hobby[1]);                                             //취미2
            reqJoin.addParams("hbb3", "");
            reqJoin.addParams("hbb4", "");
            reqJoin.addParams("hbb5", "");
        } else if (hobby.length == 3) {
            reqJoin.addParams("hbb1", hobby[0]);                                             //취미1
            reqJoin.addParams("hbb2", hobby[1]);                                             //취미2
            reqJoin.addParams("hbb3", hobby[2]);                                             //취미3
            reqJoin.addParams("hbb4", "");
            reqJoin.addParams("hbb5", "");
        } else if (hobby.length == 4) {
            reqJoin.addParams("hbb1", hobby[0]);                                             //취미1
            reqJoin.addParams("hbb2", hobby[1]);                                             //취미2
            reqJoin.addParams("hbb3", hobby[2]);                                             //취미3
            reqJoin.addParams("hbb4", hobby[3]);                                             //취미4
            reqJoin.addParams("hbb5", "");
        } else if (hobby.length == 5) {
            reqJoin.addParams("hbb1", hobby[0]);                                             //취미1
            reqJoin.addParams("hbb2", hobby[1]);                                             //취미2
            reqJoin.addParams("hbb3", hobby[2]);                                             //취미3
            reqJoin.addParams("hbb4", hobby[3]);                                             //취미4
            reqJoin.addParams("hbb5", hobby[4]);                                             //취미5
        }


        reqJoin.addParams("smk", cpv.getMainSmoke(binding.tvSmoke.getText().toString()));
        reqJoin.addParams("drk", binding.tvMaindrinking.getText().toString());

        String child = binding.tvBoy.getText().toString() + "," + binding.tvGirl.getText().toString();
        reqJoin.addParams("childcnt", child);

        String childwho = "";
        if (binding.rdoChildMe.isChecked()) {
            childwho = "나";
        } else if (binding.rdoChildWife.isChecked()) {
            childwho = "전 배우자";
        } else if (binding.rdoChildOther.isChecked()) {
            childwho = "기타";
        }
        reqJoin.addParams("childwho", childwho);

        //희망 배우자 카테고리
        for (int i = 0; i < 7; i++) {
            if (i < list.size()) {
                reqJoin.addParams("hst" + (i + 1), list.get(i).getText());
            } else {
                reqJoin.addParams("hst" + (i + 1), "");
            }
        }
        reqJoin.addParams("pint", binding.etIntroduce.getText().toString());

        reqJoin.execute(true, true);
    }

    private void setClickListener() {
        binding.llPortalArea.setOnClickListener(this);
        binding.ivInputCancel.setOnClickListener(this);

        binding.llBirthYear.setOnClickListener(this);
        binding.llMainLoc.setOnClickListener(this);
        binding.llMiddleLoc.setOnClickListener(this);
        binding.tvHopeLoc.setOnClickListener(this);
        binding.btnTermview.setOnClickListener(this);
        binding.btnJoin.setOnClickListener(this);
        binding.llTermtextArea.setOnClickListener(this);

        binding.flBack.setOnClickListener(this);

        binding.tvFamilyOrder.setOnClickListener(this);
        binding.tvFamilyFemale.setOnClickListener(this);
        binding.tvJob.setOnClickListener(this);
        binding.flJobClose.setOnClickListener(this);
        binding.tvFamilyMale.setOnClickListener(this);
        binding.tvSalary.setOnClickListener(this);
        binding.tvProperty.setOnClickListener(this);
        binding.tvEdu.setOnClickListener(this);

        binding.tvHeight.setOnClickListener(this);
        binding.tvStyle.setOnClickListener(this);
        binding.tvBloodtype.setOnClickListener(this);
        binding.tvReligion.setOnClickListener(this);
        binding.tvPersonal.setOnClickListener(this);
        binding.tvHobby.setOnClickListener(this);
        binding.tvSmoke.setOnClickListener(this);
        binding.tvMaindrinking.setOnClickListener(this);
        binding.tvBoy.setOnClickListener(this);
        binding.tvGirl.setOnClickListener(this);

        binding.flTerm.setOnClickListener(this);
}

    private void calFamilyOrder(int male, int female) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_term:
                binding.flTerm.setSelected(!binding.flTerm.isSelected());
                break;
            case R.id.fl_back:
                finish();
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
                int men = Integer.valueOf(binding.tvFamilyMale.getText().toString().replace("남", ""));
                int women = Integer.valueOf(binding.tvFamilyFemale.getText().toString().replace("녀", ""));
                int total = men + women;

                // n째
                Intent bhood3 = new Intent(this, ListDlgAct.class);
                bhood3.putExtra("subject", "bhood3");
                bhood3.putExtra("total", total);
                bhood3.putExtra("select", binding.tvFamilyOrder.getText());
                startActivityForResult(bhood3, DefaultValue.BHOOD3);
                break;

            case R.id.tv_salary:
                // 연봉
                Intent annualIntent = new Intent(this, ListDlgAct.class);
                annualIntent.putExtra("subject", "annual");
                annualIntent.putExtra("select", binding.tvSalary.getText());
                startActivityForResult(annualIntent, DefaultValue.SALARY);
                break;

            case R.id.tv_job:
                // 직업
                Intent jobIntent = new Intent(this, ListDlgAct.class);
                jobIntent.putExtra("subject", "mainjob");
                jobIntent.putExtra("select", binding.tvJob.getText());
                startActivityForResult(jobIntent, DefaultValue.MAINJOB);
                break;

            case R.id.fl_job_close:
//                binding.tvJob.setText(null);
                binding.etJob.setText(null);
//                binding.llInputArea.setVisibility(View.INVISIBLE);
                break;

            case R.id.tv_property:
                // 재산
                Intent propertyIntent = new Intent(this, ListDlgAct.class);
                propertyIntent.putExtra("subject", "property");
                propertyIntent.putExtra("select", binding.tvProperty.getText());
                startActivityForResult(propertyIntent, DefaultValue.PROPERTY);
                break;
            case R.id.tv_edu:
                // 학력
                Intent eduIntent = new Intent(this, ListDlgAct.class);
                eduIntent.putExtra("subject", "edu");
                eduIntent.putExtra("select", binding.tvEdu.getText());
                startActivityForResult(eduIntent, DefaultValue.EDU);
                break;

            case R.id.tv_height:
                // 키
                Intent heightIntent = new Intent(this, ListDlgAct.class);
                heightIntent.putExtra("subject", "height");
                heightIntent.putExtra("select", binding.tvHeight.getText());
                startActivityForResult(heightIntent, DefaultValue.PHEIGHT);
                break;
            case R.id.tv_style:
                // 스타일
                Intent stylentent = new Intent(this, ListDlgAct.class);
                stylentent.putExtra("subject", "style");
                stylentent.putExtra("select", binding.tvStyle.getText());
                startActivityForResult(stylentent, DefaultValue.STYLE);
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


            case R.id.ll_portal_area:
                Intent portalIntent = new Intent(this, ListDlgAct.class);
                portalIntent.putExtra("subject", "portal");
                portalIntent.putExtra("select", binding.tvPortal.getText());
                startActivityForResult(portalIntent, DefaultValue.PORTAL);
                break;
            case R.id.ll_birth_year:
                Intent birthIntent = new Intent(this, ListDlgAct.class);
                birthIntent.putExtra("subject", "birth");
                birthIntent.putExtra("select", binding.tvBirthYear.getText());
                startActivityForResult(birthIntent, DefaultValue.BIRTHYEAR);
                break;
            case R.id.ll_main_loc:
                Intent mainlocIntent = new Intent(this, ListDlgAct.class);
                mainlocIntent.putExtra("subject", "main_loc");
                mainlocIntent.putExtra("select", binding.tvMainLoc.getText());
                startActivityForResult(mainlocIntent, DefaultValue.MAINLOC);
                break;
            case R.id.ll_middle_loc:
                if (binding.tvMainLoc.length() > 0) {
                    Intent middlelocIntent = new Intent(this, ListDlgAct.class);
                    middlelocIntent.putExtra("subject", "middle_loc");
                    middlelocIntent.putExtra("mloc", binding.tvMainLoc.getText());
                    middlelocIntent.putExtra("select", binding.tvMiddleLoc.getText());
                    startActivityForResult(middlelocIntent, DefaultValue.MIDDLELOC);
                } else {
                    Snackbar.make(v.getRootView(), "지역을 선택해주세요", Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_hope_loc:
                Intent hopelocIntent = new Intent(this, MultipleListDlgAct.class);
                hopelocIntent.putExtra("subject", "hope_loc2");
                hopelocIntent.putExtra("select", binding.tvHopeLoc.getText().toString().replaceAll(",", "|"));
                startActivityForResult(hopelocIntent, DefaultValue.HOPELOC);
                break;
            case R.id.ll_termtext_area:
                binding.ivCheckbox.setSelected(!binding.ivCheckbox.isSelected());
                break;
            case R.id.iv_input_cancel:
                binding.llPortalInput.setVisibility(View.GONE);
                binding.llPortalArea.setVisibility(View.VISIBLE);
                binding.tvPortal.setText("naver.com");
                binding.etPortal.setText(null);
                break;

            case R.id.btn_termview:
                Common.showToastDevelop(act);
//                startActivity(new Intent(this, TermsActivity.class));
                break;

            case R.id.btn_join:
                //일반 회원가입일때만
                //이메일
                if (binding.etEmail.length() == 0) {
                    Toast.makeText(this, getString(R.string.input_email), Toast.LENGTH_SHORT).show();
                    return;
                }

                // 포털 직접입력(naver.com,google.com ...  등)
                if (binding.llPortalInput.getVisibility() == View.VISIBLE) {
                    if (binding.etPortal.length() == 0) {
                        Toast.makeText(this, getString(R.string.input_portal), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!checkEmail(binding.etEmail.getText().toString() + "@" + binding.etPortal.getText().toString())) {
                        Toast.makeText(this, "이메일을 확인해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    if (!checkEmail(binding.etEmail.getText().toString() + "@" + binding.tvPortal.getText().toString())) {
                        Toast.makeText(this, "이메일을 확인해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                //비밀번호
                if (binding.etPw.length() == 0) {
                    Toast.makeText(this, getString(R.string.input_pw), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (binding.etPw.length() < 4 || binding.etPw.length() > 12) {
                    Toast.makeText(this, "비밀번호는 4~12자로 사용할 수 있습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Pattern.matches(".*[a-zA-Z]+.*", binding.etPw.getText().toString())) {
                    Toast.makeText(this, "비밀번호에 영어가 포함되어야 합니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Pattern.matches(".*[0-9]+.*", binding.etPw.getText().toString())) {
                    Toast.makeText(this, "비밀번호에 숫자가 포함되어야 합니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (binding.etPwcheck.length() == 0) {
                    Toast.makeText(this, getString(R.string.input_pwcheck), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!binding.etPw.getText().toString().equalsIgnoreCase(binding.etPwcheck.getText().toString())) {
                    Toast.makeText(this, getString(R.string.pwcheckfail), Toast.LENGTH_SHORT).show();
                    return;
                }

                //성, 이름
                if (binding.etFamilyname.length() == 0) {
                    Toast.makeText(this, "성을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!checkNick(binding.etFamilyname.getText().toString())) {
                    Toast.makeText(this, "성은 한글만 사용가능합니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (binding.etName.length() == 0) {
                    Toast.makeText(this, "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!checkNick(binding.etName.getText().toString())) {
                    Toast.makeText(this, "이름은 한글만 사용가능합니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                //닉네임
                if (binding.etMemid.length() == 0) {
                    Toast.makeText(this, getString(R.string.input_memid), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (binding.etMemid.length() < 2) {
                    Toast.makeText(this, "닉네임은 2자 이상입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (binding.etMemid.length() > 8) {
                    Toast.makeText(this, "닉네임은 8자 이하입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!checkNick(binding.etMemid.getText().toString())) {
                    Toast.makeText(this, "닉네임은 한글만 사용가능합니다", Toast.LENGTH_SHORT).show();
                    return;
                }


                //생년
                if (binding.tvBirthYear.length() == 0) {
                    Toast.makeText(this, getString(R.string.select_byear), Toast.LENGTH_SHORT).show();
                    return;
                }

                //지역선택
                if (binding.tvMainLoc.length() == 0) {
                    Toast.makeText(this, getString(R.string.select_mainloc), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (binding.tvMiddleLoc.length() == 0) {
                    Toast.makeText(this, getString(R.string.select_midloc), Toast.LENGTH_SHORT).show();
                    return;
                }

                //배우자 희망지역
                if (binding.tvHopeLoc.length() == 0) {
                    Toast.makeText(this, getString(R.string.select_hopeloc), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!binding.tvMainLoc.getText().toString().equalsIgnoreCase("지역선택") &&
                        !binding.tvMainLoc.getText().toString().equalsIgnoreCase("상세지역")) {
                    getLocation(binding.tvMainLoc.getText().toString() + binding.tvMiddleLoc.getText().toString());
                }


                //푸시토큰
//                if (StringUtil.isNull(token)) {
//                    token = FirebaseInstanceId.getInstance().getToken();
//                    UserPref.setFcmToken(this, token);
//                    Toast.makeText(this, "푸시토큰을 가져오는 중입니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
//                    return;
//                } else {
//                    UserPref.setFcmToken(this, token);
//                }

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


                //자기소개서
                if (StringUtil.isNull(binding.etIntroduce.getText().toString())) {
                    Common.showToast(act, "자기소개서는 필수항목입니다");
                    return;
                } else {
                    if (binding.etIntroduce.length() < 30) {
                        Toast.makeText(act, "자기소개는 30자 이상입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (binding.etIntroduce.length() > 300) {
                        Toast.makeText(act, "자기소개는 300자 이하입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                //약관
                if (!binding.ivCheckbox.isSelected()) {
                    Toast.makeText(this, getString(R.string.err_termagree), Toast.LENGTH_SHORT).show();
                    return;
                }

                modifyJoin();
                break;
        }
    }


    private boolean checkEmail(String email) {
        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        boolean isNormal = m.matches();
        return isNormal;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationService();
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
            case DefaultValue.PORTAL:
                if (result.equalsIgnoreCase("직접입력")) {
                    binding.llPortalInput.setVisibility(View.VISIBLE);
                    binding.llPortalArea.setVisibility(View.GONE);
                } else {
                    binding.tvPortal.setText(result);
                }
                break;
            case DefaultValue.BIRTHYEAR:
                if (result.equals("태어난 년도")) {
                    binding.tvBirthYear.setText(null);
                } else {
                    binding.tvBirthYear.setText(result);
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
                }
                if (result.equals("지역")) {
                    binding.tvMainLoc.setText(null);
                    binding.tvMiddleLoc.setText(null);
                } else {
                    binding.tvMainLoc.setText(result);
                    binding.tvMiddleLoc.setText(midLoc);
                }
                break;
            case DefaultValue.MIDDLELOC:
                binding.tvMiddleLoc.setText(result);
                break;
            case DefaultValue.HOPELOC:
                if (result.equalsIgnoreCase("")) {
                    binding.tvHopeLoc.setText(null);
                } else {
                    binding.tvHopeLoc.setText(result.replaceAll("\\|", ","));
                }
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
            case DefaultValue.EDU:
                binding.tvEdu.setText(result);
                break;
            case DefaultValue.STYLE:
                binding.tvStyle.setText(result);
                break;

            case DefaultValue.PHEIGHT:
                binding.tvHeight.setText(result);
                break;

            case DefaultValue.SMOKE:
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
                if (StringUtil.isNull(result)) {
                    binding.tvPersonal.setText(null);
                } else {
                    binding.tvPersonal.setText(result.replaceAll("\\|", ","));
                }
                break;

            case DefaultValue.HOBBY:
                binding.tvHobby.setText(result.replaceAll("\\|", ","));
                break;


            case DefaultValue.BOY:
                if (StringUtil.isNull(result)) {
                    result = "0";
                }
                binding.tvBoy.setText(result);
                break;
            case DefaultValue.GIRL:
                if (StringUtil.isNull(result)) {
                    result = "0";
                }
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
}
