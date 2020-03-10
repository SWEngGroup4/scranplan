package com.group4sweng.scranplan;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.UserInfo.FilterType;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.HashMap;

import static com.group4sweng.scranplan.UserInfo.FilterType.filterType.ALLERGENS;

/** Class retrieves and displays public profile data on a given user based on a valid UID input string or UserInfoPrivate object.
 *  from Firebase or from local data.
 *
 *  For firebase pass an extra string via intent with ID = "UID"
 *  For local data pass a UserInfoPrivate object via intent with ID = "user"
 *
 *  Privacy checks are made within 'loadInPrivacySettings' to make sure we adhere to what the user wants to display.
 *  Privacy is always checked first before displaying any content.
 */
public class PublicProfile extends AppCompatActivity implements FilterType{

    // TAG for Profile Settings
    final String TAG = "PublicProfile";

    //  UID for the user in which we want to retrieve data from.
    private String UID;
    private UserInfoPrivate mUserProfile;

    //  Default filter type enumeration. Types shown in 'FilterType' interface.
    FilterType.filterType currentFilterType = ALLERGENS;

    // Firebase user variables.
    FirebaseApp mApp;
    FirebaseAuth mAuth;
    FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    //  Basic user settings.
    ImageView mProfileImage;
    TextView mUsername;
    TextView mAboutMe;
    TextView mNumRecipes;
    RecyclerView mFilters;

    //  Profile allergen filters.
    CheckBox mAllergy_nuts;
    CheckBox mAllergy_milk;
    CheckBox mAllergy_eggs;
    CheckBox mAllergy_shellfish;
    CheckBox mAllergy_soy;
    CheckBox mAllergy_gluten;

    //  Whether we should retrieve different information for the user. E.g. username, about me etc...
    private boolean retrieveAboutMe = false, retrieveUsername = false,
            retrieveImages = false, retrieveRecipes = false, retrieveFilters = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profile);

        initPageItems();
        //TODO add method to pass intent with a valid UID string.

    }

    @Override
    protected void onStart() {
        super.onStart();

        //  Gets extra string UID attached when an intent is sent.
        UID = getIntent().getStringExtra("UID");

        //  Gets extra UserProfilePrivate object if one is sent.
        mUserProfile = (UserInfoPrivate) getIntent().getSerializableExtra("user");

        if(mUserProfile != null){ // Check if local data is available to reference. Don't have to grab from firebase.
            Log.i(TAG, "Loading local user data");
            loadInPrivacySettings(mUserProfile.getPrivacy());
            loadLocalProfile();
        } else if(UID != null){ // If not instead search for the profile via the associated UID and reference Firebase.
            Log.i(TAG, "Loading data from Firebase");
            loadFirebase();
        } else {
            Log.e(TAG, "Unable to retrieve extra UID intent string. Cannot initialize profile.");
        }

    }

    private void initPageItems(){
        mProfileImage = findViewById(R.id.public_profile_image);
        mAboutMe = findViewById(R.id.public_profile_about_me_desc);
        mNumRecipes = findViewById(R.id.profile_recipes);
        mUsername = findViewById(R.id.profile_username);

        //  Allergens
        mAllergy_eggs = findViewById(R.id.allergy_eggs);
        mAllergy_gluten = findViewById(R.id.allergy_wheat);
        mAllergy_milk = findViewById(R.id.allergy_milk);
        mAllergy_nuts = findViewById(R.id.allergy_nuts);
        mAllergy_shellfish = findViewById(R.id.allergy_shellfish);
        mAllergy_soy = findViewById(R.id.allergy_soy);
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent(this, MainActivity.class);
        startActivity(returnIntent);
        finish(); //    We don't need to send anything back but do need to destroy the current activity.
    }

    //  Checks for if a user wants specific data to be retrieved based on there privacy settings.
    private void loadInPrivacySettings(HashMap<String, Object> privacy){
        if(privacy != null){
            retrieveAboutMe = (boolean) privacy.get("display_about_me");
            retrieveUsername = (boolean) privacy.get("display_username");
            retrieveRecipes = (boolean) privacy.get("display_recipes");
            retrieveImages = (boolean) privacy.get("display_profile_image");
            retrieveFilters = (boolean) privacy.get("display_filters");
        }
    }

    //  Load all the data grabbed from the Firebase document snapshot.
    private void loadProfile(DocumentSnapshot profile) {
        if(retrieveAboutMe){ mAboutMe.setText((String) profile.get("about")); } //  If we are allowed to retrieve this data. do so.
        /* TODO- Implement Image retrieval later,
        if(retrieveImages){ }
        */
        String numOfRecipesString = "Recipes: " + ((long) profile.get("numRecipes")); //  Convert 'long' value to something we can use.
        if(retrieveRecipes){ mNumRecipes.setText(numOfRecipesString);} else {
            mNumRecipes.setText(""); // Set number of recipes to nothing if hidden.
        }

        if(retrieveUsername){ mUsername.setText((String) profile.get("displayName")); }

        if(retrieveFilters){
            @SuppressWarnings("unchecked")
            HashMap<String, Object> filters = (HashMap<String, Object>) profile.get("preferences");
            setFilters(currentFilterType, filters);
        } else { // Remove all checkboxes if filters are hidden.
            makeFiltersInvisible();
        }
    }

    private void makeFiltersInvisible(){
        switch(currentFilterType){
            case ALLERGENS:
                Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT); // Create a transparent drawable background.

                //  Remove checkboxes by setting a transparent background.
                mAllergy_gluten.setButtonDrawable(transparentDrawable);
                mAllergy_milk.setButtonDrawable(transparentDrawable);
                mAllergy_soy.setButtonDrawable(transparentDrawable);
                mAllergy_shellfish.setButtonDrawable(transparentDrawable);
                mAllergy_eggs.setButtonDrawable(transparentDrawable);
                mAllergy_nuts.setButtonDrawable(transparentDrawable);

                //  Set top-left element (nuts) to a new 'hidden info' string and remove text from all other filters.
                mAllergy_nuts.setText((String) "Filter information has been hidden");
                mAllergy_gluten.setText("");
                mAllergy_soy.setText("");
                mAllergy_shellfish.setText("");
                mAllergy_eggs.setText("");
                mAllergy_milk.setText("");
            case RELIGIOUS:
                //TODO
                Log.i(TAG, "Religious");
            case DIETARY:
                //TODO
                Log.i(TAG, "Religious");
            case HEALTH:
                //TODO
                Log.i(TAG, "Religious");
        }

    }

    /** Set which filter checkboxes should be selected
     * @param type - Enumeration of the type of filter to be displayed. E.g. Allegern, Religious...
     */
    private void setFilters(filterType type, HashMap<String, Object> filters){
        switch(type){
            case ALLERGENS:
                mAllergy_eggs.setChecked((boolean) filters.get("allergy_eggs"));
                mAllergy_milk.setChecked((boolean) filters.get("allergy_milk"));
                mAllergy_nuts.setChecked((boolean) filters.get("allergy_nuts"));
                mAllergy_shellfish.setChecked((boolean) filters.get("allergy_shellfish"));
                mAllergy_soy.setChecked((boolean) filters.get("allergy_soya"));
                mAllergy_gluten.setChecked((boolean) filters.get("allergy_gluten"));
            case RELIGIOUS:
                //TODO
            case DIETARY:
                //TODO
            case HEALTH:
                //TODO
        }
    }

    //  Load in all associated public profile data for a given UID. (defined at Activity Start).
    private void loadFirebase() {

        //  Grab the 'users' collection corresponding to the correct document UID.
        DocumentReference usersRef = mDatabase.collection("users").document(UID);
        usersRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){ // Check a document can be accessed.
                    DocumentSnapshot document = task.getResult();

                    if(document.exists()){ // Check a document exists.
                        @SuppressWarnings("unchecked")
                        HashMap<String, Object> privacy = (HashMap<String, Object>) document.get("privacy");
                        loadInPrivacySettings(privacy); // Load in privacy settings first (always)
                        loadProfile(document); // Then we load the public users profile.
                        Log.i(TAG, "Successfully loaded the users profile");
                    } else {
                        Log.e(TAG, "No such document, " + document.toString() + " exists");
                    }
            } else {
                    Log.e(TAG, "Failed to get document for public user profile");
                }
            }
        });
    }

    //  Load the information from the local UserInfoPrivate object if the profile corresponds to that of the user using the app/
    //  Reduces amount of Firebase Queries overall.
    private void loadLocalProfile() {
        if(retrieveAboutMe){ mAboutMe.setText(mUserProfile.getAbout()); } //  If we are allowed to retrieve this data. do so.
        /* TODO- Implement Image retrieval later,
        if(retrieveImages){ }
        */
        String numOfRecipesString = "Recipes: " + (mUserProfile.getNumRecipes()); //  Convert 'long' value to something we can use.
        if(retrieveRecipes){ mNumRecipes.setText(numOfRecipesString);} else {
            mNumRecipes.setText(""); // Set number of recipes to nothing if hidden.
        }

        if(retrieveUsername){ mUsername.setText(mUserProfile.getDisplayName()); }

        if(retrieveFilters){
            HashMap<String, Object> filters = new HashMap<>();

            //  Store all filters in a HashMap for convenience.
            filters.put("allergy_nuts", mUserProfile.getPreferences().isAllergy_nuts());
            filters.put("allergy_soya", mUserProfile.getPreferences().isAllergy_soya());
            filters.put("allergy_milk", mUserProfile.getPreferences().isAllergy_milk());
            filters.put("allergy_shellfish", mUserProfile.getPreferences().isAllergy_shellfish());
            filters.put("allergy_gluten", mUserProfile.getPreferences().isAllergy_gluten());
            filters.put("allergy_eggs", mUserProfile.getPreferences().isAllergy_eggs());

            setFilters(currentFilterType, filters);

        } else { // Remove all checkboxes if filters are hidden.
            makeFiltersInvisible();
        }
    }

    
}
