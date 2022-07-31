package com.darkexceptionsoftware.thermomax_calendar.popup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
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

public class Detail extends AppCompatActivity implements View.OnTouchListener, ViewTreeObserver.OnScrollChangedListener{
    private Activity activityReference;

    private ScrollView mScrollView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.activityReference = this;
       Intent intent = getIntent();
       int position = intent.getIntExtra("pos", -1);
       String action = intent.getStringExtra("action");
       RecipeModel info = intent.getParcelableExtra("info");

        if (action.equals("show")) {
            setContentView(R.layout.rcv_row_recipe_detail);
            RcvRowRecipeDetailBinding binding = RcvRowRecipeDetailBinding.inflate(getLayoutInflater());

            TextView rv_rcp_d_recipe, rv_rcp_d_autor, rv_rcp_d_rcp, rv_rcp_d_ind;
            ImageView rv_rcp_d_img;

            rv_rcp_d_recipe = findViewById(R.id.rv_rcp_d_recipe);
            rv_rcp_d_autor = findViewById(R.id.rv_rcp_d_autor);
            rv_rcp_d_rcp = findViewById(R.id.rv_rcp_d_rcp);
            rv_rcp_d_img = findViewById(R.id.rv_rcp_d_img);
            rv_rcp_d_ind = findViewById(R.id.rv_rcp_d_ind);


            rv_rcp_d_recipe.setText(info.getName());
            rv_rcp_d_autor.setText(info.getCreator());

            String rezept = info.getSummary().replace("z. B. ", "z._B._");
            rezept = rezept.replace("ca. ", "ca._");
            rezept = rezept.replace(". ", ". \n\n");
            rezept = rezept.replace("_", " ");

            rv_rcp_d_rcp.setText(rezept);

            String builder = "";
            // for (Indrigent item : info.getINDRIGENTS()){

            //    builder += item.getAmount() + " " + item.getAmountof() + item.getName();
            // }
            rv_rcp_d_ind.setText("Zutaten");


            Glide
                            .with(this)
                            .load(info.getImagePath())
                            .centerCrop()
                            .placeholder(R.drawable.sample)
                            .into(rv_rcp_d_img);

        }




    }

    public Detail() {
    }

    public Detail(Activity _activityReference) {

        this.activityReference = _activityReference;
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

