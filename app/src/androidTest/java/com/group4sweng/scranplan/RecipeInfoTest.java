package com.group4sweng.scranplan;

import android.os.Handler;
import android.os.Looper;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.fragment.app.testing.FragmentScenario;

import com.group4sweng.scranplan.RecipeInfo.RecipeInfoFragment;


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