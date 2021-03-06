package com.imaginat.remindme.data;

/**
 * This class holds the Title info of a list
 */
public class ReminderList {

    private String mTitle;
    private int mIcon;
    private String mListId;

    public ReminderList(String title, int icon, String listId) {
        mTitle = title;
        mIcon = icon;
        mListId = listId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getIcon() {
        return mIcon;
    }

    public void setIcon(int icon) {
        mIcon = icon;
    }

    public String getListId() {
        return mListId;
    }

    public void setListId(String listId) {
        mListId = listId;
    }
}
