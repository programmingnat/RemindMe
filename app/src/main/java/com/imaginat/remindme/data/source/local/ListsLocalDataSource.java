package com.imaginat.remindme.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.imaginat.remindme.data.GeoFenceAlarmData;
import com.imaginat.remindme.data.ITaskItem;
import com.imaginat.remindme.data.ReminderList;
import com.imaginat.remindme.data.SimpleTaskItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;

/**
 *  This class gets the data from the local database and sends it usually in an ArrayList to caller
 *  The methods here return a Callable so that the data can be used with RXJava/Android,
 *  Every query to the database is wrapped inside a callable in the call method, this allows us
 *  to send the callable to RxJava and get an Observable
 */
public class ListsLocalDataSource {

    private static final String TAG = ListsLocalDataSource.class.getSimpleName();

    private RemindMeSQLHelper mSQLHelper;

    private static ListsLocalDataSource mInstance;


    /**
     *
     * private constructor
     */
    private ListsLocalDataSource(@NonNull Context context) {
        mSQLHelper = RemindMeSQLHelper.getInstance(context);
    }

    /*----------get a reference to the singleton instance------*/
    public static ListsLocalDataSource getInstance(Context c) {
        if (mInstance == null) {
            mInstance = new ListsLocalDataSource(c);
        }
        return mInstance;
    }

    //////////////LIST OF LISTS RELATED (calls to get information about the lists in the apps)//////////////////////////////////

    //========================GET ALL LISTS===================================
    /**
     *
     * method called by client to get all titles
     */
    public Observable<List<ReminderList>> getAllListTitles() {
        return mSQLHelper.getAllLists(getAllListTitles_Callable());
    }
    /**
     *
     * method that returns a callable classs that will be wrapped in RXJava observable
     */
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
                    rl.setIcon(c.getInt(c.getColumnIndex(DBSchema.lists_table.cols.LIST_ICON)));
                    titles.add(rl);

                    c.moveToNext();

                }
                return titles;
            }


        };
    }



    //==================GET SPECIFIC TITLE INFO=====================

    /**
     *
     * method that creates the callable class that you pass to RXJava to get OBservable wrapper
     */
    private Callable<List<ReminderList>> getTitleAndIconForList_Callable(final String listID) {
        return new Callable<List<ReminderList>>() {
            @Override
            public List<ReminderList> call() throws Exception {


                SQLiteDatabase db = mSQLHelper.getReadableDatabase();
                Cursor c = db.query(DBSchema.lists_table.NAME, //table
                        DBSchema.lists_table.ALL_COLUMNS, //columns
                        DBSchema.lists_table.cols.LIST_ID+"=?",//select
                        new String[]{listID},//selection args
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
                    rl.setIcon(c.getInt(c.getColumnIndex(DBSchema.lists_table.cols.LIST_ICON)));
                    titles.add(rl);

                    c.moveToNext();

                }
                return titles;
            }


        };
    }

    /**
     *
     * called by the client to get title info
     */
    public Observable<List<ReminderList>> getListTitleInfo(String listID) {
        return mSQLHelper.getAllLists(getTitleAndIconForList_Callable(listID));
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
        return createNewList(values);
    }

    /**
     *
     * create a new list
     */
    public String createNewList(ContentValues values){
        SQLiteDatabase db = mSQLHelper.getWritableDatabase();

        long id = db.insert(DBSchema.lists_table.NAME,
                null,
                values);
        return Long.toString(id);
    }

    /**
     *
     * update the list title and icon
     */
    public int updateListTitle(String listID,String title,String icon){
        ContentValues values = new ContentValues();
        values.put(DBSchema.lists_table.cols.LIST_TITLE,title);
        values.put(DBSchema.lists_table.cols.LIST_ICON,icon);
        SQLiteDatabase db= mSQLHelper.getWritableDatabase();

        return db.update(DBSchema.lists_table.NAME,
                values,
                DBSchema.lists_table.cols.LIST_ID+"=?",
                new String[]{listID});
    }

    /**
     *
     * delete list
     */
    public void deleteList(String listID){
        SQLiteDatabase db= mSQLHelper.getWritableDatabase();
        String[] argsArray = new String[]{listID};
        db.execSQL("DELETE FROM geoFenceAlarm WHERE reminder_id IN (SELECT reminder_id FROM reminders WHERE list_id=?)",
                argsArray);
        db.delete(DBSchema.reminders_table.NAME,
                DBSchema.reminders_table.cols.LIST_ID+"=?",
                argsArray);
        db.delete(DBSchema.lists_table.NAME,
                DBSchema.lists_table.cols.LIST_ID+"=?",
                argsArray);


    }
    ////////////////// TASK RELATED DATABASE CALLS (gets the individual reminders/task in a list)/////////////////////



    //================GET ALL TASKS=============================================

    /**
     *
        method called by the client
     */
    public Observable<List<ITaskItem>>getAllTasks(String listID){
        return mSQLHelper.getAllTasks(getAllTasks_Callable(listID));
    }

    /**
     *
     * method called to get the callable class
     */
    private GetTasks_Callable getAllTasks_Callable(String listID) {
        return new GetTasks_Callable(listID);
    }

    /**
     * Callable class that holds the database calls and gets wrapped in Observable
     */
    class GetTasks_Callable implements Callable<List<ITaskItem>> {

        private String mListID = null;

        GetTasks_Callable(String listID) {
            mListID = listID;
        }

        @Override
        public List<ITaskItem> call() throws Exception {
            ArrayList<ITaskItem> listItems = new ArrayList<>();

            SQLiteDatabase db = mSQLHelper.getReadableDatabase();
//            Cursor c = db.query(DBSchema.reminders_table.NAME, //table
//                    DBSchema.reminders_table.ALL_COLUMNS, //columns
//                    DBSchema.reminders_table.cols.LIST_ID + "=? ",
//                    new String[]{mListID},
//                    null,//group
//                    null,//having
//                    null,//order
//                    null);//limit

            Cursor c = db.rawQuery("SELECT r.reminder_id,r.list_id,r.reminderText,r.isCompleted,r.calendarEventID,"+
                    " gfa.street,gfa.city,gfa.state,gfa.zipcode,gfa.latitude,gfa.meterRadius,gfa.isAlarmActive,gfa.longitude,gfa.geoFenceAlarm_id,gfa.alarmTag "+
                    "FROM "+DBSchema.reminders_table.NAME+" r "+
                    "LEFT OUTER JOIN "+DBSchema.geoFenceAlarm_table.NAME+" gfa "+
                    "ON gfa."+DBSchema.geoFenceAlarm_table.cols.REMINDER_ID+"=r."+DBSchema.reminders_table.cols.REMINDER_ID+
                    " WHERE r."+DBSchema.reminders_table.cols.LIST_ID+"=?",new String[]{mListID});



            c.moveToFirst();
            while (!c.isAfterLast()) {
                int colIndex = c.getColumnIndex(DBSchema.reminders_table.cols.REMINDER_TEXT);
                String text = c.getString(colIndex);
                //Log.d(TAG, "Adding " + text);
                //int listColIndex = c.getColumnIndex(DBSchema.reminders_table.cols.LIST_ID);
                String listID=c.getString(c.getColumnIndex(DBSchema.reminders_table.cols.LIST_ID));
                int reminderColIndex = c.getColumnIndex(DBSchema.reminders_table.cols.REMINDER_ID);//"r."+DBSchema.reminders_table.cols.REMINDER_ID);
                String reminderID=c.getString(reminderColIndex);//c.getColumnIndex(DBSchema.reminders_table.cols.REMINDER_ID));
                SimpleTaskItem sti = new SimpleTaskItem(listID,reminderID);

                sti.setCompleted(c.getInt(c.getColumnIndex(DBSchema.reminders_table.cols.IS_COMPLETED))==1?true:false);
                sti.setText(text);
                sti.setCalendarEventID(c.getInt(c.getColumnIndex(DBSchema.reminders_table.cols.CALENDAR_EVENT_ID)));
                listItems.add(sti);

                //GEO FENCE DATA (if applicable)
                if(c.getString(c.getColumnIndex("alarmTag"))!=null){
                    GeoFenceAlarmData geoFenceAlarmData = new GeoFenceAlarmData();
                    //alarm id
                    geoFenceAlarmData.setAlarmID(c.getString(c.getColumnIndex(DBSchema.geoFenceAlarm_table.cols.GEOFENCE_ALARM_ID)));
                    //reminder id
                    geoFenceAlarmData.setReminderID(c.getString(c.getColumnIndex(DBSchema.geoFenceAlarm_table.cols.REMINDER_ID)));
                    //address fields
                    geoFenceAlarmData.setStreet(c.getString(c.getColumnIndex(DBSchema.geoFenceAlarm_table.cols.STREET)));
                    geoFenceAlarmData.setCity(c.getString(c.getColumnIndex(DBSchema.geoFenceAlarm_table.cols.CITY)));
                    geoFenceAlarmData.setState(c.getString(c.getColumnIndex(DBSchema.geoFenceAlarm_table.cols.STATE)));
                    geoFenceAlarmData.setZipcode(c.getString(c.getColumnIndex(DBSchema.geoFenceAlarm_table.cols.ZIPCODE)));
                    //coordinates
                    geoFenceAlarmData.setLongitude(c.getDouble(c.getColumnIndex(DBSchema.geoFenceAlarm_table.cols.LONGITUDE)));
                    geoFenceAlarmData.setLatitude(c.getDouble(c.getColumnIndex(DBSchema.geoFenceAlarm_table.cols.LATITUDE)));
                    //tag to id
                    geoFenceAlarmData.setAlarmTag(c.getString(c.getColumnIndex(DBSchema.geoFenceAlarm_table.cols.ALARM_TAG)));
                    //radius of geofence
                    geoFenceAlarmData.setMeterRadius(c.getInt(c.getColumnIndex(DBSchema.geoFenceAlarm_table.cols.RADIUS)));
                    //alarm state (active/not)
                    geoFenceAlarmData.setActive(c.getInt(c.getColumnIndex(DBSchema.geoFenceAlarm_table.cols.IS_ACTIVE))==0?false:true);

                    sti.setGeoFenceAlarmData(geoFenceAlarmData);
                }
                c.moveToNext();
            }

            return listItems;
        }
    }

    //===========================GET SINGLE TASK FROM DATABASE==================
    /**
     *
     * method used by client
     */
    public Observable<List<ITaskItem>>getSingleTask(String listID,String reminderID){
        return mSQLHelper.getSingleTask(getSingleTask_callable(listID,reminderID));
    }

    /**
     *
     * the method calls that returns the callable class
     */
    private GetSingleTask_Callable getSingleTask_callable(String listID,String reminderID) {
        return new GetSingleTask_Callable(listID,reminderID);
    }

    /**
     * The class that gets created (with the queries) and gets wrapped into observable
     */
    class GetSingleTask_Callable implements Callable<List<ITaskItem>> {

        private String mListID = null;
        private String mReminderID = null;

        GetSingleTask_Callable(String listID,String reminderID) {
            mListID = listID;
            mReminderID=reminderID;
        }

        @Override
        public List<ITaskItem> call() throws Exception {
            ArrayList<ITaskItem> listItems = new ArrayList<>();

            SQLiteDatabase db = mSQLHelper.getReadableDatabase();
            Cursor c = db.query(DBSchema.reminders_table.NAME, //table
                    DBSchema.reminders_table.ALL_COLUMNS, //columns
                    DBSchema.reminders_table.cols.LIST_ID + "=? AND "+DBSchema.reminders_table.cols.REMINDER_ID+"=?",
                    new String[]{mListID,mReminderID},
                    null,//group
                    null,//having
                    null,//order
                    null);//limit





            c.moveToFirst();
            while (!c.isAfterLast()) {
                int colIndex = c.getColumnIndex(DBSchema.reminders_table.cols.REMINDER_TEXT);
                String text = c.getString(colIndex);
                //Log.d(TAG, "Adding " + text);
                //int listColIndex = c.getColumnIndex(DBSchema.reminders_table.cols.LIST_ID);
                String listID=c.getString(c.getColumnIndex(DBSchema.reminders_table.cols.LIST_ID));
                int reminderColIndex = c.getColumnIndex(DBSchema.reminders_table.cols.REMINDER_ID);//"r."+DBSchema.reminders_table.cols.REMINDER_ID);
                String reminderID=c.getString(reminderColIndex);//c.getColumnIndex(DBSchema.reminders_table.cols.REMINDER_ID));
                SimpleTaskItem sti = new SimpleTaskItem(listID,reminderID);

                sti.setCompleted(c.getInt(c.getColumnIndex(DBSchema.reminders_table.cols.IS_COMPLETED))==1?true:false);
                sti.setText(text);
                sti.setCalendarEventID(c.getInt(c.getColumnIndex(DBSchema.reminders_table.cols.CALENDAR_EVENT_ID)));
                listItems.add(sti);


                c.moveToNext();
            }

            return listItems;
        }
    }
    //========================GET ALARM DATA FROM DATBASE=======================================

    /**
     *
     * method called by the client to get the alarms
     */
    public Observable<List<GeoFenceAlarmData>>getAllActiveAlarms(){
        return mSQLHelper.getAllActiveGeoFenceAlarms(getAllActiveAlarms_Callable());
    }

    /**
     *
     * method used to return a Callable classs
    */
    private GetAlarms_Callable getAllActiveAlarms_Callable() {
        return new GetAlarms_Callable();
    }

    /**
     * the clas that ultimately gets passed to RXJava to return an Observable
     */

    class GetAlarms_Callable implements Callable<List<GeoFenceAlarmData>>{

        @Override
        public List<GeoFenceAlarmData> call() throws Exception {

            SQLiteDatabase db = mSQLHelper.getReadableDatabase();
            Cursor c = db.query(DBSchema.geoFenceAlarm_table.NAME, //table
                    DBSchema.geoFenceAlarm_table.ALL_COLUMNS, //columns
                    DBSchema.geoFenceAlarm_table.cols.IS_ACTIVE+"=?",//select
                    new String[]{"1"},//selection args
                    null,//group
                    null,//having
                    null,//order
                    null);//limit

            c.moveToFirst();


            //Loop through results, and pull values from each column
            ArrayList<GeoFenceAlarmData> alarms = new ArrayList<>();
            while (!c.isAfterLast()) {
                GeoFenceAlarmData alarmData = new GeoFenceAlarmData();
                //alarm id
                alarmData.setAlarmID(c.getString(c.getColumnIndex(DBSchema.geoFenceAlarm_table.cols.GEOFENCE_ALARM_ID)));
                alarmData.setReminderID(c.getString(c.getColumnIndex(DBSchema.geoFenceAlarm_table.cols.REMINDER_ID)));
                alarmData.setStreet(c.getString(c.getColumnIndex(DBSchema.geoFenceAlarm_table.cols.STREET)));
                alarmData.setCity(c.getString(c.getColumnIndex(DBSchema.geoFenceAlarm_table.cols.CITY)));
                alarmData.setState(c.getString(c.getColumnIndex(DBSchema.geoFenceAlarm_table.cols.STATE)));
                alarmData.setZipcode(c.getString(c.getColumnIndex(DBSchema.geoFenceAlarm_table.cols.ZIPCODE)));
                alarmData.setLatitude(c.getDouble(c.getColumnIndex(DBSchema.geoFenceAlarm_table.cols.LATITUDE)));
                alarmData.setLongitude(c.getDouble(c.getColumnIndex(DBSchema.geoFenceAlarm_table.cols.LONGITUDE)));
                alarmData.setAlarmTag(c.getString(c.getColumnIndex(DBSchema.geoFenceAlarm_table.cols.ALARM_TAG)));
                alarmData.setMeterRadius(c.getInt(c.getColumnIndex(DBSchema.geoFenceAlarm_table.cols.RADIUS)));
                int isActive=c.getInt(c.getColumnIndex(DBSchema.geoFenceAlarm_table.cols.IS_ACTIVE));
                Log.d(TAG,"geo fence is "+isActive);
                alarmData.setActive(isActive==0?false:true);
                alarms.add(alarmData);
                c.moveToNext();
            }
            return alarms;
        }
    }
    //===================CREATE UPDATE DELETE REMINDERS==============================================

    /**
     *
     * create a new task
     */
    public long createNewTask(String listID,String text){
        ContentValues values = new ContentValues();
        values.put(DBSchema.reminders_table.cols.LIST_ID,listID);
        values.put(DBSchema.reminders_table.cols.REMINDER_TEXT,text);
        SQLiteDatabase db= mSQLHelper.getWritableDatabase();

        return db.insert(DBSchema.reminders_table.NAME,
                null,
                values);
    }

    /**
     *
     * Called when a task is checked
     */
    public int updateTaskComplete(String listID,String reminderID,int checkValue){
        ContentValues values = new ContentValues();
        values.put(DBSchema.reminders_table.cols.IS_COMPLETED,checkValue);
        SQLiteDatabase db = mSQLHelper.getWritableDatabase();
        return db.update(DBSchema.reminders_table.NAME,
                values,
                DBSchema.reminders_table.cols.LIST_ID + "=? AND " + DBSchema.reminders_table.cols.REMINDER_ID + "=?",
                new String[]{listID, reminderID});
    }

    /**
     * update the text of the reminder/task
     */
    public int updateTaskText(String listID,String reminderID,String text){
        ContentValues values = new ContentValues();
        values.put(DBSchema.reminders_table.cols.REMINDER_TEXT,text);
        SQLiteDatabase db= mSQLHelper.getWritableDatabase();

       return db.update(DBSchema.reminders_table.NAME,
                values,
                DBSchema.reminders_table.cols.LIST_ID + "=? AND " + DBSchema.reminders_table.cols.REMINDER_ID + "=?",
                new String[]{listID, reminderID});
    }

    /**
     *
     * Save the eventID from the calendar into the database
     */
    public int updateTaskCalendarEvent(String listID,String reminderID,long eventID){
        ContentValues values = new ContentValues();
        values.put(DBSchema.reminders_table.cols.CALENDAR_EVENT_ID,eventID);
        SQLiteDatabase db= mSQLHelper.getWritableDatabase();

        return db.update(DBSchema.reminders_table.NAME,
                values,
                DBSchema.reminders_table.cols.LIST_ID + "=? AND " + DBSchema.reminders_table.cols.REMINDER_ID + "=?",
                new String[]{listID, reminderID});
    }

    /**
     *
     * Delete a reminder
     */
    public void deleteReminder(String listID,String reminderID){
        SQLiteDatabase db= mSQLHelper.getWritableDatabase();
        db.delete(DBSchema.reminders_table.NAME,
                DBSchema.reminders_table.cols.LIST_ID + "=? AND " + DBSchema.reminders_table.cols.REMINDER_ID + "=?",
                new String[]{listID, reminderID});
    }

    /**
     *
     *Save data for geo fence alarm
     */
    public void saveGeoFenceAlarm(String alarmID,String reminderID,ContentValues values){
        Log.d(TAG,"saveGeoFenceAlarm Called");



        Log.d(TAG,"attempting to update first WHERE alarmID is "+alarmID+" and reminderID is "+reminderID);
        SQLiteDatabase db= mSQLHelper.getWritableDatabase();

        if(alarmID==null){
            db.insert(DBSchema.geoFenceAlarm_table.NAME,
                    null,
                    values);
            return;
        }

        int noOfRowsAffected=db.update(DBSchema.geoFenceAlarm_table.NAME,
                values,
                DBSchema.geoFenceAlarm_table.cols.GEOFENCE_ALARM_ID + "=? AND " + DBSchema.geoFenceAlarm_table.cols.REMINDER_ID + "=?",
                new String[]{alarmID, reminderID});

        if(noOfRowsAffected>0){
            Log.d(TAG,"saveGeoFenceAlarm, noOfRowsAffected "+noOfRowsAffected+" exiting");
            return;
        }


    }

}

