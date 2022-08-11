package com.darkexceptionsoftware.thermomax_calendar.popup;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.darkexceptionsoftware.thermomax_calendar.R;
import com.darkexceptionsoftware.thermomax_calendar.data.RecipeModel;
import com.darkexceptionsoftware.thermomax_calendar.databinding.PopupTimePickerBinding;
import com.google.android.material.progressindicator.BaseProgressIndicator;

import java.io.File;
import java.util.Date;

public class TimePicker extends AppCompatActivity {
    private Activity activityReference;

    private int mode = 0;
    private ScrollView mScrollView;

    private PopupTimePickerBinding b;

    private int position;
    Calendar c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE");
        Date date = new Date();
         c = Calendar.getInstance();

        b = PopupTimePickerBinding.inflate(getLayoutInflater());

        int day, month, year, hour, minute = 0;

        // Make us non-modal, so that others can receive touch events.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        // ...but notify us that it happened.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);


        this.activityReference = this;

       Intent intent = getIntent();
        position = intent.getIntExtra("pos", -1);
        RecipeModel info = intent.getParcelableExtra("info");

       //  setContentView(R.layout.popup_time_picker);
        View view = b.getRoot();
        setContentView(view);
        this.setFinishOnTouchOutside(true);


        CardView tpv1,tpv2,tpv3;
        DatePicker tpDp ;

        android.widget.TimePicker tpTp;

        TextView tpv1Message, tpv2Message, tpv3Message;

        tpDp = b.tpDp;
        tpTp = b.tpTp;

        tpv1 = b.tpv1;
        tpv2 = b.tpv2;
        tpv3 = b.tpv3;

        tpv1Message = b.tpv1Message;
        tpv2Message = b.tpv2Message;
        tpv3Message = b.tpv3Message;

        DatePicker datePicker = (DatePicker) tpDp;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());


        setByMode(0);

        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                tpv1Message.setText(String.format("%2s", dayOfMonth).replace(' ', '0') + "." + String.format("%2s", (month+1)).replace(' ', '0') +"."+ year);

                calendar.set(java.util.Calendar.DAY_OF_MONTH,dayOfMonth );
                calendar.set(java.util.Calendar.YEAR,year );
                calendar.set(java.util.Calendar.MONTH,month );

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                dateFormat.setCalendar(calendar);
                b.textView2.setText(dateFormat.format(calendar.getTime()));
            }
        });
        tpTp.setIs24HourView(true);
        tpv1Message.setText(datePicker.getDayOfMonth() + "." +datePicker.getMonth()+"."+datePicker.getYear());
        tpv2Message.setText(tpTp.getHour()+":"+tpTp.getMinute());
        tpv1.setCardBackgroundColor(getResources().getColor(R.color.yellow,getTheme()));

        tpTp.setOnTimeChangedListener(new android.widget.TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(android.widget.TimePicker timePicker, int i, int i1) {

                tpv2Message.setText(String.format("%2s", i).replace(' ', '0')+":"+String.format("%2s", i1).replace(' ', '0'));
                calendar.set(java.util.Calendar.HOUR,i );
                calendar.set(java.util.Calendar.MINUTE,i1 );
            }
        });


        tpv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tpDp.setVisibility(View.VISIBLE);
                tpTp.setVisibility(View.GONE);
                tpv1.setCardBackgroundColor(getResources().getColor(R.color.yellow,getTheme()));
                tpv2.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,getTheme()));
                mode = 0;
                setByMode(mode);

            }
        });

        tpv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tpDp.setVisibility(View.GONE);
                tpTp.setVisibility(View.VISIBLE);
                tpv1.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,getTheme()));
                tpv2.setCardBackgroundColor(getResources().getColor(R.color.yellow,getTheme()));
                mode = 1 ;
                setByMode(mode);


            }
        });

        tpv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent returnIntent = new Intent();


                Calendar cal = new GregorianCalendar(b.tpDp.getYear(),
                        b.tpDp.getMonth(),
                        b.tpDp.getDayOfMonth(),
                        b.tpTp.getHour(),
                        b.tpTp.getMinute());

                Long time = cal.getTimeInMillis();
                returnIntent.putExtra("action", "appointment");
                returnIntent.putExtra("pos", position);
                returnIntent.putExtra("day", calendar.get(Calendar.DAY_OF_MONTH));
                returnIntent.putExtra("date", cal.getTimeInMillis());


                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        b.tag1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode == 1){ b.tpTp.setHour(9);b.tpTp.setMinute(0);}else{
                    c = Calendar.getInstance();
                    b.tpDp.updateDate(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH) );
                }
            }
        });

        b.tag2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode == 1){ b.tpTp.setHour(11);b.tpTp.setMinute(0);}else{
                    c = Calendar.getInstance(); c.add(Calendar.DATE, 1);
                    b.tpDp.updateDate(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH) );
                }
            }
        });
        b.tag3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode == 1){ b.tpTp.setHour(13);b.tpTp.setMinute(0);}else{
                    c = Calendar.getInstance(); c.add(Calendar.DATE, 2);
                    b.tpDp.updateDate(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH) );
                }
            }
        });

        b.tag4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode == 1){ b.tpTp.setHour(15);b.tpTp.setMinute(0);}else{
                    c = Calendar.getInstance(); c.add(Calendar.DATE, 3);
                    b.tpDp.updateDate(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH) );
                }
            }
        });
        b.tag5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode == 1){ b.tpTp.setHour(17);b.tpTp.setMinute(0);}else{
                    c = Calendar.getInstance(); c.add(Calendar.DATE, 4);
                    b.tpDp.updateDate(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH) );
                }
            }
        });
        b.tag6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode == 1){ b.tpTp.setHour(19);b.tpTp.setMinute(0);}else{
                    c = Calendar.getInstance(); c.add(Calendar.DATE, 5);
                    b.tpDp.updateDate(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH) );
                }
            }
        });
        b.tag7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode == 1){ b.tpTp.setHour(21);b.tpTp.setMinute(0);}else{
                    c = Calendar.getInstance(); c.add(Calendar.DATE, 6);
                    b.tpDp.updateDate(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH) );
                }
            }
        });
    }

    public void setByMode(int mode){

        if (mode >1)
            mode = 0;

        if (mode == 1){
        b.tagtext11.setText("Frühstück");
        b.tagtext12.setText("Brunch");
        b.tagtext13.setText("Mittag");
        b.tagtext14.setText("Kaffee");
        b.tagtext15.setText("Snack");
        b.tagtext16.setText("Abends");
        b.tagtext17.setText("Party");
        }
        if (mode == 0){
            String dt;
            SimpleDateFormat sdf = new SimpleDateFormat("EEE");
            Date date = new Date();
            Calendar c = Calendar.getInstance();

            dt = sdf.format(c.getTime());
            b.tagtext11.setText("Heute");
            b.tagtext11.setTag(c);

            c.add(Calendar.DATE, 1);
            dt = sdf.format(c.getTime());
            b.tagtext12.setText("Morgen");

            c.add(Calendar.DATE, 1);
            dt = sdf.format(c.getTime());
            b.tagtext13.setText(dt);

            c.add(Calendar.DATE, 1);
            dt = sdf.format(c.getTime());
            b.tagtext14.setText(dt);

            c.add(Calendar.DATE, 1);
            dt = sdf.format(c.getTime());
            b.tagtext15.setText(dt);

            c.add(Calendar.DATE, 1);
            dt = sdf.format(c.getTime());
            b.tagtext16.setText(dt);

            c.add(Calendar.DATE, 1);
            dt = sdf.format(c.getTime());
            b.tagtext17.setText(dt);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, finish the activity.
        //if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("action", "nothing");
        //returnIntent.putExtra("pos", position);
        returnIntent.putExtra("date", "01-01-1988");

        setResult(Activity.RESULT_OK, returnIntent);
            finish();
            return true;
        //}

        // Delegate everything else to Activity.
        //return super.onTouchEvent(event);
    }

    public TimePicker() {
    }

    public TimePicker(Activity _activityReference) {

        this.activityReference = _activityReference;
    }







}

