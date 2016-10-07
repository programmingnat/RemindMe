package com.imaginat.remindme.geofencing;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.R;
import com.imaginat.remindme.tip_dialog.TipDialogFragment;

/**
 * The View class for GeoFenceControls. All the methods here alter the UI or show dialog
 */
public class GeoFenceFragment_Controls extends Fragment
        implements GeoFenceContract.ViewWControls,GeoFenceConfirmDialog.GeoFenceConfirmDialogListener {

    //Reference to the P (presenter) in MVP
    GeoFenceContract.Presenter mPresenter;


    //=================LIFE CYCLE METHODS==========================
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        //Inflate the layout
        View view = inflater.inflate(R.layout.geofence_fragment_controls, container, false);

        //get reference to buttons and set listeners
        Button createAlarmButton = (Button)view.findViewById(R.id.createGeoFence_Button);
        createAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.requestAddressForGeoFence();
            }
        });


        Button toggleGeoFence = (Button)view.findViewById(R.id.toggleGeoFence_Button);
        toggleGeoFence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.deactivateGeoFence();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences=
                getActivity().getSharedPreferences(GlobalConstants.PREFERENCES, Context.MODE_PRIVATE);
        boolean showIt = sharedPreferences.getInt(GlobalConstants.SHOW_VIEW_GEO_TOOLTIPS, 1)==1?true:false;
        if(showIt) {
           showToolTip();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        mPresenter.setUpForCurrentGeoAddressData(null);
    }

    @Override
    public void showToolTip() {
        TipDialogFragment tipDialog = new TipDialogFragment();
        tipDialog.setTipToDisplay(TipDialogFragment.GEOMAP_TIP);
        tipDialog.show(getActivity().getSupportFragmentManager(), "TIP");
    }

    @Override
    public void setPresenter(GeoFenceContract.Presenter presenter) {

        mPresenter=presenter;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }



    //==================METHODS THAT SHOW OR ALTER CURRENT GUI=========================

    /**
     * show the address box where user enters center of geofence
     */
    @Override
    public void showAddressDialog() {
        GeoFenceAddressDialogFragment newFragment = new GeoFenceAddressDialogFragment();
        newFragment.setPresenter(mPresenter);
        newFragment.show(getActivity().getSupportFragmentManager(), "options");
    }

    /**
     *  show the confirm dialog that would confirm that the user wanats to save the geo fence data
     */
    @Override
    public void showSaveFenceConfirmationDialog() {
        GeoFenceConfirmDialog newFragment = new GeoFenceConfirmDialog();
        newFragment.setListener(this);
        newFragment.show(getActivity().getSupportFragmentManager(), "CONFIRM");
    }

    @Override
    public void showSaveFenceConfirmation() {

    }

    @Override
    public void showUpdateFenceConfirmation() {

    }

    /**
     *
    *Set the actual text  (create vs update & cancel) of the controls based on whether or not a previous Fence exists
    */
    public void setButtonTexts(boolean update,boolean isOn){
        Button createGeoFenceButton = (Button)getView().findViewById(R.id.createGeoFence_Button);
        Button toggleGeoFenceButton = (Button)getView().findViewById(R.id.toggleGeoFence_Button);
        if(update && isOn){
            createGeoFenceButton.setText("UPDATE ALARM");
        }else{
            createGeoFenceButton.setText("CREATE ALARM");

        }


        if(isOn){
            toggleGeoFenceButton.setText("TURN ALARM OFF");
            toggleGeoFenceButton.setEnabled(true);
            toggleGeoFenceButton.setVisibility(View.VISIBLE);
        }else{
            toggleGeoFenceButton.setEnabled(false);
            toggleGeoFenceButton.setVisibility(View.GONE);
            //toggleGeoFenceButton.setText("TURN FENCE ON");
        }
    }

    /**
     *
     * Message shown to update user of the geofence (active/not active)
     */
    @Override
    public void showActiveStateChange(boolean newState) {
        String message = newState?getResources().getString(R.string.geo_state_active_msg):getResources().getString(R.string.geo_state_deactive_msg);
        Snackbar.make(getView(),message,Snackbar.LENGTH_LONG).show();

    }

    @Override
    public void showLocationDialogIfNecessary(){

        boolean gps_enabled=false,network_enabled=false;

        LocationManager lm= (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        try{
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){}
        try{
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){}

        Context context = getContext();
        if(!gps_enabled && !network_enabled){
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog = new AlertDialog.Builder(context);
            dialog.setMessage("GPS NETWORK NOT ENABLED");
            dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();

        }
    }
    //=====================METHODS IMPLEMENTED TO COMMUNICATE WITH DIALOG====================

    /**
     * When the dialog confirm button is pressed that geofence should be written
     */
    @Override
    public void geoFenceCreationConfirmed() {
        mPresenter.writeGeoFence();
    }

    /**
     * dialog cancel button pressed
     */
    @Override
    public void geoFenceCreationCancelled() {
        Toast.makeText(getContext(),"creation cancelled",Toast.LENGTH_LONG).show();
    }




}
