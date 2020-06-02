package com.group4sweng.scranplan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

public class GoldMembership extends AppCompatDialogFragment {


    Spinner mGoldDropdown;
    TextView mGoldPrice;
    Button mGoldSubscribe;

    String subscriptionDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.gold_membership, container, false);

        initPageItems(layout);
        initPageListeners();

        return layout;
    }

    private void initPageItems(View v) {
        mGoldDropdown = v.findViewById(R.id.gold_dropdown);
        mGoldPrice = v.findViewById(R.id.gold_price);
        mGoldSubscribe = v.findViewById(R.id.gold_subscribe);
    }



    private void initPageListeners() {
        mGoldDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subscriptionDate = mGoldDropdown.getSelectedItem().toString();

                switch (subscriptionDate) {
                    case "1 month":
                        mGoldPrice.setText("Price: £1.99");
                        break;
                    case "3 months":
                        mGoldPrice.setText("Price: £4.99");
                        break;
                    case "1 year":
                        mGoldPrice.setText("Price: £14.99");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mGoldSubscribe.setOnClickListener(v -> dismiss());
    }

}
