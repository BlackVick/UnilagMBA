package com.tti.unilagmba;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tti.unilagmba.Common.Common;

public class AdView extends AppCompatActivity {

    DatabaseReference usersRef;
    WebView webView;
    String adLink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_view);

        adLink = getIntent().getStringExtra("Link");

        if (adLink != null) {

            CookieSyncManager.createInstance(this);
            CookieManager.getInstance().setAcceptCookie(true);


            webView = (WebView) findViewById(R.id.adWebView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setFocusable(true);
            webView.setFocusableInTouchMode(true);
            webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setDatabaseEnabled(true);
            webView.getSettings().setAppCacheEnabled(true);
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.loadUrl(adLink);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(AdView.this, "Error Connecting To Server", Toast.LENGTH_SHORT).show();
                }
            });
        } else {



        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
