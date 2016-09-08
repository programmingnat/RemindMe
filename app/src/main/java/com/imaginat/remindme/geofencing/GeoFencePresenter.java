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
 * Created by nat on 8/30/16.
 */
public class GeoFencePresenter implements GeoFenceContract.Presenter {

    private static final String TAG= GeoFencePresenter.class.getSimpleName();

    public static final String GEOFENCE = "GEO";

    private GeoFenceContract.View mView;
    private GeoFenceContract.ViewWControls mViewWControls;
    private GeoFenceAlarmData mGeoFenceAlarmData,mNewGeoFenceData;
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
    public void passInitialInfo() {
       // mViewWControls.setButtonTexts(false,true);
        if(mGeoFenceAlarmData!=null) {
            mView.setAddressMarker(mGeoFenceAlarmData.getLatitude(), mGeoFenceAlarmData.getLongitude());
            mViewWControls.setButtonTexts(mGeoFenceAlarmData==null,mGeoFenceAlarmData.isActive());
        }else{
            mViewWControls.setButtonTexts(false,false);
        }

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

            if(mNewGeoFenceData==null){
                mNewGeoFenceData = new GeoFenceAlarmData();
            }
            mNewGeoFenceData.setLatitude(lastLocation.getLatitude());
            mNewGeoFenceData.setLongitude(lastLocation.getLongitude());

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

        if(mNewGeoFenceData==null){
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
        String address=streetAddress+" "+cityAddress+","+stateAddress+" "+zipAddress;
        Context c=  ((Fragment)mView).getContext();
        Intent intent = new Intent(c,FetchCoordinatesIntentService.class);
        intent.putExtra(GlobalConstants.RECEIVER,mReceiver);
        intent.putExtra(GlobalConstants.LOCATION_DATA_EXTRA,address);
        intent.putExtra(GlobalConstants.ALARM_TAG,getAlarmTag());
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
        ListsLocalDataSource llds = ListsLocalDataSource.getInstance(((Fragment) mViewWControls).getContext());
        String alarmID = null;

        if(mNewGeoFenceData.getAlarmID()!=null){
            alarmID=mNewGeoFenceData.getAlarmID();
        }else if(mGeoFenceAlarmData!=null){
            alarmID=mGeoFenceAlarmData.getAlarmID();
        }
        llds.saveGeoFenceAlarm(alarmID,mNewGeoFenceData.getReminderID(),mNewGeoFenceData.getAsContentValues());

        //Get reference to the item (in order to get the text & any other info)
        //ToDoListItem toDoItem = listItemManager.getSingleListItem(listID, reminderID);

        //now get refence
        RemindMeApplication remindMeApplication = (RemindMeApplication)((Fragment)mView).getActivity().getApplicationContext();
        remindMeApplication.requestStartOfLocationUpdateService();
        LocationUpdateService locationUpdateService = remindMeApplication.getServiceReference();
        locationUpdateService.addToGeoFenceList("TEXT TO BE REPLACED",mNewGeoFenceData.getLatitude(),mNewGeoFenceData.getLongitude());

        llds.getAllActiveAlarms()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<GeoFenceAlarmData>>() {
                    @Override
                    public void call(List<GeoFenceAlarmData> geoFenceAlarmDataList) {


                        if(geoFenceAlarmDataList==null){
                            Log.d(TAG,"Inside call() geoFenceAlarmDataList is null");
                        }else{
                            Log.d(TAG,"Inside call() found "+geoFenceAlarmDataList.size()+" results");
                        }

                        RemindMeApplication remindMeApplication = (RemindMeApplication)((Fragment)mView).getActivity().getApplicationContext();
                        LocationUpdateService locationUpdateService = remindMeApplication.getServiceReference();
                        locationUpdateService.populateGeofenceList(geoFenceAlarmDataList);
                        locationUpdateService.addGeofences(createAlarmTag());

                    }

                });



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
