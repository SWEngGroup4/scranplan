package com.group4sweng.scranplan.RecipeCreation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.RecipeCreation.IngredientRecyclerAdapter.IngredientData;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class BasicInfo extends Fragment {

    //Page content
    private ImageButton mRecipeImage;
    private EditText mRecipeName;
    private EditText mRecipeServes;
    private EditText mRecipeDesc;
    private EditText mIngredientName;
    private EditText mIngredientMeasurement;
    private RecyclerView mIngredients;
    private Button mAddIngredient;
    private Switch mEggs;
    private Switch mLactose;
    private Switch mNuts;
    private Switch mShellfish;
    private Switch mSoya;
    private Switch mGluten;
    private Spinner mDietDropdown;
    private Switch mFridge;
    private EditText mFridgeDays;
    private Switch mFrozen;
    private EditText mReheat;
    private Button mSubmit;

    private InputMethodManager imm;

    private ArrayList<IngredientData> mIngredientList;
    private HashMap<String, String> mIngredientMap;
    private RecyclerView.Adapter mAdapter;

    private Integer imageRequestCode = 1;
    private Uri imageUri;
    private Boolean imageSet = false;

    // Auto-generated super method
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_recipe_info, container, false);

        initPageItems(view);
        initPageListeners(view);

        return view;
    }

    private void initPageItems(View view) {
        mRecipeImage = view.findViewById(R.id.recipeStepMedia);
        mRecipeName = view.findViewById(R.id.createRecipeName);
        mRecipeServes = view.findViewById(R.id.createRecipeServes);
        mRecipeDesc = view.findViewById(R.id.createRecipeDesc);
        mIngredientName = view.findViewById(R.id.createRecipeIngName);
        mIngredientMeasurement = view.findViewById(R.id.createRecipeMeasurement);
        mIngredients = view.findViewById(R.id.createRecipeList);
        mAddIngredient = view.findViewById(R.id.createRecipeButton);
        mEggs = view.findViewById(R.id.createRecipeEggs);
        mLactose = view.findViewById(R.id.createRecipeLactose);
        mNuts = view.findViewById(R.id.createRecipeNuts);
        mShellfish = view.findViewById(R.id.createRecipeShellfish);
        mSoya = view.findViewById(R.id.createRecipeSoya);
        mGluten = view.findViewById(R.id.createRecipeGluten);
        mDietDropdown = view.findViewById(R.id.createRecipeSpinner);
        mFridge = view.findViewById(R.id.createRecipeFridge);
        mFridgeDays = view.findViewById(R.id.createRecipeFridgeDays);
        mFrozen = view.findViewById(R.id.createRecipeFrozen);
        mReheat = view.findViewById(R.id.createRecipeReheatText);
        mSubmit = view.findViewById(R.id.createRecipeSubmit);

        mIngredientList = new ArrayList<>();
        mIngredientMap = new HashMap<>();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        mIngredients.setLayoutManager(manager);
        mAdapter = new IngredientRecyclerAdapter(mIngredientList);
        mIngredients.setAdapter(mAdapter);

        imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void initPageListeners(View view) {
        mRecipeImage.setOnClickListener(v -> {
            Intent mediaSelect = new Intent(Intent.ACTION_PICK);
            mediaSelect.setType("image/");
            String[] imageTypes = {"image/jpeg"};
            mediaSelect.putExtra(Intent.EXTRA_MIME_TYPES, imageTypes);
            startActivityForResult(mediaSelect, imageRequestCode);
        });

        mAddIngredient.setOnClickListener(v -> {
            view.findViewById(R.id.createRecipeContainer).requestFocus();
            imm.hideSoftInputFromWindow(Objects.requireNonNull(requireActivity().getCurrentFocus())
                            .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            String ingredientName = String.valueOf(mIngredientName.getText());
            mIngredientName.setText("");
            String ingredientMeasurement = String.valueOf(mIngredientMeasurement.getText());
            mIngredientMeasurement.setText("");

            mIngredientList.add(new IngredientData(ingredientName, ingredientMeasurement));

            mAdapter.notifyDataSetChanged();
        });

        mFridge.setOnClickListener(v -> {
            if (mFridge.isChecked())
                mFridgeDays.setVisibility(View.VISIBLE);
            else
                mFridgeDays.setVisibility(View.INVISIBLE);
        });

        mSubmit.setOnClickListener(v -> {
            if (String.valueOf(mRecipeName.getText()).trim().equals(""))
                Toast.makeText(getContext(), "Recipe must have a title",
                        Toast.LENGTH_SHORT).show();
            else if (String.valueOf(mRecipeServes.getText()).trim().equals(""))
                Toast.makeText(getContext(), "Recipe must have a serving amount",
                        Toast.LENGTH_SHORT).show();
            else if (String.valueOf(mRecipeDesc.getText()).trim().equals(""))
                Toast.makeText(getContext(), "Recipe must have a description",
                        Toast.LENGTH_SHORT).show();
            else if (Objects.requireNonNull(mIngredients.getAdapter()).getItemCount() == 0)
                Toast.makeText(getContext(), "Recipe must have ingredients",
                        Toast.LENGTH_SHORT).show();
            else if (!imageSet)
                Toast.makeText(getContext(), "Recipe must have a photo",
                        Toast.LENGTH_SHORT).show();
            else if (mFridge.isChecked() && String.valueOf(mFridgeDays.getText()).trim().equals(""))
                Toast.makeText(getContext(), "Enter amount of days recipe can be stored in fridge",
                        Toast.LENGTH_SHORT).show();
            else {
                Bundle bundle = new Bundle();
                bundle.putString("Name", String.valueOf(mRecipeName.getText()));
                bundle.putFloat("serves", Float.parseFloat(String.valueOf(mRecipeServes.getText())));
                bundle.putString("Description", String.valueOf(mRecipeDesc.getText()));
                bundle.putString("imageURL", imageUri.toString());
                for (IngredientData ingredientData : mIngredientList) {
                    mIngredientMap.put(ingredientData.ingredient, ingredientData.measurement);
                }
                bundle.putSerializable("Ingredients", mIngredientMap);
                bundle.putBoolean("noEggs", !mEggs.isChecked());
                bundle.putBoolean("noMilk", !mLactose.isChecked());
                bundle.putBoolean("noNuts", !mNuts.isChecked());
                bundle.putBoolean("noShellfish", !mShellfish.isChecked());
                bundle.putBoolean("noSoy", !mSoya.isChecked());
                bundle.putBoolean("noWheat", !mGluten.isChecked());
                if (mDietDropdown.getSelectedItem().toString().equals("Vegan")) {
                    bundle.putBoolean("vegan", true);
                    bundle.putBoolean("vegetarian", true);
                    bundle.putBoolean("pescatarian", true);
                } else if (mDietDropdown.getSelectedItem().toString().equals("Vegetarian")) {
                    bundle.putBoolean("vegan", false);
                    bundle.putBoolean("vegetarian", true);
                    bundle.putBoolean("pescatarian", true);
                } else if (mDietDropdown.getSelectedItem().toString().equals("Pescetarian")) {
                    bundle.putBoolean("vegan", false);
                    bundle.putBoolean("vegetarian", false);
                    bundle.putBoolean("pescatarian", true);
                } else {
                    bundle.putBoolean("vegan", false);
                    bundle.putBoolean("vegetarian", false);
                    bundle.putBoolean("pescatarian", false);
                }
                if (mFridge.isChecked())
                    bundle.putFloat("fridge", Float.parseFloat(String.valueOf(mFridgeDays.getText())));
                else
                    bundle.putFloat("fridge", 0);
                bundle.putBoolean("freezer", mFrozen.isChecked());
                bundle.putString("reheat", String.valueOf(mReheat.getText()));

                ((RecipeCreation) requireActivity()).stepComplete(1, bundle);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
            if (requestCode == imageRequestCode) {
                imageUri = data.getData();
                Picasso.get().load(imageUri).fit().centerCrop().into(mRecipeImage);
                imageSet = true;
            }
    }

}
