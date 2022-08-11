package com.darkexceptionsoftware.thermomax_calendar.popup;

import static com.darkexceptionsoftware.thermomax_calendar.MainActivity._RecipeDates;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.darkexceptionsoftware.thermomax_calendar.MainActivity;
import com.darkexceptionsoftware.thermomax_calendar.R;
import com.darkexceptionsoftware.thermomax_calendar.data.RecipeModel;
import com.darkexceptionsoftware.thermomax_calendar.databinding.PopupContextMenuBinding;
import com.darkexceptionsoftware.thermomax_calendar.databinding.PopupTimePickerBinding;

import java.io.File;

public class ContextMenu_Kalender extends AppCompatActivity {
    private Activity activityReference;

    private ScrollView mScrollView;

    private int position;

    private PopupContextMenuBinding b;

    private MainActivity ref;

    public ContextMenu_Kalender() {
    }

    public ContextMenu_Kalender(Activity _activityReference) {

        this.activityReference = _activityReference;
    }

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

        ref = (MainActivity) activityReference;

        // Make us non-modal, so that others can receive touch events.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        // ...but notify us that it happened.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        this.activityReference = this;

        Intent intent = getIntent();
        position = intent.getIntExtra("pos", -1);

        b = PopupContextMenuBinding.inflate(getLayoutInflater());

        View view = b.getRoot();
        setContentView(view);

        this.setFinishOnTouchOutside(true);


        CardView cmv_1, cmv_2, cmv_3;
        DatePicker cm_dp;

        Button button = findViewById(R.id.cmv_3_confirm);


        b.cmv1.setOnClickListener(new View.OnClickListener() {
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

        b.cmv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        b.cmv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                button.setVisibility(View.VISIBLE);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                button.setVisibility(View.VISIBLE);

                Intent returnIntent = new Intent(getApplicationContext(), Confirm.class);
                returnIntent.putExtra("action", "question");
                returnIntent.putExtra("pos", position);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });




    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("action", "nothing");
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        return true;

    }


}

