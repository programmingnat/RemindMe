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
 * Created by nat on 8/12/16.
 */
public abstract class BaseActivity<T extends Fragment> extends AppCompatActivity
        implements IChangeToolbar {

    private final String TAG = BaseActivity.class.getSimpleName();
    private int TOOLBAR_ICON_INSTRUCTIONS=-1;


    abstract public int getLayoutID();
    abstract public Object createPresenter(T fragment);
    abstract public T getFragment();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());

        //set up the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        T fragment = getFragment();
        //Fragment (View)
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.my_frame, fragment);
        fragmentTransaction.commit();

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
        TOOLBAR_ICON_INSTRUCTIONS=instructions;
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
        if(TOOLBAR_ICON_INSTRUCTIONS==100) {
            MenuItem menuItem = menu.findItem(R.id.search);
            menuItem.setVisible(false);
            menuItem=menu.findItem(R.id.deleteList);
            menuItem.setVisible(true);
            menuItem=menu.findItem(R.id.shareListNFC);
            menuItem.setVisible(true);
            menuItem=menu.findItem(R.id.editListInfo);
            menuItem.setVisible(true);

        }else if(TOOLBAR_ICON_INSTRUCTIONS==200){
            MenuItem menuItem = menu.findItem(R.id.search);
            menuItem.setVisible(true);
            menuItem=menu.findItem(R.id.deleteList);
            menuItem.setVisible(false);
            menuItem=menu.findItem(R.id.shareListNFC);
            menuItem.setVisible(false);
            menuItem=menu.findItem(R.id.editListInfo);
            menuItem.setVisible(false);
        }
    }
}
