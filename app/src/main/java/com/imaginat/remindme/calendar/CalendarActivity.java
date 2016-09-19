package com.imaginat.remindme.calendar;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.imaginat.remindme.BaseActivity;
import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * The Activity encompases the MVP pattern. The activity class creates the presenter,views and ensure that each has the
 * appropriate references. This class is  specifically  used to add an event on the local calendar
 */
public class CalendarActivity extends BaseActivity<CalendarFragment>
        implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = CalendarActivity.class.getSimpleName();
    GoogleAccountCredential mCredential;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};

    private static final String PREF_ACCOUNT_NAME = "accountName";


    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    /**
     * returns the layout for this activity
     */
    @Override
    public int getLayoutID() {
        return R.layout.activity_calendar;
    }

    @Override
    public String getAssociatedFragmentTag() {
        return "CalendarFragment";
    }

    /**
     * Creates the Presenter (MVP) an assigns a reference to the View
     */
    @Override
    public Object createPresenter(CalendarFragment fragment) {

        //This activity will be called by the user from the task/reminder list
        //for a specific reminder that is identified via list and task IDs
        //that combo is passed in with the intent
        Intent theCallingIntent = getIntent();
        String listID=theCallingIntent.getStringExtra(GlobalConstants.CURRENT_LIST_ID);
        String reminderID=theCallingIntent.getStringExtra(GlobalConstants.CURRENT_TASK_ID);

        //Pass the unique combination of listID and reminder to the calendar
        CalendarPresenter presenter = new CalendarPresenter(listID,reminderID,fragment);
        fragment.setPresenter(presenter);
        return presenter;
    }

    /**
     *
     */
    @Override
    public CalendarFragment getFragment() {
        return new CalendarFragment();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        loadPermissions(Manifest.permission.GET_ACCOUNTS,400);
        getResultsFromApi();


    }

    private void getResultsFromApi(){
        if(!isGooglePlayServiceAvailable()){
            acquireGooglePlayServices();
        }else if(mCredential.getSelectedAccountName()==null){
            chooseAccount();
        }else if(! isDeviceOnline()){
            Toast.makeText(this,"No network connections avail",Toast.LENGTH_SHORT).show();
        }else{
            new MakeRequestTask(mCredential).execute();
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = "npanchee@gmail.com";//getPreferences(Context.MODE_PRIVATE)
                    //.getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(

                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }
    private boolean isDeviceOnline(){
        ConnectivityManager connMgr=
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo !=null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services is installed
     * @return
     */
    private boolean isGooglePlayServiceAvailable(){
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode=
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode== ConnectionResult.SUCCESS;

    }

    /**
     * Attempt to get out of date, invalid or disabled Google Play Service
     */
    private void acquireGooglePlayServices(){
        GoogleApiAvailability apiAvailability=
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode=
                apiAvailability.isGooglePlayServicesAvailable(this);
        if(apiAvailability.isUserResolvableError(connectionStatusCode)){
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                CalendarActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
            Log.d(TAG,"permissionGranted");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG,"permission denited");

    }

    private void loadPermissions(String perm, int requestCode) {

        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                ActivityCompat.requestPermissions(this, new String[]{perm}, requestCode);
            }
        }
    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("RemindMe")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            List<String> eventStrings = new ArrayList<String>();
            Events events = mService.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();

            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    start = event.getStart().getDate();
                }
                eventStrings.add(
                        String.format("%s (%s)", event.getSummary(), start));
            }
            Log.d(TAG,"event string "+eventStrings);
            return eventStrings;
        }


        @Override
        protected void onPreExecute() {
            Log.d(TAG,"task onPreExecute");
            //mOutputText.setText("");
            //mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            //mProgress.hide();
            if (output == null || output.size() == 0) {
                Log.d(TAG,"task on post Execute is null");
                //mOutputText.setText("No results returned.");
            } else {
                Log.d(TAG,"task onPostExecute "+output);
                //output.add(0, "Data retrieved using the Google Calendar API:");
                //mOutputText.setText(TextUtils.join("\n", output));
            }
        }

        @Override
        protected void onCancelled() {
            //mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            CalendarActivity.REQUEST_AUTHORIZATION);
                } else {
                    Log.d(TAG,"Error message "+mLastError.getMessage());
                   // mOutputText.setText("The following error occurred:\n"
                   //         + mLastError.getMessage());
                }
            } else {
                //mOutputText.setText("Request cancelled.");
                Log.d(TAG,"Request Cancelled");
            }
        }


    }
}
