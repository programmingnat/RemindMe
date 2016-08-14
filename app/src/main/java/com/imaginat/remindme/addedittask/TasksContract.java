package com.imaginat.remindme.addedittask;

import com.imaginat.remindme.BasePresenter;
import com.imaginat.remindme.BaseView;
import com.imaginat.remindme.data.ITaskItem;

import java.util.ArrayList;

/**
 * Created by nat on 8/12/16.
 */
public class TasksContract {


    interface View extends BaseView<Presenter>{
        boolean isActive();
        void showAll(ArrayList<ITaskItem>tasks);
    }
    interface ViewAdapter extends BaseView<Presenter>{
        public void setData(ArrayList<ITaskItem> arrayList);
    }
    interface Presenter extends BasePresenter {
        public void createNewReminder(String data);
        public void updateReminder(String id,String data);
        public void updateCheckStatus(String listID,String id,boolean isChecked);
    }

}
