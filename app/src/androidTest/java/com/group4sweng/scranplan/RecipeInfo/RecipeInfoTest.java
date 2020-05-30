package com.group4sweng.scranplan.RecipeInfo;

import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.testing.FragmentScenario;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class RecipeInfoTest {

    @Rule
    public FragmentScenario<RecipeInfoFragment> myFragment;



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


            }

        });

    }
}