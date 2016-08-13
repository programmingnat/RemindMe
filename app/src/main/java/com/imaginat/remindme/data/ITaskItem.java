package com.imaginat.remindme.data;

/**
 * Created by nat on 8/8/16.
 */
public interface ITaskItem {
      String mReminderID=null;
      String mListID=null;

      boolean isCompleted();

      String getText();
      void setText(String brief);
      String getListID();
      String getReminderID();

}
