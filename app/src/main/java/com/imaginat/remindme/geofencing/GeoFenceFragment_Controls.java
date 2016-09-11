package com.imaginat.remindme.geofencing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.imaginat.remindme.R;

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        mPresenter.setUpForCurrentGeoAddressData(null);
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
            createGeoFenceButton.setText("UPDATE");
        }else{
            createGeoFenceButton.setText("CREATE");

        }


        if(isOn){
            toggleGeoFenceButton.setText("TURN OFF FENCE");
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
