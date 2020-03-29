package com.group4sweng.scranplan.PreferencesTabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.UserInfo.FilterType;
import com.group4sweng.scranplan.UserInfo.Preferences;

import org.jetbrains.annotations.NotNull;

/** Tabbed Fragment of the preferences filters visible in 'profile settings' for dietary requirements.
 * Author: JButler
 * (c) CoDev 2020
 **/
public class DietaryFragment extends Fragment implements FilterType {

    private static final String TAG = "DietaryFragment"; // 'Log' tag.

    //  User dietary filters.
    private CheckBox mDietary_vegan;
    private CheckBox mDietary_vegetarian;
    private CheckBox mDietary_pescatarian;

    private Preferences mPref;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_dietary, container, false); // Inflate the fragment before doing anything else.

        Bundle bundle = this.getArguments(); // Grab our bundle passed when the view was created.

        //  Check if either the bundle passed over is invalid or the preferences serializable object from 'UserInfoPrivate' is invalid.
        //  If so throw an exception.
        if(bundle == null){
            throw new NullPointerException("Bundle has not been created and sent to dietary fragment");
        }
        mPref = (Preferences) bundle.getSerializable("preferences"); // Grab serializable content.
        if(mPref == null){
            throw new NullPointerException("User preferences have not been passed to the dietary fragment");
        }

        return view;
    }

    //  Passed after we inflate the fragment. Initial launch.
    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        initPageItems();
        setFilters();
    }

    /** Load references to the checkboxes ids. **/
    private void initPageItems(){
        mDietary_pescatarian = getView().findViewById(R.id.dietary_pescatarian);
        mDietary_vegan = getView().findViewById(R.id.dietary_vegan);
        mDietary_vegetarian = getView().findViewById(R.id.dietary_vegetarian);
    }

    /** Initiate the correct filter values by setting the correct 'checked' value for the checkboxes. **/
    private void setFilters(){
        mDietary_pescatarian.setChecked(mPref.isPescatarian());
        mDietary_vegan.setChecked(mPref.isVegan());
        mDietary_vegetarian.setChecked(mPref.isVegetarian());

    }

}
