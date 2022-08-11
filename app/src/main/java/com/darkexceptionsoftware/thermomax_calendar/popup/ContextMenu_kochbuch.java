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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.darkexceptionsoftware.thermomax_calendar.R;
import com.darkexceptionsoftware.thermomax_calendar.data.Indrigent;
import com.darkexceptionsoftware.thermomax_calendar.data.RecipeModel;
import com.darkexceptionsoftware.thermomax_calendar.databinding.RcvRowRecipeDetailBinding;
import com.darkexceptionsoftware.thermomax_calendar.web.WebViewClass;

import java.io.File;
import java.util.List;

public class ContextMenu_kochbuch extends AppCompatActivity {
    private Activity activityReference;

    private ScrollView mScrollView;

    private int position;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bundle extras = data.getExtras();


        Intent returnIntent = new Intent();
        returnIntent.putExtras(extras);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
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
        position = intent.getIntExtra("pos", -1);
        RecipeModel info = intent.getParcelableExtra("info");

        setContentView(R.layout.popup_context_menu);

        this.setFinishOnTouchOutside(true);


        CardView cmv_1,cmv_2,cmv_3;
        DatePicker cm_dp ;

        TimePicker cm_tp;

        TextView cmv_1_message, cmv_2_message, cmv_3_message;

        cm_dp = findViewById(R.id.cm_dp);
        cm_tp = findViewById(R.id.cm_tp);

        cmv_1 = findViewById(R.id.cmv_1);
        cmv_2 = findViewById(R.id.cmv_2);
        cmv_3 = findViewById(R.id.cmv_3);

        cmv_1_message = findViewById(R.id.cmv_1_message);
        cmv_2_message = findViewById(R.id.cmv_2_message);
        cmv_3_message = findViewById(R.id.cmv_3_message);


        cmv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activityReference, com.darkexceptionsoftware.thermomax_calendar.popup.TimePicker.class);
                // TextView editText = (TextView) findViewById(R.id.confirm_label);
                intent.putExtra("action", "setTime");
                intent.putExtra("pos", position);
                startActivityForResult(intent, 1);
                //moveTaskToBack(true);
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
                returnIntent.putExtra("action", "delete");
                returnIntent.putExtra("pos", position);

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
        Intent returnIntent = new Intent();
        returnIntent.putExtra("action", "nothing");
        setResult(Activity.RESULT_OK, returnIntent);
            finish();
            return true;

    }

    public ContextMenu_kochbuch() {
    }

    public ContextMenu_kochbuch(Activity _activityReference) {

        this.activityReference = _activityReference;
    }







}

