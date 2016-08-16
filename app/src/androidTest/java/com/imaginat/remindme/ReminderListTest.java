package com.imaginat.remindme;


import android.support.test.runner.AndroidJUnit4;

import com.imaginat.remindme.data.ReminderList;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by nat on 8/14/16.
 */
@RunWith(AndroidJUnit4.class)
public class ReminderListTest   {


    @Test
    public void testReminderList_badData_Returns(){
        ReminderList rl = null;//new ReminderList(" ",0,null);
        //maybe use builder pattern
        Assert.assertEquals("The object should be null",rl,null);

    }
}

