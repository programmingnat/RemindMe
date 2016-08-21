package com.imaginat.remindme.viewtasks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
       // holder.mMoreOpts.setVisibility(View.INVISIBLE);
        //holder.mDidIEdit=false;
        if(toDoListItem.isCompleted()){
            holder.mRadioButton.setChecked(true);
        }else{
            holder.mRadioButton.setChecked(false);
        }


        //setSideButton(holder);





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
        public ImageButton mOptionsButton;
        public TextView mTextView;
        public EditText mEditText;
        public String mReminderId;
        public View mItemView;
        public String mListID;
        public boolean mIsShowingOverlay =false;
        public ViewGroup mOverlayMenu;
        public ImageButton mDeleteButton;






        public TaskListItemHolder(final View itemView) {
            super(itemView);
            mItemView =itemView;
            //mViewSwitcher = (ViewSwitcher) itemView.findViewById(R.id.my_switcher);
            mEditText = (EditText)itemView.findViewById(R.id.listItemEdit);
            mRadioButton = (CheckBox) itemView.findViewById(R.id.completedRadioButton);
            mTextView = (TextView) itemView.findViewById(R.id.listItemTextView);
            mDeleteButton = (ImageButton)itemView.findViewById(R.id.delete_imageButton);
            // mOptionsButton=(Button)itemView.findViewById(R.id.editLineItemButton);
            mItemView.setOnClickListener(this);

            mOptionsButton = (ImageButton)itemView.findViewById(R.id.openMenu_button);
            mEditText.setOnKeyListener(this);
            mEditText.setOnFocusChangeListener(this);
            //mEditText.setOnLongClickListener(this);
            mEditText.setOnEditorActionListener(this);
            mOverlayMenu = (ViewGroup)itemView.findViewById(R.id.overlay_menu);
            mEditText.setSingleLine(false);



            mOptionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(),"Options clicked",Toast.LENGTH_SHORT).show();
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

            mOverlayMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mIsShowingOverlay=false;
                    LinearLayout ll = (LinearLayout)itemView.findViewById(R.id.overlay_menu);
                    ll.setVisibility(View.INVISIBLE);
                }
            });

            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTasksPresenter.deleteReminder(mListID,mReminderId);
                }
            });


            mRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTasksPresenter.updateCompletionStatus(mListID,mReminderId,((CheckBox)v).isChecked());
                }
            });


        }




        @Override
        public void onClick(View v) {
            if(!mIsShowingOverlay){
                mEditText.requestFocus();
                mEditText.setSelection(mEditText.getText().length());
            }
           // mTasksPresenter.disableAddingNewTask();
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            //Log.d(TAG,"onKey");
            //mDidIEdit=true;

            return false;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if(!hasFocus){

                mTasksPresenter.updateReminder(mListID,mReminderId,((EditText)v).getText().toString());
            }
        }



        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            /*if (actionId == EditorInfo.IME_ACTION_DONE || actionId==KeyEvent.KEYCODE_ENTER)
            {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                return true;
            }*/

            if(event!=null && (event.getKeyCode()==KeyEvent.KEYCODE_ENTER)){
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                //mTasksPresenter.enableAddingNewTask();
                return true;
            }
            return false;
        }



    }
}
