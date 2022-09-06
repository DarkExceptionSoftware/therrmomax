package com.darkexceptionsoftware.thermomax_calendar.popup;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.darkexceptionsoftware.thermomax_calendar.R;
import com.darkexceptionsoftware.thermomax_calendar.data.RecipeModel;
import com.darkexceptionsoftware.thermomax_calendar.databinding.ContentMainBinding;
import com.darkexceptionsoftware.thermomax_calendar.databinding.PopupValueEditorBinding;

import java.io.File;

public class ContextMenu_einkaufsliste extends AppCompatActivity {
    private Activity activityReference;

    private ScrollView mScrollView;

    private int position;
    private String name;


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
        this.activityReference = this;
        this.setFinishOnTouchOutside(true);

        PopupValueEditorBinding b = PopupValueEditorBinding.inflate(getLayoutInflater());

        // Make us non-modal, so that others can receive touch events.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        // ...but notify us that it happened.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);


       Intent intent = getIntent();
        position = intent.getIntExtra("pos", -1);
        name = intent.getStringExtra("name");

        setContentView(b.getRoot());

        String name_first = name.split(",")[0].trim();

        b.editTextTextPersonName.setText(name_first);


        b.cmv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                String indname = b.editTextTextPersonName.getText().toString();
                returnIntent.putExtra("action", "cat");

                returnIntent.putExtra("indname", indname);
                returnIntent.putExtra("category", 1);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        b.cmv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("action", "cat");

                returnIntent.putExtra("indname", b.editTextTextPersonName.getText().toString());
                returnIntent.putExtra("category", 2);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        b.cmv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("action", "cat");

                returnIntent.putExtra("indname", b.editTextTextPersonName.getText().toString());
                returnIntent.putExtra("category", 3);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        b.cmv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("action", "cat");

                returnIntent.putExtra("indname", b.editTextTextPersonName.getText().toString());
                returnIntent.putExtra("category", 4);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        b.cmv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("action", "cat");

                returnIntent.putExtra("indname", b.editTextTextPersonName.getText().toString());
                returnIntent.putExtra("category", 5);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        b.cmv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("action", "cat");
                returnIntent.putExtra("indname", b.editTextTextPersonName.getText().toString());
                returnIntent.putExtra("category", 6);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        b.tvIgnore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = b.editTextTextPersonName.getText().toString();
                Toast.makeText(getApplicationContext(), name +" wird jetzt ignoriert!", Toast.LENGTH_SHORT).show();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("action", "cat");
                returnIntent.putExtra("indname", name);
                returnIntent.putExtra("category", 99);
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

    public ContextMenu_einkaufsliste() {
    }

    public ContextMenu_einkaufsliste(Activity _activityReference) {

        this.activityReference = _activityReference;
    }







}

