package com.group4sweng.scranplan;

import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import androidx.test.rule.ActivityTestRule;

import com.google.android.material.tabs.TabLayout;


public class RecipeInfoTest {

    @Rule
    public ActivityTestRule<RecipeInfoFragment> mActivityTestRule = new ActivityTestRule<RecipeInfoFragment>(RecipeInfoFragment.class);

    private RecipeInfoFragment mActivity = null;
    private Button backButton;
    private TabLayout.Tab tabSwitch1;
    private TabLayout.Tab tabSwitch2;

    @Before
    public void setUp() {

        mActivity = mActivityTestRule.getActivity();
        backButton = (Button)mActivity.findViewById(R.id.ReturnButton);

    }

    @After
    public void tearDown() {

        mActivity = null;

    }

    @Test
    public void testRecipeInfo() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                backButton.callOnClick();

            }

        });

    }
}