package com.darkexceptionsoftware.thermomax_calendar.popup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.Html;
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

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.darkexceptionsoftware.thermomax_calendar.R;
import com.darkexceptionsoftware.thermomax_calendar.data.Indrigent;
import com.darkexceptionsoftware.thermomax_calendar.data.RecipeModel;
import com.darkexceptionsoftware.thermomax_calendar.databinding.PopupConfirmBinding;
import com.darkexceptionsoftware.thermomax_calendar.databinding.PopupLicenseBinding;
import com.darkexceptionsoftware.thermomax_calendar.databinding.RcvRowRecipeDetailBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Detail extends AppCompatActivity implements View.OnTouchListener, ViewTreeObserver.OnScrollChangedListener {
    private Activity activityReference;

    private ScrollView mScrollView;


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
                rv_rcp_d_recipe.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);

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

            rv_rcp_d_rcp.setText(rezept + "\n\n\n");


            Glide
                    .with(this)
                    .load(info.getImagePath())
                    .centerCrop()
                    .placeholder(R.drawable.sample)
                    .into(rv_rcp_d_img);

        }


    }

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


}

