package com.darkexceptionsoftware.thermomax_calendar.popup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.darkexceptionsoftware.thermomax_calendar.MainActivity;
import com.darkexceptionsoftware.thermomax_calendar.R;
import com.darkexceptionsoftware.thermomax_calendar.databinding.ActivityMainBinding;
import com.darkexceptionsoftware.thermomax_calendar.databinding.PopupConfirmBinding;
import com.darkexceptionsoftware.thermomax_calendar.databinding.PopupLicenseBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Confirm extends AppCompatActivity implements View.OnTouchListener, ViewTreeObserver.OnScrollChangedListener{
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
       String info = intent.getStringExtra("info");

        if (action.equals("contract")) {
            setContentView(R.layout.popup_license);

            PopupLicenseBinding binding = PopupLicenseBinding.inflate(getLayoutInflater());

            InputStream inputStream = getResources().openRawResource(R.raw.license);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            int i;
            try {
                i = inputStream.read();
                while (i != -1)
                {
                    byteArrayOutputStream.write(i);
                    i = inputStream.read();
                }
                inputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            TextView license_legal = findViewById(R.id.license_legal);
            license_legal.setText(byteArrayOutputStream.toString());

            mScrollView = findViewById(R.id.license_scrollView);
            mScrollView.setOnTouchListener(this);
            mScrollView.getViewTreeObserver().addOnScrollChangedListener(this);

            CheckBox license_checkBox = findViewById(R.id.license_checkBox);
            license_checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button license_button_yes = findViewById(R.id.license_button_yes);

                    if (license_checkBox.isChecked()) {
                        license_button_yes.setVisibility(View.VISIBLE);
                    }else{
                        license_button_yes.setVisibility(View.INVISIBLE);
                    }

                }
            });

            Button license_button_yes = findViewById(R.id.license_button_yes);
            license_button_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("action", "contract");
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            });
        }

        if (action.equals("question")) {
            setContentView(R.layout.popup_confirm);

            PopupConfirmBinding binding = PopupConfirmBinding.inflate(getLayoutInflater());


            TextView confirm_message = findViewById(R.id.confirm_message);
            confirm_message.setText(info);


            Button btnOK = findViewById(R.id.confirm_button_yes);
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("action", "question");
                    returnIntent.putExtra("pos", position);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            });

            Button btnNO = findViewById(R.id.confirm_button_no);
            btnNO.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("action", "question");
                    returnIntent.putExtra("pos", -1);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            });
        }

       //getWindow().setBackgroundDrawable(null);

    }

    public Confirm() {
    }

    public Confirm(Activity _activityReference) {

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
            CheckBox license_checkBox = findViewById(R.id.license_checkBox);
            license_checkBox.setVisibility(View.VISIBLE);

         }

    }


}

