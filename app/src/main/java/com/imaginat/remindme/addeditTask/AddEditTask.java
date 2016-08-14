package com.imaginat.remindme.addeditTask;

import android.os.Bundle;

import com.imaginat.remindme.BaseActivity;
import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.R;

public class AddEditTask extends BaseActivity<AddEditTaskFragment> {

    @Override
    public int getLayoutID() {
        return R.layout.activity_add_edit_task;
    }

    @Override
    public Object createPresenter(AddEditTaskFragment fragment) {
        String listID = getIntent().getStringExtra(GlobalConstants.CURRENT_LIST_ID);
        String taskID = getIntent().getStringExtra(GlobalConstants.CURRENT_TASK_ID);
        AddEditTaskPresenter addEditTaskPresenter = new AddEditTaskPresenter(fragment,listID,taskID);
        fragment.setPresenter(addEditTaskPresenter);
        return addEditTaskPresenter;
    }

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
