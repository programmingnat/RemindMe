package com.imaginat.remindme.viewtasks;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.imaginat.remindme.R;

/**
 * Dialog that appears before deletion of task
 */
public class ConfirmDeleteDialog extends DialogFragment {


    /**
     * interface used to pass info back to host
     */
    public interface IConfirmDeleteDialogListener {
        public void onDialogDeleteIt(String listID, String reminderID);
    }

    private String mListID, mReminderID;
    private IConfirmDeleteDialogListener mIConfirmDeleteDialogListener;


    public void setData(String listID, String reminderID, IConfirmDeleteDialogListener listener) {
        mListID = listID;
        mReminderID=reminderID;
        mIConfirmDeleteDialogListener=listener;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirm_task_delete)
                .setPositiveButton(R.string.deleteIt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mIConfirmDeleteDialogListener != null) {
                            mIConfirmDeleteDialogListener.onDialogDeleteIt(mListID, mReminderID);
                        }
                    }
                })
                .setNegativeButton(R.string.keepIt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                });
        return builder.create();
    }
}
