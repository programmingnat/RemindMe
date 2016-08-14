package com.imaginat.remindme.addedittask;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imaginat.remindme.R;
import com.imaginat.remindme.data.ITaskItem;

import java.util.ArrayList;

/**
 * Created by nat on 8/12/16.
 */
public class TasksFragment extends Fragment implements TasksContract.View {

    TasksContract.Presenter mPresenter;
    TaskReminderRecyclerAdapter mAdapter;
    RecyclerView mRecyclerView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View  view= inflater.inflate(R.layout.tasks_fragment, container, false);


       /* SimpleTaskItem simpleTaskItem1 = new SimpleTaskItem();
        simpleTaskItem1.setText("THIS IS A TEMP TEST");
        ArrayList<ITaskItem>taskItemArrayList = new ArrayList<>();
        taskItemArrayList.add(simpleTaskItem1);
        mAdapter = new TaskReminderRecyclerAdapter(getContext(),taskItemArrayList);*/



        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.theRecyclerView);
        recyclerView.setAdapter(mAdapter);
        mRecyclerView = recyclerView;
        MyLinearLayoutManager linearLayoutManager = new MyLinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);


        return view;
    }

    @Override
    public void setPresenter(TasksContract.Presenter presenter) {
        mPresenter=presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }


    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showAll(ArrayList<ITaskItem> tasks) {

    }


    //========================================================================
    //prevent error animating when updating list
    private class MyLinearLayoutManager extends LinearLayoutManager {

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }

        public MyLinearLayoutManager(Context context) {
            super(context);
        }

        public MyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }
    }
}
