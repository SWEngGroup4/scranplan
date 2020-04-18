package com.group4sweng.scranplan.RecipeInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.Helper.ImageHelpers;
import com.group4sweng.scranplan.Presentation.Presentation;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.UserInfo.FilterType;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


public class RecipeInfoFragment extends AppCompatDialogFragment implements FilterType {

    // Variables for the xml layout so data from firebase can be properly assigned
    protected ImageButton mReturnButton;
    private Button mLetsCook;
    private TabLayout mTabLayout2;
    private FrameLayout mRecipeFrameLayout;
    private TextView mTitle;
    private TextView mChefName;
    private TextView mDescription;
    private TextView mRating;
    private ImageView mRecipeImage;
    private CheckBox mFavourite;
    private RatingBar mStars;
    private TextView mServing;

    //Variables to hold the data being passed through into the fragment
    protected String recipeID;
    protected String recipeName;
    protected String recipeImage;
    protected String recipeDescription;
    protected String chefName;
    protected String recipeRating;
    protected String xmlPresentation;
    protected String reheat;
    protected ArrayList<String> ingredientArray;
    protected HashMap<String, Double> ratingMap;
    protected Boolean planner;
    protected ArrayList<String> favouriteRecipe;
    protected UserInfoPrivate mUser;
    protected Boolean isFavourite;
    protected String servingAmount;
    protected String fridgeTime;
    protected Boolean canFreeze;
    protected Boolean noEggs;
    protected Boolean noMilk;
    protected Boolean noNuts;
    protected Boolean noShellfish;
    protected Boolean noSoy;
    protected Boolean noWheat;
    protected Boolean mPescatarian;
    protected Boolean mVegan;
    protected Boolean mVegetarian;
    protected TextView mFridge;
    protected TextView mFreezer;
    protected TextView mReheatInformation;
    protected ImageButton mReheatInformationButton;
    protected String starRating;


    private FirebaseFirestore mDatabase;
    private CollectionReference mDataRef;
    private CollectionReference mUserRef;

    // Define a String ArrayList for the ingredients
    protected ArrayList<String> ingredientList = new ArrayList<>();

    // Define a ListView to display the data
    protected LinearLayout listViewIngredients;

    // Define an ArrayAdapter for the list
    protected ArrayAdapter<String> arrayAdapter;


    // Auto-generated super method
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View layout = inflater.inflate(R.layout.fragment_recipe_info, null);

        builder.setView(layout);

        //This method holds all the arguments from the bundle
        initBundleItems(layout, getArguments());

        initPageItems(layout);

        displayInfo(layout);

        allergyDisplay(layout);

        initPageListeners(layout);

        tabFragments(layout);

        addFavourite(layout);




        return layout;
    }

    /**
     * Method that scales the pop up dialog box to fill the majority of the screen
     */
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

    }

    //Assigning data passed through into the various xml views
    protected void initPageItems(View layout) {

        //Buttons
        mReturnButton = layout.findViewById(R.id.ReturnButton);
        mLetsCook = layout.findViewById(R.id.LetsCook);
        mReheatInformationButton = layout.findViewById(R.id.reheatInfoButton);
        mFavourite = layout.findViewById(R.id.addFavorite);
        mFavourite.setChecked(isFavourite);

        //Text Views
        mTitle = layout.findViewById(R.id.Title);
        mChefName = layout.findViewById(R.id.chefName);
        mDescription = layout.findViewById(R.id.description);
        mRecipeImage = layout.findViewById(R.id.recipeImage);
        mServing = layout.findViewById(R.id.serves);
        mFridge = layout.findViewById(R.id.fridge);
        mFreezer = layout.findViewById(R.id.freezer);
        mReheatInformation = layout.findViewById(R.id.reheatInfoText);

        //Tab Layouts
        mTabLayout2 = layout.findViewById(R.id.tabLayout2);
        mRecipeFrameLayout = layout.findViewById(R.id.RecipeFrameLayout);

        //For the Ingredient array
       listViewIngredients = layout.findViewById(R.id.listViewText);

        //5 star rating bar
        mStars = layout.findViewById(R.id.ratingBar);
    }


    /**
     * Arguments from the bundle passed into the fragment that contains data for the info page from the firestore
     */
    private void initBundleItems(View layout, Bundle bundle) {

        recipeID = bundle.getString("recipeID");
        recipeName = bundle.getString("recipeTitle");
        recipeImage = bundle.getString("imageURL");
        recipeDescription = bundle.getString("recipeDescription");
        chefName = bundle.getString("chefName");
        ingredientArray = bundle.getStringArrayList("ingredientList");
        ratingMap =  (HashMap<String, Double>) bundle.getSerializable("ratingMap");
        recipeRating = bundle.getString("rating");
        xmlPresentation = bundle.getString("xmlURL");
        planner = bundle.getBoolean("planner");
        recipeRating = bundle.getString("rating");
        reheat = bundle.getString("reheat");
        noEggs = bundle.getBoolean("noEggs");
        noMilk = bundle.getBoolean("noMilk");
        noNuts = bundle.getBoolean("noNuts");
        noShellfish = bundle.getBoolean("noShellfish");
        noSoy = bundle.getBoolean("noSoy");
        noWheat = bundle.getBoolean("noWheat");
        mPescatarian = bundle.getBoolean("pescatarian");
        mVegan = bundle.getBoolean("vegan");
        mVegetarian = bundle.getBoolean("vegetarian");
        servingAmount = bundle.getString("peopleServes");
        canFreeze = bundle.getBoolean("canFreeze");
        fridgeTime = bundle.getString("fridgeDays");
        favouriteRecipe = getArguments().getStringArrayList("favourite");
        mUser = (com.group4sweng.scranplan.UserInfo.UserInfoPrivate) requireActivity().getIntent().getSerializableExtra("user");
        isFavourite = getArguments().getBoolean("isFav");

    }

    /**
     * When back button is clicked within the recipe information dialogFragment,
     * Recipe information dialogFragment is closed and returns to recipe fragment
     */
    protected void initPageListeners(View layout) {

        mReturnButton = layout.findViewById(R.id.ReturnButton);
        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();

            }
        });

        // Handles info received from Meal Planner searches
        if (planner) {

            // Changes button text
            mLetsCook.setText("Add");
            // Sets fridge text
            mFridge.setText("Keep in Fridge: " + fridgeTime + " days");

            //If canFreeze boolean is set to true then the screen will display that the meal can be frozen
            if (canFreeze == true) {
                mFreezer = layout.findViewById(R.id.freezer);
                mFreezer.setText("Can be frozen");
            } else {
                mFreezer.setText("Cannot be frozen");
            }

            //Sets reheat information text above the reheat information button
            mReheatInformation.setText("Reheat Information");

            //Sets the reheat information button to visible for the paying user and creates a dialog that hold the
            //reheat information
            mReheatInformationButton.setVisibility(View.VISIBLE);
            mReheatInformationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setMessage(reheat)
                            .setTitle("Reheating Information")
                            .setIcon(R.drawable.reheat);

                    AlertDialog dialog = builder.create();

                    dialog.show();

                }
            });

            mLetsCook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Adds recipe to planner
                    Fragment fragment = getTargetFragment();
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent());
                    dismiss();
                }
            });
        } else {
            mLetsCook = layout.findViewById(R.id.LetsCook);
            mLetsCook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent presentation = new Intent(getActivity(), Presentation.class);
                    presentation.putExtra("xml_URL", xmlPresentation);
                    presentation.putExtra("recipeID", recipeID);
                    presentation.putExtra("user", mUser);
                    startActivity(presentation);
                }
            });
        }
    }

    /**
     * Method that controls the tabs within the recipe information dialogFragment
     * to select between the ingredient information and the comments section
     */
    protected void tabFragments(final View layout) {

        mTabLayout2 = layout.findViewById(R.id.tabLayout2);
        mRecipeFrameLayout = layout.findViewById(R.id.RecipeFrameLayout);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.RecipeFrameLayout, new RecipeIngredientFragment());
        fragmentTransaction.commit();

        mTabLayout2.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        fragment = new RecipeIngredientFragment();
                        break;
                    case 1:
                        fragment = new RecipeReviewFragment();
                        //creating new bundle to pass through relative information to the review fragment
                        Bundle reviewBundle = new Bundle();
                        reviewBundle.putSerializable("ratingMap", ratingMap);
                        reviewBundle.putString("recipeID", recipeID);
                        fragment.setArguments(reviewBundle);
                        break;

                }
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
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


    }

    protected void displayInfo(View layout) {

        //Getting ingredients array and assigning it to the linear layout view
        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, ingredientList);
        arrayAdapter.addAll(ingredientArray);

        //Assigning data passed through into the various xml views
        mTitle = layout.findViewById(R.id.Title);
        mChefName = layout.findViewById(R.id.chefName);
        mDescription = layout.findViewById(R.id.description);
        mRecipeImage = layout.findViewById(R.id.recipeImage);
        final int adapterCount = arrayAdapter.getCount();

        for (int i = 0; i < adapterCount; i++) {
            View item = arrayAdapter.getView(i, null, null);
            listViewIngredients.addView(item);
        }


        //Sets the serving amount for each recipe
        mServing.setText("Serves: " + servingAmount);




        //Setting the recipe star rating
        starRating = ratingMap.get("overallRating").toString();
        updateStarRating(starRating);
//        mStars.setRating(Float.parseFloat(starRating));
//        mStars.setIsIndicator(true);
//        mStars.setNumStars(5);
//        mStars.setStepSize(0.1F);

        //setting the recipe title
        mTitle.setText(recipeName);
        mDescription.setText(recipeDescription);
        Picasso.get().load(recipeImage).into(mRecipeImage);


        // Database objects for accessing recipes
        mDatabase = FirebaseFirestore.getInstance();
        mDataRef = mDatabase.collection("recipes");
        mUserRef = mDatabase.collection("users");

        final DocumentReference docRef = mDataRef.document(recipeID);

        /**
         * Adds OnCompleteListener that gets snapshot of objects from the firestore
         * taking the objects from the snapshot to assign them to the xml variables for displaying
         */
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    /**
                     * Using the UID from the Chef field in recipes, the following takes a snapshot of
                     * all the fields associated with the UID in users and assigns the name to Chef on the xml
                     * since that users UID is associated with creating that recipe
                     */
                    final DocumentReference userRef = mUserRef.document(chefName);
                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {

                                //Takes objects from the firestore and assigns them to their relevant variables
                                DocumentSnapshot userDocument = task.getResult();
                                mChefName.setText("Chef: " + (userDocument.getData().get("displayName").toString()));

                            }
                        }
                    });
                }
            }
        });
    }

    /*
     * Add/Remove the favourite recipe by the star button which means that
     * add/remove the current user ID in the "favourite" array in the firestore.
     */
    protected void addFavourite(View layout) {


        mDataRef = mDatabase.collection("recipes");
        final DocumentReference docRef = mDataRef.document(recipeID);
        final String user = mUser.getUID();

        /*
         * After each operation, it will show the text "Added to favourites!" or "Removed from favourites!".
         * If the current use ID doesn't exist in the "favourite" array, the ID will be added to it and the
         * text "Added to favourites!" will appear and vise versa.
         * */
        mFavourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mFavourite.isChecked()) {
                    isFavourite = true;
                    docRef.update("favourite", FieldValue.arrayUnion(user)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(), "Added to favourites!",
                                    Toast.LENGTH_SHORT).show();
                            //mFavourite.setChecked(true);
                        }
                    });
                } else {
                    isFavourite = false;
                    docRef.update("favourite", FieldValue.arrayRemove(user)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(), "Removed from favourites!",
                                    Toast.LENGTH_SHORT).show();
                            //mFavourite.setChecked(false);
                        }
                    });
                }
            }
        });
    }

    /**
     * Method that cycles through allergies and eating preference within the recipe collection
     * and creates an ImageView that displays on a recipe so the user knows what allergy it contains.
     */
    protected void allergyDisplay(View layout) {
        initFiltersIcons(layout, filterType.ALLERGENS);
        initFiltersIcons(layout, filterType.DIETARY);

        //allergyIconCreation(layout);
        //eatingAdviceIconCreation(layout);
    }

    private void initFiltersIcons(View layout, FilterType.filterType type){
        ArrayList<Boolean> filterValues = new ArrayList<>();
        ArrayList<ImageView> filterIcons = new ArrayList<>();
        ArrayList<String> filterMessages = ImageHelpers.getFilterIconsHoverMessage(type);
        final String title;

        switch(type){
            case ALLERGENS:
                title = "Allergy";
                filterValues.add(noEggs);
                filterValues.add(noMilk);
                filterValues.add(noNuts);
                filterValues.add(noShellfish);
                filterValues.add(noSoy);
                filterValues.add(noWheat);

                filterIcons.add(layout.findViewById(R.id.recipeInfoEggs));
                filterIcons.add(layout.findViewById(R.id.recipeInfoMilk));
                filterIcons.add(layout.findViewById(R.id.recipeInfoNuts));
                filterIcons.add(layout.findViewById(R.id.recipeInfoShellfish));
                filterIcons.add(layout.findViewById(R.id.recipeInfoSoy));
                filterIcons.add(layout.findViewById(R.id.recipeInfoWheat));
                break;
            case DIETARY:
                title = "Dietary";
                filterValues.add(mPescatarian);
                filterValues.add(mVegan);
                filterValues.add(mVegetarian);

                filterIcons.add(layout.findViewById(R.id.recipeInfoPesc));
                filterIcons.add(layout.findViewById(R.id.recipeInfoVegan));
                filterIcons.add(layout.findViewById(R.id.recipeInfoVeggie));
                break;
            default:
                title = "";
        }


        for(int i = 0; i < filterIcons.size(); i++){
            if (!filterValues.get(i)) {
                ImageView icon = filterIcons.get(i);
                String message = filterMessages.get(i);

                icon.setVisibility(View.VISIBLE);
                icon.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    AlertDialog dialog = builder.create();
                    dialog.setTitle(title);
                    dialog.setMessage(message);
                    dialog.show();
                });
            }
        }
    }

    public void updateStarRating(String newRating){

        mStars.setRating(Float.parseFloat(newRating));
        mStars.setIsIndicator(true);
        mStars.setNumStars(5);
        mStars.setStepSize(0.1F);
        starRating = newRating;

    }


    /**
     * Method that allows for the creation of the allergies icons and displays them onto the recipe information screen
     */
    /*
    private void allergyIconCreation(View layout){

    //Arrays that hold the boolean values and imageView id's for the allergy ingredients
    ArrayList<Boolean> allergyValue = new ArrayList<>();
        allergyValue.add(noEggs);
        allergyValue.add(noMilk);
        allergyValue.add(noNuts);
        allergyValue.add(noShellfish);
        allergyValue.add(noSoy);
        allergyValue.add(noWheat);

        ArrayList<String> allergyMessage = ImageHelpers.getFilterIconsHoverMessage(FilterType.filterType.ALLERGENS);

    ArrayList<ImageView> list = new ArrayList<>();
        list.add(layout.findViewById(R.id.recipeInfoEggs));
        list.add(layout.findViewById(R.id.recipeInfoMilk));
        list.add(layout.findViewById(R.id.recipeInfoNuts));
        list.add(layout.findViewById(R.id.recipeInfoShellfish));
        list.add(layout.findViewById(R.id.recipeInfoSoy));
        list.add(layout.findViewById(R.id.recipeInfoWheat));

    //Integers that hold the sizes of the two different category array sizes
    final int arrayCount1 = allergyValue.size();

    //For loop that iterates through both allergy ingredient arrays and if a recipe holds a certain
    //allergy then that allergy icon is set to visible
        for(
    int i = 0;
    i<arrayCount1;i++)

    {
        if (!allergyValue.get(i)) {
            ImageView createAllergyIcon = list.get(i);
            String message = allergyMessage.get(i);
            createAllergyIcon.setVisibility(View.VISIBLE);
            createAllergyIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    AlertDialog dialog = builder.create();

                    dialog.setTitle("Allergy");
                    dialog.setMessage(message);
                    dialog.show();
                }
            });
        }
    }


}*/

    /**
     * Method that allows for the creation of the eating preference icons and displays them onto the recipe information screen
     */
    /*
    private void eatingAdviceIconCreation(View layout) {

        //Arrays that hold the boolean values and ImageView id's for the eating preference
        ArrayList<Boolean> EatingHabitValue = new ArrayList<>();
        EatingHabitValue.add(mPescatarian);
        EatingHabitValue.add(mVegan);
        EatingHabitValue.add(mVegetarian);

        ArrayList<String> EatingHabitMessage = ImageHelpers.getFilterIconsHoverMessage(FilterType.filterType.DIETARY);

        ArrayList<ImageView> EatingHabit = new ArrayList<>();
        EatingHabit.add(layout.findViewById(R.id.recipeInfoPesc));
        EatingHabit.add(layout.findViewById(R.id.recipeInfoVegan));
        EatingHabit.add(layout.findViewById(R.id.recipeInfoVeggie));

        //Integers that hold the sizes of the two different category array sizes
        final int arrayCount2 = EatingHabit.size();


        //For loop that iterates through the eating preference arrays and if a recipe is either vegan, vegetarian
        //or pescatarian then the imageView is made visible
        for (int i = 0; i < arrayCount2; i++) {
            if (EatingHabitValue.get(i)) {
                ImageView eatingPreferenceIcon = EatingHabit.get(i);
                String message = EatingHabitMessage.get(i);
                eatingPreferenceIcon.setVisibility(View.VISIBLE);
                eatingPreferenceIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        AlertDialog dialog = builder.create();

                        dialog.setTitle("Recipe Advice");
                        dialog.setMessage(message);
                        dialog.show();
                    }
                });
            }
        }


    }*/

}
