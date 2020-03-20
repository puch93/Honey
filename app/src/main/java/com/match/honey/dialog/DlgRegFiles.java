package com.match.honey.dialog;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.match.honey.R;
import com.match.honey.databinding.DlgRegfilesBinding;
import com.match.honey.listDatas.ProfileData;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.DefaultValue;
import com.match.honey.utils.Glide4Engine;
import com.match.honey.utils.StringUtil;
import com.naver.mei.sdk.MeiGifEncoder;
import com.naver.mei.sdk.core.gif.encoder.EncodingListener;
import com.naver.mei.sdk.error.MeiSDKException;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.listener.OnSelectedListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DlgRegFiles extends Activity implements View.OnClickListener {

    DlgRegfilesBinding binding;

    String ftype;
    List<Uri> mSelected;
    int selectIdx = 0;
    ProfileData data;

    HashMap<String, File> regFiles = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        // 아래에 원하는 투명도를 넣으면 된다.
        lpWindow.dimAmount = 0.6f;
        lpWindow.height = (int) (deviceHeight * 0.85f);
        getWindow().setAttributes(lpWindow);

        binding = DataBindingUtil.setContentView(this, R.layout.dlg_regfiles);

        ftype = getIntent().getStringExtra("ftype");
        data = (ProfileData) getIntent().getSerializableExtra("profile");

        switch (ftype) {
            case "audio":
                binding.tvTitle.setText("음성 등록하기");
                binding.ivFile1.setImageResource(R.drawable.btn_profilewrite_icon01);
                binding.ivFile2.setImageResource(R.drawable.btn_profilewrite_icon01);
                binding.ivFile3.setImageResource(R.drawable.btn_profilewrite_icon01);
                binding.tvFile1.setText("음성 1");
                binding.tvFile2.setText("음성 2");
                binding.tvFile3.setText("음성 3");

                if (!StringUtil.isNull(data.getP_wave1())) {
                    binding.tvFile1.setTextColor(Color.RED);
                }
                if (!StringUtil.isNull(data.getP_wave2())) {
                    binding.tvFile2.setTextColor(Color.RED);
                }
                if (!StringUtil.isNull(data.getP_wave3())) {
                    binding.tvFile3.setTextColor(Color.RED);
                }

                break;
            case "video":
                binding.tvTitle.setText("영상 등록하기");
                binding.ivFile1.setImageResource(R.drawable.btn_profilewrite_icon02);
                binding.ivFile2.setImageResource(R.drawable.btn_profilewrite_icon02);
                binding.ivFile3.setImageResource(R.drawable.btn_profilewrite_icon02);
                binding.tvFile1.setText("동영상 1");
                binding.tvFile2.setText("동영상 2");
                binding.tvFile3.setText("동영상 3");
//                boolean isCompressSuccess=MediaController.getInstance().convertVideo(inPath,outPath);         // 동영상파일 인코딩

                if (!StringUtil.isNull(data.getP_movthumb1())) {
                    binding.ivFile1.setVisibility(View.GONE);
                    Log.i(StringUtil.TAG,"thumb: "+data.getP_movthumb1());
                    Glide.with(DlgRegFiles.this)
                            .load(data.getP_movthumb1())
                            .into(binding.ivGif1);
                }
                if (!StringUtil.isNull(data.getP_movthumb2())) {
                    binding.ivFile2.setVisibility(View.GONE);
                    Glide.with(DlgRegFiles.this)
                            .load(data.getP_movthumb2())
                            .into(binding.ivGif2);
                }
                if (!StringUtil.isNull(data.getP_movthumb3())) {
                    binding.ivFile3.setVisibility(View.GONE);
                    Glide.with(DlgRegFiles.this)
                            .load(data.getP_movthumb3())
                            .into(binding.ivGif3);
                }

                break;
            case "gif":
                binding.tvTitle.setText("움짤 등록하기");
                binding.ivFile1.setImageResource(R.drawable.btn_profilewrite_icon03);
                binding.ivFile2.setImageResource(R.drawable.btn_profilewrite_icon03);
                binding.ivFile3.setImageResource(R.drawable.btn_profilewrite_icon03);
                binding.tvFile1.setText("움짤 1");
                binding.tvFile2.setText("움짤 2");
                binding.tvFile3.setText("움짤 3");

                if (!StringUtil.isNull(data.getP_gif1())) {
                    binding.ivFile1.setVisibility(View.GONE);
                    Glide.with(DlgRegFiles.this)
                            .load(data.getP_gif1())
                            .into(binding.ivGif1);
                }

                if (!StringUtil.isNull(data.getP_gif2())) {
                    binding.ivFile2.setVisibility(View.GONE);
                    Glide.with(DlgRegFiles.this)
                            .load(data.getP_gif2())
                            .into(binding.ivGif2);
                }

                if (!StringUtil.isNull(data.getP_gif3())) {
                    binding.ivFile3.setVisibility(View.GONE);
                    Glide.with(DlgRegFiles.this)
                            .load(data.getP_gif3())
                            .into(binding.ivGif3);
                }
                break;
        }

        binding.btnRegist.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);

        binding.flFile1Area.setOnClickListener(this);
        binding.flFile2Area.setOnClickListener(this);
        binding.flFile3Area.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            Log.i(StringUtil.TAG, "data == null");
            return;
        }

        switch (requestCode) {
            case DefaultValue.AUDIO:

                File atmp = new File(StringUtil.getPath(this, data.getData()));

                if (atmp.length() >= (long) (1024 * 1024 * 10)) {
                    Toast.makeText(this, "10MB 이하의 음성파일을 선택하세요", Toast.LENGTH_SHORT).show();
                } else {
                    switch (selectIdx) {
                        case 0:
                            binding.tvFile1.setTextColor(Color.RED);
                            regFiles.put("pwav1", atmp);
                            break;
                        case 1:
                            binding.tvFile2.setTextColor(Color.RED);
                            regFiles.put("pwav2", atmp);
                            break;
                        case 2:
                            binding.tvFile3.setTextColor(Color.RED);
                            regFiles.put("pwav3", atmp);
                            break;
                    }
                }

                break;
            case DefaultValue.VIDEO:
//                https://gist.github.com/OnlyInAmerica/3dadd26fddb29c0f777d        썸네일 가져오기

                File vtmp = new File(StringUtil.getPath(this, data.getData()));
                Log.i(StringUtil.TAG,"idx: "+selectIdx);
                Log.i(StringUtil.TAG,"getData: "+data.getData());
                Log.i(StringUtil.TAG,"path: "+vtmp.getPath());

                Bitmap bitmap = null;
//                bitmap = ThumbnailUtils.createVideoThumbnail(vtmp.getPath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                bitmap = ThumbnailUtils.createVideoThumbnail(vtmp.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);

                if (bitmap == null){
                    try{
                        bitmap = createVideoThumbnail(vtmp.getPath());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                if (bitmap != null){
                    Toast.makeText(this, "썸네일 생성 완료", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "썸네일 생성 실패", Toast.LENGTH_SHORT).show();
                }

                Log.i(StringUtil.TAG,"video size: "+vtmp.length());

                if (vtmp.length() >= (long) (1024 * 1024 * 10)) {
                    Toast.makeText(this, "10MB 이하의 동영상을 선택하세요", Toast.LENGTH_SHORT).show();
                } else {
                    switch (selectIdx) {
                        case 0:
                            binding.ivFile1.setVisibility(View.GONE);
                            Log.i(StringUtil.TAG,"case 0");
                            binding.ivGif1.setImageBitmap(bitmap);
                            regFiles.put("pmov1", vtmp);
                            break;
                        case 1:
                            binding.ivFile2.setVisibility(View.GONE);
                            Log.i(StringUtil.TAG,"case 1");
                            binding.ivGif2.setImageBitmap(bitmap);
                            regFiles.put("pmov2", vtmp);
                            break;
                        case 2:
                            binding.ivFile3.setVisibility(View.GONE);
                            Log.i(StringUtil.TAG,"case 2");
                            binding.ivGif3.setImageBitmap(bitmap);
                            regFiles.put("pmov3", vtmp);
                            break;
                    }
                }

                break;
            case DefaultValue.GIF:
                if (requestCode == DefaultValue.GIF && resultCode == RESULT_OK) {
                    mSelected = Matisse.obtainResult(data);
                    Log.d(StringUtil.TAG, "mSelected: " + mSelected);
                    Toast.makeText(this, "움짤 생성중입니다", Toast.LENGTH_SHORT).show();

                    String filename = "";
                    switch (selectIdx) {
                        case 0:
                            binding.ivFile1.setVisibility(View.GONE);
                            filename = "gif1.gif";
                            break;
                        case 1:
                            binding.ivFile2.setVisibility(View.GONE);
                            filename = "gif2.gif";
                            break;
                        case 2:
                            binding.ivFile3.setVisibility(View.GONE);
                            filename = "gif3.gif";
                            break;
                    }

                    createGif(mSelected.size(), mSelected, filename);

                }
                break;
        }
    }

    /**
     * gif 파일 생성 이미지 비트맵으로 변경해서 넣어줄것 addFrame은 for문으로 돌릴것
     */
    private void createGif(final int size, final List<Uri> list, String filename) {

        final File gif = new File(Environment.getExternalStorageDirectory() + "/MARRYME/" + filename);
        Log.i(StringUtil.TAG, "path: " + gif.getPath());
        FileOutputStream fos = null;

        try {

            List<Bitmap> imgs = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                Bitmap orgin = MediaStore.Images.Media.getBitmap(getContentResolver(), list.get(i));
                Bitmap bm = Bitmap.createScaledBitmap(orgin, 1024, 1024, false);
                imgs.add(bm);
            }


            MeiGifEncoder encoder = MeiGifEncoder.newInstance();
            encoder.setQuality(10);
            encoder.setColorLevel(7);
            encoder.setDelay(500);

            EncodingListener encodingListener = new EncodingListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(DlgRegFiles.this, "움짤이 생성되었습니다", Toast.LENGTH_SHORT).show();

                    if (selectIdx == 0) {
                        File gif1 = new File(gif.getPath());
                        regFiles.put("pgif1", gif1);
                        Glide.with(DlgRegFiles.this)
                                .load(gif.getPath())
                                .into(binding.ivGif1);
                    } else if (selectIdx == 1) {
                        File gif2 = new File(gif.getPath());
                        regFiles.put("pgif2", gif2);
                        Glide.with(DlgRegFiles.this)
                                .load(gif.getPath())
                                .into(binding.ivGif2);
                    } else if (selectIdx == 2) {
                        File gif3 = new File(gif.getPath());
                        regFiles.put("pgif3", gif3);
                        Glide.with(DlgRegFiles.this)
                                .load(gif.getPath())
                                .into(binding.ivGif3);
                    }

                    Log.i(StringUtil.TAG, "onSuccess");
                }

                @Override
                public void onError(MeiSDKException e) {
                    Log.i(StringUtil.TAG, "onError: " + e.toString());
                }

            };

            encoder.encodeByBitmaps(imgs, gif.getPath(), encodingListener);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void uploadAudio() {
        ReqBasic uploadAudio = new ReqBasic(this, NetUrls.REGAUDIO) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        Toast.makeText(DlgRegFiles.this, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            // 업로드 성공
                            setResult(RESULT_OK);
                            finish();

                        } else {
                            // 업로드 실패
                        }

                    } catch (JSONException e) {
                        Toast.makeText(DlgRegFiles.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(DlgRegFiles.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }

            }
        };


        uploadAudio.addParams("uidx", UserPref.getUidx(this));
        if (regFiles.containsKey("pwav1")) {
            uploadAudio.addFileParams("pwav1", regFiles.get("pwav1"));
        } else {
            uploadAudio.addParams("pwav1", "");
        }

        if (regFiles.containsKey("pwav2")) {
            uploadAudio.addFileParams("pwav2", regFiles.get("pwav2"));
        } else {
            uploadAudio.addParams("pwav2", "");
        }

        if (regFiles.containsKey("pwav3")) {
            uploadAudio.addFileParams("pwav3", regFiles.get("pwav3"));
        } else {
            uploadAudio.addParams("pwav3", "");
        }

        uploadAudio.execute(true, true);
    }

    private void uploadVideo() {
        ReqBasic uploadVideo = new ReqBasic(this, NetUrls.REGVIDEO) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        Toast.makeText(DlgRegFiles.this, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            // 업로드 성공
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            // 업로드 실패
                        }

                    } catch (JSONException e) {
                        Toast.makeText(DlgRegFiles.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(DlgRegFiles.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }

            }
        };


        uploadVideo.addParams("uidx", UserPref.getUidx(this));

        if (regFiles.containsKey("pmov1")) {
            BitmapDrawable thum1 = (BitmapDrawable) binding.ivGif1.getDrawable();

            try {
                FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/MARRYME/thumb1.jpg");

                thum1.getBitmap().compress(Bitmap.CompressFormat.JPEG, 90, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            File thumb1 = new File(Environment.getExternalStorageDirectory() + "/MARRYME/thumb1.jpg");

            uploadVideo.addFileParams("pmov1", regFiles.get("pmov1"));
            uploadVideo.addFileParams("pmovthumb1", thumb1);

        } else {
            uploadVideo.addParams("pmov1", "");
            uploadVideo.addParams("pmovthumb1", "");
        }

        if (regFiles.containsKey("pmov2")) {
            BitmapDrawable thum2 = (BitmapDrawable) binding.ivGif2.getDrawable();

            try {
                FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/MARRYME/thumb2.jpg");

                thum2.getBitmap().compress(Bitmap.CompressFormat.JPEG, 90, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            File thumb2 = new File(Environment.getExternalStorageDirectory() + "/MARRYME/thumb2.jpg");

            uploadVideo.addFileParams("pmov2", regFiles.get("pmov2"));
            uploadVideo.addFileParams("pmovthumb2", thumb2);
        } else {
            uploadVideo.addParams("pmov2", "");
            uploadVideo.addParams("pmovthumb2", "");
        }

        if (regFiles.containsKey("pmov3")) {
            BitmapDrawable thum3 = (BitmapDrawable) binding.ivGif3.getDrawable();

            try {
                FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/MARRYME/thumb3.jpg");

                thum3.getBitmap().compress(Bitmap.CompressFormat.JPEG, 90, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            File thumb3 = new File(Environment.getExternalStorageDirectory() + "/MARRYME/thumb3.jpg");

            uploadVideo.addFileParams("pmov3", regFiles.get("pmov3"));
            uploadVideo.addFileParams("pmovthumb3", thumb3);
        } else {
            uploadVideo.addParams("pmov3", "");
            uploadVideo.addParams("pmovthumb3", "");
        }

        uploadVideo.execute(true, true);
    }

    private void uploadGif() {
        ReqBasic uploadGif = new ReqBasic(this, NetUrls.REGGIF) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        Toast.makeText(DlgRegFiles.this, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            // 업로드 성공
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            // 업로드 실패
                            Toast.makeText(DlgRegFiles.this, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(DlgRegFiles.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(DlgRegFiles.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        uploadGif.addParams("uidx", UserPref.getUidx(this));

        if (regFiles.containsKey("pgif1")) {
            uploadGif.addFileParams("pgif1", regFiles.get("pgif1"));
        } else {
            uploadGif.addParams("pgif1", "");
        }

        if (regFiles.containsKey("pgif2")) {
            uploadGif.addFileParams("pgif2", regFiles.get("pgif2"));
        } else {
            uploadGif.addParams("pgif2", "");
        }

        if (regFiles.containsKey("pgif3")) {
            uploadGif.addFileParams("pgif3", regFiles.get("pgif3"));
        } else {
            uploadGif.addParams("pgif3", "");
        }

        uploadGif.execute(true, true);
    }

    private Bitmap createVideoThumbnail(String filepath){
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try{
            retriever.setDataSource(filepath);
            bitmap = retriever.getFrameAtTime(1);
        }catch (IllegalArgumentException ex){

        }catch (RuntimeException ex){

        }catch (Exception ex){

        }finally {
            try {
                retriever.release();
            }catch (RuntimeException ex){

            }
        }

        if (bitmap == null){
            return null;
        }

        return bitmap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_regist:
                if (ftype.equalsIgnoreCase("audio")) {
                    uploadAudio();
                } else if (ftype.equalsIgnoreCase("video")) {
                    uploadVideo();
                } else if (ftype.equalsIgnoreCase("gif")) {
                    uploadGif();
                }
                break;
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.fl_file1_area:
                selectIdx = 0;
                if (ftype.equalsIgnoreCase("audio")) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("audio/*");
                    List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
                    ResolveInfo res = list.get(0);
                    intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                    startActivityForResult(intent, DefaultValue.AUDIO);

                } else if (ftype.equalsIgnoreCase("video")) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("video/*");
                    List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
                    ResolveInfo res = list.get(0);
                    intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    startActivityForResult(intent, DefaultValue.VIDEO);
                } else if (ftype.equalsIgnoreCase("gif")) {
                    Matisse.from(this)
                            .choose(MimeType.ofImage())
                            .countable(true)
                            .maxSelectable(20)
                            .imageEngine(new Glide4Engine())
                            .setOnSelectedListener(new OnSelectedListener() {
                                @Override
                                public void onSelected(@NonNull List<Uri> uriList, @NonNull List<String> pathList) {

                                }
                            })
                            .spanCount(3)
                            .forResult(DefaultValue.GIF);
                }

                break;
            case R.id.fl_file2_area:
                selectIdx = 1;
                if (ftype.equalsIgnoreCase("audio")) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("audio/*");
                    List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
                    ResolveInfo res = list.get(0);
                    intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                    startActivityForResult(intent, DefaultValue.AUDIO);
                } else if (ftype.equalsIgnoreCase("video")) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("video/*");
                    List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
                    ResolveInfo res = list.get(0);
                    intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    startActivityForResult(intent, DefaultValue.VIDEO);
                } else if (ftype.equalsIgnoreCase("gif")) {
                    Matisse.from(this)
                            .choose(MimeType.ofImage())
                            .countable(true)
                            .maxSelectable(20)
                            .imageEngine(new Glide4Engine())
                            .setOnSelectedListener(new OnSelectedListener() {
                                @Override
                                public void onSelected(@NonNull List<Uri> uriList, @NonNull List<String> pathList) {

                                }
                            })
                            .spanCount(3)
                            .forResult(DefaultValue.GIF);
                }

                break;
            case R.id.fl_file3_area:
                selectIdx = 2;
                if (ftype.equalsIgnoreCase("audio")) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("audio/*");
                    List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
                    ResolveInfo res = list.get(0);
                    intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                    startActivityForResult(intent, DefaultValue.AUDIO);
                } else if (ftype.equalsIgnoreCase("video")) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("video/*");
                    List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
                    ResolveInfo res = list.get(0);
                    intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    startActivityForResult(intent, DefaultValue.VIDEO);
                } else if (ftype.equalsIgnoreCase("gif")) {
                    Matisse.from(this)
                            .choose(MimeType.ofImage())
                            .countable(true)
                            .maxSelectable(20)
                            .imageEngine(new Glide4Engine())
                            .setOnSelectedListener(new OnSelectedListener() {
                                @Override
                                public void onSelected(@NonNull List<Uri> uriList, @NonNull List<String> pathList) {

                                }
                            })
                            .spanCount(3)
                            .forResult(DefaultValue.GIF);
                }

                break;
        }
    }
}
