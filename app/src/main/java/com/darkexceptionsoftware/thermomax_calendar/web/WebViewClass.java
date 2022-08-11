package com.darkexceptionsoftware.thermomax_calendar.web;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.darkexceptionsoftware.thermomax_calendar.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class WebViewClass extends AppCompatActivity {

    // region implements Class-Declarations
    private Activity activityReference;
    private CustomWebView myWebView;
    private View Resultview;
    private Intent intent;
    // endregion implements Class-Declarations

    private Uri targeturi;
    // region implements Constructor
    public WebViewClass() {
    }

    public WebViewClass(Activity _activityReference) {
        this.activityReference = _activityReference;
    }

    public WebViewClass(Activity _activityReference, View _viewById) {
        this.activityReference = _activityReference;
        Resultview = _viewById;
    }
    // endregion implements Constructor

    // region implements Override

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.activityReference = this;
        setContentView(R.layout.popup_webview);
        intent = getIntent();

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                dismiss();

            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);


        showWebViewWindow(this.findViewById(R.id.webviewwidget)
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clear_WebView(myWebView);
    }


    // endregion implements Override




    public void showWebViewWindow(final View view) {
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        //View popupView = inflater.inflate(R.layout.popup_webview, null);
        // ScrollingActivity ref = (ScrollingActivity) activityReference;

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        //final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);


        myWebView = findViewById(R.id.webviewwidget);
        clear_WebView(myWebView);

        WebSettings webSettings = myWebView.getSettings();

        //Set the location of the window on the screen
        //popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of our window, install the handler
        // RelativeLayout rl = popupView.findViewById(R.id.webview_layout);

        FloatingActionButton WebViewFab;


        WebViewFab = findViewById(R.id.webview_fab);
        WebViewFab.setOnClickListener(v -> {

            dismiss();

        });

        WebViewFab = findViewById(R.id.webview_back);
        WebViewFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myWebView.goBack();
            }
        });


        myWebView.setWebViewClient(new MyWebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                final Uri uri = request.getUrl();
                return handleUri(uri);
            }


            private boolean handleUri(final Uri uri) {

                final String host = uri.getHost();
                final String scheme = uri.getScheme();
                FloatingActionButton fab;

                // DO WE NEED A BACK BUTTON?
                fab = findViewById(R.id.webview_back);

                // DO WE HAVE A SCRAPEABLE LINK?

                fab = findViewById(R.id.webview_fab);

                if (uri.toString().contains("/rezepte/drucken/")) {
                    fab.setImageDrawable(activityReference.getResources().getDrawable(R.drawable.ic_baseline_check_circle_outline_24, activityReference.getTheme()));
                    targeturi = uri;
                    fab.performClick();
                    return true;
                } else {
                    fab.setImageDrawable(activityReference.getResources().getDrawable(R.drawable.ic_baseline_close_24, activityReference.getTheme()));
                    return false;

                }


            }
        });
        apply_websettings(myWebView);
        // clearApplicationCache();
        myWebView.loadUrl("https://www.chefkoch.de");
    }

    private void dismiss(){

        String url = "about:blank";

        if (targeturi != null){
        url = targeturi.toString();

        // WE HAVE A VALID URL AND THE USER WNATS OUT

        if (!url.contains("/drucken/")) {
            url = "about:blank";
        }
        }

        parseUrl(url);

        Intent returnIntent = new Intent();
        returnIntent.putExtra("action", "findWeb");
        returnIntent.putExtra("result", url);
        setResult(Activity.RESULT_OK, returnIntent);


        clear_WebView(myWebView);
        finish();

    }

    public void parseUrl(String url)
    {
        Jsoup_parse Jparse;

        try {
            Jparse = new Jsoup_parse(activityReference);


            String tvUrl = url;

            String UrlRegEx = "http?s?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}/g";
            // https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)
            UrlRegEx = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";

            if (!tvUrl.contains("http")) {
                tvUrl = "http://" + tvUrl;
            }

            if (tvUrl.matches(UrlRegEx)) {
                // Snackbar.make(view, "Trying to scrape...", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();


                //Jparse.setTextView(binding.textUrl);
                Jparse.setTargetAdress(url);
                Jparse.setReturnReference(activityReference);
                Jparse.execute();
                //Jparse.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void apply_websettings(WebView _w) {

        String webviewDBPath = activityReference.getFilesDir().getParent() + "/";  // getFilesDir().getParent() returns base path of app private data
        WebSettings _ws = _w.getSettings();
        _ws.setDatabaseEnabled(true);
        _ws.setJavaScriptCanOpenWindowsAutomatically(true);
        _ws.setSupportMultipleWindows(false);
        _ws.setBuiltInZoomControls(false);
        _ws.setJavaScriptEnabled(true);
        _ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        _ws.setDatabaseEnabled(true);
        _ws.setDomStorageEnabled(true);
        _ws.setGeolocationEnabled(true);
        _ws.setSaveFormData(false);
        _ws.setGeolocationEnabled(true);
        _ws.setDatabaseEnabled(true);
        _ws.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);

        // DEPRECIATED
        // _ws.setDatabasePath(webviewDBPath);
        // _ws.setSavePassword(false);
        // _ws.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // _ws.setPluginState(WebSettings.PluginState.ON);
        // _ws.setGeolocationDatabasePath("/data/data/selendroid");

    }

    public void clear_WebView(WebView _webview) {
        _webview.loadUrl("");
        _webview.clearHistory();
        _webview.clearFormData();
        _webview.clearCache(true);
    }

    private void clearApplicationCache() {
        File dir = activityReference.getCacheDir();

        if (dir != null && dir.isDirectory()) {
            try {
                ArrayList<File> stack = new ArrayList<File>();

                // Initialise the list
                File[] children = dir.listFiles();
                for (File child : children) {
                    stack.add(child);
                }

                while (stack.size() > 0) {
                    Log.v("CACHE", "Clearing the stack - " + stack.size());
                    File f = stack.get(stack.size() - 1);
                    if (f.isDirectory() == true) {
                        boolean empty = f.delete();

                        if (empty == false) {
                            File[] files = f.listFiles();
                            if (files.length != 0) {
                                for (File tmp : files) {
                                    stack.add(tmp);
                                }
                            }
                        } else {
                            stack.remove(stack.size() - 1);
                        }
                    } else {
                        f.delete();
                        stack.remove(stack.size() - 1);
                    }
                }
            } catch (Exception e) {
                Log.e("CACHE", "Failed to clean the cache");
            }
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}