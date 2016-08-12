package com.imaginat.remindme.lists;

import com.imaginat.remindme.BasePresenter;
import com.imaginat.remindme.BaseView;

import java.util.ArrayList;

/**
 * Created by nat on 8/8/16.
 */
public class ReminderListsContract {


    interface View extends BaseView<Presenter> {

        void showListOptions(String listID);

        void showSelectedList(String listID);

        void showAll();

    }

    interface ViewsAdapter extends BaseView<Presenter> {
        void setData(ArrayList theData);


    }


    interface Presenter extends BasePresenter {

        void loadSelectedList(String listID);
        void loadListOptions(String listID);
    }
}
