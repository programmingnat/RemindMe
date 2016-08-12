package com.imaginat.remindme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by nat on 8/12/16.
 */
public abstract class BaseActivity<T extends Fragment> extends AppCompatActivity
        implements IChangeToolbar {



    abstract public int getLayoutID();
    abstract public Object createPresenter(T fragment);
    abstract public T getFragment();

    T mFragment=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());

        //set up the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        //Fragment (View)
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.my_frame, mFragment);
        fragmentTransaction.commit();

        //Set up the presenter
        createPresenter(mFragment);

    }
}
