package com.imaginat.remindme.geofencing;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by nat on 8/23/16.
 */
public class GeoFencingService extends Service
    implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks{

    private static final String TAG = GeoFencingService.class.getSimpleName();
    private static final int REQUEST_FINE_LOCATION =100;

    //Entry point to Google Play Services
    protected GoogleApiClient mGoogleApiClient;



    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG,"Inside buildGoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        Log.d(TAG,"Leaving buildGoogleApiClient");

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
