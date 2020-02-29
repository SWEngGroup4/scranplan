package com.group4sweng.scranplan;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.Exceptions.InvalidUserException;

import java.util.HashMap;

public class ProfileSettings extends AppCompatActivity implements FilterType {

    // TAG for Profile Settings
    final String TAG = "ProfileSettings";
    filterType currentFilterType = filterType.ALLERGENS;

    // Firebase user variables.
    FirebaseApp mApp;
    FirebaseAuth mAuth;
    FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    CollectionReference mRef = mDatabase.collection("users");

    UserInfoPrivate mUserProfile;
    ImageView mProfileImage;
    TextView mUsername;
    TextView mAboutMe;
    TextView mNumRecipes;

    CheckBox mAllergy_nuts;
    CheckBox mAllergy_milk;
    CheckBox mAllergy_eggs;
    CheckBox mAllergy_shellfish;
    CheckBox mAllergy_soy;
    CheckBox mAllergy_gluten;

    Switch mDisplay_username;
    Switch mDisplay_about_me;
    Switch mDisplay_recipes;
    Switch mDisplay_profile_image;

    Button mSaveProfile;
    Button mDeleteProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        initPageItems();
    }

    @Override
    protected void onStart(){
        super.onStart();

        mUserProfile = (UserInfoPrivate) getIntent().getSerializableExtra("user");
        if(mUserProfile != null){
            loadProfileData();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(mUserProfile != null){
            loadProfileData();
        }
    }

    public void saveSettings(View v){
        try {
            updateFirebase();
        } catch (InvalidUserException e){
            e.printStackTrace();
        }
    }

    public void onCheckboxClicked(View v) {
        // Is the Checkbox Checked?
        boolean checked = ((CheckBox) v).isChecked();

        Preferences preferences = mUserProfile.getPreferences();

        //  Check the type of filter. For example, dietary, religious.
        switch(currentFilterType){
            case ALLERGENS:
                // Check which checkbox was clicked.
                switch(v.getId()) {
                    case R.id.settings_allergy_nuts:
                        if (checked)
                            preferences.setAllergy_nuts(true);
                        else
                            preferences.setAllergy_nuts(false);
                        break;
                    case R.id.settings_allergy_eggs:
                        if (checked)
                            preferences.setAllergy_eggs(true);
                        else
                            preferences.setAllergy_eggs(false);
                        break;
                    case R.id.settings_allergy_milk:
                        if (checked)
                            preferences.setAllergy_milk(true);
                        else
                            preferences.setAllergy_nuts(false);
                        break;
                    case R.id.settings_allergy_shellfish:
                        if (checked)
                            preferences.setAllergy_shellfish(true);
                        else
                            preferences.setAllergy_shellfish(false);
                        break;
                    case R.id.settings_allergy_soy:
                        if (checked)
                            preferences.setAllergy_soya(true);
                        else
                            preferences.setAllergy_soya(false);
                        break;
                    case R.id.settings_allergy_wheat:
                        if (checked)
                            preferences.setAllergy_gluten(true);
                        else
                            preferences.setAllergy_gluten(false);
                        break;
                }
            case RELIGIOUS:
                //TODO
            case DIETARY:
                //TODO
            case HEALTH:
                //TODO
        }
    }


    // TODO Make proper image download class.
    /*private void downloadImage(String UID){
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getInstance().getReference()
                .child("user_profile_images")
                .child("megaMan.png");
        imageRef.getBytes(1024*1024)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        mProfileImage.setImageBitmap(bitmap);
                    }
                });
    }*/

    private void initPageItems(){
        //  Basic user info.
        mUsername = findViewById(R.id.settings_input_username);
        mAboutMe = findViewById(R.id.settings_input_about_me);
        mNumRecipes = findViewById(R.id.public_profile_recipes);

        //  Allergens
        mAllergy_eggs = findViewById(R.id.settings_allergy_eggs);
        mAllergy_gluten = findViewById(R.id.settings_allergy_wheat);
        mAllergy_milk = findViewById(R.id.settings_allergy_milk);
        mAllergy_nuts = findViewById(R.id.settings_allergy_nuts);
        mAllergy_shellfish = findViewById(R.id.settings_allergy_shellfish);
        mAllergy_soy = findViewById(R.id.settings_allergy_soy);

        //  Privacy
        mDisplay_about_me = findViewById(R.id.settings_privacy_about_me);
        mDisplay_profile_image = findViewById(R.id.settings_privacy_profile_image);
        mDisplay_recipes = findViewById(R.id.settings_privacy_recipes);
        mDisplay_username = findViewById(R.id.settings_privacy_username);

        //   Buttons

    }

    private void loadProfileData(){
        mUsername.setText(mUserProfile.getDisplayName());
        mAboutMe.setText(mUserProfile.getAbout());
        mNumRecipes.setText(String.valueOf(mUserProfile.getNumRecipes()));

        //  Load allergen checkBoxes.
        setFilters(currentFilterType);

        //  Load privacy switches.
        setPrivacyOptions(mUserProfile.getPrivacy());
    }

    private void setFilters(filterType type){
        switch(type){
            case ALLERGENS:
                mAllergy_eggs.setChecked(mUserProfile.getPreferences().isAllergy_eggs());
                mAllergy_milk.setChecked(mUserProfile.getPreferences().isAllergy_milk());
                mAllergy_nuts.setChecked(mUserProfile.getPreferences().isAllergy_nuts());
                mAllergy_shellfish.setChecked(mUserProfile.getPreferences().isAllergy_shellfish());
                mAllergy_soy.setChecked(mUserProfile.getPreferences().isAllergy_soya());
                mAllergy_gluten.setChecked(mUserProfile.getPreferences().isAllergy_gluten());
            case RELIGIOUS:
                //TODO
            case DIETARY:
                //TODO
            case HEALTH:
                //TODO
        }
    }

    private void setPrivacyOptions(HashMap<String, Object> privacy){
        mDisplay_username.setChecked( (boolean) privacy.get("display_username"));
        mDisplay_about_me.setChecked( (boolean) privacy.get("display_about_me"));
        mDisplay_profile_image.setChecked( (boolean) privacy.get("display_profile_image"));
        mDisplay_recipes.setChecked( (boolean) privacy.get("display_recipes"));
    }



    private void updateFirebase() throws InvalidUserException {
        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance(mApp);
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){
            HashMap<String, Object> map = new HashMap<>();
            HashMap<String, Object> prefMap = new HashMap<>();
            HashMap<String, Object> privacy = new HashMap();

            map.put("displayName", mUserProfile.getDisplayName());
            map.put("imageURL", mUserProfile.getImageURL());
            map.put("chefRating", mUserProfile.getChefRating());
            map.put("numRecipes", mUserProfile.getNumRecipes());
            map.put("about", mUserProfile.getAbout());

            Preferences preferences = mUserProfile.getPreferences();

            prefMap.put("allergy_eggs", preferences.isAllergy_eggs());
            prefMap.put("allergy_gluten", preferences.isAllergy_gluten());
            prefMap.put("allergy_milk", preferences.isAllergy_milk());
            prefMap.put("allergy_nuts", preferences.isAllergy_nuts());
            prefMap.put("allergy_shellfish", preferences.isAllergy_shellfish());
            prefMap.put("allergy_soya", preferences.isAllergy_soya());

            //  TODO Finish all other preferences. Currently just set to false by default.
            prefMap.put("allergy_celery", false);
            prefMap.put("allergy_crustacean", false);
            prefMap.put("allergy_fish", false);
            prefMap.put("allergy_mustard", false);
            prefMap.put("allergy_peanuts", false);
            prefMap.put("allergy_sesame", false);
            prefMap.put("allergy_sulphide", false);
            prefMap.put("diabetic", false);
            prefMap.put("halal", false);
            prefMap.put("high_protein", false);
            prefMap.put("kosher", false);
            prefMap.put("lactose_free", false);
            prefMap.put("lactovegetarian", false);
            prefMap.put("low_carb", false);
            prefMap.put("low_sodium", false);
            prefMap.put("no_alcohol", false);
            prefMap.put("no_pork", false);
            prefMap.put("ovovegetarian", false);
            prefMap.put("pescatarian", false);
            prefMap.put("vegan", false);
            prefMap.put("vegetarian", false);

            map.put("preferences", prefMap);

            HashMap<String, Object> privacyMap = mUserProfile.getPrivacy();

            privacy.put("display_username", privacyMap.get("display_username"));
            privacy.put("display_about_me", privacyMap.get("display_about_me"));
            privacy.put("display_recipes", privacyMap.get("display_recipes"));
            privacy.put("display_profile_image", privacyMap.get("display_profile_image"));

            DocumentReference usersRef = mRef.document(user.getUid());
            usersRef.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(),"User Preferences Saved",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to save user preferences", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(getApplicationContext(),"Invalid user login credentials. Consider logging out and then back in again.",Toast.LENGTH_LONG).show();
            throw new InvalidUserException("Invalid user login credentials. Consider logging out and then back in again.");
        }
    }

    public void updatePreferences(){
        loadProfileData();
    }
}
