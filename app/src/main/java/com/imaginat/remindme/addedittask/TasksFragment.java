package com.imaginat.remindme.addedittask;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imaginat.remindme.R;

/**
 * Created by nat on 8/12/16.
 */
public class TasksFragment extends Fragment implements TasksContract.View {

    TasksContract.Presenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View  view= inflater.inflate(R.layout.tasks_fragment, container, false);

        return view;
    }

    @Override
    public void setPresenter(TasksContract.Presenter presenter) {

    }
}
