package com.imaginat.remindme.addeditlist;

import com.imaginat.remindme.BasePresenter;
import com.imaginat.remindme.BaseView;

/**
 * Interfaces defined between Presenter and View
 */
public class AddListContract {


    public interface View extends BaseView<Presenter>{
        void showTasks();
        void showPreviousInfo(String title,int icon);
    }


    public interface Presenter extends BasePresenter{
        void addNewList(String listName,int iconID);
    }
}
