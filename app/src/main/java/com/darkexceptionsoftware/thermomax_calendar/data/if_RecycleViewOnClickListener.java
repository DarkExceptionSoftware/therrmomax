package com.darkexceptionsoftware.thermomax_calendar.data;

import android.view.View;

public interface if_RecycleViewOnClickListener {
    default void onItemClick(int position, String action){}
    default void onItemlongClick(int position, String action){}
    default void onViewClick(View view, String action){}
}

