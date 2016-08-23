package com.imaginat.remindme.calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.imaginat.remindme.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by nat on 8/21/16.
 */
public class CalendarFragment extends Fragment
    implements CalendarContract.View{


    CalendarContract.Presenter mPresenter;
    EditText mTitle_editText,mDescription_editText,mLocation_editText;
    Button mStartDate_button,mStartTime_button,mEndDate_button,mEndTime_button,mCreateUpdate_button;
    Calendar mStartCalendar,mEndCalendar;


    private static final String TAG  = CalendarFragment.class.getSimpleName();
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_START_DATE = 0;
    private static final int REQUEST_START_TIME = 1;
    private static final int REQUEST_END_DATE = 2;
    private static final int REQUEST_END_TIME = 3;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_fragment, container, false);
        mStartCalendar = Calendar.getInstance();
        mEndCalendar = Calendar.getInstance();

        mTitle_editText = (EditText)view.findViewById(R.id.eventTitle_editText);
        mDescription_editText=(EditText)view.findViewById(R.id.description_editText);
        mStartDate_button=(Button)view.findViewById(R.id.startDate_button);
        mStartTime_button=(Button)view.findViewById(R.id.startTime_button);
        mEndDate_button=(Button)view.findViewById(R.id.endDate_button);
        mEndTime_button=(Button)view.findViewById(R.id.endTime_button);
        mLocation_editText=(EditText)view.findViewById(R.id.location_editText);
        mCreateUpdate_button=(Button)view.findViewById(R.id.createEvent_Button);

        mStartDate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"mStartDate_editText called");
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(new Date(System.currentTimeMillis()));
                //DatePickerFragment dialog = new DatePickerFragment();
                dialog.setTargetFragment(CalendarFragment.this, REQUEST_START_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });


        mStartTime_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(new Date(System.currentTimeMillis()));
                //TimePickerFragment dialog = new TimePickerFragment();
                dialog.setTargetFragment(CalendarFragment.this, REQUEST_START_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        } );
        mEndDate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(new Date(System.currentTimeMillis()));
                //DatePickerFragment dialog = new DatePickerFragment();
                dialog.setTargetFragment(CalendarFragment.this, REQUEST_END_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });
        mEndTime_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(new Date(System.currentTimeMillis()));
                //TimePickerFragment dialog = new TimePickerFragment();
                dialog.setTargetFragment(CalendarFragment.this, REQUEST_END_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });

        mCreateUpdate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = mTitle_editText.getText().toString();
                String description = mDescription_editText.getText().toString();
                String location = mLocation_editText.getText().toString();
                mPresenter.createEvent(title,description,mStartCalendar,mEndCalendar,location,getContext());
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        Date date = null;
        Calendar result;
        SimpleDateFormat sf=null;
        switch(requestCode){
            case REQUEST_START_DATE:
                date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                result = Calendar.getInstance();
                result.setTime(date);
                mStartCalendar.set(Calendar.MONTH,result.get(Calendar.MONTH));
                mStartCalendar.set(Calendar.DAY_OF_MONTH, result.get(Calendar.DAY_OF_MONTH));
                mStartCalendar.set(Calendar.YEAR, result.get(Calendar.YEAR));
                mStartDate_button.setText(result.get(Calendar.MONTH)+"/"+result.get(Calendar.DAY_OF_MONTH)+"/"+result.get(Calendar.YEAR));
                break;
            case REQUEST_START_TIME:
                date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
                result = Calendar.getInstance();
                result.setTime(date);
                sf = new SimpleDateFormat("hh:mm:ss a");
                mStartTime_button.setText(sf.format(date));
                mStartCalendar.set(Calendar.HOUR_OF_DAY, result.get(Calendar.HOUR_OF_DAY));
                mStartCalendar.set(Calendar.MINUTE, result.get(Calendar.MINUTE));
                mStartTime_button.setText(result.get(Calendar.HOUR_OF_DAY)+":"+result.get(Calendar.MINUTE));
                break;
            case REQUEST_END_DATE:
                date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                result = Calendar.getInstance();
                result.setTime(date);
                mEndCalendar.set(Calendar.MONTH,result.get(Calendar.MONTH));
                mEndCalendar.set(Calendar.DAY_OF_MONTH, result.get(Calendar.DAY_OF_MONTH));
                mEndCalendar.set(Calendar.YEAR, result.get(Calendar.YEAR));
                mEndDate_button.setText(result.get(Calendar.MONTH)+"/"+result.get(Calendar.DAY_OF_MONTH)+"/"+result.get(Calendar.YEAR));
                break;
            case REQUEST_END_TIME:
                date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
                result = Calendar.getInstance();
                result.setTime(date);
                sf = new SimpleDateFormat("hh:mm:ss a");
                mEndTime_button.setText(sf.format(date));
                mEndCalendar.set(Calendar.HOUR_OF_DAY, result.get(Calendar.HOUR_OF_DAY));
                mEndCalendar.set(Calendar.MINUTE, result.get(Calendar.MINUTE));
                mEndTime_button.setText(result.get(Calendar.HOUR_OF_DAY)+":"+result.get(Calendar.MINUTE));
                break;
        }



    }
    @Override
    public void setPresenter(CalendarContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showEventInfo() {

    }

    @Override
    public void showBlankForm() {

    }

    @Override
    public void showCreateConfirmation() {

    }

    @Override
    public void showUpdateConfirmation() {

    }

    @Override
    public void showCreateError() {

    }

    @Override
    public void showUpdateError() {

    }

    @Override
    public void showTasks() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }
}
