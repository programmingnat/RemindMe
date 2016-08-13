package com.imaginat.remindme.data;

/**
 * Created by nat on 8/12/16.
 */
public class SimpleTaskItem implements ITaskItem {

    private String mBriefDescription;


    @Override
    public boolean isCompleted() {
        return false;
    }


    public String getText() {
        return mBriefDescription;
    }


    public void setText(String brief) {
        mBriefDescription=brief;
    }

    @Override
    public String getListID() {
        return null;
    }

    @Override
    public String getReminderID() {
        return null;
    }
}
