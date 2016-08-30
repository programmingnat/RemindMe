package com.imaginat.remindme.geofencing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.imaginat.remindme.R;

/**
 * Created by nat on 8/30/16.
 */
public class GeoFenceFragment extends Fragment implements GeoFenceContract.View,OnMapReadyCallback {

    private  final static String TAG=GeoFenceFragment.class.getSimpleName();
    MapView mMapView;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        try {
            rootView = inflater.inflate(R.layout.geofence_fragment, container, false);
            MapsInitializer.initialize(this.getActivity());
            mMapView = (MapView) rootView.findViewById(R.id.map);
            mMapView.onCreate(savedInstanceState);
            mMapView.getMapAsync(this);
        }catch(InflateException ie){
            Log.e(TAG,"Inflation exception");
        }
       /* Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment)fragment;
        mapFragment.getMapAsync(this);*/
        return rootView;




        /*
        mMapView=(SupportMapFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        try{
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }catch(Exception e){
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);*/
        //return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
//        SupportMapFragment mapFragment = (SupportMapFragment)fragment;
//        mapFragment.getMapAsync(this);
    }

    @Override
    public void setPresenter(GeoFenceContract.Presenter presenter) {

    }

    @Override
    public void onMapReady(GoogleMap map) throws SecurityException{
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(47.17, 27.5699), 16));
        map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_media_play)).anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(47.17, 27.5699))); //Iasi, Romania
        map.setMyLocationEnabled(true);
    }
}
