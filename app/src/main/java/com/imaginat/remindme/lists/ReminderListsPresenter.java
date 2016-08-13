package com.imaginat.remindme.lists;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.imaginat.remindme.data.ReminderList;
import com.imaginat.remindme.data.source.local.ListsLocalDataSource;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

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
        //get the lists
        loadLists();


    }

    private void loadLists(){
        ListsLocalDataSource llds = ListsLocalDataSource.getInstance(((Fragment)mView).getContext());
        llds.getAllListTitles()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<ReminderList>>() {
                    @Override
                    public void call(List<ReminderList> reminderLists) {
                        Log.d(TAG,"Inside call() found "+reminderLists.size()+" results");
                        //do filter or whatever here
                        ArrayList<ReminderList>reminderListFiltered = new ArrayList<ReminderList>();
                        for(ReminderList rl:reminderLists){
                            reminderListFiltered.add(rl);
                        }
                        if(!mView.isActive()){
                            return;
                        }
                       mView.showAll(reminderListFiltered);

                    }

                });
    }
}
