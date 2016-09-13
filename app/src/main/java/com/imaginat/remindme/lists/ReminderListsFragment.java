package com.imaginat.remindme.lists;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.IChangeToolbar;
import com.imaginat.remindme.R;
import com.imaginat.remindme.addeditlist.AddEditList;
import com.imaginat.remindme.data.ReminderList;
import com.imaginat.remindme.data.source.local.ListsLocalDataSource;
import com.imaginat.remindme.viewtasks.TasksActivity;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by nat on 8/8/16.
 */
public class ReminderListsFragment extends Fragment
        implements ReminderListsContract.View {

    //TAG holds the name of the classfor debugging purposes
    private static final String TAG = ReminderListsFragment.class.getSimpleName();

    //constant used in callback (when add new list/edit list name) is called
    public static final int ADDING_NEW_LIST = 100;
    public static final int VIEW_TASKS=200;

    //Reference to the Presenter (connection the the data layer)
    ReminderListsContract.Presenter mPresenter = null;

    //Adapter used to display data in the RecyclerView (treat it as a part of the View)
    ReminderListRecycleAdapter mAdapter;

    //Reference to android.view created when xml is inflated
    private View mView;

    //reference to recycler view and empty message
    private RecyclerView mRecyclerView;
    private TextView mEmptyListTextView;

    //reference to the interface that allows modification to toolbar
    private IChangeToolbar mIChangeToolbar;


    //ref. to the original color of the activity, which changes on long click (used to change color back after long click)
    int mColorPrimary;



    //=====================LIFE CYCLE METHODS============================================

    /**
     *
     * use onCreateView to call for data and setup the View of the page
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Inflate the view & get reference to it
        View view = inflater.inflate(R.layout.list_of_lists_fragment, container, false);
        mView = view;

        mRecyclerView = (RecyclerView)mView.findViewById(R.id.theRecyclerView);
        mEmptyListTextView= (TextView)mView.findViewById(R.id.emptyList_textView);

        //Get reference to current Primary Color
        int[] attrs = {android.R.attr.colorPrimary, android.R.attr.colorPrimaryDark, android.R.attr.colorAccent};
        TypedArray ta = getContext().obtainStyledAttributes(R.style.AppTheme, attrs);
        mColorPrimary = ta.getColor(0, Color.BLACK);


        //Access the local data (rxandroid), when list is loaded, set up this page fragment
        ListsLocalDataSource llds = ListsLocalDataSource.getInstance(this.getContext());
        llds.getAllListTitles()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<ReminderList>>() {
                    @Override
                    public void call(List<ReminderList> reminderLists) {

                        //once the data is found, create the adapter
                        mAdapter = new ReminderListRecycleAdapter(getContext(), (ArrayList) reminderLists);
                        mAdapter.setPresenter(mPresenter);
                        final RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.theRecyclerView);
                        recyclerView.setAdapter(mAdapter);

                        //set the layout
                        int orientation = GridLayoutManager.VERTICAL;
                        boolean reverseLayout = false;
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, orientation, reverseLayout));
                        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getContext(), R.dimen.item_offset);
                        recyclerView.addItemDecoration(itemDecoration);

                        //Toast.makeText(ReminderListsFragment.this.getActivity(), "Found some results " + reminderLists.size(), Toast.LENGTH_SHORT).show();

                    }

                });

        //The floating action button allows user to add a new list
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrEditNewListRequested(v);
            }
        });
        return view;
    }


    /**
     *
     * isActive() will now return true if the fragmenthas been added to the activity
     */
    @Override
    public boolean isActive() {
        return isAdded();
    }

    /**
     *
     * Lifecycle method
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //allows the app to change titles in the toolbar,depending on the fragment/activity
        if (mIChangeToolbar != null) {
            mIChangeToolbar.onUpdateTitle("RemindME");
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mIChangeToolbar=(IChangeToolbar)context;
    }

    /**
     * Fragment lifecycle method
     */
    @Override
    public void onResume() {
        super.onResume();

        //activates/notifies the presenter that this fragment is displayed
        if (mPresenter != null) {
            mPresenter.start();
        }

    }


    //====================METHODS THAT REQUEST A ACTIVITY CHANGE or ALTER CURRENT VIEW=================

    /**
     *
     * Tells the presenter to bring up the add screen
     */
    public void addOrEditNewListRequested(View v) {
        mPresenter.loadAddEditList();
    }
    /**
     * Display add/edit List activity
     */
    public void showAddEditList(String id){
        Intent toAddListIntent = new Intent(this.getContext(), AddEditList.class);
        toAddListIntent.putExtra(GlobalConstants.CURRENT_LIST_ID,id);
        startActivityForResult(toAddListIntent, ADDING_NEW_LIST);
    }
    /**
     *
     * Change the tool bar to show list options
     */
    @Override
    public void showListOptions(String id) {
        //get reference to the toolbar
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);
        //change the color to tray
        toolbar.setBackgroundColor(Color.rgb(100, 100, 100));
        //change the icons
        mIChangeToolbar.swapIcons(GlobalConstants.SHOW_OPTIONS);

    }

    /**
     * show message when no lists have been added
     */
    @Override
    public void showNoLists() {
        mRecyclerView.setVisibility(View.GONE);
        mEmptyListTextView.setVisibility(View.VISIBLE);
    }


    /**
     * hide the toolbar options
     */
    @Override
    public void hideListOptions() {

        IChangeToolbar iChangeToolbar = (IChangeToolbar) getContext();
        iChangeToolbar.swapIcons(GlobalConstants.HIDE_OPTIONS);
        Toolbar toolbar = (Toolbar) ((Activity) (getContext())).findViewById(R.id.my_toolbar);
        toolbar.setBackgroundColor(mColorPrimary);
    }

    /**
     *
     * Display the tasks
     */
    @Override
    public void showSelectedList(String id) {
        Intent addEditTaskIntent = new Intent(getContext(), TasksActivity.class);
        addEditTaskIntent.putExtra(GlobalConstants.CURRENT_LIST_ID, id);
        startActivityForResult(addEditTaskIntent,VIEW_TASKS);
    }

    /**
     *
     * passes the underlying data to be shown to the adapter
     */
    @Override
    public void showAll(ArrayList<ReminderList> lists) {
        mRecyclerView.setVisibility(View.VISIBLE);
        mEmptyListTextView.setVisibility(View.GONE);
        mAdapter.setData(lists);
        mAdapter.notifyDataSetChanged();
    }

    //================================================================================
    /**
     *
     * sets the presenter in both this clas and the adapter
     */
    @Override
    public void setPresenter(ReminderListsContract.Presenter presenter) {
        mPresenter = presenter;
        if (mAdapter != null) {
            mAdapter.setPresenter(mPresenter);
        }
    }


    /**
     *
     * This gets called when the other screens (add list/view tasks) gets closed
     * allows the app to reset screen as needed
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==ADDING_NEW_LIST){
            mPresenter.unloadListOptions();
        }
    }


    /**
     * inner class used to alter the recyclerview display
     */
    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }
}
