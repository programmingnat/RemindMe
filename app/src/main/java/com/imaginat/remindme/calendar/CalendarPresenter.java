package com.imaginat.remindme.calendar;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.imaginat.remindme.data.ITaskItem;
import com.imaginat.remindme.data.SimpleTaskItem;
import com.imaginat.remindme.data.source.local.ListsLocalDataSource;

import java.util.Calendar;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Calendar Presenter class is the link between the View and the Data (ContentProvider)
 */
public class CalendarPresenter implements CalendarContract.Presenter {

    CalendarContract.View mView;

    String mListID, mReminderID;
    String mTaskText;
    int mCalendarEventFound;

    public CalendarPresenter(String listID, String reminderID, CalendarContract.View view) {
        mListID = listID;
        mReminderID = reminderID;
        mView = view;


    }

    @Override
    public void start() {
        /**
         * Load the event from the database, the event is the ID the local calendar uses
         */
        ListsLocalDataSource llds = ListsLocalDataSource.getInstance(((Fragment) mView).getContext());
        llds.getSingleTask(mListID,mReminderID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<ITaskItem>>() {
                    @Override
                    public void call(List<ITaskItem> reminderLists) {

                        SimpleTaskItem task = (SimpleTaskItem)reminderLists.get(0);
                        mTaskText=task.getText();
                        mCalendarEventFound=task.getCalendarEventID();

                        if(mCalendarEventFound>0){
                            pullEventFromCalendar(mCalendarEventFound);
                        }else{
                            mView.showTaskTitle(mTaskText);
                        }


                    }

                });
    }

    /**
     *
     * Send data to view to display previous info
     */
    private void pullEventFromCalendar(long eventID){
        CalendarProvider cp = new CalendarProvider();
        CalendarData cd = cp.fetchEventByID(((Fragment)mView).getContext(),eventID);
        if(cd!=null){
            mView.showPreviousEventInfo(cd);
        }
    }

    /**
     * create a calendar event for the local calendar
     */
    @Override
    public void createEvent(String title, String description, Calendar startTime, Calendar endTime, String location, Context context) {
        CalendarProvider cp = new CalendarProvider();
        cp.fetchCalendars(context);
        long eventID = cp.insertEventInCalendar(title, description, location, startTime, endTime, context);


        //update the database
        ListsLocalDataSource llds = ListsLocalDataSource.getInstance(((Fragment) mView).getContext());
        llds.updateTaskCalendarEvent(mListID, mReminderID, eventID);

        mView.showTasks();

    }

    /**
     *
     * update an event that is already in the local calendar
     */
    @Override
    public void updateEvent(String title, String description, Calendar startDate, Calendar endDate,  String location, Context context, long eventID) {
        CalendarProvider cp = new CalendarProvider();
        cp.update(((Fragment)mView).getContext(),eventID,title,description,startDate.getTimeInMillis(),endDate.getTimeInMillis(),location);
        //cp.update();
    }

    /**
     *
     * ensure that the start date is before the end date
     */
    @Override
    public boolean validateDates(Calendar startDate, Calendar endDate) {
        if(startDate.compareTo(endDate)<=0){
            return true;
        }
        return false;
    }
}
