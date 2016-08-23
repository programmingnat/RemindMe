package com.imaginat.remindme.calendar;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.imaginat.remindme.data.source.local.ListsLocalDataSource;

import java.util.Calendar;

/**
 * Created by nat on 8/21/16.
 */
public class CalendarPresenter implements CalendarContract.Presenter {

    CalendarContract.View mView;
    String mListID, mReminderID;


    public CalendarPresenter(String listID, String reminderID, CalendarContract.View view) {
        mListID = listID;
        mReminderID = reminderID;
        mView = view;


    }

    @Override
    public void start() {

    }

    @Override
    public void createEvent(String title, String description, Calendar startTime, Calendar endTime, String location, Context context) {
        CalendarProvider cp = new CalendarProvider();
        cp.fetchCalendars(context);
        long eventID = cp.insertEventInCalendar(title, description, location, startTime, endTime, context);


        //update the database
        ListsLocalDataSource llds = ListsLocalDataSource.getInstance(((Fragment) mView).getContext());
        llds.updateTaskCalendarEvent(mListID, mReminderID, eventID);

        mView.showTasks();

    }

    @Override
    public void updateEvent(String title, String description, String startDate, String startTime, String endDate, String endTime, String location, Context context, long eventID) {
        CalendarProvider cp = new CalendarProvider();
        //cp.update();
    }


}
