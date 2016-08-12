package com.imaginat.remindme.lists;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.imaginat.remindme.IChangeToolbar;
import com.imaginat.remindme.R;
import com.imaginat.remindme.data.ReminderList;
import com.imaginat.remindme.data.source.local.ListsLocalDataSource;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by nat on 8/8/16.
 */
public class ReminderListsFragment extends Fragment
        implements ReminderListsContract.View{

    private static final String TAG = ReminderListsFragment.class.getSimpleName();
    ReminderListsContract.Presenter mPresenter=null;
    ReminderListRecycleAdapter mAdapter;
    List<ReminderList> mReminders;
    private  View mView;
    private IChangeToolbar mIChangeToolbar;
    private boolean isLongClickOn=false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         View  view= inflater.inflate(R.layout.list_of_lists_fragment, container, false);


        mView = view;
        ListsLocalDataSource llds  =  ListsLocalDataSource.getInstance(this.getContext());


        llds.getAllListTitles()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<ReminderList>>() {
                    @Override
                    public void call(List<ReminderList> reminderLists) {
                        Log.d(TAG,"Inside call() found "+reminderLists.size()+" results");
                        mAdapter = new ReminderListRecycleAdapter(getContext(),(ArrayList)reminderLists);
                        mAdapter.setPresenter(mPresenter);
                        final RecyclerView recyclerView = (RecyclerView)mView.findViewById(R.id.theRecyclerView);
                        recyclerView.setAdapter(mAdapter);

                        int orientation= GridLayoutManager.VERTICAL;
                        boolean reverseLayout=false;
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2,orientation,reverseLayout));
                        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getContext(),R.dimen.item_offset );
                        recyclerView.addItemDecoration(itemDecoration);

                        Toast.makeText(ReminderListsFragment.this.getActivity(),"Found some results "+reminderLists.size(),Toast.LENGTH_SHORT).show();

                    }

                });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mIChangeToolbar != null) {
            mIChangeToolbar.onUpdateTitle("RemindME");
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mIChangeToolbar = (IChangeToolbar) context;
            mIChangeToolbar.onUpdateTitle("RemindME");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    @Override
    public void showListOptions(String id) {

        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.my_toolbar);
        toolbar.setBackgroundColor(Color.rgb(100,100,100));
        mIChangeToolbar.swapIcons(100);
        isLongClickOn=true;
    }

    @Override
    public void showSelectedList(String id) {

    }

    @Override
    public void showAll() {

    }

    @Override
    public void setPresenter(ReminderListsContract.Presenter presenter) {
        mPresenter=presenter;
        if(mAdapter!=null) {
            mAdapter.setPresenter(mPresenter);
        }
    }


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
