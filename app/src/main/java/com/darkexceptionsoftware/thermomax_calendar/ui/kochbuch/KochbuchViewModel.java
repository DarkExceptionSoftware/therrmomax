package com.darkexceptionsoftware.thermomax_calendar.ui.kochbuch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class KochbuchViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public KochbuchViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}