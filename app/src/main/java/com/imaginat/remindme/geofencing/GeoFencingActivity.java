package com.imaginat.remindme.geofencing;

import android.os.Bundle;

import com.imaginat.remindme.BaseActivity;
import com.imaginat.remindme.R;

public class GeoFencingActivity extends BaseActivity<GeoFenceFragment> {


    private static final String TAG = GeoFencingActivity.class.getSimpleName();


    @Override
    public int getLayoutID() {
        return R.layout.activity_geo_fencing;
    }

    @Override
    public Object createPresenter(GeoFenceFragment fragment) {
        GeoFencePresenter presenter = new GeoFencePresenter();
        fragment.setPresenter(presenter);
        return presenter;
    }

    @Override
    public GeoFenceFragment getFragment() {
        return new GeoFenceFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_fencing);
    }



}
