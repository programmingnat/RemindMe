package com.imaginat.remindme.viewtasks;

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
        void showAddNewTask(String listID);
        void showNoTasks();
        void showSuccessfullySaved();
        void showTaskMarkedComplete();
        void showTaskMarkError();
        void showTaskUpdate();
        void showNoTaskUpdated();
    }
    interface ViewAdapter extends BaseView<Presenter>{
        public void setData(ArrayList<ITaskItem> arrayList);
    }
    interface Presenter extends BasePresenter {
        public void createNewReminder();
        public void result(int requestCode,int resultCode);
        public void updateReminder(String listID,String id,String data);
        public void updateCompletionStatus(String listID,String id,boolean isChecked);
    }

}
