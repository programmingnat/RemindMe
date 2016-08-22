package com.imaginat.remindme.calendar;

import android.content.Context;

import java.util.Calendar;

/**
 * Created by nat on 8/21/16.
 */
public class CalendarPresenter implements CalendarContract.Presenter{
    @Override
    public void start() {

    }

    @Override
    public void createEvent(String title, String description, Calendar startTime, Calendar endTime, String location, Context context) {
        CalendarProvider cp = new CalendarProvider();
        cp.insertEventInCalendar(title,description,location,startTime,endTime,context);

    }

    @Override
    public void updateEvent(String title, String description, String startDate, String startTime, String endDate, String endTime, String location, Context context,long eventID) {
            CalendarProvider cp = new CalendarProvider();
            //cp.update();
    }
}
