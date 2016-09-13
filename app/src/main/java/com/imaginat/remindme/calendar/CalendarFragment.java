package com.imaginat.remindme.calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginat.remindme.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This class is the View that allows the user to set the event in their LOCAL calendar
 */

public class CalendarFragment extends Fragment
    implements CalendarContract.View{

    //Reference to the presenter
    CalendarContract.Presenter mPresenter;

    //holds any reference to previous data
    CalendarData mPrevCalendarData=null;

    //References to the Views
    EditText mTitle_editText,mDescription_editText,mLocation_editText;
    TextView mSelectedStartDate_textView,mSelectedStartTime_textView,mSelectedEndDate_textView,mSelectedEndTime_textView;
    ImageButton mStartDate_button,mStartTime_button,mEndDate_button,mEndTime_button;
    Button mCreateUpdate_button;
    Calendar mStartCalendar,mEndCalendar;

    //Tag is used for debuggin
    private static final String TAG  = CalendarFragment.class.getSimpleName();

    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";

    //Request Codes
    private static final int REQUEST_START_DATE = 0;
    private static final int REQUEST_START_TIME = 1;
    private static final int REQUEST_END_DATE = 2;
    private static final int REQUEST_END_TIME = 3;


    /**
     *
     *
     */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Inflate the view
        View view = inflater.inflate(R.layout.calendar_fragment, container, false);

        //Create instances of Calendar to hold info
        mStartCalendar = Calendar.getInstance();
        mEndCalendar = Calendar.getInstance();

        //Get references to the Views
        mTitle_editText = (EditText)view.findViewById(R.id.eventTitle_editText);
        mDescription_editText=(EditText)view.findViewById(R.id.description_editText);
        mStartDate_button=(ImageButton)view.findViewById(R.id.startDate_button);
        mStartTime_button=(ImageButton)view.findViewById(R.id.startTime_button);
        mEndDate_button=(ImageButton)view.findViewById(R.id.endDate_button);
        mEndTime_button=(ImageButton)view.findViewById(R.id.endTime_button);
        mLocation_editText=(EditText)view.findViewById(R.id.location_editText);
        mCreateUpdate_button=(Button)view.findViewById(R.id.createEvent_Button);

        mSelectedStartDate_textView =(TextView)view.findViewById(R.id.selectedStartDate_textView);
        mSelectedStartTime_textView=(TextView)view.findViewById(R.id.startTime_textView);
        mSelectedEndDate_textView =(TextView)view.findViewById(R.id.selectedEndDate_textView);
        mSelectedEndTime_textView=(TextView)view.findViewById(R.id.endTime_textView);

        //Set the behavior so the prompt goes to the next field
        mTitle_editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mTitle_editText.setSingleLine(true);

        mDescription_editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mDescription_editText.setSingleLine(true);

        mLocation_editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mLocation_editText.setSingleLine();


        mStartDate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(new Date(System.currentTimeMillis()));
                dialog.setTargetFragment(CalendarFragment.this, REQUEST_START_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });


        mStartTime_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(new Date(System.currentTimeMillis()));
                dialog.setTargetFragment(CalendarFragment.this, REQUEST_START_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        } );
        mEndDate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(new Date(System.currentTimeMillis()));
                dialog.setTargetFragment(CalendarFragment.this, REQUEST_END_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });
        mEndTime_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(new Date(System.currentTimeMillis()));
                dialog.setTargetFragment(CalendarFragment.this, REQUEST_END_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });

        mCreateUpdate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mPresenter.validateDates(mStartCalendar,mEndCalendar)==false){
                    Toast.makeText(getActivity(),"Your start date must be before you end date, and in the future",Toast.LENGTH_SHORT).show();
                    return;
                }
                String title = mTitle_editText.getText().toString();
                String description = mDescription_editText.getText().toString();
                String location = mLocation_editText.getText().toString();
                if(mPrevCalendarData!=null){
                    mPresenter.updateEvent(title,description,mStartCalendar,mEndCalendar,location,getContext(),mPrevCalendarData.getEventID());
                }else {
                    mPresenter.createEvent(title, description, mStartCalendar, mEndCalendar, location, getContext());
                }

                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        });

        return view;
    }

    /**
     *
     * After each dialog open, it sends the data back here to be processed
     */
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
                mSelectedStartDate_textView.setText(result.get(Calendar.MONTH)+"/"+result.get(Calendar.DAY_OF_MONTH)+"/"+result.get(Calendar.YEAR));
                break;
            case REQUEST_START_TIME:
                date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
                result = Calendar.getInstance();
                result.setTime(date);
                sf = new SimpleDateFormat("hh:mm:ss a");
                mSelectedStartTime_textView.setText(sf.format(date));
                mStartCalendar.set(Calendar.HOUR_OF_DAY, result.get(Calendar.HOUR_OF_DAY));
                mStartCalendar.set(Calendar.MINUTE, result.get(Calendar.MINUTE));
                mSelectedStartTime_textView.setText(result.get(Calendar.HOUR_OF_DAY)+":"+result.get(Calendar.MINUTE));
                break;
            case REQUEST_END_DATE:
                date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                result = Calendar.getInstance();
                result.setTime(date);
                mEndCalendar.set(Calendar.MONTH,result.get(Calendar.MONTH));
                mEndCalendar.set(Calendar.DAY_OF_MONTH, result.get(Calendar.DAY_OF_MONTH));
                mEndCalendar.set(Calendar.YEAR, result.get(Calendar.YEAR));
                mSelectedEndDate_textView.setText(result.get(Calendar.MONTH)+"/"+result.get(Calendar.DAY_OF_MONTH)+"/"+result.get(Calendar.YEAR));
                break;
            case REQUEST_END_TIME:
                date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
                result = Calendar.getInstance();
                result.setTime(date);
                sf = new SimpleDateFormat("hh:mm:ss a");
                mSelectedEndTime_textView.setText(sf.format(date));
                mEndCalendar.set(Calendar.HOUR_OF_DAY, result.get(Calendar.HOUR_OF_DAY));
                mEndCalendar.set(Calendar.MINUTE, result.get(Calendar.MINUTE));
                mSelectedEndTime_textView.setText(result.get(Calendar.HOUR_OF_DAY)+":"+result.get(Calendar.MINUTE));
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

    /**
     *
     *  This is called when no data can be found from the calendar content provider, used to set title
     */
    @Override
    public void showTaskTitle(String taskString) {
        mTitle_editText.setText(taskString);
    }

    /**
     *
     * this method is called if previous info was found
     */
    @Override
    public void showPreviousEventInfo(CalendarData cd) {
        mPrevCalendarData=cd;

        Intent intent = new Intent();
        Calendar startCalendar = cd.getStartCalendar();
        Date d = startCalendar.getTime();
        intent.putExtra(DatePickerFragment.EXTRA_DATE,d);
        intent.putExtra(TimePickerFragment.EXTRA_TIME,d);
        onActivityResult(REQUEST_START_DATE,Activity.RESULT_OK,intent);
        onActivityResult(REQUEST_START_TIME,Activity.RESULT_OK,intent);

        Intent intentEnd = new Intent();
        Calendar endCalendar = cd.getEndCalendar();
        Date endDate = endCalendar.getTime();
        intentEnd.putExtra(DatePickerFragment.EXTRA_DATE,endDate);
        intentEnd.putExtra(TimePickerFragment.EXTRA_TIME,endDate);
        onActivityResult(REQUEST_END_DATE,Activity.RESULT_OK,intentEnd);
        onActivityResult(REQUEST_END_TIME,Activity.RESULT_OK,intentEnd);

        mTitle_editText.setText(cd.getTitle());
        mDescription_editText.setText(cd.getDescription());
        mLocation_editText.setText(cd.getLocation());

        mCreateUpdate_button.setText("UPDATE EVENT IN CALENDAR");

    }

    @Override
    public void showTasks() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }



    //================LIFE CYCLE METHODS======

    @Override
    public void onResume() {
        super.onResume();
        if(mPresenter!=null){
            mPresenter.start();
        }
    }
}
