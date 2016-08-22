package com.imaginat.remindme.data;

/**
 * Created by nat on 8/12/16.
 */
public class SimpleTaskItem implements ITaskItem {

    private String mBriefDescription;
    String mReminderID=null;
    String mListID=null;
    boolean mIsCompleted=false;
    int mCalendarEventID=-1;

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
