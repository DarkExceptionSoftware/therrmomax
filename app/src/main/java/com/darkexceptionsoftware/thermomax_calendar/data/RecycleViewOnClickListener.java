package com.darkexceptionsoftware.thermomax_calendar.data;

import android.view.View;

public interface RecycleViewOnClickListener {
    void onItemClick(int position, String action);
    void onItemlongClick(int position, String action);
    void onViewClick(View view, String action);

}

