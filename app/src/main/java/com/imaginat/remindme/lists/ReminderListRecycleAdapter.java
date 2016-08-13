package com.imaginat.remindme.lists;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.IChangeToolbar;
import com.imaginat.remindme.R;
import com.imaginat.remindme.addedittask.TasksActivity;
import com.imaginat.remindme.data.ReminderList;

import java.util.ArrayList;

/**
 * Created by nat on 8/8/16.
 */
public class ReminderListRecycleAdapter extends RecyclerView.Adapter<ReminderListRecycleAdapter.ReminderHolder>
        implements ReminderListsContract.ViewsAdapter{



    ReminderListsContract.Presenter mPresenter;
    ArrayList<ReminderList> mReminderLists;


    private boolean onLongClick=false;

    private final static String TAG = ReminderListRecycleAdapter.class.getName();
    private Context mContext;
    private ArrayList<ReminderList> mReminders;
    private int mColorPrimary;


    @Override
    public void setData(ArrayList<ReminderList> theData) {
        mReminderLists=theData;
    }

    @Override
    public void setPresenter(ReminderListsContract.Presenter presenter) {
        mPresenter=presenter;
    }

    public class ReminderHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;
        //public RadioButton mRadioButton;
        public View mLineItemView;
        public ImageView mImageView;
        public String mList_id = "someListID";

        public ReminderHolder(View itemView) {
            super(itemView);
            mLineItemView = itemView;
            mTextView = (TextView) itemView.findViewById(R.id.listItemTextView);
            mImageView = (ImageView)itemView.findViewById(R.id.listImage);






            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClickon the main View item");
                    if(onLongClick){

                        IChangeToolbar iChangeToolbar=(IChangeToolbar)v.getContext();
                        iChangeToolbar.swapIcons(200);
                        Toolbar toolbar = (Toolbar)((Activity)(v.getContext())).findViewById(R.id.my_toolbar);
                        toolbar.setBackgroundColor(mColorPrimary);
                        onLongClick=false;
                    }else{
                        mPresenter.loadSelectedList(mList_id);
                        Intent addEditTaskIntent = new Intent(v.getContext(),TasksActivity.class);
                        v.getContext().startActivity(addEditTaskIntent);

                    }

                    v.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.textlines, null));

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    //v.setSelected(true);

                    mPresenter.loadListOptions(mList_id);
                    v.setBackgroundColor(Color.rgb(200,200,200));
                    onLongClick=true;
                    return true;
                }
            });

            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                final View insideView = ReminderHolder.this.itemView;

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        // run scale animation and make it bigger

                        ViewCompat.setElevation(insideView, 1);
                    } else {
                        // run scale animation and make it smaller

                        ViewCompat.setElevation(insideView, 0);
                    }
                }
            });
        }


    }


    public ReminderList getItem(int position) {
        return mReminders.get(position);
    }

    public ReminderListRecycleAdapter(Context context, ArrayList<ReminderList> reminders) {
        mContext = context;
        mReminders = reminders;


        int[] attrs = {android.R.attr.colorPrimary,android.R.attr.colorPrimaryDark,android.R.attr.colorAccent};
        TypedArray ta = context.obtainStyledAttributes(R.style.AppTheme,attrs);
        mColorPrimary= ta.getColor(0,Color.BLACK);

    }

    public void setToRemindersArray(ArrayList<ReminderList>list){
        mReminders=list;
    }
    @Override
    public ReminderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.list_of_lists_line_item, parent, false);

        return new ReminderHolder(view);
    }


    @Override
    public void onBindViewHolder(ReminderHolder holder, int position) {
        ReminderList reminder =  mReminders.get(position);
        if (reminder == null) {


            holder.itemView.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.textlines, null));

//            holder.mLineItemView.setBackgroundColor(Color.argb(255,255,255,255));
            holder.mTextView.setTextColor(Color.BLACK);
            // holder.mRadioButton.setVisibility(View.VISIBLE);

            holder.mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            holder.mTextView.setText("Add List Here");
        } else {
            holder.mList_id = reminder.getListId();
            holder.itemView.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.textlines, null));

//            holder.mLineItemView.setBackgroundColor(Color.argb(255, 255, 255, 255));
            holder.mTextView.setTextColor(Color.BLACK);
            //holder.mRadioButton.setVisibility(View.VISIBLE);

            holder.mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            holder.mTextView.setText(reminder.getTitle());

            //randomly assign image (temp)
            int imageIndex = reminder.getIcon();
            switch (imageIndex) {

                case GlobalConstants.WORKOUT_ICON:
                    holder.mImageView.setBackgroundResource(R.drawable.workout);
                    break;
                case GlobalConstants.SHOPPING_CART_ICON:
                    holder.mImageView.setBackgroundResource(R.drawable.shopping_cart);
                    break;
                case GlobalConstants.WORK_ICON:
                    holder.mImageView.setBackgroundResource(R.drawable.laptop);
                    break;
                case GlobalConstants.KIDS_ICON:
                    holder.mImageView.setBackgroundResource(R.drawable.kids_logo);
                    break;
                case GlobalConstants.BEACH_ICON:
                    holder.mImageView.setBackgroundResource(R.drawable.beach_logo);
                    break;
                default:
                case GlobalConstants.GENERIC_REMINDER_ICON:
                    holder.mImageView.setBackgroundResource(R.drawable.generic_reminder);
                    break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return mReminders.size();
    }
}
