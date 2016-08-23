package com.imaginat.remindme.lists;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.imaginat.remindme.BaseActivity;
import com.imaginat.remindme.R;
import com.imaginat.remindme.data.ReminderList;

public class ReminderListMain extends BaseActivity<ReminderListsFragment>{

    private static final String TAG = ReminderList.class.getSimpleName();

    ReminderListsContract.Presenter mReminderListsPresenter=null;
    ReminderListsFragment mReminderListsFragment=null;

    public static final int REQUEST_READ_CALENDAR=200;
    public static final int REQUEST_WRITE_CALENDAR=201;

        @Override
        public int getLayoutID() {
            return R.layout.activity_main;
        }

        @Override
        public Object createPresenter(ReminderListsFragment fragment) {
            Log.d(TAG,"inside createPresenter,about to create  presenter");

            mReminderListsPresenter = new ReminderListsPresenter(fragment);
            if(fragment==null){
                Log.d(TAG,"fragment is null");
            }

            if(mReminderListsPresenter==null){
                Log.d(TAG,"mReminderListsPresenter is null");
            }

            fragment.setPresenter(mReminderListsPresenter);
            return fragment;
        }

        @Override
        public ReminderListsFragment getFragment() {
            if(mReminderListsFragment==null) {
                mReminderListsFragment = new ReminderListsFragment();
            }
            return mReminderListsFragment;
        }

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
            loadPermissions(Manifest.permission.READ_CALENDAR,REQUEST_READ_CALENDAR);
            loadPermissions(Manifest.permission.READ_CALENDAR,REQUEST_WRITE_CALENDAR);




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy called");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG,"onSaveInstanceState");

    }

    private void loadPermissions(String perm, int requestCode) {

        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                ActivityCompat.requestPermissions(this, new String[]{perm}, requestCode);
            }
        }
    }
}

