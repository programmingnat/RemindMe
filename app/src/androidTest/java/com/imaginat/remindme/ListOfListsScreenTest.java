package com.imaginat.remindme;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.imaginat.remindme.lists.ReminderListMain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by nat on 8/15/16.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ListOfListsScreenTest {

    @Rule
    public ActivityTestRule<ReminderListMain>mActivityRule = new ActivityTestRule<ReminderListMain>(ReminderListMain.class);

    @Test
    public void testListSelection() throws Exception{

        Espresso.onView(withId(R.id.theRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(6,click()));

    }


}
