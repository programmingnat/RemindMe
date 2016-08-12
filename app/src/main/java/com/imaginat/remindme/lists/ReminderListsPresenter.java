package com.imaginat.remindme.lists;

import android.util.Log;

/**
 * Created by nat on 8/8/16.
 */
public class ReminderListsPresenter implements ReminderListsContract.Presenter {

    ReminderListsContract.View mView;
    private final String TAG = ReminderListsPresenter.class.getSimpleName();

    public ReminderListsPresenter(ReminderListsContract.View view){
        mView=view;
    }



    @Override
    public void loadSelectedList(String id) {
        Log.d(TAG,"loadSelectedList called");
        mView.showSelectedList(id);
    }

    @Override
    public void loadListOptions(String id) {
        Log.d(TAG,"loadListOptions called");
        mView.showListOptions(id);

    }

    @Override
    public void start() {

    }
}
