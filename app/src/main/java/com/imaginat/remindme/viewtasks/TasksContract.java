package com.imaginat.remindme.viewtasks;

import com.imaginat.remindme.BasePresenter;
import com.imaginat.remindme.BaseView;
import com.imaginat.remindme.data.GeoFenceAlarmData;
import com.imaginat.remindme.data.ITaskItem;

import java.util.ArrayList;

/**
 * Created by nat on 8/12/16.
 */
public class TasksContract {


    interface View extends BaseView<Presenter>{
        boolean isActive();
        void showAll(ArrayList<ITaskItem>tasks);
        void showAddNewTask(String listID);
        void showNoTasks();
        void showSuccessfullySaved();
        void showTaskMarkedComplete();
        void showTaskMarkError();
        void showOptionsOverlay();
        void showFAB();
        void hideFAB();
        void showCalendar(String listID,String reminderID);
        void showGeoFenceAlarm(String listID,String reminderID,GeoFenceAlarmData geoFenceAlarmData);
        void showTaskUpdate();
        void showNoTaskUpdated();
    }
    interface ViewAdapter extends BaseView<Presenter>{
        void setData(ArrayList<ITaskItem> arrayList);



    }
    interface Presenter extends BasePresenter {
        void createNewReminder();
        void result(int requestCode,int resultCode);
        void updateReminder(String listID,String id,String data);
        void updateCompletionStatus(String listID,String id,boolean isChecked);
        void deleteReminder(String listID,String id);
        void openCalendar(String listID,String reminderID);
        void openGeoFenceOptions(String listID, String reminderID, GeoFenceAlarmData geoFenceAlarmData);
        void disableAddingNewTask();
        void enableAddingNewTask();
        void loadTaskOptions(String listID,String taskID);
    }

}
