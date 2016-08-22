package com.imaginat.remindme.calendar;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.imaginat.remindme.data.source.local.ListsLocalDataSource;

import java.util.Calendar;

/**
 * Created by nat on 8/21/16.
 */
public class CalendarPresenter implements CalendarContract.Presenter {

    CalendarContract.View mView;
    String mListID, mReminderID;
    public static final int REQUEST_READ_CALENDAR=200;
    public static final int REQUEST_WRITE_CALENDAR=201;

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
        long eventID = cp.insertEventInCalendar(title, description, location, startTime, endTime, context);

        loadPermissions(Manifest.permission.READ_CALENDAR,REQUEST_READ_CALENDAR);
        loadPermissions(Manifest.permission.READ_CALENDAR,REQUEST_WRITE_CALENDAR);
        //update the database
        ListsLocalDataSource llds = ListsLocalDataSource.getInstance(((Fragment) mView).getContext());
        llds.updateTaskCalendarEvent(mListID, mReminderID, eventID);

    }

    @Override
    public void updateEvent(String title, String description, String startDate, String startTime, String endDate, String endTime, String location, Context context, long eventID) {
        CalendarProvider cp = new CalendarProvider();
        //cp.update();
    }

    private void loadPermissions(String perm, int requestCode) {
        Context c = ((Fragment) mView).getContext();
        Activity a = ((Fragment) mView).getActivity();
        if (ContextCompat.checkSelfPermission(c, perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(a, perm)) {
                ActivityCompat.requestPermissions(a, new String[]{perm}, requestCode);
            }
        }
    }
}
