package com.group4sweng.scranplan.RecipeInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.group4sweng.scranplan.Presentation.Presentation;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;
import com.group4sweng.scranplan.Presentation;
import com.group4sweng.scranplan.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;


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
    private ImageButton mFavourite;

    //Variables to hold the data being passed through into the fragment
    private String recipeID;
    private String recipeName;
    private String recipeImage;
    private String recipeDescription;
    private String chefName;
    private String recipeRating;
    private String xmlPresentation;
    private ArrayList<String> ingredientArray;
    private ArrayList<String> favouriteRecipe;
    private UserInfoPrivate mUser;
    private Boolean isFavourite;
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
        favouriteRecipe = getArguments().getStringArrayList("favourite");
        recipeRating = getArguments().getString("rating");
        xmlPresentation = getArguments().getString("xmlURL");
        mUser = (UserInfoPrivate) getArguments().getSerializable("user");
        isFavourite = getArguments().getBoolean("isFav");
        planner = getArguments().getBoolean("planner");

        builder.setView(layout);

        displayInfo(layout);

        initPageListeners(layout);

        tabFragments(layout);

        addFavourite(layout);

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

        if (planner) {
            mLetsCook = layout.findViewById(R.id.LetsCook);
            mLetsCook.setText("Add"); // Changes button text
            mLetsCook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Adds recipe to planner
                    Fragment fragment = getTargetFragment();
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent());
                    dismiss();
                }
            });
        }
        else {
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

    /*
    * Add/Remove the favourite recipe by the star button which means that
    * add/remove the current user ID in the "favourite" array in the firestore.
    */
    private void addFavourite(View layout){

        mFavourite = layout.findViewById(R.id.addFavorite);
        mDataRef = mDatabase.collection("recipes");
        final DocumentReference docRef = mDataRef.document(recipeID);
        final String user = mUser.getUID();

        /*
        * After each operation, it will show the text "Added to favourites!" or "Removed from favourites!".
        * If the current use ID doesn't exist in the "favourite" array, the ID will be added to it and the
        * text "Added to favourites!" will appear and vise versa.
        * */
        mFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFavourite){
                    isFavourite = true;
                    docRef.update("favourite", FieldValue.arrayUnion(user)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(),"Added to favourites!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    isFavourite = false;
                    docRef.update("favourite", FieldValue.arrayRemove(user)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(),"Removed from favourites!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }
}