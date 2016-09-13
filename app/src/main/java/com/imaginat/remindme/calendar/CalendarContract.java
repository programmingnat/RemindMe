package com.imaginat.remindme.calendar;

import android.content.Context;

import com.imaginat.remindme.BasePresenter;
import com.imaginat.remindme.BaseView;

import java.util.Calendar;

/**
 * Calendar Contract
 */
public class CalendarContract {

    interface View extends BaseView<Presenter> {
        void showEventInfo();
        void showBlankForm();
        void showCreateConfirmation();
        void showUpdateConfirmation();
        void showCreateError();
        void showUpdateError();
        void showPreviousEventInfo(CalendarData cd);
        void showTaskTitle(String taskString);
        void showTasks();
    }

    interface Presenter extends BasePresenter {
        boolean validateDates(Calendar startDate,Calendar endDate);
        void createEvent(String title, String description, Calendar startDate, Calendar endDate, String location, Context context);
        void updateEvent(String title, String description,Calendar startDate, Calendar endDate,String location,Context context,long eventID);
    }
}
