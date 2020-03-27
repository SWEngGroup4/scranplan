package com.group4sweng.scranplan.PreferencesTabs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.UserInfo.Preferences;

import org.jetbrains.annotations.NotNull;

public class DietaryFragment extends Fragment {

    private static final String TAG = "DietaryFragment";
    //  User dietary filters.
    private CheckBox mDietary_vegan;
    private CheckBox mDietary_vegetarian;
    private CheckBox mDietary_pescatarian;

    private Preferences mPref;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_dietary, container, false);

        Bundle bundle = this.getArguments();
        if(bundle == null){
            throw new NullPointerException("Bundle has not been created and sent to dietary fragment");
        }
        mPref = (Preferences) bundle.getSerializable("preferences");
        if(mPref == null){
            throw new NullPointerException("User preferences have not been passed to the dietary fragment");
        }

        Log.e(TAG, "IS PESCATARIAN: " + mPref.isPescatarian());

        return view;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        initPageItems();
        setFilters();
    }

    private void initPageItems(){
        //  Allergens
        mDietary_pescatarian = getView().findViewById(R.id.dietary_pescatarian);
        mDietary_vegan = getView().findViewById(R.id.dietary_vegan);
        mDietary_vegetarian = getView().findViewById(R.id.dietary_vegetarian);
    }


    private void setFilters(){
        mDietary_pescatarian.setChecked(mPref.isPescatarian());
        mDietary_vegan.setChecked(mPref.isVegan());
        mDietary_vegetarian.setChecked(mPref.isVegetarian());

    }

}
