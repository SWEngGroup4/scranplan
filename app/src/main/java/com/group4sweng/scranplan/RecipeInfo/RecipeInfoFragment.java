package com.group4sweng.scranplan.RecipeInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.group4sweng.scranplan.Helper.RecipeHelpers;
import com.group4sweng.scranplan.MealPlanner.Ingredients.Ingredient;
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
    protected LinearLayout mLayoutForPlanner;
    private Button mLetsCook;
    private TabLayout mTabLayout2;
    private FrameLayout mRecipeFrameLayout;
    private TextView mTitle;
    private TextView mChefName;
    private TextView mDescription;
    private TextView mRating;
    private ImageView mRecipeImage;
    private CheckBox mFavourite;
    private CheckBox mKudos;
    private RatingBar mStars;
    protected TextView mServing;

    //Variables to hold the data being passed through into the fragment
    protected String recipeID;
    protected String recipeName;
    protected String recipeImage;
    protected String recipeDescription;
    protected String chefName;
    protected String recipeRating;
    protected String xmlPresentation;
    protected String reheat;
    private String docID;
    protected HashMap<String, String> ingredientHashMap;
    protected ArrayList<String> ingredientArray;
    protected HashMap<String, Double> ratingMap;
    protected Boolean planner;
    protected ArrayList<String> favouriteRecipe;
    protected UserInfoPrivate mUser;
    protected Boolean isFavourite;
    protected Boolean kudosGiven;
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
    protected ImageButton mChangePortions;
    //protected TextView mServing;
    protected String starRating;

    protected FirebaseFirestore mDatabase;
    protected CollectionReference mDataRef;
    protected CollectionReference mUserRef;

    // Define a String ArrayList for the ingredients
    protected ArrayList<String> ingredientList = new ArrayList<>();

    // Define a ListView to display the data
    protected LinearLayout linearLayoutIngredients;

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

        addKudos(layout);

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

        mKudos = layout.findViewById(R.id.addKudos);

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
        mLayoutForPlanner = layout.findViewById(R.id.mealPlannerLinearLayout);

        //For the Ingredient array
        linearLayoutIngredients = layout.findViewById(R.id.ingredient_list);

        //5 star rating bar
        mStars = layout.findViewById(R.id.ratingBar);

        docID = mUser.getUID() + "-" + recipeID;
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
        ingredientHashMap = (HashMap<String, String>) bundle.getSerializable("ingredientHashMap");
        ratingMap =  (HashMap<String, Double>) bundle.getSerializable("ratingMap");
        xmlPresentation = bundle.getString("xmlURL");
        planner = bundle.getBoolean("planner");
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
                    Intent i = new Intent();
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
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
                        reviewBundle.putString("recipeDescription", recipeDescription);
                        reviewBundle.putString("recipeTitle",recipeName);
                        reviewBundle.putString("recipeImageURL",recipeImage);
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

    protected void updateIngredientsList(){
        ArrayList<Ingredient> ingredientList = RecipeHelpers.convertToIngredientFormat(ingredientHashMap);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        for(Ingredient ingredient : ingredientList){
            View ingredientView = inflater.inflate(R.layout.ingredient, linearLayoutIngredients, false);

            TextView name = ingredientView.findViewById(R.id.ingredient_name);
            name.setText(ingredient.getName());

            TextView portion = ingredientView.findViewById(R.id.ingredient_portion);
            portion.setText(ingredient.getPortion());

            linearLayoutIngredients.addView(ingredientView);
        }
    }


    protected void displayInfo(View layout) {

        updateIngredientsList();

        //Assigning data passed through into the various xml views
        mTitle = layout.findViewById(R.id.Title);
        mChefName = layout.findViewById(R.id.chefName);
        mDescription = layout.findViewById(R.id.description);
        mRecipeImage = layout.findViewById(R.id.recipeImage);
        mChangePortions = layout.findViewById(R.id.changePortions);
        mChangePortions.setVisibility(View.INVISIBLE);

        //Sets the serving amount for each recipe
        mServing.setText("Serves: " + servingAmount);

        //Setting the recipe star rating
        starRating = ratingMap.get("overallRating").toString();
        updateStarRating(starRating);

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

        if (isFavourite) {
            mFavourite.setChecked(true);
        } else {
            mFavourite.setChecked(false);
        }
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
                    docRef.update("favourite", FieldValue.arrayUnion(mUser.getUID())).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(), "Added to favourites!",
                                    Toast.LENGTH_SHORT).show();
                            //mFavourite.setChecked(true);
                        }
                    });
                } else {
                    isFavourite = false;
                    docRef.update("favourite", FieldValue.arrayRemove(mUser.getUID())).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    /*
     * Method to give kudos to a recipe chef if the user likes the recipe
     */
    protected void addKudos(View layout) {

        //Checking firebase first to see if user has already given kudos to the creator of the recipe.
        mDatabase.collection("kudos").document(docID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().exists()){
                        kudosGiven = true;
                        mKudos.setChecked((boolean)task.getResult().get("kudosGiven"));
                        mKudos.setClickable(false);
                    }else{
                        kudosGiven = false;
                        mKudos.setChecked(false);
                    }
                    //Once kudos is given, icon changes to signify this aswell as the firebase being updated
                    mKudos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (mKudos.isChecked()) {

                                mKudos.setClickable(false);
                                HashMap<String, Object> kudosPost = new HashMap<>();
                                kudosPost.put("kudosGiven", true);
                                kudosPost.put("user", mUser.getUID());
                                kudosPost.put("recipe", recipeID);
                                mDatabase.collection("kudos").document(docID).set(kudosPost);
                                mDatabase.collection("users").document(chefName).update("kudos", FieldValue.increment(1));

                                Toast.makeText(getContext(), "Kudos to the Chef!",
                                        Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(getContext(), "Already given Kudos to the Chef!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Log.e("FdRc", "User details retrieval : Unable to retrieve user document in Firestore ");
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


}
