package com.imaginat.remindme.addeditlist;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.imaginat.remindme.R;
import com.imaginat.remindme.lists.ReminderListsFragment;
import com.imaginat.remindme.lists.ReminderListsPresenter;

public class AddEditList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_list);

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
    }
}
