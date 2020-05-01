package com.group4sweng.scranplan.MealPlanner;

import com.group4sweng.scranplan.Credentials;

import org.junit.Test;

public class PortionsTest implements Credentials {

    private String TAG = "portionsTest";
    private static final int THREAD_SLEEP_TIME = 4000;



    @Test
    public void testChangePortionsDisplayed(){

    @After
    public void tearDown() throws Exception {
        EspressoHelper.shouldSkip = false;
        this.mActivityTestRule.finishActivity();
    }
}
