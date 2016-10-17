package com.imaginat.remindme;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

/**
 * This is extended by all activities in the app
 * Trying to implement the MVP based on Google's best practices MVP example
 * Each section has an activity that links instance of Model, View, Presenter together
    The Presenter can talk to the Model and to the View, but they(Model and View) cannot talk to each other
 */
public abstract class BaseActivity<T extends Fragment> extends AppCompatActivity
        implements IChangeToolbar {

    private final String TAG = BaseActivity.class.getSimpleName();
    private int mToolbarIconInstructions =-1;


    abstract public int getLayoutID();
    abstract public Object createPresenter(T fragment);
    abstract public T getFragment();
    abstract public String getAssociatedFragmentTag();





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(getLayoutID());

        //set up the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);




        //When the app is rotated, we dont want re-instantiate and readd another fragment
        //instead, we only wna
        T fragment=null;
        if(savedInstanceState==null){
            //Get the fragment
             fragment= getFragment();

            //Fragment (View)
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.my_frame, fragment,getAssociatedFragmentTag());
            fragmentTransaction.commit();
        }else{
            fragment = (T)getSupportFragmentManager().findFragmentByTag(getAssociatedFragmentTag());
        }





        //Set up the presenter
        createPresenter(fragment);


        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        Drawable backArrow;
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            // Do something for lollipop and above versions
            backArrow= ResourcesCompat.getDrawable(getResources(), R.drawable.abc_ic_ab_back_material, null);

        } else{
            // do something for phones running an SDK before lollipop
            backArrow= getResources().getDrawable(R.drawable.abc_ic_ab_back_material);

        }


       // backArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);


        RemindMeApplication remindMeApp = (RemindMeApplication)getApplicationContext();
        remindMeApp.startServiceAsNeeded(true);

    }
    @Override
    public void onUpdateTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public void swapIcons(int instructions) {
        Log.d(TAG,"inside swapIcons");
        mToolbarIconInstructions =instructions;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lists_of_lists_dropdown, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateIcons(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    public void updateIcons(Menu menu){
        Log.d(TAG,"Inside updateIcons");
        if(mToolbarIconInstructions ==GlobalConstants.SHOW_OPTIONS) {
            //MenuItem menuItem = menu.findItem(R.id.search);
            //menuItem.setVisible(false);
            MenuItem menuItem=menu.findItem(R.id.deleteList);
            menuItem.setVisible(true);
            //menuItem=menu.findItem(R.id.shareListNFC);
            //menuItem.setVisible(true);
            menuItem=menu.findItem(R.id.editListInfo);
            menuItem.setVisible(true);

        }else if(mToolbarIconInstructions ==GlobalConstants.HIDE_OPTIONS){
            //MenuItem menuItem = menu.findItem(R.id.search);
            //menuItem.setVisible(true);
            MenuItem menuItem=menu.findItem(R.id.deleteList);
            menuItem.setVisible(false);
            //menuItem=menu.findItem(R.id.shareListNFC);
            //menuItem.setVisible(false);
            menuItem=menu.findItem(R.id.editListInfo);
            menuItem.setVisible(false);
        }
    }

    public void processOptionItemSelected(int id){

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        //LocationUpdateServiceManager remindMeApp = LocationUpdateServiceManager.getInstance(getApplicationContext());
        RemindMeApplication remindMeApp = (RemindMeApplication)getApplicationContext();

        switch (item.getItemId()) {
            case R.id.testStartService:
                Log.d(TAG, "startService selected");

                remindMeApp.startServiceAsNeeded(true);
                //Intent startServiceIntent = new Intent(BaseActivity.this, LocationUpdateService.class);
                //startService(startServiceIntent);
                return true;
            case R.id.testStopService:
                Log.d(TAG, "stopService selected");

                remindMeApp.requestStopOfLocationUpdateService();
                //LocationUpdateService lus=remindMeApp.getServiceReference();

                //lus.stopIt();
                //Intent stopServiceIntent = new Intent(BaseActivity.this, LocationUpdateService.class);
                //stopService(stopServiceIntent);
                return true;
            case R.id.testIsServiceRunning:


                remindMeApp.isServiceRunning();
                return true;
            case R.id.deleteList:
                processOptionItemSelected(R.id.deleteList);
                return true;
            case R.id.editListInfo:
                processOptionItemSelected(R.id.editListInfo);
                return true;
            case R.id.shareListNFC:
                processOptionItemSelected(R.id.shareListNFC);
                return true;
            case R.id.unhide_toolTips:
                SharedPreferences sharedPreferences= getSharedPreferences(GlobalConstants.PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor ed = sharedPreferences.edit();
                ed.putInt(GlobalConstants.SHOW_VIEW_GEO_TOOLTIPS, 1);
                ed.putInt(GlobalConstants.SHOW_VIEW_TASKS_TOOLTIPS,1);
                ed.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
