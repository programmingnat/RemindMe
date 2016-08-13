package com.imaginat.remindme.lists;

import android.os.Bundle;

import com.imaginat.remindme.BaseActivity;
import com.imaginat.remindme.R;

public class ReminderListMain extends BaseActivity<ReminderListsFragment>{


    ReminderListsContract.Presenter mReminderListsPresenter=null;
    ReminderListsFragment mReminderListsFragment=null;
        @Override
        public int getLayoutID() {
            return R.layout.activity_main;
        }

        @Override
        public Object createPresenter(ReminderListsFragment fragment) {
            mReminderListsPresenter = new ReminderListsPresenter(fragment);
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





    }


}

