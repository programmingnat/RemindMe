package com.imaginat.remindme.viewtasks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.R;
import com.imaginat.remindme.addeditTask.AddEditTask;
import com.imaginat.remindme.calendar.CalendarActivity;
import com.imaginat.remindme.data.GeoFenceAlarmData;
import com.imaginat.remindme.data.ITaskItem;
import com.imaginat.remindme.geofencing.GeoFencingActivity;

import java.util.ArrayList;

/**
 * Created by nat on 8/12/16.
 */
public class TasksFragment extends Fragment implements TasksContract.View {

    TasksContract.Presenter mPresenter;
    TaskReminderRecyclerAdapter mAdapter;
    RecyclerView mRecyclerView;
    TextView mNoTasksTextView;
    FloatingActionButton mFloatingActionButton;

    public static final int REQUEST_ADD_TASK=100;
    public static final int OPEN_CALENDAR=200;
    public static final int OPEN_GEOFENCE=300;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View  view= inflater.inflate(R.layout.tasks_fragment, container, false);
        mNoTasksTextView = (TextView)view.findViewById(R.id.noTasks_TextView);

       /* SimpleTaskItem simpleTaskItem1 = new SimpleTaskItem();
        simpleTaskItem1.setText("THIS IS A TEMP TEST");
        ArrayList<ITaskItem>taskItemArrayList = new ArrayList<>();
        taskItemArrayList.add(simpleTaskItem1);*/
        mAdapter = new TaskReminderRecyclerAdapter(getContext(),new ArrayList<ITaskItem>());
        mAdapter.setPresenter(mPresenter);



        mRecyclerView = (RecyclerView) view.findViewById(R.id.theRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        MyLinearLayoutManager linearLayoutManager = new MyLinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mFloatingActionButton = (FloatingActionButton)view.findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TasksFragment.this.getActivity(),"CLICKED ",Toast.LENGTH_SHORT).show();
                mPresenter.createNewReminder();


            }
        });



        return view;
    }

    @Override
    public void showFAB() {
        mFloatingActionButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideFAB() {
        mFloatingActionButton.setVisibility(View.GONE);
    }

    @Override
    public void showCalendar(String listID,String reminderID) {
        Intent calendarActivityIntent = new Intent(TasksFragment.this.getActivity(), CalendarActivity.class);
        calendarActivityIntent.putExtra(GlobalConstants.CURRENT_LIST_ID,listID);
        calendarActivityIntent.putExtra(GlobalConstants.CURRENT_TASK_ID,reminderID);
        startActivityForResult(calendarActivityIntent,TasksFragment.OPEN_CALENDAR);
    }

    @Override
    public void showGeoFenceAlarm(String listID, String reminderID, GeoFenceAlarmData geoFenceAlarmData) {
        Intent geoFenceAlarmIntent = new Intent(TasksFragment.this.getActivity(), GeoFencingActivity.class);
        geoFenceAlarmIntent.putExtra(GlobalConstants.CURRENT_LIST_ID,listID);
        geoFenceAlarmIntent.putExtra(GlobalConstants.CURRENT_TASK_ID,reminderID);
        geoFenceAlarmIntent.putExtra(GlobalConstants.GEO_ALARM_DATA_EXTRA,geoFenceAlarmData);
        startActivityForResult(geoFenceAlarmIntent, TasksFragment.OPEN_GEOFENCE);
    }

    @Override
    public void showAddNewTask(String listID) {
        Intent addEditTaskIntent = new Intent(TasksFragment.this.getActivity(),AddEditTask.class);
        addEditTaskIntent.putExtra(GlobalConstants.CURRENT_LIST_ID,listID);
        startActivityForResult(addEditTaskIntent, TasksFragment.REQUEST_ADD_TASK);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode,resultCode);
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
        if(mPresenter!=null){
            mPresenter.start();
        }

    }


    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showAll(ArrayList<ITaskItem> tasks) {
        mAdapter.setData(tasks);
        mRecyclerView.setVisibility(View.VISIBLE);
        mNoTasksTextView.setVisibility(View.GONE);
    }

    @Override
    public void showNoTasks() {
        mRecyclerView.setVisibility(View.GONE);
        mNoTasksTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSuccessfullySaved() {
        showMessage("Successfully Added New Task");
    }

    @Override
    public void showTaskMarkedComplete() {
        showMessage("Task Marked");
    }

    @Override
    public void showTaskMarkError() {
        showMessage("Error marking task");
    }

    @Override
    public void showTaskUpdate() {
        showMessage("Task updated");
    }

    @Override
    public void showNoTaskUpdated() {
        showMessage("No task updated");
    }

    private void showMessage(String message){
        Snackbar.make(getView(),message,Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showOptionsOverlay() {

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
