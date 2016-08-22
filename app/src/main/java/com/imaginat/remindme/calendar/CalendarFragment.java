package com.imaginat.remindme.calendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.imaginat.remindme.R;

/**
 * Created by nat on 8/21/16.
 */
public class CalendarFragment extends Fragment
    implements CalendarContract.View{
    CalendarContract.Presenter mPresenter;
    EditText mTitle_editText,mDescription_editText,mStartDate_editText,mStartTime_editText,mEndDate_editText,
            mEndTime_editText,mLocation_editText;
    Button mCreateUpdate_button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_fragment, container, false);
        mTitle_editText = (EditText)view.findViewById(R.id.eventTitle_editText);
        mDescription_editText=(EditText)view.findViewById(R.id.description_editText);
        mStartDate_editText=(EditText)view.findViewById(R.id.startDate_editText);
        mStartTime_editText=(EditText)view.findViewById(R.id.startTime_editText);
        mEndDate_editText=(EditText)view.findViewById(R.id.endDate_editText);
        mEndTime_editText=(EditText)view.findViewById(R.id.endTime_editText);
        mLocation_editText=(EditText)view.findViewById(R.id.location_editText);

        mStartDate_editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Toast.makeText(CalendarFragment.this.getActivity(),"CLICKY",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mStartTime_editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Toast.makeText(CalendarFragment.this.getActivity(),"CLICKY",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mEndDate_editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Toast.makeText(CalendarFragment.this.getActivity(),"CLICKY",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mEndTime_editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Toast.makeText(CalendarFragment.this.getActivity(),"CLICKY",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mCreateUpdate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });

        return view;
    }

    @Override
    public void setPresenter(CalendarContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
