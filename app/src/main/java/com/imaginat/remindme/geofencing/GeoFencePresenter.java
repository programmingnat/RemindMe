package com.imaginat.remindme.geofencing;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
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


    @Override
    public void start() {
        setUpForCurrentGeoAddressData(mNewGeoFenceData);
    }

    //============Methods that prepare the map to be viewed========================================
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

        RemindMeApplication remindMeApplication = (RemindMeApplication) ((Fragment)mView).getActivity().getApplicationContext();
        LocationUpdateService locationUpdateService = remindMeApplication.getServiceReference();
        Location location = locationUpdateService.getCurrentLocation();

        if (alarmData != null) {

            mViewWControls.setButtonTexts(true, alarmData.isActive());
            if(alarmData.isActive()){
                mView.setAddressMarker(alarmData.getLatitude(), alarmData.getLongitude());
            }else{
               // mView.setAddressMarker(location.getLatitude(), location.getLongitude());
                if(location!=null) {
                    mView.setLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                }
            }
        } else {
            //tell the controls there is no prevous data, and the alarm is not active
            mViewWControls.setButtonTexts(false, false);
            if(location!=null) {
                mView.setLocation(new LatLng(location.getLatitude(), location.getLongitude()));
            }
        }



    }


    @Override
    public GeoFenceAlarmData getLatestGeoFenceData() {
        return mNewGeoFenceData;
    }


    //===================CALLS THAT CHANGE UI=========================================
    /**
    * Displays the dialog that prompts user for address to use in geo fence
     */
    @Override
    public void requestAddressForGeoFence() {
        mViewWControls.showAddressDialog();
    }

    /**
     *  If previous fence data exists, asks user to confirm change
     */
    @Override
    public void saveGeoFenceCoordinates() {

        //check if previouly exist, if so confirm
        if (mGeoFenceAlarmData != null) {
            mViewWControls.showSaveFenceConfirmationDialog();
        } else {
            writeGeoFence();
        }
    }
    //==================METHODS THAT TURN STREET ADDRESS TO COORDINATES=========================================
    /**
    * When the user enters an address (as the center of the geofence) that data is sent here and is fwd to the IntentService
    * that looks up the coordinates. The results are
     */
    @Override
    public void processStreetAddress(String streetAddress, String cityAddress, String stateAddress, String zipAddress) {
        if (mReceiver == null) {
            mReceiver = new CoordinatesResultReceiver(new Handler());
        }

        if (mNewGeoFenceData == null) {
            mNewGeoFenceData = new GeoFenceAlarmData();
        }

        //save the info in a GeoFenceData object (so it can be readily accessed throughout this activity)
        mNewGeoFenceData.setReminderID(mTaskID);
        mNewGeoFenceData.setAlarmTag(getAlarmTag());
        mNewGeoFenceData.setStreet(streetAddress);
        mNewGeoFenceData.setCity(cityAddress);
        mNewGeoFenceData.setState(stateAddress);
        mNewGeoFenceData.setZipcode(zipAddress);


        //Set the callback of the receiver
        mReceiver.setResult(this);

        //Concat the address into one string
        String address = streetAddress + " " + cityAddress + "," + stateAddress + " " + zipAddress;

        //Set the intents
        Context c = ((Fragment) mView).getContext();
        Intent intent = new Intent(c, FetchCoordinatesIntentService.class);
        intent.putExtra(GlobalConstants.RECEIVER, mReceiver);
        intent.putExtra(GlobalConstants.LOCATION_DATA_EXTRA, address);
        intent.putExtra(GlobalConstants.ALARM_TAG, getAlarmTag());
        intent.putExtra(GlobalConstants.CURRENT_TASK_ID, mTaskID);
        intent.putExtra(GlobalConstants.CURRENT_LIST_ID, mListID);

        //Call the service to tranlate address to coordinate
        c.startService(intent);
    }

    /**
     * This method receives the results of the address to coordinates translation
     */
    @Override
    public void onReceiveCoordinatesResult(int resultCode, Bundle resultData) {

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

            //Update View with a marker in the map
            mView.setAddressMarker(lastLocation.getLatitude(), lastLocation.getLongitude());

            //initialize and set the newly created data
            if (mNewGeoFenceData == null) {
                mNewGeoFenceData = new GeoFenceAlarmData();
            }
            mNewGeoFenceData.setLatitude(lastLocation.getLatitude());
            mNewGeoFenceData.setLongitude(lastLocation.getLongitude());

            //confirm
            saveGeoFenceCoordinates();

        }else{
            //show error message, unable to translate into coordinates
            Toast.makeText(((Fragment)mViewWControls).getActivity(),"Unable to translate into coordinates. Please try again.",Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * writeGeoFence() does 2 things. Saves in geofence info in local database, and sends geoFence to Android Process
     */
    @Override
    public void writeGeoFence() {

        Log.d(TAG, "Inside writeGeoFence");

        //===A. SAVE TO DATABASE===

        //anytime you create or update a geofence, it automatically turns on
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


        //====B. ADD TO GEO FENCE====

        //Request the location service to start (if not already started)
        RemindMeApplication remindMeApplication = (RemindMeApplication) ((Fragment) mView).getActivity().getApplicationContext();
        remindMeApplication.requestStartOfLocationUpdateService();

        //In different thread, make call to local database to get all active geoFences
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
                        locationUpdateService.addGeofences(GlobalConstants.PENDING_INTENT_REQUEST_CODE);

                    }

                });


        /*----Called again to setup the control texts--*/
        setUpForCurrentGeoAddressData(mNewGeoFenceData);
    }

    /**
     * TO deactivate a geo fence, remove it from location process
     */
    @Override
    public void deactivateGeoFence() {
        //Log.d(TAG,"deactivateGeoFence");

        //if this has no data associated with it then no need to do any work
        if(mNewGeoFenceData==null){
            return;
        }

        //get the new state by just making it opposite of its current state
        boolean newActiveState=!mNewGeoFenceData.isActive();
        //set the new state in geoFenceData object
        mNewGeoFenceData.setActive(newActiveState);

        //Update the local database table
        ListsLocalDataSource llds = ListsLocalDataSource.getInstance(((Fragment) mViewWControls).getContext());
        llds.saveGeoFenceAlarm(mNewGeoFenceData.getAlarmID(),mNewGeoFenceData.getReminderID(),mNewGeoFenceData.getAsContentValues());
        mViewWControls.showActiveStateChange(newActiveState);

        //Remove from GeoFence
        RemindMeApplication remindMeApplication = (RemindMeApplication) ((Fragment) mView).getActivity().getApplicationContext();
        remindMeApplication.requestStartOfLocationUpdateService();
        LocationUpdateService locationUpdateService = remindMeApplication.getServiceReference();
        locationUpdateService.removeGeofencesByTag(mNewGeoFenceData.getAlarmTag());

        setUpForCurrentGeoAddressData(mNewGeoFenceData);


    }

    //===================HELPER METHODS=================================================

    /**
     *
     * Creates a tag to associate with every geofence based on the list and taskID.
     */
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


}
