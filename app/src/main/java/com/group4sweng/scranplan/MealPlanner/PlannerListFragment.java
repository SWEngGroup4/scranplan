package com.group4sweng.scranplan.MealPlanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.group4sweng.scranplan.RecipeInfo.RecipeInfoFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchListFragment;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.Objects;

//Extends functionality of search results
public class PlannerListFragment extends SearchListFragment {

    private Bundle mBundle;

    public PlannerListFragment(UserInfoPrivate userSent) {
        super(userSent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void recipeSelected(DocumentSnapshot documentSnapshot) {
        super.recipeSelected(documentSnapshot);
    }

    /* Overrides normal opening process to let info fragment know it is being launched from
       the planner */
    @Override
    protected void openRecipeInfo(Bundle bundle) {
        mBundle = bundle;
        bundle.putBoolean("planner", true);

        //Loads info fragment for selected recipe
        RecipeInfoFragment recipeInfoFragment = new RecipeInfoFragment();
        recipeInfoFragment.setArguments(bundle);
        recipeInfoFragment.setTargetFragment(PlannerListFragment.this, 1);
        recipeInfoFragment.show(getParentFragmentManager(), "Show recipe dialog fragment");

    }

    //Runs on completion of info fragment activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Launches activtiy result method on target fragment
        if (resultCode == Activity.RESULT_OK) {
            Intent i = new Intent();
            i.putExtras(mBundle);
            Objects.requireNonNull(getTargetFragment()).onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
            dismiss();
        }
    }

}