package com.imaginat.remindme.addeditlist;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.imaginat.remindme.data.ReminderList;
import com.imaginat.remindme.data.RemindersContentProvider;
import com.imaginat.remindme.data.source.local.DBSchema;
import com.imaginat.remindme.data.source.local.ListsLocalDataSource;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * This class connects the View (fragment) to the Model (data)
 */
public class AddListPresenter implements AddListContract.Presenter {

    //reference to the class name for debugging purposes
    private final String TAG = AddListPresenter.class.getSimpleName();

    //reference to the view (MVP)
    AddListContract.View mView;

    //id of list to alter
    String mListID = null;


    public AddListPresenter(AddListContract.View view) {
        mView = view;
    }

    /**
     * called by the Activity (when setting up MVP), a non null listID indicates that this is an UPDATE vs new creation
     */
    public void setListID(String listID){
        mListID=listID;
    }

    /**
     * called by the view when title changed or added
     */
    @Override
    public void addNewList(String listName, int iconID) {


        if(mListID!=null){
            //If this is an update
            ListsLocalDataSource llds  =  ListsLocalDataSource.getInstance(((Fragment)mView).getContext());
            llds.updateListTitle(mListID,listName,Integer.toString(iconID));

        }else{
            //If this is adding a new list

            //Changing over to using content provider
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBSchema.lists_table.cols.LIST_TITLE, listName);
            contentValues.put(DBSchema.lists_table.cols.LIST_ICON, iconID);

            Uri uri = ((Fragment) mView).getContext().getContentResolver().insert(RemindersContentProvider.CONTENT_URI, contentValues);

            // ListsLocalDataSource llds  =  ListsLocalDataSource.getInstance(((Fragment)mView).getContext());
            // llds.createNewList(listName,iconID);
        }



        //GO BACK to previous page
        mView.showTasks();

    }

    @Override
    public void start() {

        //if mListID is not null, that means we are editing/updating
        if(mListID!=null) {
            //Get the information currently in the database
            ListsLocalDataSource llds = ListsLocalDataSource.getInstance(((Fragment) mView).getContext());
            llds.getListTitleInfo(mListID)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<ReminderList>>() {
                        @Override
                        public void call(List<ReminderList> reminderLists) {

                            ReminderList rl = reminderLists.get(0);
                            mView.showPreviousInfo(rl.getTitle(),rl.getIcon());

                        }

                    });
        }
    }
}
