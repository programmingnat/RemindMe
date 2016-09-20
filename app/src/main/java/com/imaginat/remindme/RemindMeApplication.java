package com.imaginat.remindme;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.imaginat.remindme.data.GeoFenceAlarmData;
import com.imaginat.remindme.data.source.local.ListsLocalDataSource;
import com.imaginat.remindme.geofencing.LocationUpdateService;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class RemindMeApplication extends Application {

    private static final String TAG = RemindMeApplication.class.getSimpleName();

    //hold a reference to the running service
    LocationUpdateService mLocationUpdateService;

    //flag indicating if the service is bound
    boolean mLocationUpdateServiceBound;

    //class used to bind to service
    MyServiceConnection mServiceConnection;

    //Save App options and preferences
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        //shared preferences
        mSharedPreferences = getSharedPreferences(GlobalConstants.PREFERENCES, Context.MODE_PRIVATE);

    }

    public void startServiceAsNeeded(){

        //Th
        //If service is not running start it
        //Bind to service (if service is running)

        if (isServiceRunning() == false) {



//            ListsLocalDataSource llds = ListsLocalDataSource.getInstance(this);
//            llds.getAllActiveAlarms()
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Action1<List<GeoFenceAlarmData>>() {
//                        @Override
//                        public void call(List<GeoFenceAlarmData> geoFenceAlarmDataList) {
//                            if (geoFenceAlarmDataList != null) {
//
//                            }
//
//                        }
//
//                    });


            //look in shared preference to see if anybody needs it, if it does start it up
            int totalNoOfSharedPreferences = mSharedPreferences.getInt(GlobalConstants.GEO_ALARM_COUNT, -1);
            int totalNoInDatabase = 1;


            if (totalNoInDatabase != totalNoOfSharedPreferences) {
                //reset shared preferences
                SharedPreferences.Editor ed = mSharedPreferences.edit();
                ed.putInt(GlobalConstants.GEO_ALARM_COUNT, totalNoInDatabase);
                ed.commit();
            }
            Log.d(TAG,"totalInShared: "+totalNoOfSharedPreferences+" totalDatabase"+totalNoInDatabase);
            int totalNoOfActiveGeoAlarms = totalNoInDatabase;
            if (totalNoOfActiveGeoAlarms > 0) {
                //start up the service
                Intent startUpServiceIntent = new Intent(RemindMeApplication.this, LocationUpdateService.class);
                startService(startUpServiceIntent);
                mServiceConnection=new MyServiceConnection();
                bindService(startUpServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);


            }


        } else {
            //service is already up and running, now bind if not already bound
            if (mLocationUpdateServiceBound == false) {
                Intent boundIntent = new Intent(this, LocationUpdateService.class);
                mServiceConnection=new MyServiceConnection();
                bindService(boundIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
            }
        }

    }

    public boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            //Log.d(TAG, "CHECKING " + service.service.getClassName());
            if ("com.imaginat.remindme.geofencing.LocationUpdateService".equalsIgnoreCase(service.service.getClassName())) {
                Toast.makeText(this, "LocationUpdateService is RUNNING", Toast.LENGTH_LONG).show();
                //Log.d(TAG, "LocationUpdateService is currently running");
                return true;
            }
        }
        Toast.makeText(this, "LocationUpdateService is NOT RUNNING", Toast.LENGTH_LONG).show();
        //Log.d(TAG, "LocationUpdateService is NOT currently running");
        return false;
    }



    public LocationUpdateService getServiceReference() {
        if (mLocationUpdateServiceBound) {
            return mLocationUpdateService;
        }
        //check if number of geoFenceAlarms warrants system to start
        int totalNoOfActiveGeoAlarms = mSharedPreferences.getInt(GlobalConstants.GEO_ALARM_COUNT, -1);
        if (totalNoOfActiveGeoAlarms > 0 && mLocationUpdateServiceBound == false) {
            //bind it here
            Intent bindingIntent = new Intent(this, LocationUpdateService.class);
            mServiceConnection=new MyServiceConnection();
            bindService(bindingIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
            mLocationUpdateServiceBound = true;
            mLocationUpdateService =mServiceConnection.locationUpdateService;
            return mLocationUpdateService;
        }
        return null;
    }


    public void requestStartOfLocationUpdateService() {
        //add to count of alarm using it
        int currentTotal = mSharedPreferences.getInt(GlobalConstants.GEO_ALARM_COUNT, 0);
        currentTotal++;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(GlobalConstants.GEO_ALARM_COUNT, currentTotal);
        editor.commit();
        if (isServiceRunning() == false) {
            //start the service
            Intent startServiceIntent = new Intent(this, LocationUpdateService.class);
            startService(startServiceIntent);
        }

    }


    public void requestStopOfLocationUpdateService() {
        //reduce the number of geoFence kept in shared preferences
        //onDestroy will stop service if count is less than 1
        int currentTotal = mSharedPreferences.getInt(GlobalConstants.GEO_ALARM_COUNT, 0);
        currentTotal--;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(GlobalConstants.GEO_ALARM_COUNT, currentTotal);

        if(currentTotal<=0) {
            mLocationUpdateService.stopSelf();
            //mLocationUpdateService.unbindService(mServiceConnection);
            unbindService(mServiceConnection);
            stopService(new Intent(RemindMeApplication.this, LocationUpdateService.class));
        }
    }

    public void reloadGeoFences(final LocationUpdateService locationUpdateService){
        //In different thread, make call to local database to get all active geoFences
        ListsLocalDataSource llds = ListsLocalDataSource.getInstance(this);
        llds.getAllActiveAlarms()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<GeoFenceAlarmData>>() {
                    @Override
                    public void call(List<GeoFenceAlarmData> geoFenceAlarmDataList) {


                        if (geoFenceAlarmDataList == null) {
                            Log.d(TAG, "Inside call() geoFenceAlarmDataList is null");
                        } else {
                            Log.d(TAG, "Inside call() found " + geoFenceAlarmDataList.size() + " results");
                        }


                        mLocationUpdateService.populateGeofenceList(geoFenceAlarmDataList);
                        mLocationUpdateService.addGeofences(GlobalConstants.PENDING_INTENT_REQUEST_CODE);

                    }

                });



    }
    //==========================================================================
    private class MyServiceConnection implements ServiceConnection {
        public LocationUpdateService locationUpdateService;

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            LocationUpdateService.MyLocationUpdateServiceBinder myBinder = (LocationUpdateService.MyLocationUpdateServiceBinder) service;
            mLocationUpdateService = myBinder.getService();
            locationUpdateService=mLocationUpdateService;
            mLocationUpdateServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mLocationUpdateServiceBound = false;
        }
    }
}
