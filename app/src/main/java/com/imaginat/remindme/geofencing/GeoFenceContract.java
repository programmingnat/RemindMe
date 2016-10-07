package com.imaginat.remindme.geofencing;

import com.google.android.gms.maps.model.LatLng;
import com.imaginat.remindme.BasePresenter;
import com.imaginat.remindme.BaseView;
import com.imaginat.remindme.data.GeoFenceAlarmData;

/**
 * Created by nat on 8/30/16.
 */
public class GeoFenceContract {


    interface View extends BaseView<Presenter>{
        void setAddressMarker(double lat,double longitude);
        void setLocation(LatLng ll);
    }

    interface ViewWControls extends BaseView<Presenter>{
        void showAddressDialog();
        void showSaveFenceConfirmationDialog();
        void setButtonTexts(boolean isNew,boolean isON);
        void showActiveStateChange(boolean newState);
        void showToolTip();
        void showLocationDialogIfNecessary();
        void showSaveFenceConfirmation();
        void showUpdateFenceConfirmation();
    }


    interface Presenter extends BasePresenter, CoordinatesResultReceiver.ICoordinateReceiver{

        void setUpForCurrentGeoAddressData(GeoFenceAlarmData alarmData);
        void processStreetAddress(String address,String city,String state, String zip);
        void requestAddressForGeoFence();
        void saveGeoFenceCoordinates();
        void deactivateGeoFence();
        GeoFenceAlarmData getLatestGeoFenceData();
        void writeGeoFence();

    }
}
