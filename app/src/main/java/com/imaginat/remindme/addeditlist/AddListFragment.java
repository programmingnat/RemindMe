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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.R;

/**
 * This represents the View (MVP). It is responsible for only displaying view, all logic is
 * managed by the presenter
 */
public class AddListFragment extends Fragment implements AddListContract.View{

    //TAG for debugging purposes
    private static final String TAG=AddListFragment.class.getSimpleName();

    //Referrence to the presenter
    AddListContract.Presenter mPresenter;

    //Adapter for the recycler list used to display the preset icons available to the suer
    IconListAdapter iconListAdapter=null;

    //Ref to the view that allows users to change or create new list titles
    private EditText mEditTextOfListName;


    /**
     *
     * createView handles page setup
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Inflate the view
        View view = inflater.inflate(R.layout.add_new_list, container, false);

        setHasOptionsMenu(true);

        //Get reference to the editText
        mEditTextOfListName = (EditText)view.findViewById(R.id.addNameOfNewList_EditText);

        //Get reference to button and set event listener to call when add is pressed
        Button doneButton = (Button)view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedIcon = iconListAdapter.getSelectedIcon();

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

        //set up the the list (of icons)
        RecyclerView listView = (RecyclerView) view.findViewById(R.id.listOfIcons);
        iconListAdapter = new IconListAdapter();
        listView.setAdapter(iconListAdapter);

        LinearLayoutManager llm=new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        listView.setLayoutManager(llm);

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AddEditList addEditListActivity = (AddEditList)getActivity();
        addEditListActivity.swapIcons(GlobalConstants.HIDE_OPTIONS);

        int[] attrs = {android.R.attr.colorPrimary,android.R.attr.colorPrimaryDark,android.R.attr.colorAccent};
        TypedArray ta = addEditListActivity.obtainStyledAttributes(R.style.AppTheme,attrs);
        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.my_toolbar);
        toolbar.setBackgroundColor(ta.getColor(0, Color.BLACK));
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mPresenter!=null){
            mPresenter.start();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);



    }


    public void setPresenter(AddListContract.Presenter presenter) {
        mPresenter=presenter;
    }

    //==========================METHODS THAT WILL CHANGE THE LOOK ON THE SCREEN===============

    /**
     * Goes back to the list
     */
    @Override
    public void showTasks() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    /**
     *
     *Display the previously saved info
     */
    @Override
    public void showPreviousInfo(String title, int icon) {
        mEditTextOfListName.setText(title);

    }
}
