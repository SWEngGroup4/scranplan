package com.group4sweng.scranplan;

import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.rule.ActivityTestRule;

import com.google.android.material.tabs.TabLayout;

import static org.junit.Assert.*;

public class RecipeInfoTest {

    @Rule
    public FragmentScenario<RecipeInfoFragment> myFragment;

    private Button backButton;
    private TabLayout.Tab tabSwitch1;
    private TabLayout.Tab tabSwitch2;

    @Before
    public void setUp() {



    }

    @After
    public void tearDown() {


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