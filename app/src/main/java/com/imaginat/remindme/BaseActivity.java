package com.imaginat.remindme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * This is extended by all activities in the app
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
            MenuItem menuItem = menu.findItem(R.id.search);
            menuItem.setVisible(false);
            menuItem=menu.findItem(R.id.deleteList);
            menuItem.setVisible(true);
            //menuItem=menu.findItem(R.id.shareListNFC);
            //menuItem.setVisible(true);
            menuItem=menu.findItem(R.id.editListInfo);
            menuItem.setVisible(true);

        }else if(mToolbarIconInstructions ==GlobalConstants.HIDE_OPTIONS){
            MenuItem menuItem = menu.findItem(R.id.search);
            menuItem.setVisible(true);
            menuItem=menu.findItem(R.id.deleteList);
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
        RemindMeApplication remindMeApp = (RemindMeApplication)getApplicationContext();
        switch (item.getItemId()) {
            case R.id.testStartService:
                Log.d(TAG, "startService selected");

                remindMeApp.startServiceAsNeeded();
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
                //RemindMeApplication remindMeApp = (RemindMeApplication)getApplicationContext();

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
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
