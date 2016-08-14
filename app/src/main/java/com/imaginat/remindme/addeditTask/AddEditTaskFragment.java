package com.imaginat.remindme.addeditTask;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.imaginat.remindme.R;

/**
 * Created by nat on 8/14/16.
 */
public class AddEditTaskFragment extends Fragment
        implements AddEditTaskContract.View {

    AddEditTaskContract.Presenter mPresenter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.add_edit_task_fragment, container, false);

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
                String theText = editText.getText().toString();
                mPresenter.saveTask(theText);
            }
        });
        return view;
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
}
