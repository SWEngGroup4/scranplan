package com.group4sweng.scranplan;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoginTest {

    @Rule
    public ActivityTestRule<Login> mActivityTestRule = new ActivityTestRule<Login>(Login.class);

    private Login mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void onCreate() {
    }

    @Test
    public void testLaunch(){
        //Checking that the page is displaying the XML associated with the Login page
        View view = mActivity.findViewById(R.id.loginButton);

        assertNotNull(view);
    }

    @After
    public void tearDown() throws Exception {

        mActivity = null;
    }


}