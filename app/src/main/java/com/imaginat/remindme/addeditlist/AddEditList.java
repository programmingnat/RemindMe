package com.imaginat.remindme.addeditlist;

import android.os.Bundle;

import com.imaginat.remindme.BaseActivity;
import com.imaginat.remindme.R;

public class AddEditList extends BaseActivity<AddListFragment> {

    AddListPresenter mAddListPresenter;
    AddListFragment mAddListFragment;

    @Override
    public int getLayoutID() {
        return R.layout.activity_add_edit_list;
    }

    @Override
    public Object createPresenter(AddListFragment fragment) {
        if(mAddListPresenter==null){
           mAddListPresenter = new AddListPresenter((AddListContract.View)fragment);
        }
        fragment.setPresenter(mAddListPresenter);
        return mAddListPresenter;
    }

    @Override
    public AddListFragment getFragment() {
        if(mAddListFragment==null){
            mAddListFragment = new AddListFragment();
        }
        return mAddListFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
