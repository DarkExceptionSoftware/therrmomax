package com.darkexceptionsoftware.thermomax_calendar.web;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class CustomWebView extends WebView {
    public CustomWebView(Context context) {
        super(context);

        init();
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    protected void init() {
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

  //  @Override
  //  public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
  //      BaseInputConnection baseInputConnection = new BaseInputConnection(this, false);
  //      outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE;
  //      outAttrs.inputType = EditorInfo.TYPE_CLASS_TEXT;
  //      return baseInputConnection;
  //  }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

}