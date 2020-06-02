package com.group4sweng.scranplan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Objects;

public class GoldMembership extends AppCompatDialogFragment {


    Spinner mGoldDropdown;
    Button mGoldSubscribe;

    String subscriptionDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog()).setTitle("Slide settings");

        View layout = inflater.inflate(R.layout.create_recipe_dialog, container, false);

        assert getArguments() != null;
        initPageItems(layout);
        initPageListeners(layout);

        return layout;
    }

    private void initPageItems(View v) {
        mGoldDropdown = v.findViewById(R.id.gold_dropdown);
        mGoldSubscribe = v.findViewById(R.id.gold_subscribe);
    }

    private void initPageListeners(View v) {
        mGoldDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subscriptionDate = mGoldDropdown.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mGoldSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
