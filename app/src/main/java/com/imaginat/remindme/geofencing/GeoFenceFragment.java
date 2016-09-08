package com.imaginat.remindme.geofencing;

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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.imaginat.remindme.R;

/**
 * Created by nat on 8/30/16.
 */
public class GeoFenceFragment extends Fragment implements GeoFenceContract.View,OnMapReadyCallback {

    private GeoFenceContract.Presenter mPresenter;
    private  final static String TAG=GeoFenceFragment.class.getSimpleName();
    MapView mMapView;
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
        if(mPresenter!=null) {
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
    public void setPresenter(GeoFenceContract.Presenter presenter) {
        mPresenter=presenter;
        //
    }


    @Override
    public void onMapReady(GoogleMap map) throws SecurityException{


        mGoogleMap = map;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(40.961761 ,-73.815249 ), 16));
        map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_media_play)).anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(40.961761 , -73.815249 )));
        map.setMyLocationEnabled(true);

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                setAddressMarker(latLng);
            }
        });


    }


    @Override
    public void setAddressMarker(double latitude, double longitude) throws SecurityException {
        LatLng ll = new LatLng(latitude, longitude);
        setAddressMarker(ll);
    }
    public void setAddressMarker(LatLng ll){

        if(mGoogleMap==null){
            return;
        }

        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(ll).title("MARK"));
        marker.setTag(0);


        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                ll, 16));

    }


}
