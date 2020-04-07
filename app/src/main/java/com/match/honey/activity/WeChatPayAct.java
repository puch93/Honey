package com.match.honey.activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.match.honey.R;
import com.match.honey.databinding.ActivityWeChatPayBinding;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.Common;
import com.match.honey.utils.StringUtil;

import org.apache.http.util.EncodingUtils;
import org.apache.http.util.NetUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WeChatPayAct extends AppCompatActivity {
    ActivityWeChatPayBinding binding;
    AppCompatActivity act;

    String outTradeNo = "";
    String whereFrom = "Android hunliain";
    String mobile = "01074717614";
    String sumMoney = "100";
    String notifyUrl = NetUrls.RETURN_WECHATPAY;
    String redirectUrl = "";
    String body = "testBody";
    String userName = "testName";
    String message = "testMessage";
    String ext = "testExt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_we_chat_pay, null);
        act = this;

        requestPay();
    }

    private void request_h5_pay() {
        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

//        binding.webView.setWebChromeClient(new WebChromeClient());
//        binding.webView.setWebViewClient(new MyWebViewClient());
        binding.webView.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                Uri uri = Uri.parse(view.getUrl());
                Uri uri = request.getUrl();

                String url = uri.toString();
                Log.e(StringUtil.TAG, "loadurl: " + url);

                if (url.startsWith("weixin://")) {
                    System.out.println("loadurl2222 : " + url);
                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        // url의 경우에는 밑의 코드 참고
//        Base64.encodeToString(resulturl.getBytes(), 0)
//        String str =
//                "Referer: http://pay.82ucc.com" + "&" +
//                        "url=/wxpay/H5Pay" + "&" +
//                        "outTradeNo="+ System.currentTimeMillis() + "&" +
//                        "whereFrom=Android hunliain" + "&" +
//                        "mobile=01074717614" + "&" +
//                        "sumMoney=100" + "&" +
//                        "notifyUrl=" + NetUrls.RETURN_WECHATPAY + "&" +
//                        "redirectUrl=" + "&" +
//                        "body=testBody" + "&" +
//                        "userName=testName" + "&" +
//                        "message=testMessage" + "&" +
//                        "ext=testExt";

//                Log.e(StringUtil.TAG, "전달값: " + str);

        String request_values =
                        "Referer: http://pay.82ucc.com" + "&" +
                        "url=/wxpay/H5Pay" + "&" +
                        "outTradeNo=" + outTradeNo + "&" +
                        "whereFrom=" + whereFrom + "&" +
                        "mobile=" + mobile + "&" +
                        "sumMoney=" + sumMoney+ "&" +
                        "notifyUrl=" + notifyUrl + "&" +
                        "redirectUrl=" + redirectUrl + "&" +
                        "body=" + body + "&" +
                        "userName=" + userName + "&" +
                        "message=" + message + "&" +
                        "ext=" + ext;

        Log.e(StringUtil.TAG, "request_h5_pay: " + request_values.replaceAll("&", "\n") );
        Common.showToastLong(act, request_values.replaceAll("&", "\n"));

        binding.webView.postUrl("http://pay.82ucc.com", request_values.getBytes());

//        Map<String, String> extraHeaders = new HashMap<String, String>();
//        extraHeaders.put("Referer", "http://pay.82ucc.com");

//        binding.webView.loadUrl("http://pay.82ucc.com/");
//        EncodingUtils.getBytes(str, "BASE64")
//        binding.webView.loadUrl("http://pay.82ucc.com");
    }


    private void requestPay() {
        ReqBasic logout = new ReqBasic(this, NetUrls.REQ_WECHATPAY) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("success")) {
                            outTradeNo = StringUtil.getStr(jo, "outTradeNo");
                            UserPref.setPayNum(act, outTradeNo);

                            Common.showToastLong(act, jo.toString());

                            request_h5_pay();
                        } else {
                            Common.showToastNet(act);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        };
        logout.setTag("Request Pay");
        logout.addParams("whereFrom", whereFrom);
        logout.addParams("mobile", mobile);
        logout.addParams("sumMoney", sumMoney);
        logout.addParams("body", body);
        logout.addParams("userName", userName);
        logout.addParams("message", message);
        logout.addParams("ext",ext);
        logout.execute(true, false);
    }
}
