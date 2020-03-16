package com.match.honey.activity;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.match.honey.R;
import com.match.honey.adapters.list.BlocklistAdapter;
import com.match.honey.databinding.ActivityBlockmemBinding;
import com.match.honey.listDatas.BlockmemData;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.Common;
import com.match.honey.utils.ItemOffsetDecoration;
import com.match.honey.utils.ItemOffsetDecorationBlock;
import com.match.honey.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BlockmemberAct extends Activity implements View.OnClickListener{

    ActivityBlockmemBinding binding;

    ArrayList<BlockmemData> list = new ArrayList<>();
    BlocklistAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_blockmem);

        binding.flBack.setOnClickListener(this);
        binding.btnBlockcancel.setOnClickListener(this);
        binding.llHome.setOnClickListener(this);

        binding.rcvBlockmem.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BlocklistAdapter(this,list);
        binding.rcvBlockmem.setAdapter(adapter);
        binding.rcvBlockmem.addItemDecoration(new ItemOffsetDecorationBlock(this));

        blockMembers();

    }

    private void blockMembers(){
        ReqBasic block = new ReqBasic(this, NetUrls.BLOCKLIST) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null){

                    try{
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Block List Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)){

                            JSONArray ja = new JSONArray(jo.getString("value"));
                            ArrayList<BlockmemData> tmplist = new ArrayList<>();


                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject obj = ja.getJSONObject(i);
                                BlockmemData data = new BlockmemData();

                                data.setMidx(obj.getString("bu_idx"));
                                data.setNickname(obj.getString("nick"));
                                data.setMembertype(obj.getString("couple_type"));

                                SimpleDateFormat orgin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm");
                                try {
                                    Date old = orgin.parse(obj.getString("regdate"));
                                    data.setDate(sdf.format(old));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                data.setGender(obj.getString("gender"));
                                data.setProfimg(obj.getString("pimg"));
                                data.setProfimg_ck(obj.getString("pimg_ck"));
                                data.setIntroduce(obj.getString("p_introduce"));
                                data.setFamilyname(obj.getString("familyname"));
                                data.setName(obj.getString("name"));
                                data.setLastnameYN(obj.getString("lastnameYN"));
                                data.setByear(StringUtil.calcAge(obj.getString("byear")));
                                data.setAddr1(obj.getString("addr1"));
                                data.setAddr2(obj.getString("addr2"));
                                data.setPimg_ck(obj.getString("pimg_ck"));
                                data.setPint_ck(obj.getString("pint_ck"));
                                data.setCharacter(Common.getCharacterDrawable(obj.getString("character"), obj.getString("gender")));

                                tmplist.add(data);
                            }

                            list.addAll(tmplist);
                            adapter.notifyDataSetChanged();


                        }else{
                            adapter.notifyDataSetChanged();
                        }

                    }catch (JSONException e){
                        adapter.notifyDataSetChanged();
                        e.printStackTrace();
                        Toast.makeText(BlockmemberAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    adapter.notifyDataSetChanged();
                    Toast.makeText(BlockmemberAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        block.addParams("uidx", UserPref.getUidx(this));
        block.execute(true,true);
    }

    private void unBlock(String unMembers){
        ReqBasic unBlock = new ReqBasic(this,NetUrls.UNBLOCK) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null){

                    try{
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)){
                            Toast.makeText(BlockmemberAct.this, jo.getString("value"), Toast.LENGTH_SHORT).show();

                            if (list.size() > 0){
                                list.clear();
                            }
                            blockMembers();
                        }else{
                            Toast.makeText(BlockmemberAct.this, jo.getString("value"), Toast.LENGTH_SHORT).show();
                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                        Toast.makeText(BlockmemberAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(BlockmemberAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }

            }
        };

        unBlock.addParams("uidx",UserPref.getUidx(this));
        unBlock.addParams("tidxs",unMembers);
        unBlock.execute(true,true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fl_back:
                finish();
                break;

            case R.id.ll_home:
                ((MainActivity) MainActivity.act).fromHomeBtn();
                finish();
                break;

            case R.id.btn_blockcancel:
                String checkMem = "";
                for (BlockmemData item : list){
                    if (item.isCheckState()){
                        if (StringUtil.isNull(checkMem)){
                            checkMem = item.getMidx();
                        }else{
                            checkMem += "|" + item.getMidx();
                        }
                    }
                }

                if (StringUtil.isNull(checkMem)){
                    Toast.makeText(BlockmemberAct.this, "선택 항목이 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (list.size() > 0){
                    list.clear();
                }
                unBlock(checkMem);
                break;
        }
    }
}
