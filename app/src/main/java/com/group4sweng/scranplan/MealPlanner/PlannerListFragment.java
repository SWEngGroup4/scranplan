package com.group4sweng.scranplan.MealPlanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.group4sweng.scranplan.RecipeInfoFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchListFragment;


public class PlannerListFragment extends SearchListFragment {

    private DocumentSnapshot mDocumentSnapshot;
    private Bundle mBundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Test", "ON PLANNERLISTFRAGMENT");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void recipeSelected(DocumentSnapshot documentSnapshot) {
        mDocumentSnapshot = documentSnapshot;
        super.recipeSelected(documentSnapshot);
    }

    @Override
    protected void openRecipeInfo(Bundle bundle) {
        mBundle = bundle;
        bundle.putBoolean("planner", true);

        RecipeInfoFragment recipeInfoFragment = new RecipeInfoFragment();
        recipeInfoFragment.setArguments(bundle);
        recipeInfoFragment.setTargetFragment(PlannerListFragment.this, 1);
        recipeInfoFragment.show(getFragmentManager(), "Show recipe dialog fragment");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Intent i = new Intent();
            i.putExtras(mBundle);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
            dismiss();
        }
    }

}
