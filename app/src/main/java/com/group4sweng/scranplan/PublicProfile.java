package com.group4sweng.scranplan;

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

import java.util.HashMap;

public class PublicProfile extends AppCompatActivity implements FilterType{

    // TAG for Profile Settings
    final String TAG = "PublicProfile";
    private String UID = "EUEmeOMdqFevJYKXqjgCsgl6sz42";

    //  Default filter type enumeration. Types shown in 'FilterType' interface.
    FilterType.filterType currentFilterType = FilterType.filterType.ALLERGENS;

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

        /* TODO - Add intents
            UID = getIntent().getStringExtra("UID");
         */
        if(UID != null){
            loadFirebase();
        } else {
            Log.e(TAG, "Unable to retrieve extra UID intent string.");
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

        //  TODO - Returning intent
        //Intent returnIntent = new Intent(this, MainActivity.class);
        //setResult(Activity.RESULT_OK, returnIntent);
        //startActivity(returnIntent);
        finish();
    }

    private void loadInPrivacySettings(HashMap<String, Object> privacy){
        if(privacy != null){
            retrieveAboutMe = (boolean) privacy.get("display_about_me");
            retrieveUsername = (boolean) privacy.get("display_username");
            retrieveRecipes = (boolean) privacy.get("display_recipes");
            retrieveImages = (boolean) privacy.get("display_profile_image");
            retrieveFilters = (boolean) privacy.get("display_filters");
        }
    }

    private void loadProfile(DocumentSnapshot profile) {
        if(retrieveAboutMe){ mAboutMe.setText((String) profile.get("about")); }
        /* TODO- Implement this later
        if(retrieveImages){ }
        */
        String numOfRecipesString = "Recipes: " + ((long) profile.get("numRecipes"));
        if(retrieveRecipes){ mNumRecipes.setText(numOfRecipesString);} else {
            Log.e(TAG, "Not retrieving recipes");
        }
        if(retrieveUsername){ mUsername.setText((String) profile.get("displayName")); }

        if(retrieveFilters){
            @SuppressWarnings("unchecked")
            HashMap<String, Object> filters = (HashMap<String, Object>) profile.get("preferences");

            setFilters(currentFilterType, filters);
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

    private void loadFirebase() {

        DocumentReference usersRef = mDatabase.collection("users").document(UID);
        usersRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();

                    if(document.exists()){
                        @SuppressWarnings("unchecked")
                        HashMap<String, Object> privacy = (HashMap<String, Object>) document.get("privacy");
                        loadInPrivacySettings(privacy);
                        loadProfile(document);
                    } else {
                        Log.e(TAG, "No such document, " + document.toString() + " exists");
                    }
            } else {
                    Log.e(TAG, "Failed to get document for public user profile");
                }
            }
        });
    }

    
}
