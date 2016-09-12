package com.imaginat.remindme.addeditTask;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.imaginat.remindme.R;

/**
 * This page allows the user to enter a new task/reminder
 */
public class AddEditTaskFragment extends Fragment
        implements AddEditTaskContract.View {

    //reference to the presenter that does the logic
    AddEditTaskContract.Presenter mPresenter;

    //reference to the View that
    EditText mEditText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Inflate and get reference to the view
        View view = inflater.inflate(R.layout.add_edit_task_fragment, container, false);

        //Get reference to the edit text field, and allow to indicate "done" using the keyboard
        mEditText = (EditText)view.findViewById(R.id.task_editText);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                boolean handled =false;

                //this tells the system that when the user peforms a "done" operation
                //to save
                if(actionID== EditorInfo.IME_ACTION_DONE){
                    handled=true;
                    saveClicked();
                }

                return handled;
            }
        });


        //In case the user doesnt perform a "done" operation, the user
        //can explicitly notify the app that the task is done by hitting the enter button
        Button enterButton = (Button)view.findViewById(R.id.enter_button);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parentView = (View)v.getParent();
                EditText editText = (EditText)parentView.findViewById(R.id.task_editText);
                if(editText==null||editText.getText().toString().isEmpty()){
                    editText.setError("Please enter data here");
                    return;
                }
                saveClicked();

            }
        });


        //Cancel button returns user to the task page
        Button cancelButton = (Button)view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTaskList();
            }
        });

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }


    /**
     * Send the text (of the new task) to the presenter
     */
    private void saveClicked(){
        String theText = mEditText.getText().toString();
        mPresenter.saveTask(theText);
    }

    @Override
    public void setPresenter(AddEditTaskContract.Presenter presenter) {
        mPresenter = presenter;
    }

    //======================METHODS CALLED BY THE PRESENTER TO CHANGE THE VIEW==============================

    @Override
    public void showTaskList() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void showError() {
        Snackbar.make(getView(), "unable to update list",Snackbar.LENGTH_LONG).show();
    }


}
