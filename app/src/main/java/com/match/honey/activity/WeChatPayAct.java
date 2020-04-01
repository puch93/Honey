package com.match.honey.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.match.honey.R;
import com.match.honey.databinding.ActivityWeChatPayBinding;

import org.apache.http.util.EncodingUtils;

public class WeChatPayAct extends AppCompatActivity {
    ActivityWeChatPayBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_we_chat_pay, null);
        act = this;

        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        binding.webView.setWebChromeClient(new WebChromeClient());
        binding.webView.setWebViewClient(new MyWebViewClient());

        // url의 경우에는 밑의 코드 참고
//        Base64.encodeToString(resulturl.getBytes(), 0)
        String str =
                "outTradeNo=1234" + "&" +
                "whereFrom=testFrom" + "&" +
                "mobile=01074717614" + "&" +
                "sumMoney=1000" + "&" +
                "notifyUrl=about:blank" + "&" +
                "redirectUrl=about:blank" + "&" +
                "body=testBody" + "&" +
                "userName=testName" + "&" +
                "message=testMessage" + "&" +
                "ext=testExt";

//        binding.webView.loadUrl("http://pay.82ucc.com/");
//        EncodingUtils.getBytes(str, "BASE64")
        binding.webView.postUrl("http://pay.82ucc.com/wxpay/H5Pay", str.getBytes());
    }

    private class MyWebViewClient extends WebViewClient {

        @Nullable
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //Log.d(HoUtils.TAG, "url : " + url);
            binding.webView.loadUrl("javascript:window.AndroidInterface.getHtml(document.getElementsByTagName('body')[0].innerHTML);");

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.webView.loadUrl(request.getUrl().toString());
            } else {
                Toast.makeText(act, "안드로이드 버전 미달", Toast.LENGTH_SHORT).show();
            }
            return true;
        }


        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);

            StringBuilder sb = new StringBuilder();

            if (error != null) {
                switch (error.getPrimaryError()) {
                    case SslError.SSL_EXPIRED:
                        sb.append("이 사이트의  보안 인증서가 만료되었습니다.\n");
                        break;
                    case SslError.SSL_IDMISMATCH:
                        sb.append("이 사이트의 보안 인증서 ID가 일치하지 않습니다.\n");
                        break;
                    case SslError.SSL_NOTYETVALID:
                        sb.append("이 사이트의 보안 인증서가 아직 유효하지 않습니다.\n");
                        break;
                    case SslError.SSL_UNTRUSTED:
                        sb.append("이 사이트의 이 사이트의 보안 인증서는 신뢰할 수 없습니다.\n");
                        break;
                    default:
                        sb.append("보안 인증서에 오류가 있습니다.\n");
                        break;
                }
            }

            sb.append("계속 진행하시겠습니까?");

            final AlertDialog.Builder builder = new AlertDialog.Builder(act);
            builder.setMessage(sb.toString());
            builder.setPositiveButton("진행", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });

            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });

            final AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
