package com.imaginat.remindme.geofencing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.imaginat.remindme.R;

/**
 * Created by nat on 8/31/16.
 */
public class GeoFenceFragment_Controls extends Fragment
        implements GeoFenceContract.ViewWControls,GeoFenceConfirmDialog.GeoFenceConfirmDialogListener {

    GeoFenceContract.Presenter mPresenter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.geofence_fragment_controls, container, false);

        Button createAlarmButton = (Button)view.findViewById(R.id.createGeoFence_Button);
        createAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.saveGeoFenceCoordinates();
            }
        });

        Button enterAddressButton = (Button)view.findViewById(R.id.enterGeoFenceAddress_Button);
        enterAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.requestAddressForGeoFence();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.passInitialInfo();
    }

    @Override
    public void setPresenter(GeoFenceContract.Presenter presenter) {
        mPresenter=presenter;
    }

    @Override
    public void showAddressDialog() {
        GeoFenceAddressDialogFragment newFragment = new GeoFenceAddressDialogFragment();
        newFragment.setPresenter(mPresenter);
        newFragment.show(getActivity().getSupportFragmentManager(), "options");
    }

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

    @Override
    public void geoFenceCreationConfirmed() {
        mPresenter.writeGeoFence();
    }

    @Override
    public void geoFenceCreationCancelled() {
        Toast.makeText(getContext(),"creation cancelled",Toast.LENGTH_LONG).show();
    }


    public void setButtonTexts(boolean update,boolean isOn){
        Button createGeoFenceButton = (Button)getView().findViewById(R.id.createGeoFence_Button);
        Button toggleGeoFenceButton = (Button)getView().findViewById(R.id.toggleGeoFence_Button);
        if(update){
            createGeoFenceButton.setText("update fence");
        }else{
            createGeoFenceButton.setText("create fence");
        }


        if(isOn){
            toggleGeoFenceButton.setText("OFF");
        }else{
            toggleGeoFenceButton.setText("ON");
        }
    }
}
