package com.imaginat.remindme.lists;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.imaginat.remindme.IChangeToolbar;
import com.imaginat.remindme.R;

public class ReminderListMain extends AppCompatActivity
    implements IChangeToolbar {

    private static final String TAG = ReminderListMain.class.getName();
    private static final int REQUEST_FINE_LOCATION = 0;
    private static final int REQUEST_LOCATION = 12;
    private int TOOLBAR_ICON_INSTRUCTIONS=-1;

    ReminderListsContract.Presenter mReminderListsPresenter=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        //Fragment (View)
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ReminderListsFragment fragment = new ReminderListsFragment();
        fragmentTransaction.add(R.id.my_frame, fragment);
        fragmentTransaction.commit();

        //Set up the presenter
        mReminderListsPresenter = new ReminderListsPresenter(fragment);
        fragment.setPresenter(mReminderListsPresenter);



        ////////////////////////////////////////////////////////////////////////////////
        //ListsLocalDataSource llds  =  ListsLocalDataSource.getInstance(this);
        //llds.createNewList("LIST ONE",1);
        //llds.createNewList("LIST TWO",2);
/*
        llds.getAllListTitles()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<ReminderList>>() {
                    @Override
                    public void call(List<ReminderList> reminderLists) {
                        Toast.makeText(ReminderListsMain.this,"Found some results",Toast.LENGTH_SHORT).show();
                        ((TextView)findViewById(R.id.helloworld)).setText("FOUNDN SOME RESULTS");
                    }
                });
                */
        ///////////////////////////////////////////////////////////////////
    }

    @Override
    public void onUpdateTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
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
