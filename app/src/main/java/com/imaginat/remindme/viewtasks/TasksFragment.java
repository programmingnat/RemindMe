package com.imaginat.remindme.viewtasks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.R;
import com.imaginat.remindme.addeditTask.AddEditTask;
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
        taskItemArrayList.add(simpleTaskItem1);*/
        mAdapter = new TaskReminderRecyclerAdapter(getContext(),new ArrayList<ITaskItem>());




        mRecyclerView = (RecyclerView) view.findViewById(R.id.theRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        MyLinearLayoutManager linearLayoutManager = new MyLinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TasksFragment.this.getActivity(),"CLICKED ",Toast.LENGTH_SHORT).show();
                mPresenter.createNewReminder();


            }
        });


        return view;
    }

    @Override
    public void showAddNewTask(String listID) {
        Intent addEditTaskIntent = new Intent(TasksFragment.this.getActivity(),AddEditTask.class);
        addEditTaskIntent.putExtra(GlobalConstants.CURRENT_LIST_ID,listID);
        TasksFragment.this.getActivity().startActivity(addEditTaskIntent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mAdapter.setData(tasks);
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
