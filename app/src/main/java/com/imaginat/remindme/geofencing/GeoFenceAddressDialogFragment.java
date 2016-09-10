package com.imaginat.remindme.geofencing;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.imaginat.remindme.R;
import com.imaginat.remindme.data.GeoFenceAlarmData;

/**
 * Created by nat on 9/1/16.
 */
public class GeoFenceAddressDialogFragment extends DialogFragment {

    GeoFenceContract.Presenter mPresenter;
    EditText mStreetAddress_edit;
    EditText mCityAddress_edit;
    EditText mStateAddress_edit;
    EditText mZipAddress_edit;


    void setPresenter(GeoFenceContract.Presenter presenter) {
        mPresenter = presenter;
    }



    @Override
    public void onResume() {
        super.onResume();
        //Once view is loaded, see if can prepopulate fields (if applicable)
        GeoFenceAlarmData geoFenceAlarmData=mPresenter.getLatestGeoFenceData();
        if(geoFenceAlarmData!=null) {
            mStreetAddress_edit.setText(geoFenceAlarmData.getStreet());
            mCityAddress_edit.setText(geoFenceAlarmData.getCity());
            mStateAddress_edit.setText(geoFenceAlarmData.getState());
            mZipAddress_edit.setText(geoFenceAlarmData.getZipcode());
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.geofence_enter_address_dialog, null);
        builder.setView(view);

        mStreetAddress_edit = (EditText) view.findViewById(R.id.streetAddress_editText);
        mCityAddress_edit = (EditText) view.findViewById(R.id.cityAddress_editText);
        mStateAddress_edit = (EditText) view.findViewById(R.id.stateAddress_editText);
        mZipAddress_edit = (EditText) view.findViewById(R.id.zipAddress_editText);

        Button enterButton = (Button) view.findViewById(R.id.enter_dialogButton);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View p = (View) view.getParent().getParent();

                String streetAddress = null, cityAddress = null, stateAddress = null, zipAddress = null;
                if (mStreetAddress_edit.getText().toString().length() == 0) {
                    mStreetAddress_edit.setError("Please enter an address here");
                    return;
                } else {
                    streetAddress = mStreetAddress_edit.getText().toString();
                }
                if (mCityAddress_edit.getText().toString().isEmpty()) {
                    mCityAddress_edit.setError("Please enter city");
                    return;
                } else {
                    cityAddress = mCityAddress_edit.getText().toString();
                }
                if (mStateAddress_edit.getText().toString().isEmpty()) {
                    mStateAddress_edit.setError("Please enter state");
                    return;
                } else {
                    stateAddress = mStateAddress_edit.getText().toString();
                }
                if (mZipAddress_edit.getText().toString().isEmpty()) {
                    mZipAddress_edit.setError("Please enter zip");
                    return;
                } else {
                    zipAddress = mZipAddress_edit.getText().toString();
                }

                mPresenter.processStreetAddress(streetAddress, cityAddress, stateAddress, zipAddress);
                dismiss();
            }
        });
        Button cancelButton = (Button) view.findViewById(R.id.cancel_dialogButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        return builder.create();
    }
}
