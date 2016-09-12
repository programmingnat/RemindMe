package com.imaginat.remindme.calendar;

import java.util.Calendar;

/**
 * Created by nat on 9/12/16.
 */
public class CalendarData {

    public long getEventID() {
        return mEventID;
    }

    public void setEventID(long eventID) {
        mEventID = eventID;
    }

    long mEventID;

    String mStartDateMilli,mEndDateMilli;
    String mTitle,mDescription,mLocation;

    public String getStartDateMilli() {
        return mStartDateMilli;
    }

    public void setStartDateMilli(String startDateMilli) {
        mStartDateMilli = startDateMilli;
    }

    public String getEndDateMilli() {
        return mEndDateMilli;
    }

    public void setEndDateMilli(String endDateMilli) {
        mEndDateMilli = endDateMilli;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }


    public Calendar getStartCalendar(){
        Calendar cal = Calendar.getInstance();
       long milli = Long.parseLong(mStartDateMilli);
        cal.setTimeInMillis(milli);
        return cal;
    }

    public Calendar getEndCalendar(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(mEndDateMilli));
        return cal;
    }
}
