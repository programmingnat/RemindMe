package com.imaginat.remindme.lists;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.imaginat.remindme.GlobalConstants;
import com.imaginat.remindme.R;
import com.imaginat.remindme.data.ReminderList;

import java.util.ArrayList;

/**
 * The Recycler Adapter for the List of Lists. The adapter and the Holder inner class handles clicks, long clicks, and
 * the actual appearance of each item in the list (in this case each item is a List Title)
 */
public class ReminderListRecycleAdapter extends RecyclerView.Adapter<ReminderListRecycleAdapter.ReminderHolder>
        implements ReminderListsContract.ViewsAdapter {

    //For debugging purposes
    private final static String TAG = ReminderListRecycleAdapter.class.getName();

    //Reference to presenter. Treating the Adapter as part of the View (so all logic goes through the presenter)
    ReminderListsContract.Presenter mPresenter;

    ///flag used to indicate long click has been pressed (used by normal click-to turn off long click mode)
    private boolean mOnLongClick = false;

    //Reference to the context that uses this Adapter
    //private Context mContext;

    //The underlying data
    private ArrayList<ReminderList> mReminderLists = new ArrayList<>();




    /**
     * constructor
     */
    public ReminderListRecycleAdapter(Context context, ArrayList<ReminderList> reminders) {
        //mContext = context;
        mReminderLists = reminders;
    }

    /**
     * Method to set to change the underlying data that the Adapter uses
     */
    @Override
    public void setData(ArrayList<ReminderList> theData) {
        mReminderLists.clear();
        mReminderLists.addAll(theData);
        notifyDataSetChanged();
    }


    @Override
    public void setPresenter(ReminderListsContract.Presenter presenter) {
        mPresenter = presenter;

    }


    //============INNER HOLDER CLASS======================================
    public class ReminderHolder extends RecyclerView.ViewHolder {

        //References to GUI objects
        public TextView mTextView;
        public View mLineItemView;
        public ImageView mImageView;

        //Reference to the identifier of this list
        public String mList_id = "someListID";

        /**
         * Holder constructor
         */
        public ReminderHolder(View itemView) {
            super(itemView);

            //initialize references to GUI objects
            mLineItemView = itemView;
            mTextView = (TextView) itemView.findViewById(R.id.listItemTextView);
            mImageView = (ImageView) itemView.findViewById(R.id.listImage);


            //===============SET THE LISTENERS=====================================
            /**
             * set the behavior on the click an item in the list
             */
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //After a user long clicks, the user does a normal click to get back out
                    if (mOnLongClick) {
                        mPresenter.unloadListOptions();
                        mOnLongClick = false;
                    } else {
                        //a normal click (which means the user is selected a specific list to view)
                        mPresenter.loadSelectedList(mList_id);
                    }

                    //set the look
                    v.setBackground(ResourcesCompat.getDrawable(v.getContext().getResources(), R.drawable.textlines, null));

                }
            });

            /**
             * set the behavior of a long click on an item
             */
            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    //Long click means you want to edit the list info, share the list, or delete the list
                    mPresenter.loadListOptions(mList_id);
                    v.setBackgroundColor(Color.rgb(200, 200, 200));
                    mOnLongClick = true;
                    return true;
                }
            });

            //Highlight the click a little bit
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



    //===============RECYCLER ADAPTER METHODS THAT REQUIRE IMPLEMENTATION =====================

    /**
     * create a holder to represent the underlying data
     */
    @Override
    public ReminderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_of_lists_line_item, parent, false);

        return new ReminderHolder(view);
    }

    /**
     * Bind methods gets called to link up the holder and the underlying data
     */
    @Override
    public void onBindViewHolder(ReminderHolder holder, int position) {
        //Link the underlying data with the views
        ReminderList reminder = mReminderLists.get(position);

        holder.mList_id = reminder.getListId();

        holder.itemView.setBackground(ResourcesCompat.getDrawable(holder.itemView.getContext().getResources(), R.drawable.textlines, null));

        holder.mTextView.setTextColor(Color.BLACK);

        holder.mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

        holder.mTextView.setText(reminder.getTitle());

        holder.itemView.setTag(reminder.getTitle());


        //Get the image to display
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

    @Override
    public int getItemCount() {
        return mReminderLists.size();
    }
}
