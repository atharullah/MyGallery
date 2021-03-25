package com.hisenberg.mygallery;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Activity_Image_SearchResult extends AppCompatActivity {

  WebView webViewImgResult;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity__image__search_result);

    webViewImgResult = findViewById(R.id.webviewImageResult);
    WebSettings setting=webViewImgResult.getSettings();
    setting.setJavaScriptEnabled(true);
    setting.setBuiltInZoomControls(true);
    setting.setLoadsImagesAutomatically(true);

    webViewClient client=new webViewClient();
    webViewImgResult.setWebViewClient(client);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && webViewImgResult.canGoBack()) {
      webViewImgResult.goBack();
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  class webViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
      return false;
    }
  }
}
