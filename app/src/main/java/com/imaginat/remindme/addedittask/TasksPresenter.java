package com.imaginat.remindme.addedittask;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.imaginat.remindme.data.ITaskItem;
import com.imaginat.remindme.data.source.local.ListsLocalDataSource;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by nat on 8/12/16.
 */
public class TasksPresenter implements TasksContract.Presenter {

    private String mListID;
    private String TAG = TasksPresenter.class.getSimpleName();
    TasksContract.View mView=null;


    public TasksPresenter(String listID,TasksContract.View view){
        mListID=listID;
        mView = view;
    }
    @Override
    public void createNewReminder(String data) {

    }

    @Override
    public void updateReminder(String id, String data) {

    }

    @Override
    public void updateCheckStatus(String listID, String id, boolean isChecked) {

    }

    @Override
    public void start() {
        loadTasks();
    }

    private void loadTasks(){
        ListsLocalDataSource llds = ListsLocalDataSource.getInstance(((Fragment)mView).getContext());
        if(mListID==null || mListID.isEmpty()){
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
                            mView.showAll(taskListFiltered);

                        }

                    });
        }

    }

}
