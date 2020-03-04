package com.match.marryme.network;

import android.content.Context;
import android.util.Log;

import com.match.marryme.network.netUtil.BaseReq;
import com.match.marryme.network.netUtil.HttpResult;
import com.match.marryme.utils.StringUtil;

public abstract class ReqBasic extends BaseReq{
    public ReqBasic(Context context, String url) {
        super(context, url);
    }

    @Override
    public HttpResult onParse(String jsonString) {

        HttpResult res = new HttpResult();
        if (StringUtil.isNull(jsonString)){
            res.setResult(null);
            if(!StringUtil.isNull(TAG))
                Log.e(StringUtil.TAG, TAG + " Get Info: null");

        }else{
            res.setResult(jsonString);
            if(!StringUtil.isNull(TAG))
                Log.e(StringUtil.TAG, TAG + " Get Info: " + jsonString);

        }
        return res;
    }
}
