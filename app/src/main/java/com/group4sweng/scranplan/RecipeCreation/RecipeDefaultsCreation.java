package com.group4sweng.scranplan.RecipeCreation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.group4sweng.scranplan.R;

import java.util.Objects;

public class RecipeDefaultsCreation extends AppCompatDialogFragment {

    private Button mBackgroundButton;
    private Spinner mSlideFont;
    private Spinner mFontSize;
    private Button mFontColourButton;
    private Button mFontBackgroundButton;
    private Button mSubmit;

    private Integer mBackgroundColour;
    private String mFont;
    private String mSize;
    private Integer mFontColour;
    private Integer mFontBackground;

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
        initBundleItems(getArguments());
        initPageItems(layout);
        initPageListeners(layout);

        return layout;
    }

    // Get previously defined defaults
    private void initBundleItems(Bundle bundle) {
        mBackgroundColour = bundle.getInt("backColour");
        mFont = bundle.getString("font");
        mSize = bundle.getString("size");
        mFontColour = bundle.getInt("fontColour");
        mFontBackground = bundle.getInt("fontBackground");
    }

    private void initPageItems(View v) {
        // Set up background and display selected colour
        mBackgroundButton = v.findViewById(R.id.slideBackgroundPicker);
        mBackgroundButton.setBackgroundColor(mBackgroundColour);

        // Set up font selection and display selected font
        mSlideFont = v.findViewById(R.id.slideDefaultsFontSelect);
        ArrayAdapter<CharSequence> fontAdapter = ArrayAdapter.createFromResource(v.getContext(), R.array.rcFonts, R.layout.support_simple_spinner_dropdown_item);
        fontAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mSlideFont.setAdapter(fontAdapter);
        mSlideFont.setSelection(fontAdapter.getPosition(mFont));

        // Set up font size selection and display selected size
        mFontSize = v.findViewById(R.id.slideDefaultsFontSize);
        ArrayAdapter<CharSequence> sizeAdapter = ArrayAdapter.createFromResource(v.getContext(), R.array.rcFontSize, R.layout.support_simple_spinner_dropdown_item);
        mFontSize.setAdapter(sizeAdapter);
        mFontSize.setSelection(sizeAdapter.getPosition(mSize));

        // Set up font colour selection and display selected colour
        mFontColourButton = v.findViewById(R.id.fontColourPicker);
        mFontColourButton.setBackgroundColor(mFontColour);

        // Setup font background selection and display selected colour
        mFontBackgroundButton = v.findViewById(R.id.fontBackgroundPicker);
        mFontBackgroundButton.setBackgroundColor(mFontBackground);

        mSubmit = v.findViewById(R.id.slideDefaultsSubmit);
    }

    private void initPageListeners(View v) {
        // Open colour picker to chose background colour
        mBackgroundButton.setOnClickListener(view -> ColorPickerDialogBuilder
            .with(v.getContext())
            .setTitle("Choose color")
            .initialColor(Color.WHITE)
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .density(12)
            .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
                mBackgroundButton.setBackgroundColor(selectedColor); // Set button to correct colour
                mBackgroundColour = selectedColor; })
            .setNegativeButton("cancel", (dialog, which) -> {})
            .build()
            .show()
        );

        // Handle dropdown list selection
        mSlideFont.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFont = mSlideFont.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Handle dropdown list selection
        mFontSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSize = mFontSize.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Open colour picker to chose font colour
        mFontColourButton.setOnClickListener(view -> ColorPickerDialogBuilder
            .with(v.getContext())
            .setTitle("Choose color")
            .initialColor(Color.WHITE)
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .density(12)
            .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
                mFontColourButton.setBackgroundColor(selectedColor);
                mFontColour = selectedColor; })
            .setNegativeButton("cancel", (dialog, which) -> {})
            .build()
            .show()
        );

        // Open colour picker to chose font background colour
        mFontBackgroundButton.setOnClickListener(view -> ColorPickerDialogBuilder
            .with(v.getContext())
            .setTitle("Choose color")
            .initialColor(Color.WHITE)
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .density(12)
            .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
                mFontBackgroundButton.setBackgroundColor(selectedColor);
                mFontBackground = selectedColor; })
            .setNegativeButton("cancel", (dialog, which) -> {})
            .build()
            .show()
        );

        // Send selected settings back to main view
        mSubmit.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("backColour", mBackgroundColour);
            intent.putExtra("font", mFont);
            intent.putExtra("size", mSize);
            intent.putExtra("fontColour", mFontColour);
            intent.putExtra("fontBackground", mFontBackground);

            Objects.requireNonNull(getTargetFragment()).onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            dismiss();
        });
    }

}
