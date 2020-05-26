package com.group4sweng.scranplan.MealPlanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.Home;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.SearchFunctions.RecipeFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchPrefs;
import com.group4sweng.scranplan.SearchFunctions.SearchQuery;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PlannerFragment extends Fragment {

    //Database references
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mUserRef = mDatabase.collection("users");

    //List for storing current meal plan
    private List<HashMap<String, Object>> plannerList = new ArrayList<>();
    //Used for hardcoded string generation
    private List<String> days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
    private ImageButton currentSelection; //Keeps track of position user pressed

    //Fragment handlers
    private FragmentTransaction fragmentTransaction;
    private RecipeFragment recipeFragment;

    //User information
    private com.group4sweng.scranplan.UserInfo.UserInfoPrivate mUser;
    private SearchPrefs prefs;

    //Menu items
    private SearchView searchView;
    private MenuItem sortButton;

    private Integer recipeFragmentRequest = 1;

    public PlannerFragment(UserInfoPrivate userSent){mUser = userSent;}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Generates layouts and buttons for layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_planner, container, false);

        //Grabs user and user's meal planner
        if (mUser != null) plannerList = mUser.getMealPlanner();

        Home home = (Home) getActivity();
        if (home != null) {
            // Gets search activity from home class and make it invisible
            searchView = home.getSearchView();
            sortButton = home.getSortView();

            sortButton.setVisible(false);
            searchView.setVisibility(View.INVISIBLE);
            setSearch();



            //Gets search preferences from home class
            prefs = home.getSearchPrefs();
        }

        //Generates rows of meal planners
        LinearLayout topView = view.findViewById(R.id.plannerLinearLayout);
        for (int i = 0; i < 7; i++) {
            TextView textView = new TextView(view.getContext());
            textView.setText(days.get(i)); //Sets text to day string

            //Creates and formats linear layout container for recipes
            LinearLayout linearLayout = new LinearLayout(view.getContext());
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setWeightSum(3);

            //Generates image buttons housing planned recipes
            for (int j = 0; j < 3; j++) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f
                );

                //Creates image button
                final ImageButton imageButton = new ImageButton(view.getContext());
                final int id = (i*3) + j; //ID resembles sequential position
                imageButton.setId(id);
                imageButton.setLayoutParams(params);
                imageButton.setAdjustViewBounds(true);
                imageButton.setPadding(10,10,10,10);
                imageButton.setBackground(null);

                //Sets listener for long clicks - resetting buttons to their default state
                imageButton.setOnLongClickListener(v -> {
                    defaultButton(imageButton);
                    return true;
                });

                //If the planner doesn't have a meal entry for the time period
                if (plannerList.get(id) == null) {
                    defaultButton(imageButton); //Default state
                } else {
                    //Loads image of recipe and sets click action to open the recipe info dialog
                    Picasso.get().load(Objects.requireNonNull(plannerList.get(id).get("imageURL")).toString()).into(imageButton);
                    imageButton.setOnClickListener(v -> openRecipeInfo(plannerList.get(id)));
                }
                linearLayout.addView(imageButton); //Adds button to layout
            }
            topView.addView(textView); //Adds day of the week to fragment
            topView.addView(linearLayout); // Adds buttons to the layout
        }

        return view;
    }

    //Opens list fragment on searching
    private void openRecipeDialog(SearchQuery query) {
        // Creates and launches fragment with required query
        PlannerListFragment plannerListFragment = new PlannerListFragment(mUser);
        plannerListFragment.setValue(query.getQuery());
        plannerListFragment.setIndex(query.getIndex());
        plannerListFragment.setTargetFragment(this, 1);
        plannerListFragment.show(getParentFragmentManager(), "search");
    }

    // Sets default parameters for buttons
    private void defaultButton(final ImageButton imageButton) {
        imageButton.setImageResource(R.drawable.add); //Default image
        imageButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putBoolean("planner", true); //Condition to let child fragments know access is from planner
            currentSelection = imageButton; //Allows tracking of button pressed

            //Creates and launches recipe fragment
            recipeFragment = new RecipeFragment();
            recipeFragment.setArguments(bundle);
            recipeFragment.setTargetFragment(PlannerFragment.this, recipeFragmentRequest);
            fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.frameLayout, recipeFragment); //Overlays fragment on existing one
            fragmentTransaction.commitNow(); //Waits for fragment transaction to be completed
            requireView().setVisibility(View.INVISIBLE); //Sets current fragment invisible

            //Makes search bar icon visible
            searchView.setQuery("", false);
            searchView.setVisibility(View.VISIBLE);
            setSearch();
        });

        //Updates planner and user
        plannerList.set(imageButton.getId(), null);
        mUser.setMealPlanner(plannerList);
        updateMealPlan();
    }

    //Opens info dialog for selected recipe
    private void openRecipeInfo(HashMap<String, Object> map) {
        map.put("planner", false); //Allows lauching of presentation
        Bundle bundle = new Bundle();
        bundle.putSerializable("hashmap", map);

        //Creates and launches info fragment
        PlannerInfoFragment plannerInfoFragment = new PlannerInfoFragment();
        plannerInfoFragment.setArguments(bundle);
        plannerInfoFragment.show(getParentFragmentManager(), "Show recipe dialog fragment");
    }

    //Handles child fragment exit results
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == recipeFragmentRequest) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();

                //Hides menu options
                sortButton.setVisible(false);

                //Clears search view
                searchView.clearFocus();
                searchView.onActionViewCollapsed();
                searchView.setVisibility(View.INVISIBLE);

                //Compiles bundle into a hashmap object for serialization
                final HashMap<String, Object> map = new HashMap<>();
                if (bundle != null) {
                    for (String key : bundle.keySet()) {
                        map.put(key, bundle.get(key));
                    }

                    //  Adds the ingredient Hash Map
                    HashMap<String, String> ingredientHashMap = (HashMap<String, String>) data.getSerializableExtra("ingredientHashMap");
                    if(ingredientHashMap != null){ map.put("ingredientHashMap", ingredientHashMap); }

                    //Sets new listener for inserted recipe to open info fragment
                    currentSelection.setOnClickListener(v -> openRecipeInfo(map));

                    //Loads recipe image
                    Picasso.get().load(bundle.getString("imageURL")).into(currentSelection);

                    //Sets and updates user planner
                    plannerList.set(currentSelection.getId(), map);
                    mUser.setMealPlanner(plannerList);
                    updateMealPlan();
                }
            }
            //Removes recipe fragment overlay and makes planner fragment visible
            fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.remove(recipeFragment).commitNow();
            requireView().setVisibility(View.VISIBLE);
        }
    }

    //Quick function to reset search menu functionality
    private void setSearch() {
        Home home = (Home) getActivity();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                prefs = home.getSearchPrefs();
                SearchQuery query = new SearchQuery( s, prefs);
                openRecipeDialog(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    //Updates meal plan for user on the database
    private void updateMealPlan() {
        HashMap<String, Object> updateMap = new HashMap<>();
        DocumentReference documentReference = mUserRef.document(mUser.getUID());
        updateMap.put("mealPlan", plannerList);
        documentReference.update(updateMap);
    }
}
