package com.imaginat.remindme.geofencing;


import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.data.GeoFenceAlarmData;

import java.util.ArrayList;
import java.util.List;

/**
 * Location Service class extends Android Service class and is used to handles GeoFencing and Location Awareness
 * The Service class will exist after the app is closed ONLY IF reminders have geofence alarms attached
 */
public class LocationUpdateService extends Service
        implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, ResultCallback<Status>, LocationListener {



    //TAG used for debugging
    private static final String TAG = LocationUpdateService.class.getSimpleName();

    //Tag use for permission callback
    private static final int REQUEST_FINE_LOCATION = 100;


    //Entry point to Google Play Services
    protected GoogleApiClient mGoogleApiClient;

    //list of geofences
    protected ArrayList<Geofence> mGeofenceList;

    //Pending Intent - used to add or remove geofences
    private PendingIntent mGeofencePendingIntent;

    //flag, keep track if client started or not
    private boolean isGoogleApiClientCreated = false;

    //for binding (to link the service and the rest of the app)
    private IBinder mBinder = new MyLocationUpdateServiceBinder();


    // Stores parameters for requests to the FusedLocationProviderApi.
    protected LocationRequest mLocationRequest;

    //Represents a geographical location.
    protected Location mCurrentLocation;

    //==============SERVICE LIFE CYCLE METHOD================================
    @Override
    public void onCreate() {
        super.onCreate();

        //Log.d(TAG, "Service onCreate called");
        isGoogleApiClientCreated = false;
        mGeofencePendingIntent = null;
        mGeofenceList = new ArrayList<>();

        buildGoogleApiClient();
        createLocationRequest();
        //Log.d(TAG,"Service onCreate done");

    }





    /* The start command is called when the service is started, return START_STICKY to tell system this service should
    * stick around
    */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);

        //use STICKY to keep the service around after app closes
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d(TAG, "onDestroy called");
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        mGoogleApiClient = null;
    }


    //===========================BINDING SERVICE RELATED======================
    public class MyLocationUpdateServiceBinder extends Binder {

        public LocationUpdateService getService() {
            return LocationUpdateService.this;
        }
    }

    //================BIND RELATED METHODS====================================
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "in on bind");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(TAG, "onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "onUnbind");
        return true;
    }

    //=============METHODS RELATED TO CONNECTING TO GOOGLE API CLIENT=======================================
    /**
     * Builds a GoogleApiClient. Used here mainly to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        //Log.d(TAG,"Inside buildGoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        //Log.d(TAG,"Leaving buildGoogleApiClient");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        loadPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION);
        Log.d(TAG, "connected to google play services");
        isGoogleApiClientCreated = true;

        //set the intial value of mCurrentlocation to the actual current location
        try {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }catch(SecurityException sec){
            sec.printStackTrace();
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "connection to google play services suspended");

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "connection to google play services suspended");
    }


    //================HELPER=======================================
    private void loadPermissions(String perm, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Problem loading permissions");
        }
    }

    //==========================CODE TO GET LOCATION=============================

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to requestLocationUpdates() is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException se) {
            se.printStackTrace();
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to requestLocationUpdates() is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(GlobalConstants.UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(GlobalConstants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    public Location getCurrentLocation(){
        return mCurrentLocation;
    }
    //=========================GEO FENCE SPECIFIC CODE ===================================

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofences(int pendingIntentID) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "google api client not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent(pendingIntentID)
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    /**
     * geofences are added with tags, to disable the geofence, use the same tag
     *
     * */
    public void removeGeofencesByTag(String tag) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "google api client not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> removalList = new ArrayList();
        removalList.add(tag);

        try {
            // Remove geofences.
            com.google.android.gms.location.LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    removalList
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if(status.isSuccess()){
                        Log.d(TAG,"Remove geofence successfully");
                    }else{
                        Log.d(TAG,"Removed geofence unsuccessfully");
                        Toast.makeText(LocationUpdateService.this,"An error occurred while trying to remove this geofence",Toast.LENGTH_SHORT).show();
                    }
                }
            }); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    /**
     * Removes geofences, which stops further notifications when the device enters or exits
     * previously registered geofences.
     */
    public void removeGeofences(int pendingIntentID) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "google api client not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Remove geofences.
            com.google.android.gms.location.LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getGeofencePendingIntent(pendingIntentID)
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }


    public void addToGeoFenceList(String requestID, double latitude, double longitude) {


        //mGeofenceList.clear();
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(requestID)

                // Set the circular region of this geofence.
                .setCircularRegion(
                        latitude,
                        longitude,
                        GlobalConstants.GEOFENCE_RADIUS_IN_METERS
                )

                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                .setExpirationDuration(GlobalConstants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)// Geofence.GEOFENCE_TRANSITION_EXIT

                // Create the geofence.
                .build());

    }

    public void populateGeofenceList(List<GeoFenceAlarmData> fenceData) {

        for (GeoFenceAlarmData f : fenceData) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(f.getAlarmTag())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            f.getLatitude(),
                            f.getLongitude(),
                            GlobalConstants.GEOFENCE_RADIUS_IN_METERS
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(GlobalConstants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)

                    // Create the geofence.
                    .build());
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent(int intentID) {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(this, intentID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;

    }

    public void onResult(Status status) {
        Log.d(TAG, "inside onResult");
        if (status.isSuccess()) {
            Log.d(TAG, "Geofence successfully added");
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }





}
