package com.darkexceptionsoftware.thermomax_calendar.web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebBackForwardList;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.darkexceptionsoftware.thermomax_calendar.MainActivity;
import com.darkexceptionsoftware.thermomax_calendar.R;
import com.darkexceptionsoftware.thermomax_calendar.databinding.PopupWebviewBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;

public class WebViewClass extends AppCompatActivity {

    public String url = "about:blank";
    float density;
    int touchcount;
    Handler hlm;
    Runnable rlm;
    boolean parsegone = false;
    boolean noparse = false;

    int scrape = 0;
    // region implements Class-Declarations
    private Activity activityReference;
    private MainActivity ref;
    private View root;
    private View Resultview;
    private CustomWebView myWebView;
    private Intent intent;
    // endregion implements Class-Declarations
    private boolean stop = false;
    private int blink = 0;
    // endregion implements Constructor

    // region implements Override
    private Handler handler;
    private Runnable runnable;


    // endregion implements Override
    private PopupWebviewBinding binding;
    private Uri targeturi;
    private String html;

    // region implements Constructor
    public WebViewClass() {
    }

    public WebViewClass(Activity _activityReference) {
        this.activityReference = _activityReference;
        ref = (MainActivity) activityReference;

    }

    public WebViewClass(Activity _activityReference, View _viewById) {
        this.activityReference = _activityReference;
        Resultview = _viewById;
        ref = (MainActivity) activityReference;

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }

        if (imm.isAcceptingText())
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        binding = PopupWebviewBinding.inflate(getLayoutInflater());

        activityReference = this;
        intent = getIntent();

        density = getApplicationContext().getResources().getDisplayMetrics().density;

        if (intent != null)
            url = intent.getStringExtra("url");

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                float density = getApplicationContext().getResources().getDisplayMetrics().density;

                binding.wvLeft.setVisibility(View.GONE);
                binding.wvRight.setVisibility(View.GONE);

                CardView wvcv = binding.wvCloseCardview;
                wvcv.setVisibility(View.VISIBLE);
                binding.wvFade.setVisibility(View.VISIBLE);
                binding.wvFade.animate().alpha(1.0f);

                wvcv.animate().translationY(8 * density);


                // dismiss();

            }
        };

        binding.wvCloseBtnOk.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        finish();

                                                    }
                                                }

        );


        this.getOnBackPressedDispatcher().addCallback(this, callback);


        showWebViewWindow(binding.webviewwidget);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (blink <= 0) {
                    /* if (binding.tipWebhelp.getVisibility() == View.GONE)
                        binding.tipWebhelp.setVisibility(View.VISIBLE); */
                    // binding.tipWebhelp.animate().alpha(1.0f);
                } else {
                    blink--;
                }
            }
        };
        stop = false;
        hlm = new Handler();
        rlm = new Runnable() {
            @Override
            public void run() {

                if (leftmenuopen){
                binding.wvLeft.animate().translationX(-14 * density);
                binding.leftmenuOpener.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_keyboard_arrow_left_24));
                }
                if (!parsegone)
                    binding.wvRight.animate().translationX(14 * density);

            }
        };

        binding.webviewwidget.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                binding.wvLeft.animate().translationX(-68 * density);
                binding.leftmenuOpener.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_keyboard_arrow_right_24));
                binding.wvRight.animate().translationX(64 * density);

                hlm.postDelayed(rlm, 2000);
                hideKeyboard(activityReference);
                return false;
            }
        });

        binding.wvFade.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // binding.tipWebhelp.setVisibility(View.GONE);
                //inding.tipWebhelp.animate().alpha(0.0f);

                binding.wvLeft.setVisibility(View.VISIBLE);

                if (!noparse)
                    binding.wvRight.setVisibility(View.VISIBLE);

                binding.wvFade.animate().alpha(0.0f);
                binding.wvCloseCardview.animate().translationY(70 * density);
                blink = 2;
                return false;
            }


        });

        //binding.tipWebhelp.setVisibility(View.VISIBLE);
        handler.postDelayed(runnable, 1000);

        View view = binding.getRoot();

        setContentView(view);


        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setLoadWithOverviewMode(true);

        if (webviewstate == null) {
        } else
            myWebView.restoreState(webviewstate);    // Restore the state

        if (url.contains("?noparse")) {
            binding.wvRight.setVisibility(View.GONE);
            noparse = true;
        }
    }


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


        myWebView = binding.webviewwidget;


        WebSettings webSettings = myWebView.getSettings();

        //Set the location of the window on the screen
        //popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of our window, install the handler
        // RelativeLayout rl = popupView.findViewById(R.id.webview_layout);

        ImageButton rightmenuParse;


        rightmenuParse = binding.rightmenuParse;
        ImageButton finalWebViewFab1 = rightmenuParse;
        rightmenuParse.setOnClickListener(v -> {

            if (scrape == 0) {
                myWebView.addJavascriptInterface(new MyJavaScriptInterface(this), "HtmlViewer");
                scrape = 1;
                // myWebView.loadUrl(targeturi.toString());
                myWebView.loadUrl( "javascript:window.location.reload( true )" );
                parsegone = true;
                binding.wvRight.animate().translationX(64 * density);

            } else if (scrape == 2) {
                scrape = 3;
                finalWebViewFab1.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));

            } else if (scrape == 3) {
                html = MyJavaScriptInterface.getResult();

                dismiss();
            }

        });


        ImageButton leftmenuBack;
        leftmenuBack = binding.leftmenuBack;
        leftmenuBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myWebView.goBack();
            }
        });

        Switch javaswitch = binding.leftmenuJavaswitch;

        javaswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (javaswitch.isChecked()) {
                    myWebView.getSettings().setJavaScriptEnabled(true);
                } else {
                    myWebView.getSettings().setJavaScriptEnabled(false);
                }
                myWebView.reload();
            }
        });

        binding.leftmenuHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myWebView.loadUrl("file:///android_asset/index.html?noparse");
                noparse = true;
                binding.wvRight.setVisibility(View.GONE);
            }
        });

        binding.leftmenuOpener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (leftmenuopen) {
                    binding.wvLeft.animate().translationX(-68 * density);
                    binding.leftmenuOpener.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_keyboard_arrow_right_24));
                    leftmenuopen = false;
                } else {
                    leftmenuopen = true;
                    binding.wvLeft.animate().translationX(-16 * density);
                    binding.leftmenuOpener.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_keyboard_arrow_left_24));
                }
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
                finalWebViewFab1.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.ind_gewürz)));

                parsegone = true;
                binding.wvRight.animate().translationX(64 * density);

                targeturi = uri;

                if (targeturi.toString().contains("?noparse")) {
                    noparse = true;
                    binding.wvRight.setVisibility(View.GONE);
                } else {
                    noparse = false;

                    binding.wvRight.setVisibility(View.VISIBLE);
                }

                if (targeturi.toString().contains("?finish"))
                    finish();


                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                if (scrape == 1) {
                    scrape = 2;
                    myWebView.loadUrl("javascript:window.HtmlViewer.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                    finalWebViewFab1.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.cardview_today)));
                }
                parsegone = false;
                binding.wvRight.animate().translationX(14 * density);


            }
        });
        apply_websettings(myWebView);
        // clearApplicationCache();

        myWebView.onResume();
        myWebView.loadUrl(url);

    }
    boolean leftmenuopen = true;
    private void dismiss() {


        myWebView.onPause();
        myWebView.stopLoading();
        if (targeturi != null) {
            url = targeturi.toString();

        }

        // parseUrl(url);

        Intent returnIntent = new Intent();
        // returnIntent.putExtra("action", "findWeb");
        returnIntent.putExtra("action", "parseany");
        returnIntent.putExtra("result", url);
        //returnIntent.putExtra("html", html);
        setResult(Activity.RESULT_OK, returnIntent);

        String file = getApplication().getFilesDir() + "/htmltenp.txt";

        File fdelete = new File(file);

        if (fdelete.exists())
            fdelete.delete();

        FileWriter fw = null;

        try {
            FileWriter fileWriter = new FileWriter(file);

            BufferedWriter out = new BufferedWriter(fileWriter);

            out.write(html);

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.finish();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        myWebView = null;
        // binding = null;
    }

    public void parseUrl(String url) {
        Jsoup_parse Jparse;

        try {
            Jparse = new Jsoup_parse(activityReference);


            String tvUrl = url;

            // https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)
            String UrlRegEx = "https?:\\\\/\\\\/(www\\.)?[-a-zA-Z0-9@:%._\\\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\\\+.~#?&/=]*)";

            if (!tvUrl.contains("http")) {
                tvUrl = "http://" + tvUrl;
            }

            if (tvUrl.matches(UrlRegEx)) {
                // Snackbar.make(view, "Trying to scrape...", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show()

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

        String webviewDBPath = this.getFilesDir().getParent() + "/";  // getFilesDir().getParent() returns base path of app private data
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
        _webview.loadUrl("about:blank");
        _webview.clearHistory();
        _webview.clearFormData();
        _webview.clearCache(true);
    }

    private void clearApplicationCache() {
        File dir = getApplicationContext().getCacheDir();

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

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    private Bundle webviewstate;

    // Saving state
    @Override
    public void onPause() {
        super.onPause();
        webviewstate = new Bundle();
        myWebView.saveState(webviewstate);
    }


    static class MyJavaScriptInterface {

        private static String result = "";
        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        public static String getResult() {
            return result;
        }

        @JavascriptInterface
        public void showHTML(String html) {
            /* new AlertDialog.Builder(ctx).setTitle("HTML").setMessage(html)
                    .setPositiveButton(android.R.string.ok, null).setCancelable(false).create().show();
                    */
            result = html;
        }

    }


}

