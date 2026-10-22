package com.gkmnetworks.admin;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

  private WebView printWebView; // keep a reference so it isn't garbage collected mid-print

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getBridge().getWebView().addJavascriptInterface(new PrintBridge(), "AndroidPrint");
  }

  private class PrintBridge {
    @JavascriptInterface
    public void printHtml(final String html, final String title) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          final WebView wv = new WebView(MainActivity.this);
          wv.getSettings().setJavaScriptEnabled(true);
          wv.getSettings().setDomStorageEnabled(true);
          wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(final WebView view, String url) {
              // small delay so images / fonts finish rendering
              new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                  PrintManager pm = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                  PrintDocumentAdapter adapter = view.createPrintDocumentAdapter(title);
                  pm.print(title, adapter, new PrintAttributes.Builder().build());
                }
              }, 700);
            }
          });
          printWebView = wv;
          wv.loadDataWithBaseURL("https://localhost/", html, "text/html", "UTF-8", null);
        }
      });
    }
  }
}
