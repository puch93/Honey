package com.match.marryme.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.match.marryme.R;
import com.match.marryme.databinding.ActivityBoarddetailBinding;
import com.match.marryme.network.ReqBasic;
import com.match.marryme.network.netUtil.HttpResult;
import com.match.marryme.network.netUtil.NetUrls;
import com.match.marryme.sharedPref.UserPref;
import com.match.marryme.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class TokboardDetailAct extends Activity implements View.OnClickListener{

    ActivityBoarddetailBinding binding;
    public static Activity act;
    String idx,title,img,hit,hint, commentCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_boarddetail);
        act = this;

        binding.flBack.setOnClickListener(this);
        binding.llHome.setOnClickListener(this);
        binding.btnBoardReg.setOnClickListener(this);
        binding.tvComment.setOnClickListener(this);

        idx = getIntent().getStringExtra("idx");
        title = getIntent().getStringExtra("title");
        img = getIntent().getStringExtra("img");
        hit = getIntent().getStringExtra("hit");
        hint = getIntent().getStringExtra("hint");
        commentCount = getIntent().getStringExtra("commentCount");

        binding.tvTitletext.setText(title);
        Glide.with(this)
                .load(img)
                .into(binding.ivBoardSubimg);
        binding.tvViewcount.setText(hit);
        binding.etBoardContents.setHint(hint);
        binding.tvParticnt.setText(commentCount);

        updatePageCount();
    }

    private void write(){
        ReqBasic write = new ReqBasic(this,NetUrls.WRITETOKBOARD) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null){
                    try{
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Tok Write Get Info: " + jo);
                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)){
                            Toast.makeText(TokboardDetailAct.this, jo.getString("comment"), Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(TokboardDetailAct.this, jo.getString("comment"), Toast.LENGTH_SHORT).show();
                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                        Toast.makeText(TokboardDetailAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(TokboardDetailAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }

            }
        };

        write.addParams("tbidx",idx);
        write.addParams("uidx", UserPref.getUidx(this));
        write.addParams("comment",binding.etBoardContents.getText().toString());
        write.execute(true,true);
    }


    private void updatePageCount(){
        ReqBasic write = new ReqBasic(this,NetUrls.UPDATETOKBOARD) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null){
                    try{
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Tok Count Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)){
                        }else{
                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                        Toast.makeText(TokboardDetailAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(TokboardDetailAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }

            }
        };

        write.addParams("tidx",idx);
        write.execute(true,true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fl_back:
                finish();
                break;

            case R.id.ll_home:
                if(TokboardAct.act != null) {
                    TokboardAct.act.finish();
                }
                ((MainActivity) MainActivity.act).fromHomeBtn();
                finish();
                break;

            case R.id.tv_comment:
                Intent intent = new Intent(act, TokboardCommentAct.class);
                intent.putExtra("idx",idx);
                act.startActivity(intent);
                break;

            case R.id.btn_board_reg:

                if (binding.etBoardContents.length() == 0){
                    Toast.makeText(TokboardDetailAct.this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                write();
                break;
        }
    }
}
