package com.darkexceptionsoftware.thermomax_calendar.data;

public interface if_IOnBackPressed {
    /**
     * If you return true the back press will not be taken into account, otherwise the activity will act naturally
     *
     * @return true if your processing has priority if not false
     */
    boolean BackPressed();
}
