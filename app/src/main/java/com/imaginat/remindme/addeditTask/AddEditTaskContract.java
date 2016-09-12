package com.imaginat.remindme.addeditTask;

import com.imaginat.remindme.BasePresenter;
import com.imaginat.remindme.BaseView;

/**
 * Created by nat on 8/14/16.
 */
public class AddEditTaskContract {

    interface View extends BaseView<Presenter> {
         void showTaskList();
          void showError();
    }

    interface Presenter extends BasePresenter{
        void saveTask(String s);

    }
}
