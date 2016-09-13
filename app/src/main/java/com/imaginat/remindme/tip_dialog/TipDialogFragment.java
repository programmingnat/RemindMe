package com.imaginat.remindme.tip_dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.R;

/**
 * Tool Tip Dialog
 */
public class TipDialogFragment extends DialogFragment {

    public static final int TASKS_TIPS=100;
    public static final int GEOMAP_TIP=200;

    //reference to the layoutID
    private int layoutID=R.layout.view_all_tasks_tip_dialog;

    //reference to the preference in shared Preferences
    private String sharedPrefKey=GlobalConstants.SHOW_VIEW_TASKS_TOOLTIPS;

    //the user choice
    private int mTipNo=TASKS_TIPS;

    //This allows the tooltip to change xml files depending on the setting
    public void setTipToDisplay(int tipNo){
        mTipNo=tipNo;
        switch(tipNo){
            case TASKS_TIPS:
                layoutID=R.layout.view_all_tasks_tip_dialog;
                sharedPrefKey=GlobalConstants.SHOW_VIEW_TASKS_TOOLTIPS;
                break;
            case GEOMAP_TIP:
                layoutID=R.layout.geofence_tip_dialog;
                sharedPrefKey=GlobalConstants.SHOW_VIEW_GEO_TOOLTIPS;
                break;
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //get the layout infloater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(layoutID,null);

        CheckBox gotItCheckBox = (CheckBox)view.findViewById(R.id.gotIt_checkbox);
        gotItCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox)view;
                SharedPreferences sharedPreferences= getActivity().getSharedPreferences(GlobalConstants.PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor ed = sharedPreferences.edit();
                int showIt =1;
                if(checkBox.isChecked()){
                    showIt=0;
                }else{
                    showIt=1;
                }
                ed.putInt(sharedPrefKey, showIt);
                ed.commit();
            }
        });
        //Inflate and set the layout for the dilaog
        builder.setView(view)
                .setPositiveButton(getResources().getString(R.string.tooltip_confirm),new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                });

        return builder.create();
    }


}
