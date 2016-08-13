package com.imaginat.remindme.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.imaginat.remindme.data.ITaskItem;
import com.imaginat.remindme.data.ReminderList;
import com.imaginat.remindme.data.SimpleTaskItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;

/**
 * Created by nat on 8/8/16.
 */
public class ListsLocalDataSource {

    private static final String TAG = ListsLocalDataSource.class.getSimpleName();

    private RemindMeSQLHelper mSQLHelper;
    private static ListsLocalDataSource mInstance;

    private ListsLocalDataSource(@NonNull Context context) {
        mSQLHelper = RemindMeSQLHelper.getInstance(context);

    }

    public static ListsLocalDataSource getInstance(Context c) {
        if (mInstance == null) {
            mInstance = new ListsLocalDataSource(c);
        }
        return mInstance;
    }

    //////////////LIST OF LISTS RELATED//////////////////////////////////
    private Callable<List<ReminderList>> getAllListTitles_Callable() {
        return new Callable<List<ReminderList>>() {
            @Override
            public List<ReminderList> call() throws Exception {

                Log.d(TAG, "Inside call()");
                SQLiteDatabase db = mSQLHelper.getReadableDatabase();
                Cursor c = db.query(DBSchema.lists_table.NAME, //table
                        DBSchema.lists_table.ALL_COLUMNS, //columns
                        null,//select
                        null,//selection args
                        null,//group
                        null,//having
                        null,//order
                        null);//limit

                c.moveToFirst();
                ArrayList<ReminderList> titles = new ArrayList<>();
                while (c.isAfterLast() == false) {
                    ReminderList rl = new ReminderList(c.getString(c.getColumnIndex(DBSchema.lists_table.cols.LIST_TITLE)),
                            c.getInt(c.getColumnIndex(DBSchema.lists_table.cols.LIST_TITLE)),
                            c.getString(c.getColumnIndex(DBSchema.lists_table.cols.LIST_ID)));
                    titles.add(rl);
                    c.moveToNext();

                }
                return titles;
            }


        };
    }

    public Observable<List<ReminderList>> getAllListTitles() {
        return mSQLHelper.getAllLists(getAllListTitles_Callable());
    }

    /**
     * Create New List
     *
     * @param listName
     * @param selectedIcon
     * @return
     */
    public String createNewList(String listName, int selectedIcon) {
        ContentValues values = new ContentValues();
        values.put(DBSchema.lists_table.cols.LIST_TITLE, listName);
        values.put(DBSchema.lists_table.cols.LIST_ICON, selectedIcon);
        SQLiteDatabase db = mSQLHelper.getWritableDatabase();

        long id = db.insert(DBSchema.lists_table.NAME,
                null,
                values);
        return Long.toString(id);
    }

    ////////////////// TASK RELATED /////////////////////
    public Observable<List<ITaskItem>>getAllTasks(String listID){
        return mSQLHelper.getAllTasks(getAllTasks_Callable(listID));
    }
    private GetTasks_Callable getAllTasks_Callable(String listID) {
        return new GetTasks_Callable(listID);
    }

    class GetTasks_Callable implements Callable<List<ITaskItem>> {

        private String mListID = null;

        GetTasks_Callable(String listID) {
            mListID = listID;
        }

        @Override
        public List<ITaskItem> call() throws Exception {
            ArrayList<ITaskItem> listItems = new ArrayList<>();

            SQLiteDatabase db = mSQLHelper.getReadableDatabase();
            Cursor c = db.query(DBSchema.reminders_table.NAME, //table
                    DBSchema.reminders_table.ALL_COLUMNS, //columns
                    DBSchema.reminders_table.cols.LIST_ID + "=? ",
                    new String[]{mListID},
                    null,//group
                    null,//having
                    null,//order
                    null);//limit


            c.moveToFirst();
            while (!c.isAfterLast()) {
                int colIndex = c.getColumnIndex(DBSchema.reminders_table.cols.REMINDER_TEXT);
                String text = c.getString(colIndex);
                Log.d(TAG, "Adding " + text);
                SimpleTaskItem sti = new SimpleTaskItem();
                sti.setText(text);
                listItems.add(sti);
                c.moveToNext();
            }

            return listItems;
        }
    }
}

