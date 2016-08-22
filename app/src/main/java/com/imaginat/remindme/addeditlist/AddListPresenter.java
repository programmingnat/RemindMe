package com.imaginat.remindme.addeditlist;

import android.support.v4.app.Fragment;

import com.imaginat.remindme.data.source.local.ListsLocalDataSource;

/**
 * Created by nat on 8/9/16.
 */
public class AddListPresenter implements AddListContract.Presenter {

    AddListContract.View mView;

    public AddListPresenter(AddListContract.View view){
        mView = view;
    }
    @Override
    public void addNewList(String listName, int iconID) {


        ListsLocalDataSource llds  =  ListsLocalDataSource.getInstance(((Fragment)mView).getContext());
        llds.createNewList(listName,iconID);
        mView.showTasks();
        //llds.createNewList("LIST TWO",2);
    }

    @Override
    public void start() {

    }
}
