package com.darkexceptionsoftware.thermomax_calendar.popup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.darkexceptionsoftware.thermomax_calendar.R;
import com.darkexceptionsoftware.thermomax_calendar.data.Indrigent;
import com.darkexceptionsoftware.thermomax_calendar.data.RecipeModel;
import com.darkexceptionsoftware.thermomax_calendar.databinding.RcvRowRecipeDetailBinding;

import java.io.File;
import java.util.List;

public class ContextMenu extends AppCompatActivity {
    private Activity activityReference;

    private ScrollView mScrollView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);


        // Make us non-modal, so that others can receive touch events.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        // ...but notify us that it happened.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);


        this.activityReference = this;
       Intent intent = getIntent();
       int position = intent.getIntExtra("pos", -1);
        RecipeModel info = intent.getParcelableExtra("info");

        setContentView(R.layout.popup_context_menu);

        this.setFinishOnTouchOutside(true);


        CardView cmv_1,cmv_2,cmv_3;

        cmv_1 = findViewById(R.id.cmv_1);
        cmv_2 = findViewById(R.id.cmv_2);
        cmv_3 = findViewById(R.id.cmv_3);

        cmv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        cmv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        cmv_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String RecDir = activityReference.getApplicationContext().getApplicationInfo().dataDir + "/files/";
                delete(RecDir, info.getId() + "");

                Intent returnIntent = new Intent();
                returnIntent.putExtra("action", "refresh");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });


    }


    public boolean delete(String _RecDir, String _filename){
        boolean myFile;
        _filename +=".rcp";
        myFile = new File(_RecDir + _filename + "/",  "recipe.rcp").delete();
        myFile = new File(_RecDir + _filename + "/",  "indrigents.ind").delete();
        myFile = new File(_RecDir + _filename + "/",  "image.png").delete();
        myFile = new File(_RecDir + _filename + "/").delete();

        return myFile;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, finish the activity.
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            finish();
            return true;
        }

        // Delegate everything else to Activity.
        return super.onTouchEvent(event);
    }

    public ContextMenu() {
    }

    public ContextMenu(Activity _activityReference) {

        this.activityReference = _activityReference;
    }







}

