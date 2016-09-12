package com.imaginat.remindme.calendar;

import android.content.Intent;
import android.os.Bundle;

import com.imaginat.remindme.BaseActivity;
import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.R;

/**
 * The Activity encompases the MVP pattern. The activity class creates the presenter,views and ensure that each has the
 * appropriate references. This class is  specifically  used to add an event on the local calendar
 */
public class CalendarActivity extends BaseActivity<CalendarFragment> {


    /**
     * returns the layout for this activity
     */
    @Override
    public int getLayoutID() {
        return R.layout.activity_calendar;
    }

    /**
     * Creates the Presenter (MVP) an assigns a reference to the View
     */
    @Override
    public Object createPresenter(CalendarFragment fragment) {

        //This activity will be called by the user from the task/reminder list
        //for a specific reminder that is identified via list and task IDs
        //that combo is passed in with the intent
        Intent theCallingIntent = getIntent();
        String listID=theCallingIntent.getStringExtra(GlobalConstants.CURRENT_LIST_ID);
        String reminderID=theCallingIntent.getStringExtra(GlobalConstants.CURRENT_TASK_ID);

        //Pass the unique combination of listID and reminder to the calendar
        CalendarPresenter presenter = new CalendarPresenter(listID,reminderID,fragment);
        fragment.setPresenter(presenter);
        return presenter;
    }

    /**
     *
     */
    @Override
    public CalendarFragment getFragment() {
        return new CalendarFragment();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
