package com.imaginat.remindme.addeditTask;

import android.support.v4.app.Fragment;

import com.imaginat.remindme.data.source.local.ListsLocalDataSource;

/**
 * Created by nat on 8/14/16.
 */
public class AddEditTaskPresenter implements AddEditTaskContract.Presenter {

    AddEditTaskContract.View mView;
    String mListID=null;
    String mTaskID=null;


    public AddEditTaskPresenter(AddEditTaskContract.View view,String listID,String taskID){
        mView = view;
        mListID=listID;
    }
    @Override
    public void start() {

    }

    @Override
    public void saveTask(String s) {
        if(mListID==null|| mListID.isEmpty()){
            return;
        }
        ListsLocalDataSource localDataSource = ListsLocalDataSource.getInstance(((Fragment)mView).getContext());
        if(mTaskID==null) {
            localDataSource.createNewTask(mListID, s);
        }else{

        }
    }

    @Override
    public void updateTask(String s, String taskID) {

    }
}
