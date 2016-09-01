package com.imaginat.remindme.geofencing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.imaginat.remindme.BaseActivity;
import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.R;
import com.imaginat.remindme.data.GeoFenceAlarmData;

public class GeoFencingActivity extends BaseActivity<GeoFenceFragment> {


    private static final String TAG = GeoFencingActivity.class.getSimpleName();


    @Override
    public int getLayoutID() {
        return R.layout.activity_geo_fencing;
    }

    @Override
    public Object createPresenter(GeoFenceFragment fragment) {
        return null;
    }


    public Object createPresenter(GeoFenceFragment fragment,GeoFenceFragment_Controls fragment_controls) {
        Intent callingIntent  = getIntent();
        String currentListID = callingIntent.getStringExtra(GlobalConstants.CURRENT_LIST_ID);
        String currentTaskID = callingIntent.getStringExtra(GlobalConstants.CURRENT_TASK_ID);
        GeoFenceAlarmData geoFenceAlarmData = callingIntent.getParcelableExtra(GlobalConstants.GEO_ALARM_DATA_EXTRA);
        GeoFencePresenter presenter = new GeoFencePresenter(fragment,fragment_controls,currentListID,currentTaskID,geoFenceAlarmData);
        fragment.setPresenter(presenter);
        fragment_controls.setPresenter(presenter);
        return presenter;
    }

    @Override
    public GeoFenceFragment getFragment() {
        return new GeoFenceFragment();
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        Log.d(TAG,"onCreate");
        //set up the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        GeoFenceFragment fragment = getFragment();
        //Fragment (View)
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.my_frame, fragment);
        fragmentTransaction.commit();


        GeoFenceFragment_Controls fragmentControls = new GeoFenceFragment_Controls();
        FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
        fragmentTransaction2.add(R.id.my_frame2, fragmentControls);
        fragmentTransaction2.commit();

        //Set up the presenter
        createPresenter(fragment,fragmentControls);

    }

}
