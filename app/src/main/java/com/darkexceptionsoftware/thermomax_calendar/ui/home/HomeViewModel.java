package com.darkexceptionsoftware.thermomax_calendar.ui.home;


import static androidx.core.app.ActivityCompat.startActivityForResult;
import static java.security.AccessController.getContext;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.darkexceptionsoftware.thermomax_calendar.BuildConfig;
import com.darkexceptionsoftware.thermomax_calendar.MainActivity;
import com.darkexceptionsoftware.thermomax_calendar.popup.Confirm;

public class HomeViewModel extends ViewModel {


    private final MutableLiveData<String> mText = new MutableLiveData<>();

    private Application activityReference;
    private Context context;



    public HomeViewModel() {



        int i = BuildConfig.BUILD_COUNT ;



        // binding.textViewVersion.setText("ThermoMax Build r("+i+") V_0.01 (c) Darkexception");

        mText.setValue("ThermoMax Build r_"+i+" V_0.01 (c) Darkexception");




    }

    public LiveData<String> getText() {
        return mText;
    }
}