package com.darkexceptionsoftware.thermomax_calendar.ui.ingredient;


import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.darkexceptionsoftware.thermomax_calendar.BuildConfig;

public class IngredientViewModel extends ViewModel {


    private final MutableLiveData<String> mText = new MutableLiveData<>();

    private Application activityReference;
    private Context context;



    public IngredientViewModel() {




    }

    public LiveData<String> getText() {
        return mText;
    }
}