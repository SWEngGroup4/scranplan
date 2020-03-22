package com.group4sweng.scranplan;

import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class RecipeInfoFragmentTest  {

    @Rule
    //public ActivityTestRule mActivityTestRule = new ActivityTestRule (MainActivity.class);


    private MainActivity mActivity = null;
    private ImageButton imageButton;


    @Before
    public void setUp() throws Exception {

        //mActivity = (MainActivity) mActivityTestRule.getActivity();

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void fragment_can_be_instantiated() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}