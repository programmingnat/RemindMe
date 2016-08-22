package com.imaginat.remindme.calendar;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by nat on 8/21/16.
 */
public class CalendarProvider {

    public void fetchCalendars(Context context) throws SecurityException {
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String[] columns = new String[] {
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.OWNER_ACCOUNT
        };

        Cursor cursor = context.getContentResolver().query(
                uri,
                columns,
                CalendarContract.Calendars.ACCOUNT_NAME + " = ?",
                //TODO: insert your email address that will be associated with the calendar
                new String[] {"npanchee@gmail.com"},
                null
        );

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID));
            String accountName = cursor.getString(1);
            String displayName = cursor.getString(2);
            String owner = cursor.getString(3);
            Log.d("ContentProvider", "ID: " + id +
                    ", account: " + accountName +
                    ", displayName: " + displayName +
                    ", owner: " + owner
            );
        }
    }


    public long insertEventInCalendar(String title, String description, String location,Calendar startTime,Calendar endTime,Context context)
    throws SecurityException{
        long mLastEnteredID;
        long calID=3;
        long startMillis=0;
        long endMillis=0;
        //Calendar startInst = Calendar.getInstance();
        //startInst.set(2016,2,1);
        startMillis = startTime.getTimeInMillis();
        //Calendar endInst = Calendar.getInstance();
        //endInst.set(2016,2,4);
        endMillis = endTime.getTimeInMillis();
        // 1. get 2 calendar instances: startTime and endTime in milliseconds and set March 1 as the
        // date of the event. The event can last as long as you want, so you can set any time.

        // 2. set the following properties of the event and save the event in the provider
        //   - CalendarContract.Events.DTSTART
        //   - CalendarContract.Events.DTEND
        //   - CalendarContract.Events.TITLE
        //   - CalendarContract.Events.DESCRIPTION
        //   - CalendarContract.Events.CALENDAR_ID (the value 1 should give the default calendar)
        //   - CalendarContract.Events.EVENT_TIMEZONE
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.EVENT_LOCATION, location);
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().toString());
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        Log.d("MainActivity","Inserted into calendar");
        //  3. after inserting the row in the provider, retrieve the id of the event using the method below.
        // Just uncomment the line below. You will need this id to update and delete this event later.
        mLastEnteredID = Long.parseLong(uri.getLastPathSegment());
        Log.d("MainActivity","the eventID is "+mLastEnteredID);
        // recentlyAddedEventId = Long.parseLong(uri.getLastPathSegment());
        // fetch
//  3. after inserting the row in the provider, retrieve the id of the event using the method below.
// Just uncomment the line below. You will need this id to update and delete this event later.
        // long eventId = Long.parseLong(uri.getLastPathSegment());
        return mLastEnteredID;
    }

    public void fetchEventByID(Context context,long eventID) throws SecurityException{
        Cursor cursor;
        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        String selection = "(("+CalendarContract.Events._ID+"=?"+"))";
        String[] selectionArgs =  new String[] {Long.toString(eventID)};

        String[] columns = new String[] {CalendarContract.Events._ID, CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND};
        cursor = cr.query(uri, columns, selection, selectionArgs, CalendarContract.Events.DTSTART + " DESC LIMIT 100");

        Log.d("MainActivity ","cursor count "+cursor.getCount());

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID));
            String title = cursor.getString(1);
            String start = cursor.getString(2);
            String end = cursor.getString(3);
            Log.d("ContentProvider", "ID: " + id +
                    ", title: " + title +
                    ", start: " + start +
                    ", end: " + end
            );
        }
    }

    //This method should return all the events from your calendar from February 29th till March 4th
    // in the year 2016.
    public void fetchEvents(Context context)
    throws SecurityException{
        //TODO:
        // 1. get 2 calendar instances: startTime (Feb 29) and endTime (March 4) in milliseconds
        Calendar startTime = Calendar.getInstance();
        startTime.set(2016, 7, 20, 12, 0, 0);
        long startMillis = startTime.getTimeInMillis();

        Calendar endTime = Calendar.getInstance();
        endTime.set(2016, 7, 31, 12, 0, 0);
        long endMillis = endTime.getTimeInMillis();

        // 2. set the limit of 100 events and order DESC
        String limitAndOrder = CalendarContract.Events.DTSTART + " DESC LIMIT 100";
        // 3. get all the events within that period using a cursor object

        // 4. once you get a cursor object, uncomment the code below to see the events displayed in the
        // list view.
        Cursor cursor;
        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        String selection = "((" + CalendarContract.Events.DTSTART + " >= ?) AND ("+CalendarContract.Events.DTEND + " <= ?))";
        String[] selectionArgs =  new String[] {String.valueOf(startMillis),String.valueOf(endMillis)};
        // Submit the query and get a Cursor object back.
        String[] columns = new String[] {CalendarContract.Events._ID, CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND};
        cursor = cr.query(uri, columns, selection, selectionArgs, CalendarContract.Events.DTSTART + " DESC LIMIT 100");

        Log.d("MainActivity ","cursor count "+cursor.getCount());

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID));
            String title = cursor.getString(1);
            String start = cursor.getString(2);
            String end = cursor.getString(3);
            Log.d("ContentProvider", "ID: " + id +
                    ", title: " + title +
                    ", start: " + start +
                    ", end: " + end
            );
        }
/*
        ListAdapter listAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_expandable_list_item_2,
                cursor,
                new String[] {CalendarContract.Events._ID, CalendarContract.Events.TITLE},
                new int[] {android.R.id.text1, android.R.id.text2},
                0
        );

        lv.setAdapter(listAdapter);
        */
    }

    public void update(Context context,long eventID,String title,String description)
    throws SecurityException{
        //TODO: Using the number eventID from the method insertEventInCalendar(), update the event
        // that was added in that method

        ContentResolver contentResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.TITLE,title);
        values.put(CalendarContract.Events.DESCRIPTION,description);
        Uri updateURI = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI,eventID);
        int rows = context.getContentResolver().update(updateURI,values,null,null);
    }

    public void delete(Context context,long idToDelete) {
        //TODO: Using the number eventID from the method insertEventInCalendar(), delete the event
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        Uri deleteURI = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI,idToDelete);
        context.getContentResolver().delete(deleteURI,null,null);
    }
    private void loadPermissions(String perm, int requestCode,Context c,Activity a) {

        if (ContextCompat.checkSelfPermission(c, perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(a, perm)) {
                ActivityCompat.requestPermissions(a, new String[]{perm}, requestCode);
            }
        }
    }
}
