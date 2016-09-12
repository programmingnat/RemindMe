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
        long resultID=-1;
        ListsLocalDataSource localDataSource = ListsLocalDataSource.getInstance(((Fragment)mView).getContext());
        if(mTaskID==null) {
            //update
            resultID=localDataSource.createNewTask(mListID, s);
        }else{
            //update
        }

        if(resultID<=0){
            //error
            mView.showError();
        }else{
            mView.showTaskList();
        }

    }

}
