package com.imaginat.remindme.addedittask;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.imaginat.remindme.R;

/**
 * Created by nat on 8/12/16.
 */
public class MoreOptionsDialogFragment extends DialogFragment {


    private String mListID,mReminderID;

    public String getListID() {
        return mListID;
    }

    public void setListID(String listID) {
        mListID = listID;
    }

    public String getReminderID() {
        return mReminderID;
    }

    public void setReminderID(String reminderID) {
        mReminderID = reminderID;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.more_options_dialog, null);
        builder.setView(view);
//        builder
//
//                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                });
        // Create the AlertDialog object and return it

        final Button deleteButton = (Button)view.findViewById(R.id.options_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mMoreOptionsDialogListener.onDeleteButton(mListID,mReminderID);
                dismiss();
            }
        });
        Button toAlarmsButton = (Button)view.findViewById(R.id.options_alarms);
        toAlarmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mMoreOptionsDialogListener.onMoreOptionsButton(mListID,mReminderID);
                dismiss();
            }
        });
        return builder.create();
    }




}