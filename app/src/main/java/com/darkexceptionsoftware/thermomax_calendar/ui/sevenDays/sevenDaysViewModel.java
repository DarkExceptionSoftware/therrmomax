package com.darkexceptionsoftware.thermomax_calendar.ui.sevenDays;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class sevenDaysViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public sevenDaysViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}