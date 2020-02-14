package com.group4sweng.scranplan;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.material.tabs.TabLayout;


public class RecipeInfo extends AppCompatActivity {

    final String TAG = "RecipeInfo";

    Button mReturnButton;
    TabLayout mTabLayout2;
    FrameLayout mRecipeFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_info);
        Log.e(TAG, "Starting recipe Info activity");

        initPageItems();
        initPageListeners();

        //assigns id's from activity page to variables
        mTabLayout2 = findViewById(R.id.tabLayout2);
        mRecipeFrameLayout = findViewById(R.id.RecipeFrameLayout);

        mTabLayout2.setTabGravity(TabLayout.GRAVITY_FILL);

        //controls the tabs within the recipe information page to select between the ingredient information
        //and the comments section
        mTabLayout2.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        fragment = new RecipeIngredientFragment();
                        break;
                    case 1:
                        fragment = new RecipeCommentsFragment();
                        break;

                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.RecipeFrameLayout, fragment);
                fragmentTransaction.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }



    private void initPageItems(){
        //Defining all relevant members of recipe information page
        mReturnButton = (Button) findViewById(R.id.ReturnButton);

    }

    //When back button is clicked, Recipe information activity is closed and returns to recipe fragment
    private void initPageListeners(){
        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                //Intent goBack = new Intent(RecipeInfo.this, MainActivity.class);
                //startActivity(goBack);


            }
        });
    }
}
