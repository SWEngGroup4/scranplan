package com.group4sweng.scranplan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class RecipeInfoFragment extends AppCompatDialogFragment {

    // Variables for the xml layout so data from firebase can be properly assigned
    protected Button mReturnButton;
    private Button mLetsCook;
    private TabLayout mTabLayout2;
    private FrameLayout mRecipeFrameLayout;
    private TextView mTitle;
    private TextView mChefName;
    private TextView mDescription;
    private TextView mRating;
    private ImageView mRecipeImage;

    //Variables to hold the data being passed through into the fragment
    protected String recipeID;
    protected String recipeName;
    protected String recipeImage;
    protected String recipeDescription;
    protected String chefName;
    protected String recipeRating;
    protected String xmlPresentation;
    protected ArrayList<String> ingredientArray;
    protected Boolean planner;

    private FirebaseFirestore mDatabase;
    private CollectionReference mDataRef;
    private CollectionReference mUserRef;

    // Define a String ArrayList for the ingredients
    private ArrayList<String> ingredientList = new ArrayList<>();

    // Define a ListView to display the data
    private ListView listViewIngredients;

    // Define an ArrayAdapter for the list
    private ArrayAdapter<String> arrayAdapter;


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

        recipeID = getArguments().getString("recipeID");
        recipeName = getArguments().getString("recipeTitle");
        recipeImage = getArguments().getString("imageURL");
        recipeDescription = getArguments().getString("recipeDescription");
        chefName = getArguments().getString("chefName");
        ingredientArray = getArguments().getStringArrayList("ingredientList");
        recipeRating = getArguments().getString("rating");
        xmlPresentation = getArguments().getString("xmlURL");
        planner = getArguments().getBoolean("planner");

        builder.setView(layout);

        displayInfo(layout);

        initPageListeners(layout);

        tabFragments(layout);

        return layout;
    }

    /**
     * Method that scales the pop up dialog box to fill the majority of the screen
     */
    public void onResume(){
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

    }

    /**
     * When back button is clicked within the recipe information dialogFragment,
     * Recipe information dialogFragment is closed and returns to recipe fragment
     */
    protected void initPageListeners(View layout){

        mReturnButton = layout.findViewById(R.id.ReturnButton);
        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        // Handles info received from Meal Planner searches
        if (planner) {
            mLetsCook = layout.findViewById(R.id.LetsCook);
            mLetsCook.setText("Add"); // Changes button text
            mLetsCook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Adds recipe to planner
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
                    startActivity(presentation);
                }
            });
        }
    }

    /**
     * Method that controls the tabs within the recipe information dialogFragment
     * to select between the ingredient information and the comments section
     */
    protected void tabFragments(final View layout){

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
                        fragment = new RecipeCommentsFragment();
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

    protected void displayInfo(View layout){

        //Getting ingredients array and assigning it to the list layout view
        listViewIngredients = layout.findViewById(R.id.listViewText);
        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, ingredientList);
        arrayAdapter.addAll(ingredientArray);
        listViewIngredients.setAdapter(arrayAdapter);

        //Assigning data passed through into the various xml views
        mTitle = layout.findViewById(R.id.Title);
        mChefName = layout.findViewById(R.id.chefName);
        mDescription = layout.findViewById(R.id.description);
        mRecipeImage = layout.findViewById(R.id.recipeImage);
        mRating = layout.findViewById(R.id.Rating);
        mTitle.setText(recipeName);
        mDescription.setText(recipeDescription);
        mRating.setText("Rating: " + recipeRating);
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
                if (task.isSuccessful()){

                    /**
                     * Using the UID from the Chef field in recipes, the following takes a snapshot of
                     * all the fields associated with the UID in users and assigns the name to Chef on the xml
                     * since that users UID is associated with creating that recipe
                     */
                    final DocumentReference userRef = mUserRef.document(chefName);
                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()){

                                //Takes objects from the firestore and assigns them to their relevant variables
                                DocumentSnapshot userDocument = task.getResult();
                                mChefName.setText("Chef: " +(userDocument.getData().get("displayName").toString()));

                            }
                        }
                    });
                }
            }
        });
    }

}
