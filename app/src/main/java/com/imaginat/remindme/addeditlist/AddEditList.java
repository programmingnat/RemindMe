package com.imaginat.remindme.addeditlist;

import android.content.Intent;
import android.os.Bundle;

import com.imaginat.remindme.BaseActivity;
import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.R;

/**
 * The Activity encompases the MVP pattern. The activity class creates the presenter,views and ensure that each has the
 * appropriate references. This class is  specifically  used to ADD and EDIT the list title and icon
 */
public class AddEditList extends BaseActivity<AddListFragment> {

    //Reference to the presenter
    AddListPresenter mAddListPresenter;

    //Reference to the view (in this case implemented as a Fragment)
    AddListFragment mAddListFragment;

    /**
     * returns the main xml file that this activity will use
     */
    @Override
    public int getLayoutID() {
        return R.layout.activity_add_edit_list;
    }

    /**
     *
     * Create the presenter and assign it to the view
     */
    @Override
    public Object createPresenter(AddListFragment fragment) {

        if(mAddListPresenter==null){

           mAddListPresenter = new AddListPresenter(fragment);

            //Get the intent to see if listID was passed in, if it was that means we are updating list title(vs creating new)
            Intent creatorIntent = getIntent();
            String listID = creatorIntent.getStringExtra(GlobalConstants.CURRENT_LIST_ID);

            if(listID!=null){
                mAddListPresenter.setListID(listID);
            }
        }

        //give the View (MVP) a reference to the presenter
        fragment.setPresenter(mAddListPresenter);

        return mAddListPresenter;
    }

    /**
     *  creates the "View"
     */
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
