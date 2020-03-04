package com.match.marryme.termFrag;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.match.marryme.R;
import com.match.marryme.databinding.TermsDetailBinding;
import com.match.marryme.network.ReqBasic;
import com.match.marryme.network.netUtil.HttpResult;
import com.match.marryme.network.netUtil.NetUrls;
import com.match.marryme.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class TermSub01 extends Fragment{

    TermsDetailBinding binding;

    public static TermSub01 newInstance() {

        Bundle args = new Bundle();

        TermSub01 fragment = new TermSub01();
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

                        if(jo.has("app_term_condition")){
                            binding.tvTermsContent.setText(jo.getString("app_term_condition"));
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
