package com.imaginat.remindme.geofencing;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.imaginat.remindme.R;

/**
 * Created by nat on 9/3/16.
 */
public class GeoFenceConfirmDialog extends DialogFragment {

    public interface GeoFenceConfirmDialogListener{
        void geoFenceCreationConfirmed();
        void geoFenceCreationCancelled();
    }

    GeoFenceConfirmDialogListener mListener=null;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirmGeoFence)
                .setPositiveButton(R.string.create_geo_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(mListener!=null){
                            mListener.geoFenceCreationConfirmed();
                        }
                        dismiss();

                    }
                })
                .setNegativeButton(R.string.cancel_geo_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(mListener!=null){
                            mListener.geoFenceCreationCancelled();
                        }
                        dismiss();

                    }
                });
        return builder.create();
    }

    public void setListener(GeoFenceConfirmDialogListener listener){
        mListener=listener;
    }
}
