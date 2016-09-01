package com.imaginat.remindme.geofencing;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.data.GeoFenceAlarmData;
import com.imaginat.remindme.data.source.local.DBSchema;

import java.util.HashMap;

/**
 * Created by nat on 8/30/16.
 */
public class GeoFencePresenter implements GeoFenceContract.Presenter {

    private static final String TAG= GeoFencePresenter.class.getSimpleName();

    private GeoFenceContract.View mView;
    private GeoFenceContract.ViewWControls mViewWControls;
    private GeoFenceAlarmData mGeoFenceAlarmData,mNewGeoFence;
    private String mListID,mTaskID;
    private CoordinatesResultReceiver mReceiver;

    public GeoFencePresenter(GeoFenceContract.View view, GeoFenceContract.ViewWControls controls, String listID, String reminderID, GeoFenceAlarmData alarmData){
        mListID=listID;
        mTaskID=reminderID;
        mView=view;
        mViewWControls = controls;
        mGeoFenceAlarmData=alarmData;
    }

    @Override
    public void onReceiveCoordinatesResult(int resultCode, Bundle resultData) {

        Log.d(TAG,"onReceiveCoordinatesResult");

        if (GlobalConstants.SUCCESS_RESULT == resultCode) {
            //NOW ADD FENCE
            Location lastLocation = resultData.getParcelable(GlobalConstants.RESULT_DATA_KEY);
            String requestID = resultData.getString(GlobalConstants.ALARM_TAG);
            String reminderID = resultData.getString(GlobalConstants.CURRENT_TASK_ID);
            String listID = resultData.getString(GlobalConstants.CURRENT_LIST_ID);
            //mLocationServices.addToGeoFenceList(requestID, lastLocation.getLatitude(), lastLocation.getLongitude());
            Log.d(TAG, "ABOUT TO SAVE SOME INFO");

            //save info to local databasae
            HashMap<String, String> data = new HashMap<>();
            data.put(DBSchema.geoFenceAlarm_table.cols.ALARM_TAG, requestID);
            data.put(DBSchema.geoFenceAlarm_table.cols.LATITUDE, Double.toString(lastLocation.getLatitude()));
            data.put(DBSchema.geoFenceAlarm_table.cols.LONGITUDE, Double.toString(lastLocation.getLongitude()));
            data.put(DBSchema.geoFenceAlarm_table.cols.IS_ACTIVE, "1");

            mView.setAddressMarker(lastLocation.getLatitude(),lastLocation.getLongitude());


            /*
            ToDoListItemManager listItemManager = ToDoListItemManager.getInstance(getContext());
            listItemManager.saveGeoFenceAlarm(requestID, reminderID, data);



            //now get refence
            mIGeoOptions.requestStartOfLocationUpdateService();
            LocationUpdateService locationUpdateService = mIGeoOptions.getServiceReference();


            //locationUpdateService.addToGeoFenceList(theText,lastLocation.getLatitude(),lastLocation.getLongitude());
            ArrayList<FenceData> datas = listItemManager.getActiveFenceData();
            locationUpdateService.populateGeofenceList(datas);
            locationUpdateService.addGeofences(createAlarmTag(GEOFENCE));
            */
        }
    }

    @Override
    public void processStreetAddress(String streetAddress,String cityAddress,String stateAddress, String zipAddress) {
        if(mReceiver==null){
            mReceiver= new CoordinatesResultReceiver(new Handler());
        }

        //save the info
        mNewGeoFence.setStreet(streetAddress);
        mNewGeoFence.setCity(cityAddress);
        mNewGeoFence.setState(stateAddress);
        mNewGeoFence.setZipcode(zipAddress);


        mReceiver.setResult(this);
        String address=streetAddress+" "+cityAddress+","+stateAddress+" "+zipAddress;
        Context c=  ((Fragment)mView).getContext();
        Intent intent = new Intent(c,FetchCoordinatesIntentService.class);
        intent.putExtra(GlobalConstants.RECEIVER,mReceiver);
        intent.putExtra(GlobalConstants.LOCATION_DATA_EXTRA,address);
        intent.putExtra(GlobalConstants.ALARM_TAG,getAlarmID());
        intent.putExtra(GlobalConstants.CURRENT_TASK_ID,mTaskID);
        intent.putExtra(GlobalConstants.CURRENT_LIST_ID,mListID);

        c.startService(intent);
    }

    @Override
    public void requestAddressForGeoFence() {
        mViewWControls.showAddressDialog();
    }

    @Override
    public void saveGeoFenceCoordinates() {

        //check if previouly exist, if so confirm
        if(mGeoFenceAlarmData!=null){
            mViewWControls.showSaveFenceConfirmationDialog();
        }else{
            writeGeoFence();
        }
    }

    @Override
    public void writeGeoFence() {
        //save to database

    }

    private String getAlarmID() {

        return "_L" + mListID + "I" + mTaskID + "GEOFENCE";
    }
    @Override
    public void start() {

    }
}
