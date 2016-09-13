package com.imaginat.remindme.lists;

import android.support.v4.app.Fragment;

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

    //TAG used to display class name during debugging
    private final String TAG = ReminderListsPresenter.class.getSimpleName();

    //Reference to the VIEW in MVP (which is a fragment)
    ReminderListsContract.View mView;

    //Reference to the listitem selected on long click
    String mLongClickedListID=null;

    public ReminderListsPresenter(ReminderListsContract.View view){
        mView=view;
    }

    /**
     *
     * Asks the view to display the contents of the ist
     */
    @Override
    public void loadSelectedList(String id,String currentListName) {
        mView.showSelectedList(id,currentListName);
    }

    /**
     *
     * Asks the view to display list options in the toolbar
     */
    @Override
    public void loadListOptions(String id) {
        mView.showListOptions(id);
        mLongClickedListID=id;
    }

    /**
     * Asks the view to unload the list options in the toolbar
     */
    @Override
    public void unloadListOptions() {
        mView.hideListOptions();
        mLongClickedListID=null;
    }

    /**
     * Asks the view to show the add/edit title list
     */
    @Override
    public void loadAddEditList() {
        // mView.showListOptions("random");
        mView.showAddEditList(mLongClickedListID);
    }
    /**
     * Called by the resume lifecycle of the fragment
     */
    @Override
    public void start() {
        //get the lists
        loadLists();
    }


    /**
     * Gets the data from the local database
     */
    private void loadLists(){
        ListsLocalDataSource llds = ListsLocalDataSource.getInstance(((Fragment)mView).getContext());
        llds.getAllListTitles()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<ReminderList>>() {
                    @Override
                    public void call(List<ReminderList> reminderLists) {
                        //Log.d(TAG,"Inside call() found "+reminderLists.size()+" results");
                        //do filter or whatever here
                        ArrayList<ReminderList>reminderListFiltered = new ArrayList<ReminderList>();
                        for(ReminderList rl:reminderLists){
                            reminderListFiltered.add(rl);
                        }
                        if(!mView.isActive()){
                            return;
                        }
                        if(reminderListFiltered.size()==0){
                            mView.showNoLists();
                        }else {
                            mView.showAll(reminderListFiltered);
                        }

                    }

                });
    }

    public void deleteList(){
        if(mLongClickedListID!=null){
            ListsLocalDataSource llds = ListsLocalDataSource.getInstance(((Fragment)mView).getContext());
            llds.deleteList(mLongClickedListID);
            loadLists();
        }
        unloadListOptions();

    }




}
