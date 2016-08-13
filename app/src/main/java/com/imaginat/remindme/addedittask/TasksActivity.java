package com.imaginat.remindme.addedittask;

import android.os.Bundle;

import com.imaginat.remindme.BaseActivity;
import com.imaginat.remindme.R;

public class TasksActivity extends BaseActivity<TasksFragment> {


    @Override
    public int getLayoutID() {
        return R.layout.activity_tasks;
    }

    @Override
    public Object createPresenter(TasksFragment fragment) {
        TasksPresenter tasksPresenter = new TasksPresenter();
        fragment.setPresenter(tasksPresenter);
        return tasksPresenter;
    }

    @Override
    public TasksFragment getFragment() {
        return new TasksFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
}
