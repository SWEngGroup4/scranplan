package com.group4sweng.scranplan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

public class RecipeInfoFragment extends AppCompatDialogFragment {

    Button mReturnButton;
    TabLayout mTabLayout2;
    FrameLayout mRecipeFrameLayout;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.fragment_recipe_info, null);

        builder.setView(layout);

        mReturnButton = layout.findViewById(R.id.ReturnButton);
        mTabLayout2 = layout.findViewById(R.id.tabLayout2);
        mRecipeFrameLayout = layout.findViewById(R.id.RecipeFrameLayout);


        initPageListeners();

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
                FragmentManager fragmentManager = getFragmentManager();
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

        return builder.create();
    }

    public void onResume(){
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

    }

    //When back button is clicked, Recipe information activity is closed and returns to recipe fragment
    private void initPageListeners(){
        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();

            }
        });
    }

}
