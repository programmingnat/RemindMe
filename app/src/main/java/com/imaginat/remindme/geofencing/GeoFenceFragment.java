package com.imaginat.remindme.geofencing;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.imaginat.remindme.R;
import com.imaginat.remindme.RemindMeApplication;

/**
 * Created by nat on 8/30/16.
 */
public class GeoFenceFragment extends Fragment implements GeoFenceContract.View,OnMapReadyCallback {

    private GeoFenceContract.Presenter mPresenter;
    private  final static String TAG=GeoFenceFragment.class.getSimpleName();

    GoogleMap mGoogleMap;
    private View rootView;
    private SupportMapFragment fragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        try {
            rootView = inflater.inflate(R.layout.geofence_fragment, container, false);

        }catch(InflateException ie){
            Log.e(TAG,"Inflation exception");
        }

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        if(mPresenter!=null) {
            Log.d(TAG,"onResume calls mPresenter.start");
            mPresenter.start();
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, fragment).commit();
        }
        fragment.getMapAsync(this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void setPresenter(GeoFenceContract.Presenter presenter) {
        mPresenter=presenter;
    }


    /**
     *
     * initiated w call to getMapAsync, and called when map is ready
     */
    @Override
    public void onMapReady(GoogleMap map) throws SecurityException{

        //store reference to the map (for later use)
        mGoogleMap = map;

        //Get ref to location service for coordinates
        RemindMeApplication remindMeApplication = (RemindMeApplication) this.getActivity().getApplicationContext();
        LocationUpdateService locationUpdateService = remindMeApplication.getServiceReference();
        Location location = locationUpdateService.getCurrentLocation();

        if(location==null){
            return;
        }

        //Move camera and add marker
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude() ,location.getLongitude() ), 16));
        map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_media_play)).anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(location.getLatitude() , location.getLongitude())));


        map.setMyLocationEnabled(true);

        if(mPresenter!=null){
            mPresenter.start();
        }


        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //setAddressMarker(latLng);
            }
        });


    }



    /**
     *
     * Set the map to center to user's current location (if no geofence is set)
     */
    public void setLocation(LatLng ll){

        if(mGoogleMap==null){
            Log.d(TAG,"inside set AdressMarker, goni to return null");
            return;
        }

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                ll, 16));
    }

    /**
     *
     * set the marker of the geofence (when it is set and active)
     */
    @Override
    public void setAddressMarker(double latitude, double longitude) throws SecurityException {
        LatLng ll = new LatLng(latitude, longitude);
        setAddressMarker(ll);
    }

    public void setAddressMarker(LatLng ll){

        if(mGoogleMap==null){
            Log.d(TAG,"inside set AdressMarker, goni to return null");
            return;
        }

        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(ll).title("MARK"));
        marker.setTag(0);


        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                ll, 16));

        CircleOptions circleOptions = new CircleOptions()
                .center(marker.getPosition())
                .strokeColor((Color.argb(50,70,70,70)))
                .fillColor(Color.argb(100,150,150,150))
                .radius(100);
        mGoogleMap.addCircle(circleOptions);

    }


}
