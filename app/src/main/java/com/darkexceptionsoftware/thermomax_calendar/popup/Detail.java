package com.darkexceptionsoftware.thermomax_calendar.popup;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.ScriptGroup;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.darkexceptionsoftware.thermomax_calendar.R;
import com.darkexceptionsoftware.thermomax_calendar.data.Indrigent;
import com.darkexceptionsoftware.thermomax_calendar.data.RecipeModel;
import com.darkexceptionsoftware.thermomax_calendar.databinding.PopupConfirmBinding;
import com.darkexceptionsoftware.thermomax_calendar.databinding.PopupLicenseBinding;
import com.darkexceptionsoftware.thermomax_calendar.databinding.RcvRowRecipeDetailBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Detail extends AppCompatActivity implements View.OnTouchListener, ViewTreeObserver.OnScrollChangedListener {
    private Activity activityReference;

    private ScrollView mScrollView;
    boolean local = false;


    public Detail() {
    }

    public Detail(Activity _activityReference) {

        this.activityReference = _activityReference;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.activityReference = this;
        Intent intent = getIntent();
        int position = intent.getIntExtra("pos", -1);
        String action = intent.getStringExtra("action");
        RecipeModel info = intent.getParcelableExtra("info");


        List<Indrigent> indrigents = info.getINDRIGENTS();


        if (action.equals("show")) {
            setContentView(R.layout.rcv_row_recipe_detail);
            RcvRowRecipeDetailBinding binding = RcvRowRecipeDetailBinding.inflate(getLayoutInflater());

            TextView rv_rcp_d_recipe, rv_rcp_d_autor, rv_rcp_d_rcp, rv_rcp_d_ind, rv_rcp_d_ind2;
            ImageView rv_rcp_d_img;
            WebView rv_rcp_webview;

            rv_rcp_d_recipe = findViewById(R.id.rv_rcp_d_recipe);
            rv_rcp_d_autor = findViewById(R.id.rv_rcp_d_autor);
            rv_rcp_d_rcp = findViewById(R.id.rv_rcp_d_rcp);
            rv_rcp_d_img = findViewById(R.id.rv_rcp_d_img);

            rv_rcp_webview = findViewById(R.id.rv_rcp_webview);


            rv_rcp_d_recipe.setText(info.getName());

            if (info.getName().length() > 25)
                rv_rcp_d_recipe.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            rv_rcp_d_autor.setText(info.getCreator());


            long id = info.getId();

            String ar = "</p>";
            String _ar = "<p style=text-align:right>";
            String _al = "<p style=text-align:left>";
            String builder = "";
            String builder2 = "";
            builder2 = "<table>";
            builder2 += "<tr>";
            builder2 += "<th style=\"width: 100px\">" + _ar + "Menge" + ar + "</th>";
            // builder2 +="<th>Art</th>";
            builder2 += "<th>" + _al + "Zutat" + ar + "</th>";
            builder2 += "</tr>";


            for (Indrigent item : info.getINDRIGENTS()) {
                builder2 += "<tr>";

                String _t = item.getAmount().toString();

                if (_t.equals("0.0")) {
                    _t = "";

                } else {
                    _t = _t.replace(".0", "");

                }
                builder2 += "<td valign=\"top\" style=\"height: 30px\">" + _ar + "<strong>" + _t + " " + item.getAmountof() + "</strong>" + ar + "</td>";

                builder2 += "<td valign=\"top\"><strong>" + item.getName() + "</strong></td>";
                builder2 += "</tr>";
            }
            builder2 += "</table>";
            final String mimeType = "text/html";
            final String encoding = "UTF-8";
            rv_rcp_webview.loadDataWithBaseURL("", builder2, mimeType, encoding, "");


            String rezept = info.getSummary().replace("z. B. ", "z._B._");
            rezept = rezept.replace("ca. ", "ca._");
            rezept = rezept.replace(". ", ". \n\n");
            rezept = rezept.replace("_", " ");

            rv_rcp_d_rcp.setText(info.getImagePath() + "\n\n" + rezept + "\n\n\n");


            try {
                boolean success = true;

                String get_image_from;
                local = false;

                if (info.getImagePath_internal().equals("")) {
                    get_image_from = info.getImagePath();
                } else {
                    get_image_from = info.getImagePath_internal();
                    local = true;
                }
                Log.d("Gilde", "DVIEW - INT - " + info.getImagePath_internal());
                Log.d("Gilde", "DVIEW - SRC - " + info.getImagePath());

                Glide
                        .with(this)
                        .asBitmap()
                        .load(get_image_from)
                        //.centerCrop()
                        .into(new CustomTarget<Bitmap>(600, 600) {
                            @Override
                            public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {

                                if (!local) {
                                    Boolean permission = false;
                                    if (ContextCompat.checkSelfPermission(
                                            getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                            PackageManager.PERMISSION_GRANTED) {
                                        // You can use the API that requires the permission.
                                        permission = true;
                                    } else {
                                        // You can directly ask for the permission.
                                        // The registered ActivityResultCallback gets the result of this request.
                                        //MainActivity.requestPermissionLauncher.launch(
                                        //       Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                    }

                                    String int_path = "";

                                    if (permission)
                                        int_path = saveImage(info.getId() + "", bitmap);   // save your bitmap

                                    info.setImagePath_internal(int_path);
                                    Log.d("Gilde", "DVIEW - RES - " + int_path);
                                }
                                rv_rcp_d_img.setImageBitmap(bitmap);
                                // rv_rcp_d_img.setScaleType(ImageView.ScaleType.CENTER_CROP);


                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }

                        });


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        return false;
    }

    @Override
    public void onScrollChanged() {
        View view = mScrollView.getChildAt(mScrollView.getChildCount() - 1);
        int topDetector = mScrollView.getScrollY();
        int bottomDetector = view.getBottom() - (mScrollView.getHeight() + mScrollView.getScrollY());
        if (bottomDetector == 0) {

        }

    }

    private String saveImage(String name, Bitmap image) {
        String savedImagePath = null;

        String imageFileName = name + ".jpg";
        String ImageDir = getApplicationContext().getApplicationInfo().dataDir + "/files";

        File storageDir = new File(ImageDir, "pictures");
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {

            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Add the image to the system gallery
            // galleryAddPic(savedImagePath);
            //Toast.makeText(getApplicationContext(), "IMAGE SAVED", Toast.LENGTH_LONG).show();
        }
        return savedImagePath;
    }


}

