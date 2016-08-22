package com.imaginat.remindme.calendar;

import android.content.Context;

import com.imaginat.remindme.BasePresenter;
import com.imaginat.remindme.BaseView;

import java.util.Calendar;

/**
 * Created by nat on 8/21/16.
 */
public class CalendarContract {

    interface View extends BaseView<Presenter> {
        void showEventInfo();
        void showBlankForm();
        void showCreateConfirmation();
        void showUpdateConfirmation();
        void showCreateError();
        void showUpdateError();
        void showTasks();
    }

    interface Presenter extends BasePresenter {
        void createEvent(String title, String description, Calendar startDate, Calendar endDate, String location, Context context);
        void updateEvent(String title, String description,String startDate, String startTime,String endDate,String endTime,String location,Context context,long eventID);
    }
}
