package com.imaginat.remindme.calendar;

import android.os.Bundle;

import com.imaginat.remindme.BaseActivity;
import com.imaginat.remindme.R;

public class CalendarActivity extends BaseActivity<CalendarFragment> {



    @Override
    public int getLayoutID() {
        return R.layout.activity_calendar;
    }

    @Override
    public Object createPresenter(CalendarFragment fragment) {
        CalendarPresenter presenter = new CalendarPresenter();
        fragment.setPresenter(presenter);
        return presenter;
    }

    @Override
    public CalendarFragment getFragment() {
        return new CalendarFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //CalendarProvider mCalendarProvider;
        //setContentView(R.layout.activity_calendar);

        //mCalendarProvider = new CalendarProvider();
       // mCalendarProvider.fetchCalendars(this);
        //mCalendarProvider.fetchEvents(this);
       // mCalendarProvider.fetchEventByID(this,57);
    }
}
