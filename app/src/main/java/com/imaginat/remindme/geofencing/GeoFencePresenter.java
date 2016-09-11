package com.imaginat.remindme.geofencing;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.RemindMeApplication;
import com.imaginat.remindme.data.GeoFenceAlarmData;
import com.imaginat.remindme.data.source.local.DBSchema;
import com.imaginat.remindme.data.source.local.ListsLocalDataSource;

import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * This class is the link between View(control panel & the map) and the Data.
 * Primary job is to get requests for data from the view, and pass data to view
 */
public class GeoFencePresenter implements GeoFenceContract.Presenter {

    private static final String TAG = GeoFencePresenter.class.getSimpleName();

    //References to views
    private GeoFenceContract.View mView;
    private GeoFenceContract.ViewWControls mViewWControls;

    //References to alarm data  and reminder ids
    final private GeoFenceAlarmData mGeoFenceAlarmData;
    private GeoFenceAlarmData mNewGeoFenceData;
    private String mListID, mTaskID;

    //The Receiver used to get the coordinates from the service that transforms address to coordinates
    private CoordinatesResultReceiver mReceiver;


    public GeoFencePresenter(GeoFenceContract.View view, GeoFenceContract.ViewWControls controls, String listID, String reminderID, GeoFenceAlarmData alarmData) {
        mListID = listID;
        mTaskID = reminderID;
        mView = view;
        mViewWControls = controls;
        mGeoFenceAlarmData = alarmData;

        //create a new copy of the geofence alarm data to modify (original is for undo)
        if(mGeoFenceAlarmData!=null){
            mNewGeoFenceData = new GeoFenceAlarmData(mGeoFenceAlarmData);
        }


    }



    /**
     * This method is called by the Views (and passed in null) to request initial when presenter created (if it exists)
     * The method can also be called after a geofence status change,  so the controls can reflect the changes
     */
    @Override
    public void setUpForCurrentGeoAddressData(GeoFenceAlarmData alarmData) {

        //if alarmData is null, use the data that was intially sent in to this activity
        if(alarmData==null){
            alarmData=mGeoFenceAlarmData;
        }

        if (alarmData != null) {
            mView.setAddressMarker(alarmData.getLatitude(), alarmData.getLongitude());
            mViewWControls.setButtonTexts(alarmData == null?false:true, alarmData.isActive());
        } else {
            mViewWControls.setButtonTexts(false, false);
        }

    }

    @Override
    public GeoFenceAlarmData getLatestGeoFenceData() {
        return mNewGeoFenceData;
    }

    /**
     * TO deactivate a geo fence, remove it from location process
     */
    @Override
    public void deactivateGeoFence() {
        Log.d(TAG,"deactivateGeoFence");
        if(mNewGeoFenceData==null){
            return;
        }
        boolean newActiveState=!mNewGeoFenceData.isActive();
        mNewGeoFenceData.setActive(newActiveState);

        //Update the table
        ListsLocalDataSource llds = ListsLocalDataSource.getInstance(((Fragment) mViewWControls).getContext());
        llds.saveGeoFenceAlarm(mNewGeoFenceData.getAlarmID(),mNewGeoFenceData.getReminderID(),mNewGeoFenceData.getAsContentValues());
        mViewWControls.showActiveStateChange(newActiveState);

        //This block of code removes the GeoFence from the
        RemindMeApplication remindMeApplication = (RemindMeApplication) ((Fragment) mView).getActivity().getApplicationContext();
        remindMeApplication.requestStartOfLocationUpdateService();
        LocationUpdateService locationUpdateService = remindMeApplication.getServiceReference();
        locationUpdateService.removeGeofencesByTag(mNewGeoFenceData.getAlarmTag());

        setUpForCurrentGeoAddressData(mGeoFenceAlarmData);


    }

    /**
     * This method receives the results of the address to coordinates translation
     */
    @Override
    public void onReceiveCoordinatesResult(int resultCode, Bundle resultData) {

        Log.d(TAG, "onReceiveCoordinatesResult");

        if (GlobalConstants.SUCCESS_RESULT == resultCode) {

            //If address translated successfully, add data to the database
            Location lastLocation = resultData.getParcelable(GlobalConstants.RESULT_DATA_KEY);
            String requestID = resultData.getString(GlobalConstants.ALARM_TAG);
            Log.d(TAG, "ABOUT TO SAVE SOME INFO");

            //save info to local database
            HashMap<String, String> data = new HashMap<>();
            data.put(DBSchema.geoFenceAlarm_table.cols.ALARM_TAG, requestID);
            data.put(DBSchema.geoFenceAlarm_table.cols.LATITUDE, Double.toString(lastLocation.getLatitude()));
            data.put(DBSchema.geoFenceAlarm_table.cols.LONGITUDE, Double.toString(lastLocation.getLongitude()));
            data.put(DBSchema.geoFenceAlarm_table.cols.IS_ACTIVE, "1");

            //Update View with a pi in the map
            mView.setAddressMarker(lastLocation.getLatitude(), lastLocation.getLongitude());

            //initialize and set the newly created data
            if (mNewGeoFenceData == null) {
                mNewGeoFenceData = new GeoFenceAlarmData();
            }
            mNewGeoFenceData.setLatitude(lastLocation.getLatitude());
            mNewGeoFenceData.setLongitude(lastLocation.getLongitude());


        }
    }

    @Override
    public void processStreetAddress(String streetAddress, String cityAddress, String stateAddress, String zipAddress) {
        if (mReceiver == null) {
            mReceiver = new CoordinatesResultReceiver(new Handler());
        }

        if (mNewGeoFenceData == null) {
            mNewGeoFenceData = new GeoFenceAlarmData();
        }

        //save the info
        mNewGeoFenceData.setReminderID(mTaskID);
        mNewGeoFenceData.setAlarmTag(getAlarmTag());
        mNewGeoFenceData.setStreet(streetAddress);
        mNewGeoFenceData.setCity(cityAddress);
        mNewGeoFenceData.setState(stateAddress);
        mNewGeoFenceData.setZipcode(zipAddress);


        mReceiver.setResult(this);
        String address = streetAddress + " " + cityAddress + "," + stateAddress + " " + zipAddress;
        Context c = ((Fragment) mView).getContext();
        Intent intent = new Intent(c, FetchCoordinatesIntentService.class);
        intent.putExtra(GlobalConstants.RECEIVER, mReceiver);
        intent.putExtra(GlobalConstants.LOCATION_DATA_EXTRA, address);
        intent.putExtra(GlobalConstants.ALARM_TAG, getAlarmTag());
        intent.putExtra(GlobalConstants.CURRENT_TASK_ID, mTaskID);
        intent.putExtra(GlobalConstants.CURRENT_LIST_ID, mListID);

        c.startService(intent);
    }

    @Override
    public void requestAddressForGeoFence() {
        mViewWControls.showAddressDialog();
    }

    @Override
    public void saveGeoFenceCoordinates() {

        //check if previouly exist, if so confirm
        if (mGeoFenceAlarmData != null) {
            mViewWControls.showSaveFenceConfirmationDialog();
        } else {
            writeGeoFence();
        }
    }

    /**
     * writeGeoFence() does 2 things. Saves in geofence info in local database, and sends geoFence to Android Process
     */
    @Override
    public void writeGeoFence() {

        Log.d(TAG, "Inside writeGeoFence");

        //anytime you create or update a geofence, it automatically turns out
        mNewGeoFenceData.setActive(true);

        //reference to database
        ListsLocalDataSource llds = ListsLocalDataSource.getInstance(((Fragment) mViewWControls).getContext());

        //Determine if this is an update (previously exists) or a new insert
        String alarmID = null;
        if (mNewGeoFenceData.getAlarmID() != null) {
            alarmID = mNewGeoFenceData.getAlarmID();
        }

        //alarmID would still be null if no previous alarm was set
        //save in table
        llds.saveGeoFenceAlarm(alarmID, mNewGeoFenceData.getReminderID(), mNewGeoFenceData.getAsContentValues());


        //now add it to GeoFence proecss

        RemindMeApplication remindMeApplication = (RemindMeApplication) ((Fragment) mView).getActivity().getApplicationContext();
        remindMeApplication.requestStartOfLocationUpdateService();
        //LocationUpdateService locationUpdateService = remindMeApplication.getServiceReference();
        //locationUpdateService.addToGeoFenceList("TEXT TO BE REPLACED", mNewGeoFenceData.getLatitude(), mNewGeoFenceData.getLongitude());

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

                        RemindMeApplication remindMeApplication = (RemindMeApplication) ((Fragment) mView).getActivity().getApplicationContext();
                        LocationUpdateService locationUpdateService = remindMeApplication.getServiceReference();
                        locationUpdateService.populateGeofenceList(geoFenceAlarmDataList);
                        locationUpdateService.addGeofences(createAlarmTag());

                    }

                });


        /*----Called again to setup the control texts--*/
        setUpForCurrentGeoAddressData(mNewGeoFenceData);
    }


    private String getAlarmTag() {

        return "_L" + mListID + "I" + mTaskID + "GEOFENCE";
    }


    private int createAlarmTag() {
        String result = getAlarmTag();
        int strlen = result.length();
        int hash = 7;
        for (int i = 0; i < strlen; i++) {
            hash = hash * 31 + result.charAt(i);
        }
        return hash;
    }


    @Override
    public void start() {

    }
}
