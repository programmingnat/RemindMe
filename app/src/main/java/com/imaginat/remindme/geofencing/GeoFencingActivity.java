package com.imaginat.remindme.geofencing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.imaginat.remindme.R;

public class GeoFencingActivity extends AppCompatActivity {

    //Entry point to Google Play Services
    protected GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_fencing);
    }
}
