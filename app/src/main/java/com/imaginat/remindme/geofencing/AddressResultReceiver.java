package com.imaginat.remindme.geofencing;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.imaginat.remindme.GlobalConstants;

/**
 * Created by nat on 10/13/16.
 */

@SuppressLint("ParcelCreator")
public class AddressResultReceiver extends ResultReceiver {
    private static final String TAG = ResultReceiver.class.getSimpleName();
    private IAddressReceiver mIAddressReceiver;

    public interface IAddressReceiver {
        void onReceiveAddressResult(int resultCode, Bundle resultData);

    }

    public AddressResultReceiver(Handler handler) {
        super(handler);
    }

    public void setResultReceiver(IAddressReceiver addressReceiver){
        mIAddressReceiver=addressReceiver;
    }
    /**
     *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
     */
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {



        // Display the address string or an error message sent from the intent service.
        String addressOutput = resultData.getString(GlobalConstants.RESULT_DATA_KEY);

        // Show a toast message if an address was found.
        if (resultCode == GlobalConstants.SUCCESS_RESULT) {
            Log.d(TAG,"onReceiveResult the address is "+addressOutput);
        }else{
            Log.d(TAG,"onReceiveResult, but is not success");
        }


        mIAddressReceiver.onReceiveAddressResult(resultCode,resultData);
    }
}