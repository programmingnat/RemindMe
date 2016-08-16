package com.imaginat.remindme.viewtasks;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginat.remindme.R;
import com.imaginat.remindme.data.ITaskItem;

import java.util.ArrayList;

/**
 * Created by nat on 8/12/16.
 */
public class TaskReminderRecyclerAdapter extends RecyclerView.Adapter<TaskReminderRecyclerAdapter.TaskListItemHolder>
implements TasksContract.ViewAdapter{

    private static final String TAG = TaskReminderRecyclerAdapter.class.getSimpleName();
    private Context mContext;
    TasksContract.Presenter mTasksPresenter;
    private ArrayList<ITaskItem> mITaskItems;




    public TaskReminderRecyclerAdapter(Context context, ArrayList<ITaskItem> arrayList) {
        mContext = context;
        mITaskItems = arrayList;


    }

    public void setToDoListArray(ArrayList<ITaskItem> list) {
        mITaskItems = list;
    }

    @Override
    public TaskListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.task_line_item, parent, false);



        return new TaskListItemHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskListItemHolder holder, int position) {

        ITaskItem toDoListItem = (ITaskItem) mITaskItems.get(position);
        if (toDoListItem == null) {

            holder.mEditText.setText("");
            holder.mEditText.setHint("ADD A REMINDER");
            holder.mRadioButton.setVisibility(View.GONE);
            return;
        }
        holder.mRadioButton.setVisibility(View.VISIBLE);

        holder.mRadioButton.setChecked(toDoListItem.isCompleted());
        holder.mEditText.setText(toDoListItem.getText());
        holder.mListID=toDoListItem.getListID();
        holder.mReminderId = toDoListItem.getReminderID();
        //((LinearLayout)holder.itemView.findViewById(R.id.lineItemOptionsButton)).setVisibility(View.GONE);
        holder.mMoreOpts.setVisibility(View.INVISIBLE);
        holder.mDidIEdit=false;
        if(toDoListItem.isCompleted()){
            holder.mRadioButton.setChecked(true);
        }else{
            holder.mRadioButton.setChecked(false);
        }



    }


    @Override
    public int getItemCount() {
        return mITaskItems.size();
    }

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

        public CheckBox mRadioButton;
        public Button mDeleteButton;
        public Button mOptionsButton;
        public TextView mTextView;
        public EditText mEditText;
        public String mReminderId;
        public View mItemView;
        public ImageButton mMoreOpts;
        public String mListID;
        public boolean mDidIEdit=false;




        public TaskListItemHolder(View itemView) {
            super(itemView);
            mItemView =itemView;
            //mViewSwitcher = (ViewSwitcher) itemView.findViewById(R.id.my_switcher);
            mEditText = (EditText)itemView.findViewById(R.id.listItemEdit);
            mRadioButton = (CheckBox) itemView.findViewById(R.id.completedRadioButton);
            mTextView = (TextView) itemView.findViewById(R.id.listItemTextView);
            // mDeleteButton = (Button)itemView.findViewById(R.id.deleteLineItemButton);
            // mOptionsButton=(Button)itemView.findViewById(R.id.editLineItemButton);
            mItemView.setOnClickListener(this);
            mMoreOpts = (ImageButton)itemView.findViewById(R.id.moreOptionsButton);
            mEditText.setOnKeyListener(this);
            mEditText.setOnFocusChangeListener(this);
            //mEditText.setOnLongClickListener(this);
            mEditText.setOnEditorActionListener(this);

            mEditText.setSingleLine(false);
            /*Button hideOptionsButton = (Button)itemView.findViewById(R.id.hideOptions);
            hideOptionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMoreOpts.setVisibility(View.GONE);
                    LinearLayout ll = (LinearLayout)mItemView.findViewById(R.id.lineItemOptionsButton);
                    ll.setVisibility(View.GONE);
                    mEditText.setTextColor(Color.BLACK);
                }
            });*/


            mRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //Log.d(TAG,"onCheckedChanged "+mListID+" "+mReminderId);

                }
            });

            mRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int temp = mITaskItems.size();
                    //mClickInterface.handleClickToUpdateCheckStatus(mListID,mReminderId,((CheckBox)v).isChecked());
                    mTasksPresenter.updateCompletionStatus(mListID,mReminderId,((CheckBox)v).isChecked());
                }
            });


            mItemView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    Log.d(TAG,"mItemView onKey ");
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        Log.d(TAG,"mItemView onKey entered pressed");
                        // Perform action on key press
                        // Toast.makeText(mContext, "ADDING TO DATABASE", Toast.LENGTH_SHORT).show();
                        if(mRadioButton.getVisibility()==View.GONE) {
                            Log.d(TAG,"mItemView onKey bisiblity of radio button is GONE");

                        }else{
                            Log.d(TAG,"mItemView onKey visiblity of radio button is NOT gone");
                        }
                        //((ViewSwitcher) v.getParent()).showPrevious();

                    }
                    return false;
                }
            });

            mEditText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //ImageButton imageButton = (ImageButton)v.findViewById(R.id.moreOptionsButton);
                    mMoreOpts.setVisibility(View.VISIBLE);

                    return false;
                }
            });
            /*mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickInterface.handleDeleteButton(mReminderId);
                }
            });*/

            /*mOptionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mClickInterface.handleClick("MORE_OPTIONS");
                    mClickInterface.handleMoreOptions(mListID,mReminderId);
                }
            });*/


            mMoreOpts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MoreOptionsDialogFragment newFragment = new MoreOptionsDialogFragment();
                    newFragment.setListID(mListID);
                    newFragment.setReminderID(mReminderId);
                    AppCompatActivity a = ((AppCompatActivity) v.getContext());

                    newFragment.show(a.getSupportFragmentManager(), "options");




                }
            });

        }



        @Override
        public void onClick(View v) {


            //Log.d(TAG,"onClick called");
            mEditText.requestFocus();
            if(mMoreOpts.getVisibility()!=View.VISIBLE && mRadioButton.getVisibility()==View.VISIBLE){
                mMoreOpts.setVisibility(View.VISIBLE);

            }


        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            //Log.d(TAG,"onKey");
            mDidIEdit=true;

            return false;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            Log.d(TAG, "onFocusChange called");
            if(!hasFocus){

                mMoreOpts.setVisibility(View.INVISIBLE);
                mEditText.setTextColor(Color.BLACK);

                //((ViewSwitcher)v.getParent()).showPrevious();
                if(mDidIEdit && mRadioButton.getVisibility()==View.VISIBLE){
                    mTasksPresenter.updateReminder(mListID,mReminderId,((EditText)v).getText().toString());
                    mDidIEdit=false;
                    Log.d(TAG,"CALL UPDATE STUFF HERE");
                }

            }else{
                //mMoreOpts.setVisibility(View.VISIBLE);
            }
        }



        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            Log.d(TAG,"Inside onEditorAction");
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId==KeyEvent.KEYCODE_ENTER)
            {
                if(mRadioButton.getVisibility()==View.GONE) {
                    Toast.makeText(mContext, "ADDING TO DATABASE", Toast.LENGTH_SHORT).show();

                    //((ViewSwitcher) v.getParent()).showPrevious();
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
            return false;
        }



    }
}
