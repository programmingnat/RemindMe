package com.imaginat.remindme.geofencing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.imaginat.remindme.R;

public class GeoFencingActivity extends AppCompatActivity {


    private static final String TAG = GeoFencingActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_fencing);
    }



}
