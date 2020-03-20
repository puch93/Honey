package com.match.honey.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.match.honey.R;
import com.match.honey.adapters.list.MyQnaListAdapter;
import com.match.honey.databinding.ActivityQnaBinding;
import com.match.honey.listDatas.MyQnaData;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.DefaultValue;
import com.match.honey.utils.ItemOffsetDecorationCustom;
import com.match.honey.utils.StringUtil;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QnaAct extends Activity implements View.OnClickListener {

    static final int REQUEST_TAKE_PHOTO = 2001;
    static final int REQUEST_TAKE_ALBUM = 2002;
    static final int REQUEST_IMAGE_CROP = 2003;

    ActivityQnaBinding binding;

    ArrayList<MyQnaData> list = new ArrayList<>();
    MyQnaListAdapter adapter;

    private Uri photoUri;
    private String mImgFilePath;
    File sendFile = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_qna);

        binding.flBack.setOnClickListener(this);
        binding.llHome.setOnClickListener(this);

        binding.llQnat01.setOnClickListener(this);
        binding.llQnat02.setOnClickListener(this);
        binding.btnQuestion.setOnClickListener(this);

        binding.llQnatype.setOnClickListener(this);
        binding.flPhotoarea.setOnClickListener(this);

        binding.llQnat01.setSelected(true);

        binding.rcvMyquestion.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyQnaListAdapter(this, list, "qna");
        binding.rcvMyquestion.setAdapter(adapter);
        binding.rcvMyquestion.addItemDecoration(new ItemOffsetDecorationCustom(this, getResources().getDimensionPixelSize(R.dimen.dimen_7)));

    }

    private void initTabbtn() {
        binding.llQnat01.setSelected(false);
        binding.llQnat02.setSelected(false);
    }

    private void showImgloadDlg() {
        LayoutInflater dialog = LayoutInflater.from(this);
        View dlgLayout = dialog.inflate(R.layout.dlg_basicimgload, null);
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

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "ybquestion" + timeStamp;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/MARRYME/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }

    private void getAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
    }

    private void cropImage() {
        Log.i(StringUtil.TAG, "cropImage");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            grantUriPermission("com.android.camera", photoUri,
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
                photoUri = FileProvider.getUriForFile(this,
                        "com.match.honey.provider", tempFile);
            } else {
                photoUri = Uri.fromFile(tempFile);
            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            intent.putExtra("return-pdata", false);
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

    /**
     * 문의사항 전송
     *
     * @param qType 문의종류(login:로그인,connecterr:접속오류,service:서비스,payment:결제,dropuser:탈퇴,etc:기타)
     */
    private void regQuestion(String qType) {
        ReqBasic question = new ReqBasic(this, NetUrls.REGQUESTION) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            Toast.makeText(QnaAct.this, jo.getString("comment"), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(QnaAct.this, jo.getString("comment"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(QnaAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(QnaAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        question.addParams("uidx", UserPref.getUidx(this));
        question.addParams("qtype", qType);
//        question.addParams("ucell", binding.etCellnum.getText().toString());
        question.addParams("qcontent", binding.etQcontents.getText().toString());

        if (sendFile != null) {
            question.addFileParams("rfile", sendFile);
        }
        question.execute(true, true);
    }

    private void myQnaList() {
        ReqBasic myQna = new ReqBasic(this, NetUrls.QNALIST) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Qna List Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {

                            JSONArray ja = new JSONArray(jo.getString("value"));
                            ArrayList<MyQnaData> tmplist = new ArrayList<>();

                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject obj = ja.getJSONObject(i);
                                MyQnaData data = new MyQnaData();

                                String answer = obj.getString("a_content");
                                answer = answer.replaceAll("<br\\/>", "\n");
                                data.setAnswer(answer);
                                data.setIdx(obj.getString("idx"));

                                String qtype = "";
                                if (obj.getString("q_type").equalsIgnoreCase("login")) {
                                    qtype = "로그인";
                                } else if (obj.getString("q_type").equalsIgnoreCase("connecterr")) {
                                    qtype = "접속오류";
                                } else if (obj.getString("q_type").equalsIgnoreCase("service")) {
                                    qtype = "서비스";
                                } else if (obj.getString("q_type").equalsIgnoreCase("payment")) {
                                    qtype = "결제문의";
                                } else if (obj.getString("q_type").equalsIgnoreCase("dropuser")) {
                                    qtype = "탈퇴하기";
                                } else if (obj.getString("q_type").equalsIgnoreCase("etc")) {
                                    qtype = "기타";
                                }
                                data.setQnatype(qtype);

                                String question = obj.getString("q_content");
                                question = question.replaceAll("<br\\/>", "\n");
                                data.setQtext(question);

                                if (obj.getString("qna_status").equalsIgnoreCase("answer")) {
                                    data.setReplystate(true);
                                } else {
                                    data.setReplystate(false);
                                }

                                data.setRegdate(obj.getString("q_regdate"));
                                data.setAttach(obj.getString("q_referfile"));

                                tmplist.add(data);

                                Log.e("TEST_HOME", "onData: " + data);

                            }
                            list.addAll(tmplist);
                            adapter.notifyDataSetChanged();


                            Log.e("TEST_HOME", "success");
                        } else {
                            Log.e("TEST_HOME", "non-success");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(QnaAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(QnaAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }

            }
        };

        myQna.addParams("uidx", UserPref.getUidx(this));
        myQna.execute(true, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            Log.i(StringUtil.TAG, "pdata == null");
            return;
        }

        switch (requestCode) {
            case DefaultValue.QUESTION:

                String result = data.getStringExtra("data");

                Log.i(StringUtil.TAG, "res: " + result);
                binding.tvQnatype.setText(result);
                break;
            case REQUEST_TAKE_PHOTO:
                Log.i(StringUtil.TAG, "REQUEST_TAKE_PHOTO");
                binding.ivCameraimg.setVisibility(View.GONE);
                cropImage();

                break;
            case REQUEST_TAKE_ALBUM:
                if (data == null) {
                    Toast.makeText(this, "사진열기 실패 / 사진 불러오기 실패", Toast.LENGTH_SHORT).show();
                    return;
                }
                photoUri = data.getData();
                binding.ivCameraimg.setVisibility(View.GONE);
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

                    binding.ivCameraimg.setVisibility(View.GONE);

                    Glide.with(this)
                            .load(mImgFilePath)
                            .into(binding.ivQnacontents);

                    sendFile = new File(mImgFilePath);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
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
                if (cellnum.length() < 11) {
                    returnValue = false;
                }
            }
            return returnValue;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.ll_home:
                if(ServiceCenterAct.act != null) {
                    ServiceCenterAct.act.finish();
                }

                ((MainActivity) MainActivity.act).fromHomeBtn();

                finish();
                break;
            case R.id.ll_qnatype:
                Intent question = new Intent(this, ListDlgAct.class);
                question.putExtra("subject", "question");
                question.putExtra("select", binding.tvQnatype.getText());
                startActivityForResult(question, DefaultValue.QUESTION);
                break;
            case R.id.fl_photoarea:
                showImgloadDlg();
                break;
            case R.id.ll_qnat01:
                initTabbtn();
                binding.llQnat01.setSelected(true);
                binding.scT01Layout.setVisibility(View.VISIBLE);
                binding.rcvMyquestion.setVisibility(View.GONE);
                break;
            case R.id.ll_qnat02:
                initTabbtn();
                binding.llQnat02.setSelected(true);
                binding.scT01Layout.setVisibility(View.GONE);
                binding.rcvMyquestion.setVisibility(View.VISIBLE);

                if (list.size() > 0) {
                    list.clear();
                }
                myQnaList();
                break;
            case R.id.btn_question:

                if (binding.tvQnatype.getText().toString().equalsIgnoreCase("문의종류")) {
                    Toast.makeText(QnaAct.this, "문의종류를 선택하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

//                if (binding.etCellnum.length() == 0) {
//                    Toast.makeText(QnaAct.this, "핸드폰번호를 입력해주세요", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (!checkCellnum(binding.etCellnum.getText().toString())) {
//                    Toast.makeText(QnaAct.this, "핸드폰번호를 확인해주세요", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                if (binding.etQcontents.length() == 0) {
                    Toast.makeText(QnaAct.this, "문의내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                String qeustion = "";
                if (binding.tvQnatype.getText().toString().equalsIgnoreCase("로그인")) {
                    qeustion = "login";
                } else if (binding.tvQnatype.getText().toString().equalsIgnoreCase("접속오류")) {
                    qeustion = "connecterr";
                } else if (binding.tvQnatype.getText().toString().equalsIgnoreCase("서비스")) {
                    qeustion = "service";
                } else if (binding.tvQnatype.getText().toString().equalsIgnoreCase("결제")) {
                    qeustion = "payment";
                } else if (binding.tvQnatype.getText().toString().equalsIgnoreCase("탈퇴")) {
                    qeustion = "dropuser";
                } else if (binding.tvQnatype.getText().toString().equalsIgnoreCase("기타")) {
                    qeustion = "etc";
                }

                regQuestion(qeustion);
                break;
        }
    }
}
