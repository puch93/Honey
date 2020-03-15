package com.match.honey.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.match.honey.R;
import com.match.honey.adapters.list.ImagelistAdapter;
import com.match.honey.databinding.ActivityMyprofileBinding;
import com.match.honey.listDatas.ImagesData;
import com.match.honey.listDatas.ProfileData;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.Common;
import com.match.honey.utils.DefaultValue;
import com.match.honey.utils.Glide4Engine;
import com.match.honey.utils.ItemOffsetDecorationMatisse;
import com.match.honey.utils.StringUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.listener.OnSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyProfileAct extends Activity implements View.OnClickListener {

    ActivityMyprofileBinding binding;
    ProfileData data;

    public static Activity act;

    static final int REQUEST_TAKE_PHOTO = 2001;
    static final int REQUEST_TAKE_ALBUM = 2002;
    static final int REQUEST_IMAGE_CROP = 2003;
    private Uri photoUri;
    private String mImgFilePath;

    List<Uri> mSelected;
    ArrayList<ImagesData> imgpath = new ArrayList<>();
    ImagelistAdapter imgAdapter;


    int pic_all_count = -1;
    int pic_profile_count = -1;
    public int pic_intro_count = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_myprofile);

        act = this;
        binding.flBack.setOnClickListener(this);

        binding.llBtnMyprof.setOnClickListener(this);

        binding.btnModify.setOnClickListener(this);
        binding.ivProfadd.setOnClickListener(this);

        getMyProfile();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.ivProfimg.setClipToOutline(true);
        }

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

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rcvImages.setLayoutManager(layoutManager);
        imgAdapter = new ImagelistAdapter(this, imgpath);
        binding.rcvImages.setAdapter(imgAdapter);
        binding.rcvImages.addItemDecoration(new ItemOffsetDecorationMatisse(this, getResources().getDimensionPixelSize(R.dimen.dimen_8)));

        binding.llRegphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pic_intro_count == -1) {
                    Common.showToast(act, "회원정보를 불러오는 중입니다\n잠시만 기다려주세요");
                } else if (pic_intro_count >= 6) {
                    Common.showToast(act, "소개 이미지는 최대 6장 등록 가능합니다");
                } else {
                    Matisse.from(act)
                            .choose(MimeType.ofImage())
                            .countable(true)
                            .maxSelectable(6 - pic_intro_count)
                            .imageEngine(new Glide4Engine())
                            .setOnSelectedListener(new OnSelectedListener() {
                                @Override
                                public void onSelected(@NonNull List<Uri> uriList, @NonNull List<String> pathList) {

                                }
                            })
                            .spanCount(3)
                            .forResult(DefaultValue.IMAGES);
                }
            }
        });
    }

    private void setIntroduce(final String introduce) {
        ReqBasic setIntro = new ReqBasic(this, NetUrls.SETINTRODUCE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            if (StringUtil.isNull(introduce)) {
                                binding.etIntroduce.setText(null);
                                Toast.makeText(MyProfileAct.this, "자기소개 삭제완료", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MyProfileAct.this, "등록이 완료되었습니다.\n사진/자기소개서 수정은 신청후 승인까지\n최대 1일 정도 소요됩니다.", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(MyProfileAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MyProfileAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MyProfileAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }

            }
        };

        setIntro.addParams("uidx", UserPref.getUidx(this));
        setIntro.addParams("pintroduce", introduce);
        setIntro.execute(true, true);
    }

//    private void exit() {
//        ((MainActivity) MainActivity.act).fromHomeBtn();
//        finish();
//    }

    public void modifyIntroImageCount() {
        binding.tvIntroimgCount.setText(String.valueOf(--pic_intro_count));
        binding.tvAllCount.setText(String.valueOf(pic_profile_count + pic_intro_count));
    }

    private void getMyProfile() {
        ReqBasic myprofile = new ReqBasic(this, NetUrls.MYPROFILE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {

                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e(TAG, "spot01");
                        Log.e("TEST_HOME", "MyProfileAct List Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            Log.e(TAG, "spot02");
                            JSONObject obj = new JSONObject(jo.getString("value"));
                            data = new ProfileData();

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
                            data.setFamilyname(obj.getString("familyname"));
                            data.setName(obj.getString("name"));
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
                            data.setPw(UserPref.getPw(act));
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

                            pic_profile_count = 0;
                            pic_all_count = 0;
                            pic_intro_count = 0;

                            //하단 이미지들
                            if (obj.has("piimg") && !StringUtil.isNull(obj.getString("piimg"))) {
                                JSONArray imgs = new JSONArray(obj.getString("piimg"));
                                if (imgpath.size() > 0) {
                                    imgpath.clear();
                                }

                                pic_all_count += imgs.length();
                                pic_intro_count += imgs.length();


                                for (int k = 0; k < imgs.length(); k++) {
                                    JSONObject io = imgs.getJSONObject(k);
                                    ImagesData img = new ImagesData();

                                    img.setIdx(io.getString("idx"));
                                    img.setUidx(io.getString("u_idx"));
                                    img.setPiimg(io.getString("pi_img"));
                                    img.setRegdate(io.getString("pi_regdate"));

                                    imgpath.add(img);
                                }
                                imgAdapter.notifyDataSetChanged();
                            }


//                            //프로필 이미지
//                            if (!StringUtil.isNull(data.getPimg())) {
//                                binding.ivProfimg.setVisibility(View.VISIBLE);
//                                binding.ivNoprofimg.setVisibility(View.GONE);
//                                Glide.with(act)
//                                        .load(data.getPimg())
//                                        .into(binding.ivProfimg);
//
//                                ++pic_all_count;
//                                ++pic_profile_count;
//                            } else {
//                                binding.ivProfimg.setVisibility(View.GONE);
//                                binding.ivNoprofimg.setVisibility(View.VISIBLE);
//                                if (data.getGender().equalsIgnoreCase("male")) {
//                                    Glide.with(act)
//                                            .load(R.drawable.getmarried_img_main_mnoimg_190918)
//                                            .into(binding.ivNoprofimg);
//                                } else {
//                                    Glide.with(act)
//                                            .load(R.drawable.getmarried_img_main_wnoimg_190918)
//                                            .into(binding.ivNoprofimg);
//                                }
//                            }

                            //프로필이미지
                            if (!StringUtil.isNull(data.getPimg())) {
                                binding.ivProfimg.setVisibility(View.VISIBLE);
                                binding.ivNoprofimg.setVisibility(View.GONE);
                                Glide.with(act)
                                        .load(data.getPimg())
                                        .into(binding.ivProfimg);

                                ++pic_all_count;
                                ++pic_profile_count;
                            } else {
                                binding.ivProfimg.setVisibility(View.GONE);
                                binding.ivNoprofimg.setVisibility(View.VISIBLE);
                                Glide.with(act)
                                        .load(data.getCharacter_int())
                                        .into(binding.ivNoprofimg);
                            }


                            //이미지 개수 세팅
                            binding.tvAllCount.setText(String.valueOf(pic_all_count));
                            binding.tvProfileimgCount.setText(String.valueOf(pic_profile_count));
                            binding.tvIntroimgCount.setText(String.valueOf(pic_intro_count));

                            if (data.getCouple_type().equalsIgnoreCase("marry")) {
                                binding.tvMembertype.setText("결혼");
                                binding.tvMembertype.setTextColor(ContextCompat.getColor(act, R.color.color_407ff3));
                                binding.tvMembertype.setBackgroundResource(R.drawable.marriage_bg);
                            } else if (data.getCouple_type().equalsIgnoreCase("remarry")) {
                                binding.tvMembertype.setText("재혼");
                                binding.tvMembertype.setTextColor(ContextCompat.getColor(act, R.color.color_f34075));
                                binding.tvMembertype.setBackgroundResource(R.drawable.remarriage_bg);
                            } else if (data.getCouple_type().equalsIgnoreCase("friend")) {
                                binding.tvMembertype.setText("재혼");
                                binding.tvMembertype.setTextColor(ContextCompat.getColor(act, R.color.color_adapter_friend));
                                binding.tvMembertype.setBackgroundResource(R.drawable.remarriage_bg);
                            } else {
                                binding.tvMembertype.setText("-");
                                binding.tvMembertype.setTextColor(ContextCompat.getColor(act, R.color.color_407ff3));
                                binding.tvMembertype.setBackgroundResource(R.drawable.marriage_bg);
                            }

                            binding.tvName.setText(data.getFamilyname() + data.getName());

                            if (data.getGender().equalsIgnoreCase("male")) {
                                binding.tvGender.setText("남성");
                                binding.tvGender.setTextColor(ContextCompat.getColor(act, R.color.color_407ff3));
                            } else {
                                binding.tvGender.setText("여성");
                                binding.tvGender.setTextColor(ContextCompat.getColor(act, R.color.color_f34075));
                            }

                            binding.tvNick.setText(data.getNick());

                            binding.tvAge.setText(StringUtil.calcAge(data.getByear()) + "세");


                            binding.tvLocation.setText(data.getAddr1() + " " + data.getAddr2());

                            if (!StringUtil.isNull(data.getP_introduce())) {
                                binding.etIntroduce.setText(data.getP_introduce());
                            }
                        } else {
                            Toast.makeText(MyProfileAct.this, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MyProfileAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MyProfileAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        myprofile.addParams("uidx", UserPref.getUidx(this));
        myprofile.execute(true, true);
    }

    private void setImages() {
        ReqBasic setImages = new ReqBasic(this, NetUrls.REGIMAGES) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            JSONArray imgs = new JSONArray(jo.getString("imglist"));

                            if (imgpath.size() > 0) {
                                imgpath.clear();
                            }
                            getMyProfile();
//                            for (int i = 0; i < imgs.length(); i++) {
//                                JSONObject io = imgs.getJSONObject(i);
//                                ImagesData img = new ImagesData();
//
//                                img.setIdx(io.getString("idx"));
//                                img.setUidx(io.getString("u_idx"));
//                                img.setPiimg(io.getString("pi_img"));
//                                img.setRegdate(io.getString("pi_regdate"));
//
//                                imgpath.add(img);
//                            }
//                            imgAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(act, jo.getString("message"), Toast.LENGTH_SHORT).show();
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

        setImages.setTag("MyProfile Images");
        setImages.addParams("uidx", UserPref.getUidx(this));
        for (int i = 0; i < mSelected.size(); i++) {
            File img = new File(StringUtil.getPath(this, mSelected.get(i)));
//            Log.e("TEST_HOME", "path " + i + ": " + img.getPath());
            setImages.addFileParams("piimg[]", img);
        }
        setImages.execute(true, true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case DefaultValue.MODIFY:
                    getMyProfile();
                    break;
                case DefaultValue.IMAGES:
                    if (requestCode == DefaultValue.IMAGES && resultCode == RESULT_OK) {
                        mSelected = Matisse.obtainResult(data);
                        Log.e("TEST_HOME", "mSelected: " + mSelected);

                        if (imgpath.size() > 0) {
                            imgpath.clear();
                        }
                        setImages();
                    }
                    break;

                case DefaultValue.FUPLOAD:
                    getMyProfile();
                    break;

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
                        Log.e("TEST_HOME", "mImgFilePath: " + mImgFilePath);
                        regPimg();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else {
            switch (requestCode) {
                case REQUEST_IMAGE_CROP:
                    Toast.makeText(act, "사진잘라내기 실패", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void regPimg() {
        if (StringUtil.isNull(mImgFilePath)) {
            Log.e("TEST_HOME", "mImgFilePath: null");
        } else {
            Log.e("TEST_HOME", "mImgFilePath: null은아님");
            Log.e("TEST_HOME", "mImgFilePath: " + mImgFilePath);
        }

        ReqBasic regPimg = new ReqBasic(this, NetUrls.PIMGUP) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Profile Image Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            getMyProfile();
                            if (StringUtil.isNull(mImgFilePath)) {
                                Toast.makeText(act, "프로필 이미지가 삭제되었습니다", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(act, "등록이 완료되었습니다.\n사진/자기소개서 수정은 신청후 승인까지\n최대 1일 정도 소요됩니다.", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        } else {
                            if (StringUtil.isNull(mImgFilePath)) {
                                Toast.makeText(act, "이미지 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(act, "이미지 등록에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
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

        if (StringUtil.isNull(mImgFilePath)) {
            regPimg.addParams("uidx", UserPref.getUidx(this));
        } else {
            File f = new File(mImgFilePath);
            regPimg.addParams("uidx", UserPref.getUidx(this));
            regPimg.addFileParams("pimg", f);
        }
        regPimg.execute(true, true);
    }

    //TODO
    private void showImgloadDlg() {
        LayoutInflater dialog = LayoutInflater.from(this);
        View dlgLayout = dialog.inflate(R.layout.dlg_basicimgload_profile, null);
        final Dialog dlgImgload = new Dialog(this);
        dlgImgload.setContentView(dlgLayout);

        dlgImgload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dlgImgload.show();

        TextView btn_album = (TextView) dlgLayout.findViewById(R.id.btn_album);
        TextView btn_camera = (TextView) dlgLayout.findViewById(R.id.btn_camera);
        TextView btn_delete = (TextView) dlgLayout.findViewById(R.id.btn_delete);

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImgFilePath = null;
                regPimg();
                if (dlgImgload.isShowing()) {
                    dlgImgload.dismiss();
                }
            }
        });

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

    private void getAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "marryme" + timeStamp;
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

    private void cropImage() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoUri, "image/*");


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


        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", photoUri);

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(cropIntent, 0);

        Intent i = new Intent(cropIntent);
        ResolveInfo res = list.get(0);

        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        grantUriPermission(res.activityInfo.packageName, photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
        startActivityForResult(i, REQUEST_IMAGE_CROP);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;

            case R.id.iv_profadd:
                showImgloadDlg();
                break;

            case R.id.ll_btn_myprof:
                Intent modify1 = new Intent(this, MyprofileModifyAct.class);
                startActivityForResult(modify1, DefaultValue.MODIFY);
//                getMyProfile();
                break;

            case R.id.btn_modify:

                if (StringUtil.isNull(binding.etIntroduce.getText().toString())) {
                    Common.showToast(act, "자기소개서는 필수항목입니다");
                    return;
                } else {
                    if (binding.etIntroduce.length() < 30) {
                        Toast.makeText(MyProfileAct.this, "자기소개는 30자 이상입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (binding.etIntroduce.length() > 300) {
                        Toast.makeText(MyProfileAct.this, "자기소개는 300자 이하입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                setIntroduce(binding.etIntroduce.getText().toString());
                break;
        }
    }
}
