package com.group4sweng.scranplan.TimelinePlanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.SearchFunctions.SearchPrefs;
import com.group4sweng.scranplan.SearchFunctions.SearchTime;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static com.group4sweng.scranplan.R.id.time_frame;

public class TimelinePlanner extends Fragment {

    TextView mTimeFrame;
    RadioButton mBreakfastBox;
    RadioButton mLunchBox;
    RadioButton mDinnerBox;
    AlertDialog timeFrameDialog;
    AlertDialog.Builder builder;
    SearchTime time;


    public TimelinePlanner() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initTimeMenu();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    public void initTimeRadioButton(){
        mBreakfastBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mBreakfastBox.isChecked()) {
                    mLunchBox.setChecked(false);
                    mDinnerBox.setChecked(false);
                }
            }
        });
        mLunchBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mLunchBox.isChecked()) {
                    mBreakfastBox.setChecked(false);
                    mDinnerBox.setChecked(false);
                }
            }
        });
        mDinnerBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mDinnerBox.isChecked()) {
                    mBreakfastBox.setChecked(false);
                    mLunchBox.setChecked(false);
                }
            }
        });
    }

    public void initTimeMenu(){
        final Dialog timeDialog = new Dialog (getContext());
        timeDialog.setContentView(R.layout.filter_time_dialog);
        mBreakfastBox = timeDialog.findViewById(R.id.BreackfastRadioButton);
        mLunchBox = timeDialog.findViewById(R.id.LunchRadioButton);
        mDinnerBox = timeDialog.findViewById(R.id.DinnerRadioButton);
        initTimeRadioButton();

        builder = new AlertDialog.Builder(getActivity());
        builder
                .setCancelable(false)
                // if positive picked then create a new preferences variable to reflect what the user has selected
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                time = new SearchTime(mBreakfastBox.isChecked(), mLunchBox.isChecked(), mDinnerBox.isChecked());

                            }
                        })
                .setNegativeButton("Cancel",
                        // If negative button clicked then cancel the action of changing user prefs
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        builder.setTitle("Search options");
        timeFrameDialog = builder.create();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Opening the filter menu
        int id = item.getItemId();
        if (id == time_frame) {
            timeFrameDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    

}