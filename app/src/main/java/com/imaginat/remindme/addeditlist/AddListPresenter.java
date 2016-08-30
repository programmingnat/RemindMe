package com.imaginat.remindme.addeditlist;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.imaginat.remindme.data.RemindersContentProvider;
import com.imaginat.remindme.data.source.local.DBSchema;

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

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBSchema.lists_table.cols.LIST_TITLE,listName);
        contentValues.put(DBSchema.lists_table.cols.LIST_ICON,iconID);

        Uri uri =((Fragment)mView).getContext().getContentResolver().insert(RemindersContentProvider.CONTENT_URI,contentValues);

       // ListsLocalDataSource llds  =  ListsLocalDataSource.getInstance(((Fragment)mView).getContext());
       // llds.createNewList(listName,iconID);
        mView.showTasks();

    }

    @Override
    public void start() {

    }
}
