package com.imaginat.remindme.data;

/**
 * Each task is a SimpleTaskItem  (right now)
 */
public class SimpleTaskItem implements ITaskItem {

    private String mBriefDescription;
    String mReminderID=null;
    String mListID=null;
    boolean mIsCompleted=false;
    int mCalendarEventID=-1;
    GeoFenceAlarmData mGeoFenceAlarmData=null;


    @Override
    public boolean hasGeoAlarm() {
        if(mGeoFenceAlarmData==null)
            return false;

        return true;
    }

    @Override
    public GeoFenceAlarmData getGeoFenceAlarmData() {
        return mGeoFenceAlarmData;
    }

    public void setGeoFenceAlarmData(GeoFenceAlarmData geoFenceAlarmData) {
        mGeoFenceAlarmData = geoFenceAlarmData;
    }

    public int getCalendarEventID() {
        return mCalendarEventID;
    }

    public void setCalendarEventID(int calendarEventID) {
        mCalendarEventID = calendarEventID;
    }

    public SimpleTaskItem(String list, String reminderID){
        mListID=list;
        mReminderID=reminderID;

    }

    @Override
    public boolean isCompleted() {
        return mIsCompleted;
    }

    @Override
    public void setCompleted(boolean b) {
        mIsCompleted=b;
    }

    public String getText() {
        return mBriefDescription;
    }


    public void setText(String brief) {
        mBriefDescription=brief;
    }

    @Override
    public String getListID() {
        return mListID;
    }

    @Override
    public String getReminderID() {
        return mReminderID;
    }
}
