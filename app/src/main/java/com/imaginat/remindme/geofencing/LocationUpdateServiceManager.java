package com.imaginat.remindme.geofencing;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.data.GeoFenceAlarmData;
import com.imaginat.remindme.data.source.local.ListsLocalDataSource;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by nat on 9/20/16.
 */
public class LocationUpdateServiceManager {

    private static final String TAG = LocationUpdateServiceManager.class.getSimpleName();

    //hold a reference to the running service
    LocationUpdateService mLocationUpdateService;

    //flag indicating if the service is bound
    boolean mLocationUpdateServiceBound;

    //class used to bind to service
    MyServiceConnection mServiceConnection;

    //Save App options and preferences
    private SharedPreferences mSharedPreferences;

    private static LocationUpdateServiceManager mInstance;


    private LocationUpdateServiceManager(Context c) {

        //shared preferences
        mSharedPreferences = c.getSharedPreferences(GlobalConstants.PREFERENCES, Context.MODE_PRIVATE);
    }


    public static LocationUpdateServiceManager getInstance(Context c){
        if(mInstance==null){
            mInstance = new LocationUpdateServiceManager(c);
        }
        return mInstance;
    }

    public void startServiceAsNeeded(Context context){

        //Th
        //If service is not running start it
        //Bind to service (if service is running)

        if (isServiceRunning(context) == false) {


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
                Intent startUpServiceIntent = new Intent(context, LocationUpdateService.class);
                context.startService(startUpServiceIntent);
                mServiceConnection=new MyServiceConnection();
                context.bindService(startUpServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);


            }


        } else {
            //service is already up and running, now bind if not already bound
            if (mLocationUpdateServiceBound == false) {
                Intent boundIntent = new Intent(context, LocationUpdateService.class);
                mServiceConnection=new MyServiceConnection();
                context.bindService(boundIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
            }
        }

    }

    public boolean isServiceRunning(Context c) {
        ActivityManager manager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            //Log.d(TAG, "CHECKING " + service.service.getClassName());
            if ("com.imaginat.remindme.geofencing.LocationUpdateService".equalsIgnoreCase(service.service.getClassName())) {
                Toast.makeText(c, "LocationUpdateService is RUNNING", Toast.LENGTH_LONG).show();
                //Log.d(TAG, "LocationUpdateService is currently running");
                return true;
            }
        }
        Toast.makeText(c, "LocationUpdateService is NOT RUNNING", Toast.LENGTH_LONG).show();
        //Log.d(TAG, "LocationUpdateService is NOT currently running");
        return false;
    }



    public LocationUpdateService getServiceReference(Context context) {
        if (mLocationUpdateServiceBound) {
            return mLocationUpdateService;
        }
        //check if number of geoFenceAlarms warrants system to start
        int totalNoOfActiveGeoAlarms = mSharedPreferences.getInt(GlobalConstants.GEO_ALARM_COUNT, -1);
        if (totalNoOfActiveGeoAlarms > 0 && mLocationUpdateServiceBound == false) {
            //bind it here
            Intent bindingIntent = new Intent(context, LocationUpdateService.class);
            mServiceConnection=new MyServiceConnection();
            context.bindService(bindingIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
            mLocationUpdateServiceBound = true;

            return mLocationUpdateService;
        }
        return null;
    }


    public void requestStartOfLocationUpdateService(Context c) {
        //add to count of alarm using it
        int currentTotal = mSharedPreferences.getInt(GlobalConstants.GEO_ALARM_COUNT, 0);
        currentTotal++;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(GlobalConstants.GEO_ALARM_COUNT, currentTotal);
        editor.commit();
        if (isServiceRunning(c) == false) {
            //start the service
            Intent startServiceIntent = new Intent(c, LocationUpdateService.class);
            c.startService(startServiceIntent);
        }

    }


    public void requestStopOfLocationUpdateService(Context c) {
        //reduce the number of geoFence kept in shared preferences
        //onDestroy will stop service if count is less than 1
        int currentTotal = mSharedPreferences.getInt(GlobalConstants.GEO_ALARM_COUNT, 0);
        currentTotal--;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(GlobalConstants.GEO_ALARM_COUNT, currentTotal);

        if(currentTotal<=0) {
            mLocationUpdateService.stopSelf();
            //mLocationUpdateService.unbindService(mServiceConnection);
            c.unbindService(mServiceConnection);
            c.stopService(new Intent(c, LocationUpdateService.class));
        }
    }

    public void reloadGeoFences(Context c,final LocationUpdateService locationUpdateService){
        //In different thread, make call to local database to get all active geoFences
        ListsLocalDataSource llds = ListsLocalDataSource.getInstance(c);
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


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            LocationUpdateService.MyLocationUpdateServiceBinder myBinder = (LocationUpdateService.MyLocationUpdateServiceBinder) service;
            final LocationUpdateService locationUpdateService = myBinder.getService();

            if(locationUpdateService!=null){
                ListsLocalDataSource llds = ListsLocalDataSource.getInstance(((LocationUpdateService.MyLocationUpdateServiceBinder) service).getService().getApplicationContext());
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


                                locationUpdateService.populateGeofenceList(geoFenceAlarmDataList);
                                locationUpdateService.addGeofences(GlobalConstants.PENDING_INTENT_REQUEST_CODE);

                            }

                        });
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mLocationUpdateServiceBound = false;
        }
    }
}
