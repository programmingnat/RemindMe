package com.imaginat.remindme.geofencing;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.imaginat.remindme.GlobalConstants;

/**
 * Created by nat on 8/30/16.
 */
@SuppressLint("ParcelCreator")
public class CoordinatesResultReceiver extends ResultReceiver {
    public interface ICoordinateReceiver {
        public void onReceiveCoordinatesResult(int resultCode, Bundle resultData);

    }
    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    private static final String TAG = "CoordinatesResultRec";
    private ICoordinateReceiver mIReceiver;


    public CoordinatesResultReceiver(Handler handler) {
        super(handler);
    }


    public void setResult(ICoordinateReceiver receiver){
        mIReceiver = receiver;
    }
    /**
     * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
     */
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        Log.d(TAG,"coordresulterrecevier onRsceiResults");
        // Display the address string or an error message sent from the intent service.
        Location location = resultData.getParcelable(GlobalConstants.RESULT_DATA_KEY);

        // Show a toast message if an address was found.
        if (resultCode == GlobalConstants.SUCCESS_RESULT) {

            Log.d(TAG, "onReceiveResult the coordinates are is " + location.getLongitude()+" "+location.getLatitude());
            Log.d(TAG,"onReceiveResult the tag should be " +resultData.getString(GlobalConstants.ALARM_TAG));
        } else {
            Log.d(TAG, "onReceiveResult, but is not success");
        }
        mIReceiver.onReceiveCoordinatesResult(resultCode,resultData);




    }
}
