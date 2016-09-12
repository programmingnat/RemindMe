package com.imaginat.remindme.geofencing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.imaginat.remindme.BaseActivity;
import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.R;
import com.imaginat.remindme.data.GeoFenceAlarmData;

/**
 * The Activity encompases the MVP pattern. The activity class creates the presenter,views and ensure that each has the
 * appropriate references. This class is  specifically  used to allow the user to set geofence data
 */
public class GeoFencingActivity extends BaseActivity<GeoFenceFragment> {


    private static final String TAG = GeoFencingActivity.class.getSimpleName();

    //TAGS to track the fragments
    private static final String TAG_MAP_FRAGMENT = "map_fragment";
    private static final String TAG_CONTROLS_FRAGMENT = "control_fragment";

    @Override
    public int getLayoutID() {
        return R.layout.activity_geo_fencing;
    }

    @Override
    public Object createPresenter(GeoFenceFragment fragment) {
        return null;
    }

    @Override
    public String getAssociatedFragmentTag() {
        return "GeoFenceFragments";
    }

    public Object createPresenter(GeoFenceFragment fragment, GeoFenceFragment_Controls fragment_controls) {
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

        //set up the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);


        //Fragment (View)
        FragmentManager fragmentManager = getSupportFragmentManager();

        GeoFenceFragment fragment=null;
        fragment = (GeoFenceFragment)fragmentManager.findFragmentByTag(TAG_MAP_FRAGMENT);
        if(fragment==null){
            fragment = getFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.my_frame, fragment,TAG_MAP_FRAGMENT);
            fragmentTransaction.commit();
        }




        GeoFenceFragment_Controls fragmentControls =null;
        fragmentControls = (GeoFenceFragment_Controls)fragmentManager.findFragmentByTag(TAG_CONTROLS_FRAGMENT);
        if(fragmentControls==null){
            fragmentControls = new GeoFenceFragment_Controls();
            FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
            fragmentTransaction2.add(R.id.my_frame2, fragmentControls,TAG_CONTROLS_FRAGMENT);
            fragmentTransaction2.commit();
        }



        //Set up the presenter
        createPresenter(fragment,fragmentControls);

    }

}
