package vip.yyzhu.ymyy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import vip.yyzhu.ymyy.tool.Tool;

public class WebActivity extends AppCompatActivity {
    WebView webView;
    TextView top_title;
    Context con;
    View loading;
    // 加载动画效果
    Animation rotating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        con = WebActivity.this;

        webView = findViewById(R.id.web);
        View back = findViewById(R.id.back);
        View refresh = findViewById(R.id.refresh);
        top_title = findViewById(R.id.top_title);
        loading = findViewById(R.id.loading);

        // 动画文件
        rotating = AnimationUtils.loadAnimation(con, R.anim.rotating);
        // 将动画加载到 View 上，开启动画
        loading.startAnimation(rotating);
        // 显示View
        loading.setVisibility(View.VISIBLE);
//        webView.setAlpha(0);

        final WebSettings webSettings = webView.getSettings();
        webSettings.setNeedInitialFocus(false); // 是否需要获取焦点
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);//允许其加载混合网络协议
        webSettings.setSupportZoom(false); //支持缩放
        webSettings.setJavaScriptEnabled(true); // 设置WebView属性,运行执行js脚本
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                System.out.println("加载开始的操作");
                top_title.setText("加载中");

                loading.startAnimation(rotating);// 将动画加载到 View 上
                loading.setVisibility(View.VISIBLE);// 显示View
            }

            @Override

            public void onPageFinished(WebView view, String url) {
                //设定加载结束的操作
                loading.clearAnimation(); //停止动画
                loading.setVisibility(View.GONE); // 隐藏View
                top_title.setText(view.getTitle());
                webView.setAlpha(1);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                loading.clearAnimation(); //停止动画
                loading.setVisibility(View.GONE);// 隐藏View
                top_title.setText("出现错误 " + errorCode);
                webView.setAlpha(1);
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                load();
                webView.reload();
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                load();
            }
        }).start();
    }

    public void load() {
        webView.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                int mod = intent.getIntExtra("mod", -1);
                if (mod == -1) {
                    Tool.makeText1(con, "出错啦");
                } else {
                    if (mod == 1) {
                        String url = intent.getStringExtra("url");
                        webView.loadUrl(url);
                    }
                }
            }
        });


    }

}