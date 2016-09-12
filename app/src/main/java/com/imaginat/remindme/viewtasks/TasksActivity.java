package com.imaginat.remindme.viewtasks;

import android.os.Bundle;

import com.imaginat.remindme.BaseActivity;
import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.R;

/**
 * The Activity encompases the MVP pattern. The activity class creates the presenter,views and ensure that each has the
 * appropriate references. This class is  specifically  used to view the reminders and tasks in each list
 */
public class TasksActivity extends BaseActivity<TasksFragment> {


    /**
     * returns the XML file layout used by this activity
     */
    @Override
    public int getLayoutID() {
        return R.layout.activity_tasks;
    }

    @Override
    public String getAssociatedFragmentTag() {
        return "TasksActivityFragment";
    }

    /**
     *
     * creates the "Presenter" and assigns a reference to the View
     */
    @Override
    public Object createPresenter(TasksFragment fragment) {
        String currentListID=getIntent().getStringExtra(GlobalConstants.CURRENT_LIST_ID);
        TasksPresenter tasksPresenter = new TasksPresenter(currentListID,fragment);
        fragment.setPresenter(tasksPresenter);
        return tasksPresenter;
    }

    /**
     * create the View class (MVP framework)
     */
    @Override
    public TasksFragment getFragment() {
        return new TasksFragment();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }
}
