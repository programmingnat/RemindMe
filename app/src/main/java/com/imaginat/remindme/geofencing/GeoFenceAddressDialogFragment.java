package com.imaginat.remindme.geofencing;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.imaginat.remindme.R;

/**
 * Created by nat on 9/1/16.
 */
public class GeoFenceAddressDialogFragment extends DialogFragment {

    GeoFenceContract.Presenter mPresenter;

    void setPresenter(GeoFenceContract.Presenter presenter){
        mPresenter = presenter;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.geofence_enter_address_dialog, null);
        builder.setView(view);


        Button enterButton = (Button)view.findViewById(R.id.enter_dialogButton);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View p = (View)view.getParent().getParent();
                EditText streetAddress_edit = (EditText)p.findViewById(R.id.streetAddress_editText);
                EditText cityAddress_edit = (EditText)p.findViewById(R.id.cityAddress_editText);
                EditText stateAddress_edit = (EditText)p.findViewById(R.id.stateAddress_editText);
                EditText zipAddress_edit = (EditText)p.findViewById(R.id.zipAddress_editText);
                String streetAddress=null,cityAddress=null,stateAddress=null,zipAddress=null;
                if(streetAddress_edit.getText().toString().length()==0){
                    streetAddress_edit.setError("Please enter an address here");
                    return;
                }else{
                    streetAddress = streetAddress_edit.getText().toString();
                }
                if(cityAddress_edit.getText().toString().isEmpty()){
                    cityAddress_edit.setError("Please enter city");
                    return;
                }else{
                    cityAddress = cityAddress_edit.getText().toString();
                }
                if(stateAddress_edit.getText().toString().isEmpty()){
                    stateAddress_edit.setError("Please enter state");
                    return;
                }else{
                    stateAddress = stateAddress_edit.getText().toString();
                }
                if(zipAddress_edit.getText().toString().isEmpty()){
                    zipAddress_edit.setError("Please enter zip");
                    return;
                }else{
                    zipAddress = zipAddress_edit.getText().toString();
                }

                mPresenter.processStreetAddress(streetAddress,cityAddress,stateAddress,zipAddress);
                dismiss();
            }
        });
        Button cancelButton = (Button)view.findViewById(R.id.cancel_dialogButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });




        return builder.create();
    }
}
