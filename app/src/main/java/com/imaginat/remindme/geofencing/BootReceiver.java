package com.imaginat.remindme.geofencing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.lists.ReminderListMain;

/**
 * Created by nat on 9/19/16.
 */
public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = BootReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG,"onReceive of boot receiver called");

        //start the Service
        Intent startServiceIntent = new Intent(context, LocationUpdateService.class);
        context.startService(startServiceIntent);

        //start the app
        Intent i = new Intent(context, ReminderListMain.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(GlobalConstants.LAUNCH_SOURCE,"receiver");
        context.startActivity(i);
    }
}
