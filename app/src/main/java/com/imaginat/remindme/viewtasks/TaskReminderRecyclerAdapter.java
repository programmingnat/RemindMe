package com.imaginat.remindme.viewtasks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imaginat.remindme.R;
import com.imaginat.remindme.data.GeoFenceAlarmData;
import com.imaginat.remindme.data.ITaskItem;
import com.imaginat.remindme.data.SimpleTaskItem;

import java.util.ArrayList;

/**
 * The reminders are displayed in a recycler view. THis is the adapter class that manages the underlying data
 * (the reminders) for the recycler view
 * The majority of the code that deals with interaction with the tasks appear in this class
 */
public class TaskReminderRecyclerAdapter extends RecyclerView.Adapter<TaskReminderRecyclerAdapter.TaskListItemHolder>
        implements TasksContract.ViewAdapter {

    //TAG is used for debuggin
    private static final String TAG = TaskReminderRecyclerAdapter.class.getSimpleName();

    //Reference to presenter
    TasksContract.Presenter mTasksPresenter;

    //refrence to the underlying data
    private ArrayList<ITaskItem> mITaskItems;

    //tracks the index number of selected (to be used for autoscrolling?)
    private int mSelectedIndexNumber;

    /**
     * Constructor
     */
    public TaskReminderRecyclerAdapter(Context context, ArrayList<ITaskItem> arrayList) {
        mITaskItems = arrayList;
        //mContext = context;
    }


    /**
     * used during testing to set the underlying data
     */
    public void setToDoListArray(ArrayList<ITaskItem> list) {
        mITaskItems = list;
    }

    /**
     * REcyclerView Adapter Method
     * create the view from the XML and passes it to the holder
     */
    @Override
    public TaskListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the View
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        //reference to the View
        View view = layoutInflater.inflate(R.layout.task_line_item, parent, false);

        //create the holder to hold the view
        return new TaskListItemHolder(view);
    }


    /**
     * REcyclerView Adapter Method
     * links the holder to the underlying data
     */
    @Override
    public void onBindViewHolder(TaskListItemHolder holder, int position) {

        //Get a reference to the data
        ITaskItem toDoListItem = (ITaskItem) mITaskItems.get(position);


        //Set the Views based on the array item
        //set text,listID,reminderID,alarmData
        holder.mRadioButton.setChecked(toDoListItem.isCompleted());
        holder.mEditText.setText(toDoListItem.getText());
        holder.mListID = toDoListItem.getListID();
        holder.mReminderId = toDoListItem.getReminderID();
        holder.mGeoFenceAlarmData = toDoListItem.getGeoFenceAlarmData();

        //this doesnt have to be exact, used in helping scroll
        holder.mPositionInArray = position;
        //holder.mRadioButton.setVisibility(View.VISIBLE);

        //Mark the item as completed or not
        if (toDoListItem.isCompleted()) {
            holder.mRadioButton.setChecked(true);
        } else {
            holder.mRadioButton.setChecked(false);
        }

        //get rid of overlay if it is showing
        holder.mIsShowingOverlay = false;
        LinearLayout ll = (LinearLayout) holder.itemView.findViewById(R.id.overlay_menu);
        ll.setVisibility(View.INVISIBLE);

    }


    /**
     * RecyclerViewAdapter method, returns the number of underlying data
     */
    @Override
    public int getItemCount() {
        return mITaskItems.size();
    }

    /**
     * Allows the client to change the underlying data
     */
    @Override
    public void setData(ArrayList<ITaskItem> arrayList) {
        mITaskItems.clear();
        mITaskItems.addAll(arrayList);
        notifyDataSetChanged();

    }

    /**
     * Keeps track of the position of the item selected. Allows the recyclerview to autoscroll to ensure the view is on screen and visiible
     */
    public int getSelectedIndexNumber() {
        return mSelectedIndexNumber;
    }

    @Override
    public void setPresenter(TasksContract.Presenter presenter) {
        mTasksPresenter = presenter;

    }


    /**
     * Allows insertion to the end of the list
     * the return boolean indicates whether or not the list should be redrawn and reloaded(which is the case when the first view is added)
     * or if the animation will occur (which doesnt happen when the first view i added)
     */
    public boolean addItemToEnd(String listID, String reminderID) {

        int position = mITaskItems.size();
        SimpleTaskItem element = new SimpleTaskItem(listID, reminderID);
        mITaskItems.add(position, element);
        mSelectedIndexNumber = position + 1;

        if (position <= 1) {
            notifyDataSetChanged();
            return true;
        } else {
            notifyItemInserted(position);
            return false;
        }
    }

    //====================================================================================
    public class TaskListItemHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener,
            TextView.OnEditorActionListener, View.OnFocusChangeListener{

        //References to all the views that make up a line in the task list
        public CheckBox mRadioButton;

        public TextView mTextView;
        public EditText mEditText;
        public String mReminderId;
        public View mItemView;
        public String mListID;

        public ViewGroup mOverlayMenu;
        public ImageButton mDeleteButton;
        public ImageButton mCalendarButton;
        public ImageButton mGeoFenceButton;
        public Button mOpenOptionsButton;

        public boolean mIsShowingOverlay = false;
        public GeoFenceAlarmData mGeoFenceAlarmData;
        public int mPositionInArray = 0;


        public TaskListItemHolder(final View itemView) {
            super(itemView);

            //Set all the View references
            mItemView = itemView;//mItemView is the parent group to all the other views here

            mEditText = (EditText) itemView.findViewById(R.id.listItemEdit);
            mRadioButton = (CheckBox) itemView.findViewById(R.id.completedRadioButton);
            mTextView = (TextView) itemView.findViewById(R.id.listItemTextView);
            mDeleteButton = (ImageButton) itemView.findViewById(R.id.delete_imageButton);
            mGeoFenceButton = (ImageButton) itemView.findViewById(R.id.geofence_imageButton);

            //Set the listener for when a line is clicked
            mItemView.setOnClickListener(this);

            //Set the listeners for the edit text

            //mEditText.setOnKeyListener(this);
            mEditText.setOnFocusChangeListener(this);
            mEditText.setOnEditorActionListener(this);
            //allow multiple lines for edit text (doesnt work in xml)
            mEditText.setSingleLine(true);


            mOverlayMenu = (ViewGroup) itemView.findViewById(R.id.overlay_menu);

            mCalendarButton = (ImageButton) itemView.findViewById(R.id.calendar_imageButton);

            //set the listener to show the overlay button
            mOpenOptionsButton = (Button) itemView.findViewById(R.id.openOptions_button);
            mOpenOptionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Toggle the overlay menu
                    if (mIsShowingOverlay) {
                        mIsShowingOverlay = false;
                        LinearLayout ll = (LinearLayout) itemView.findViewById(R.id.overlay_menu);
                        ll.setVisibility(View.INVISIBLE);
                    } else {
                        mIsShowingOverlay = true;
                        LinearLayout ll = (LinearLayout) itemView.findViewById(R.id.overlay_menu);
                        ll.setVisibility(View.VISIBLE);
                    }

                }
            });

            //If you click anywhere on the overlay itself  (once it is open), it will close
            mOverlayMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mIsShowingOverlay = false;
                    LinearLayout ll = (LinearLayout) itemView.findViewById(R.id.overlay_menu);
                    ll.setVisibility(View.INVISIBLE);
                }
            });



            //====THE OVERLAY OPTION Listeners========================

            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTasksPresenter.requestToDelete(mListID, mReminderId);


                }
            });

            mCalendarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTasksPresenter.openCalendar(mListID, mReminderId);

                }
            });

            mGeoFenceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTasksPresenter.openGeoFenceOptions(mListID, mReminderId, mGeoFenceAlarmData);
                }
            });
            mRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTasksPresenter.updateCompletionStatus(mListID, mReminderId, ((CheckBox) v).isChecked());
                }
            });


            //This is to allow the button to "grow" with the edit text
            itemView.post(new Runnable() {
                @Override
                public void run() {
                    int height = itemView.getHeight();
                    mOpenOptionsButton.setHeight(height);
                }
            });


            //Alternative way to "grow" the button with edit text
//            ViewTreeObserver vto = itemView.getViewTreeObserver();
//            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//
//                    itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                    int width = itemView.getMeasuredWidth();
//                    int height = itemView.getMeasuredHeight();
//
//                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(new ViewGroup.MarginLayoutParams(80,height));
//                    mOpenOptionsButton.setHeight(height);
//                    // view.mOpenOptionsButton.setLayoutParams(lp);
//                }
//            });

        }


        /**
         * When the user clicks the View line(holding edit text), send the focus to the end of the edit text
         */
        @Override
        public void onClick(View v) {
            if (!mIsShowingOverlay) {
                mEditText.requestFocus();
                /*if(mEditText.getText().length()>0){

                    mEditText.setSelection(mEditText.getText().length());
                }*/
                mSelectedIndexNumber = mPositionInArray;
            }

        }




        /**
         * When the focus changes (user leaves an edit text field), save the data
         */
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (!hasFocus) {
                //update the data when a user leaves (and presumably  done editing)
                mTasksPresenter.updateReminder(mListID, mReminderId, ((EditText) v).getText().toString());
            } else {
                mSelectedIndexNumber = mPositionInArray;
            }
        }


        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            Log.d(TAG, "onEditorAction");
            if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                //If Enter is hit, hide the pop out keyboard
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
            return false;
        }


    }
}
