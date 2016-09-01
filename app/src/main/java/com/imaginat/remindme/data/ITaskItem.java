package com.imaginat.remindme.data;

/**
 * Created by nat on 8/8/16.
 */
public interface ITaskItem {


      boolean isCompleted();

      void setCompleted(boolean b);
      String getText();
      void setText(String brief);
      String getListID();
      String getReminderID();
      boolean hasGeoAlarm();
      GeoFenceAlarmData getGeoFenceAlarmData();

}
