package com.imaginat.remindme.addeditTask;

import android.os.Bundle;

import com.imaginat.remindme.BaseActivity;
import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.R;

/**
 * The Activity encompases the MVP pattern. The activity class creates the presenter,views and ensure that each has the
 * appropriate references. This class is  specifically  used to ADD a task to a list
 */
public class AddEditTask extends BaseActivity<AddEditTaskFragment> {

    /**
     * returns the main xml file that this activity will use
     */
    @Override
    public int getLayoutID() {
        return R.layout.activity_add_edit_task;
    }

    @Override
    public String getAssociatedFragmentTag() {
        return "AddTaskFragment";
    }

    /**
     *Create the presenter and assign it to the view
     */
    @Override
    public Object createPresenter(AddEditTaskFragment fragment) {
        String listID = getIntent().getStringExtra(GlobalConstants.CURRENT_LIST_ID);
        String taskID = getIntent().getStringExtra(GlobalConstants.CURRENT_TASK_ID);
        AddEditTaskPresenter addEditTaskPresenter = new AddEditTaskPresenter(fragment,listID,taskID);
        fragment.setPresenter(addEditTaskPresenter);
        return addEditTaskPresenter;
    }

    /**
     *  creates the "View"
     */
    @Override
    public AddEditTaskFragment getFragment() {
        return new AddEditTaskFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
