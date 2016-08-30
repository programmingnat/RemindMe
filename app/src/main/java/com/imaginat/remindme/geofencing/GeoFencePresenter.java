package com.imaginat.remindme.geofencing;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.data.source.local.DBSchema;

import java.util.HashMap;

/**
 * Created by nat on 8/30/16.
 */
public class GeoFencePresenter implements GeoFenceContract.Presenter {

    private static final String TAG= GeoFencePresenter.class.getSimpleName();


    @Override
    public void onReceiveCoordinatesResult(int resultCode, Bundle resultData) {

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
    public void start() {

    }
}
