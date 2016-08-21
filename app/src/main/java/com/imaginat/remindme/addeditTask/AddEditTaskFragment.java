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
 * Created by nat on 8/14/16.
 */
public class AddEditTaskFragment extends Fragment
        implements AddEditTaskContract.View {

    AddEditTaskContract.Presenter mPresenter;
    EditText mEditText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.add_edit_task_fragment, container, false);

        mEditText = (EditText)view.findViewById(R.id.task_editText);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                boolean handled =false;
                if(actionID== EditorInfo.IME_ACTION_DONE){

                    handled=true;
                    saveClicked();
                }
                return handled;
            }
        });
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


        Button cancelButton = (Button)view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTaskList();
            }
        });
        return view;

    }

    private void saveClicked(){
        String theText = mEditText.getText().toString();
        mPresenter.saveTask(theText);
    }
    @Override
    public void setPresenter(AddEditTaskContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

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
