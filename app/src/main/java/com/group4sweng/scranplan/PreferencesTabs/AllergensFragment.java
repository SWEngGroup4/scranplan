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

public class AllergensFragment extends Fragment implements FilterType {

    private static final String TAG = "AllergensFragment";
    //  User allegern filters.
    private CheckBox mAllergy_nuts;
    private CheckBox mAllergy_milk;
    private CheckBox mAllergy_eggs;
    private CheckBox mAllergy_shellfish;
    private CheckBox mAllergy_soy;
    private CheckBox mAllergy_gluten;

    private Preferences mPref;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_allergens, container, false);

        Bundle bundle = this.getArguments();

        if(bundle == null){
            throw new NullPointerException("Bundle has not been created and sent to allergens fragment");
        }
        mPref = (Preferences) bundle.getSerializable("preferences");
        if(mPref == null){
            throw new NullPointerException("User preferences have not been passed to the allergens fragment");
        }

        return view;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        initPageItems();
        setFilters();
    }

    private void initPageItems(){
        //  Allergens
        mAllergy_eggs = getView().findViewById(R.id.allergy_eggs);
        mAllergy_gluten = getView().findViewById(R.id.allergy_wheat);
        mAllergy_milk = getView().findViewById(R.id.allergy_milk);
        mAllergy_nuts = getView().findViewById(R.id.allergy_nuts);
        mAllergy_shellfish = getView().findViewById(R.id.allergy_shellfish);
        mAllergy_soy = getView().findViewById(R.id.allergy_soy);
    }


    private void setFilters(){
        mAllergy_eggs.setChecked(mPref.isAllergy_eggs());
        mAllergy_milk.setChecked(mPref.isAllergy_milk());
        mAllergy_nuts.setChecked(mPref.isAllergy_nuts());
        mAllergy_shellfish.setChecked(mPref.isAllergy_shellfish());
        mAllergy_soy.setChecked(mPref.isAllergy_soya());
        mAllergy_gluten.setChecked(mPref.isAllergy_gluten());
        }


}
