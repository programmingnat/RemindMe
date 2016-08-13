package com.imaginat.remindme.addeditlist;

import com.imaginat.remindme.BasePresenter;
import com.imaginat.remindme.BaseView;

/**
 * Created by nat on 8/9/16.
 */
public class AddListContract {


    public interface View extends BaseView<Presenter>{

    }


    public interface Presenter extends BasePresenter{
        public void addNewList(String listName,int iconID);
    }
}
