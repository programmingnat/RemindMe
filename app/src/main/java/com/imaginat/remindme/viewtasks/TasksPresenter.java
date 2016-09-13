package com.imaginat.remindme.viewtasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.data.GeoFenceAlarmData;
import com.imaginat.remindme.data.ITaskItem;
import com.imaginat.remindme.data.source.local.ListsLocalDataSource;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * This is the logic class that links the VIEW to the MODEL or the UI to the Database
 */
public class TasksPresenter implements TasksContract.Presenter {

    //for debugging purposes
    private String TAG = TasksPresenter.class.getSimpleName();

    //list reference (the list that is being displayed)
    private String mListID;

    //reference to the view
    TasksContract.View mView=null;


    public TasksPresenter(String listID,TasksContract.View view){
        mListID=listID;
        mView = view;
    }

    /**
     *  calls the databasae and creates a new, empty task/reminder
     */
    @Override
    public void createNewReminder() {
        ListsLocalDataSource llds = ListsLocalDataSource.getInstance(((Fragment)mView).getContext());
        String reminderID=Long.toString(llds.createNewTask(mListID,""));
        mView.showAddNewTask(mListID,reminderID);
    }


    /**
     *
     *gets called during the delete task flow, asks the view to show confirmation
     */
    @Override
    public void requestToDelete(String listID, String reminderID) {
        mView.showDeletionConfirmMsg(listID,reminderID);
    }

    /**
     * hide the button that adds task (testing purposes)
     */
    @Override
    public void disableAddingNewTask() {
        mView.hideFAB();
    }

    /**
     * endables the button that lets a user add task (testing purpose)
     */
    @Override
    public void enableAddingNewTask() {
        mView.showFAB();
    }

    /**
     *
     * delete from database
     */
    @Override
    public void deleteReminder(String listID, String id) {
        ListsLocalDataSource llds = ListsLocalDataSource.getInstance(((Fragment)mView).getContext());
        llds.deleteReminder(listID,id);
        loadTasks();
    }

    /**
     *update the task (text change)
     */
    @Override
    public void updateReminder(String listID, String taskID,String data) {
        ListsLocalDataSource llds = ListsLocalDataSource.getInstance(((Fragment)mView).getContext());
        llds.updateTaskText(listID,taskID,data);
        loadTasks();
    }

    /**
     *
     *  update the checkbox
     */
    @Override
    public void updateCompletionStatus(String listID, String id, boolean isChecked) {
        ListsLocalDataSource llds = ListsLocalDataSource.getInstance(((Fragment)mView).getContext());
        int result = llds.updateTaskComplete(listID,id,isChecked?1:0);
        if(result==1){
            mView.showTaskMarkedComplete();
        }else{
            mView.showTaskMarkError();
        }
        loadTasks();
    }

    @Override
    public void start() {
        SharedPreferences sharedPreferences= ((Fragment)mView).
                getActivity().getSharedPreferences(GlobalConstants.PREFERENCES, Context.MODE_PRIVATE);
        boolean showIt = sharedPreferences.getInt(GlobalConstants.SHOW_VIEW_TASKS_TOOLTIPS, 1)==1?true:false;
        mView.setToolTip(showIt);
        loadTasks();
    }

    /**
     *
     * tell mView that someone has been looking
     */
    @Override
    public void loadTaskOptions(String listID, String taskID) {
        mView.showOptionsOverlay();
    }

    /**
     * loads all the tasks of the chosen list
     */
    private void loadTasks(){
        ListsLocalDataSource llds = ListsLocalDataSource.getInstance(((Fragment)mView).getContext());
        if(mListID==null || mListID.isEmpty()) {
                return;
        }
            llds.getAllTasks(mListID)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<ITaskItem>>() {
                        @Override
                        public void call(List<ITaskItem> tasksLists) {
                            Log.d(TAG,"Inside call() found "+tasksLists.size()+" results");
                            //do filter or whatever here
                            ArrayList<ITaskItem> taskListFiltered = new ArrayList<ITaskItem>();
                            for(ITaskItem item:tasksLists){
                                taskListFiltered.add(item);
                            }
                            if(!mView.isActive()){
                                return;
                            }
                            processTasks(taskListFiltered);

                        }

                    });
        }
    private void processTasks(ArrayList<ITaskItem>tasks){
        if(tasks.isEmpty()){
            processEmptyTasks();
        }else{
            mView.showAll(tasks);
        }
    }

    private void processEmptyTasks(){
        mView.showNoTasks();
    }

    @Override
    public void result(int requestCode, int resultCode) {
        if (TasksFragment.REQUEST_ADD_TASK == requestCode && Activity.RESULT_OK == resultCode) {
            mView.showSuccessfullySaved();
        }
    }

    @Override
    public void openCalendar(String listID, String reminderID) {
        mView.showCalendar(listID,reminderID);
    }

    @Override
    public void openGeoFenceOptions(String listID, String reminderID, GeoFenceAlarmData alarmData) {
        mView.showGeoFenceAlarm(listID,reminderID,alarmData);
    }
}
