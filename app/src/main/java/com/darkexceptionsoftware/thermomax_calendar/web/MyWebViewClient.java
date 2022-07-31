package com.darkexceptionsoftware.thermomax_calendar.web;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.darkexceptionsoftware.thermomax_calendar.MainActivity;

import java.util.concurrent.atomic.AtomicBoolean;

public class MyWebViewClient extends WebViewClient {
    private static AtomicBoolean crash = new AtomicBoolean(true);

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        // Crash the first WebView that finishes the page loading
        if (crash.compareAndSet(true, false)) {
            crashPage(view);
        }
    }

    protected void crashPage(WebView view) {
     //   Logger.e("MyWebViewClient", "crashPage() ... crashing WebView: " +view.hashCode());
        // Do something bad that will crash the renderer process
    //    view.evaluateJavascript("javascript:(function() { txt = \"a\"; while(1){ txt += \"a\"; } })();", null);
    }


    @Override
    public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
        Log.e("MyWebViewClient", "onRenderProcessGone() ... WebView: " +view.hashCode());
        return true; // Return true so the other callbacks are called too
    }


}