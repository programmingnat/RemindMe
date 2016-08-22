package com.imaginat.remindme.addeditlist;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.imaginat.remindme.R;

/**
 * Created by nat on 8/9/16.
 */
public class AddListFragment extends Fragment implements AddListContract.View{
    private static final String TAG=AddListFragment.class.getSimpleName();
    private EditText mEditTextOfListName;
    private boolean mUseInEditMode;
    IconListAdapter iconListAdapter=null;
    AddListContract.Presenter mPresenter;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_new_list, container, false);
        setHasOptionsMenu(true);
        mEditTextOfListName = (EditText)view.findViewById(R.id.addNameOfNewList_EditText);
        Button doneButton = (Button)view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick called");
                int selectedIcon = iconListAdapter.getSelectedIcon();
                Log.d(TAG,"selectedIcon is "+selectedIcon);
                if(mEditTextOfListName.getText()==null || mEditTextOfListName.getText().length()==0){
                    //indicate error
                    mEditTextOfListName.setError("FILL thIS IN");
                }

                if(mEditTextOfListName.getText()==null ||mEditTextOfListName.getText().length()==0){
                    mEditTextOfListName.setError("ENTER A NAME");
                }else{
                    String newListName = mEditTextOfListName.getText().toString();
                    mPresenter.addNewList(newListName,iconListAdapter.getSelectedIcon());
                }


                getFragmentManager().popBackStackImmediate();
            }
        });

        //the list
        RecyclerView listView = (RecyclerView) view.findViewById(R.id.listOfIcons);
        iconListAdapter = new IconListAdapter();
        listView.setAdapter(iconListAdapter);
        LinearLayoutManager llm=new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        listView.setLayoutManager(llm);

        return view;

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        //Toast.makeText(getContext(),"Attempted to disable", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AddEditList addEditListActivity = (AddEditList)getActivity();
        addEditListActivity.swapIcons(200);

        int[] attrs = {android.R.attr.colorPrimary,android.R.attr.colorPrimaryDark,android.R.attr.colorAccent};
        TypedArray ta = addEditListActivity.obtainStyledAttributes(R.style.AppTheme,attrs);
        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.my_toolbar);
        toolbar.setBackgroundColor(ta.getColor(0, Color.BLACK));
    }


    public void setPresenter(AddListContract.Presenter presenter) {
        mPresenter=presenter;
    }

    @Override
    public void showTasks() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }
}
