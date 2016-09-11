package com.imaginat.remindme.geofencing;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.imaginat.remindme.GlobalConstants;

/**
 * Create a new ResultReceive to receive results.  The
 * onReceiveResult method will be called from the thread running
 * handler if given, or from an arbitrary thread if null.
 *
 */
@SuppressLint("ParcelCreator")
public class CoordinatesResultReceiver extends ResultReceiver {

    /*
    * Interface implemented by client who wants to receive the coordinates
     */
    public interface ICoordinateReceiver {
        void onReceiveCoordinatesResult(int resultCode, Bundle resultData);

    }

    //TAG is for debugging purposes
    private static final String TAG = CoordinatesResultReceiver.class.getSimpleName();

    //A reference to the client who wants to receive the coordinates
    private ICoordinateReceiver mIReceiver;


    public CoordinatesResultReceiver(Handler handler) {
        super(handler);
    }


    public void setResult(ICoordinateReceiver receiver){
        mIReceiver = receiver;
    }

    /**
     * Receives data sent from FetchAddressIntentService and passes it on to the client
     */
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        // Show a  message if an address was found.
        if (resultCode != GlobalConstants.SUCCESS_RESULT) {
            Log.e(TAG, "onReceiveResult, but is not success");
        }

        //Send the information over
        mIReceiver.onReceiveCoordinatesResult(resultCode,resultData);




    }
}
