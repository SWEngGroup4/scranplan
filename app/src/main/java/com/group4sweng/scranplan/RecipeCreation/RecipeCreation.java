package com.group4sweng.scranplan.RecipeCreation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.group4sweng.scranplan.R;

import java.util.HashMap;

public class RecipeCreation extends AppCompatActivity {

    HashMap<String, Object> recipeMap;

    Fragment fragment;
    FragmentManager fragmentManager;
    FrameLayout frameLayout;

    ProgressBar spinner;

    private final static int BASIC_INFO_FLAG = 1;
    private final static int RECIPE_STEPS_FLAG = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        spinner = findViewById(R.id.createRecipeSpinner);
        recipeMap = new HashMap<>();

        frameLayout = findViewById(R.id.createRecipeFrame);
        fragment = new BasicInfo();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.createRecipeFrame, fragment).commitNow();
        spinner.setVisibility(View.INVISIBLE);
    }

    public void stepComplete(int requestCode, Bundle bundle) {
        if (bundle!= null)
            for (String key : bundle.keySet())
                recipeMap.put(key, bundle.get(key));

        switch (requestCode) {
            case BASIC_INFO_FLAG:
                fragment = new RecipeSteps();
                fragmentManager.beginTransaction().replace(R.id.createRecipeFrame, fragment).
                        setCustomAnimations(R.anim.exit_to_right, R.anim.enter_from_left).commit();

            case RECIPE_STEPS_FLAG:
                //TODO - Upload the data to firebase
                finish();
        }
    }
}
