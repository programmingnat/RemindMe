package com.imaginat.remindme.calendar;

import android.content.Intent;
import android.os.Bundle;

import com.imaginat.remindme.BaseActivity;
import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.R;

public class CalendarActivity extends BaseActivity<CalendarFragment> {



    @Override
    public int getLayoutID() {
        return R.layout.activity_calendar;
    }

    @Override
    public Object createPresenter(CalendarFragment fragment) {
        Intent theCallingIntent = getIntent();
        String listID=theCallingIntent.getStringExtra(GlobalConstants.CURRENT_LIST_ID);
        String reminderID=theCallingIntent.getStringExtra(GlobalConstants.CURRENT_TASK_ID);
        CalendarPresenter presenter = new CalendarPresenter(listID,reminderID,fragment);
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
