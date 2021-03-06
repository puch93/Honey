package com.match.honey.termFrag;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.match.honey.R;
import com.match.honey.databinding.TermsDetailBinding;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;



public class TermSub03 extends Fragment{

    TermsDetailBinding binding;

    public static TermSub03 newInstance() {

        Bundle args = new Bundle();

        TermSub03 fragment = new TermSub03();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.terms_detail,container,false);

        getTerms();
        return binding.getRoot();
    }

    private void getTerms(){
        ReqBasic getTerms = new ReqBasic(getActivity(), NetUrls.TERMS) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                Log.i(StringUtil.TAG, "getTerms:  " + resultData.getResult() + "\ncode: " + resultCode);

                if(resultData.getResult() != null){
                    try{
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if(jo.has("app_gps_service")){
                            binding.tvTermsContent.setText(jo.getString("app_gps_service"));
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
//                        Toast.makeText(getActivity(), getString(R.string.network_err), Toast.LENGTH_SHORT).show();
                    }

                }else{
//                    Toast.makeText(getActivity(), getString(R.string.network_err), Toast.LENGTH_SHORT).show();
                }
            }
        };

        getTerms.execute(true,true);
    }
}
