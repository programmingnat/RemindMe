package com.imaginat.remindme.viewtasks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;

/**
 * The reminders are displayed in a recycler view. THis is the adapter class that manages the underlying data
 * (the reminders) for the recycler view
 * The majority of the code that deals with interaction with the tasks appear in this class
 */
public class TaskReminderRecyclerAdapter extends RecyclerView.Adapter<TaskReminderRecyclerAdapter.TaskListItemHolder>
    implements TasksContract.ViewAdapter{

    //TAG is used for debuggin
    private static final String TAG = TaskReminderRecyclerAdapter.class.getSimpleName();

    //Reference to presenter
    TasksContract.Presenter mTasksPresenter;

    //refrence to the underlying data
    private ArrayList<ITaskItem> mITaskItems;


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
        holder.mListID=toDoListItem.getListID();
        holder.mReminderId = toDoListItem.getReminderID();
        holder.mGeoFenceAlarmData=toDoListItem.getGeoFenceAlarmData();
        //holder.mRadioButton.setVisibility(View.VISIBLE);

        //Mark the item as completed or not
        if(toDoListItem.isCompleted()){
            holder.mRadioButton.setChecked(true);
        }else{
            holder.mRadioButton.setChecked(false);
        }


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

    @Override
    public void setPresenter(TasksContract.Presenter presenter) {
        mTasksPresenter=presenter;
    }




    //====================================================================================
    public class TaskListItemHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnKeyListener,
            TextView.OnEditorActionListener,View.OnFocusChangeListener{

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

        public boolean mIsShowingOverlay =false;
        public GeoFenceAlarmData mGeoFenceAlarmData;






        public TaskListItemHolder(final View itemView) {
            super(itemView);

            //Set all the View references
            mItemView =itemView;//mItemView is the parent group to all the other views here

            mEditText = (EditText)itemView.findViewById(R.id.listItemEdit);
            mRadioButton = (CheckBox) itemView.findViewById(R.id.completedRadioButton);
            mTextView = (TextView) itemView.findViewById(R.id.listItemTextView);
            mDeleteButton = (ImageButton)itemView.findViewById(R.id.delete_imageButton);
            mGeoFenceButton = (ImageButton)itemView.findViewById(R.id.geofence_imageButton);

            //Set the listener for when a line is clicked
            mItemView.setOnClickListener(this);

            //Set the listeners for the edit text
            mEditText.setOnKeyListener(this);
            mEditText.setOnFocusChangeListener(this);
            mEditText.setOnEditorActionListener(this);
            //allow multiple lines for edit text (doesnt work in xml)
            mEditText.setSingleLine(false);


            mOverlayMenu = (ViewGroup)itemView.findViewById(R.id.overlay_menu);

            mCalendarButton = (ImageButton)itemView.findViewById(R.id.calendar_imageButton);

            //set the listener to show the overlay button
            mOpenOptionsButton = (Button)itemView.findViewById(R.id.openOptions_button);
            mOpenOptionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Toggle the overlay menu
                    if(mIsShowingOverlay){
                        mIsShowingOverlay=false;
                        LinearLayout ll = (LinearLayout)itemView.findViewById(R.id.overlay_menu);
                        ll.setVisibility(View.INVISIBLE);
                    }else{
                        mIsShowingOverlay=true;
                        LinearLayout ll = (LinearLayout)itemView.findViewById(R.id.overlay_menu);
                        ll.setVisibility(View.VISIBLE);
                    }

                }
            });

            //If you click anywhere on the overlay itself  (once it is open), it will close
            mOverlayMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mIsShowingOverlay=false;
                    LinearLayout ll = (LinearLayout)itemView.findViewById(R.id.overlay_menu);
                    ll.setVisibility(View.INVISIBLE);
                }
            });


            //====THE OVERLAY OPTION Listeners

            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTasksPresenter.deleteReminder(mListID,mReminderId);
                }
            });

            mCalendarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   mTasksPresenter.openCalendar(mListID,mReminderId);

                }
            });

            mGeoFenceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTasksPresenter.openGeoFenceOptions(mListID,mReminderId,mGeoFenceAlarmData);
                }
            });
            mRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTasksPresenter.updateCompletionStatus(mListID,mReminderId,((CheckBox)v).isChecked());
                }
            });



            //This is to allow the button to "grow" with the edit text
            itemView.post(new Runnable(){
                @Override
                public void run(){
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
            if(!mIsShowingOverlay){
                mEditText.requestFocus();
                mEditText.setSelection(mEditText.getText().length());
            }

        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            return false;
        }

        /**
         *
         * When the focus changes (user leaves an edit text field), save the data
         */
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if(!hasFocus){
                //update the data when a user leaves (and presumably  done editing)
                mTasksPresenter.updateReminder(mListID,mReminderId,((EditText)v).getText().toString());
            }
        }



        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            if(event!=null && (event.getKeyCode()==KeyEvent.KEYCODE_ENTER)){
                //If Enter is hit, hide the pop out keyboard
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                return true;
            }
            return false;
        }



    }
}
