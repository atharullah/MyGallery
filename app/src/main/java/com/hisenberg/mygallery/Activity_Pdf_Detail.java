package com.hisenberg.mygallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Activity_Pdf_Detail extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__pdf__detail);

        webView = findViewById(R.id.pdfDetailWebView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setBuiltInZoomControls(true);
        webView.setWebViewClient(new WebViewClient());

        Intent currentItent=getIntent();
        String pdfUrl=currentItent.getStringExtra(Constants.IntExtraFileUrl);
        webView.loadUrl(pdfUrl);
    }
}
