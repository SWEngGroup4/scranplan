package com.group4sweng.scranplan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.UserInfo.Preferences;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.HashMap;


public class InitialUserCustomisation extends AppCompatActivity {
    final String TAG = "userCustomisation";

    FirebaseApp mApp;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    UserInfoPrivate userDetails;
    final FirebaseFirestore database = FirebaseFirestore.getInstance();

    Preferences preferences;
    Button mSkipButton;
    Button mSubmitButton;
    CheckBox mVegetarianBox;
    CheckBox mVeganBox;
    CheckBox mNutsBox;
    CheckBox mMilkBox;
    CheckBox mEggsBox;
    CheckBox mWheatBox;
    CheckBox mShellfishBox;
    CheckBox mSoyBox;
    CheckBox mPescatarianBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sets the page to the initial filer page
        setContentView(R.layout.activity_initial_user_customisation);
        userDetails = (UserInfoPrivate) getIntent().getSerializableExtra("user");


        if (userDetails != null) { //only if user details are retrieved
            preferences = userDetails.getPreferences();
            initPageItems();
            initCheckBoxes();
            getResources().getColor(R.color.colorBackground);
            initPageListeners();
            initFirebase();
        }

        //getSupportActionBar().hide();
        Log.e(TAG, "Starting initial user customisation activity");

    }

    private void initFirebase() {
        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    private void initPageItems() {
        //Defining all buttons and check boxes on the page
        mSkipButton = findViewById(R.id.skipButton);
        mSubmitButton = findViewById(R.id.submitButton);
        mVegetarianBox = findViewById(R.id.VegCheckBox);
        mVeganBox = findViewById(R.id.veganCheckBox);
        mNutsBox = findViewById(R.id.nutCheckBox);
        mEggsBox = findViewById(R.id.eggCheckBox);
        mMilkBox = findViewById(R.id.milkCheckBox);
        mWheatBox = findViewById(R.id.wheatCheckBox);
        mShellfishBox = findViewById(R.id.shellfishCheckBox);
        mSoyBox = findViewById(R.id.soyCheckBox);
        mPescatarianBox = findViewById(R.id.pescatarianCheckBox);

    }

    public void initCheckBoxes(){
    //initialise all check boxes
        preferences.setPescatarian(preferences.isPescatarian());
        preferences.setVegetarian(preferences.isVegetarian());
        preferences.setVegan(preferences.isVegan());
        preferences.setAllergy_soya(preferences.isAllergy_soya());
        preferences.setAllergy_gluten(preferences.isAllergy_gluten());
        preferences.setAllergy_eggs(preferences.isAllergy_eggs());
        preferences.setAllergy_nuts(preferences.isAllergy_nuts());
        preferences.setAllergy_shellfish(preferences.isAllergy_shellfish());
        preferences.setAllergy_milk(preferences.isAllergy_milk());

        //setting so only one box can be ticked out of vegetarian, vegan and pescatarian
        mVegetarianBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mVegetarianBox.isChecked()){
                    mVeganBox.setChecked(false);
                    mPescatarianBox.setChecked(false);
                }
            }
        });

        mVeganBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mVeganBox.isChecked()){
                    mPescatarianBox.setChecked(false);
                    mVegetarianBox.setChecked(false);
                }
            }
        });
        mPescatarianBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mPescatarianBox.isChecked()){
                    mVeganBox.setChecked(false);
                    mVegetarianBox.setChecked(false);
                }
            }
        });
    }

    private void initPageListeners() {
        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //skip button takes user directly to the main page
                Log.e(TAG, "Initial user returning to home");

                finishActivity();
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
             public void onClick(View view) {
                 //submit button saves the users preferences before
                 Log.e(TAG, "Initial user returning to home");
                 savePref();
                 finishActivity();
             }
        });

    }

    //make function to add info to firestore
    private void savePref() {

        CollectionReference colRef = database.collection("users");
        DocumentReference usersRef = colRef.document(mAuth.getCurrentUser().getUid()); //gets the current user ID

        preferences.setAllergy_eggs(mEggsBox.isChecked());
        preferences.setAllergy_milk(mMilkBox.isChecked());
        preferences.setAllergy_nuts(mNutsBox.isChecked());
        preferences.setAllergy_shellfish(mShellfishBox.isChecked());
        preferences.setAllergy_gluten(mWheatBox.isChecked());
        preferences.setAllergy_soya(mSoyBox.isChecked());

        preferences.setPescatarian(mPescatarianBox.isChecked());
        preferences.setVegan(mVeganBox.isChecked());
        preferences.setVegetarian(mVegetarianBox.isChecked());

        userDetails.setPreferences(preferences);

        HashMap<String, Object> updatedPrefs = new HashMap<>();
        //maps the updated preferences by checking if they are ticked, if they are the allergy is set to true
        updatedPrefs.put("allergy_nuts", userDetails.getPreferences().isAllergy_nuts());
        updatedPrefs.put("allergy_milk", userDetails.getPreferences().isAllergy_milk());
        updatedPrefs.put("allergy_soya", userDetails.getPreferences().isAllergy_soya());
        updatedPrefs.put("allergy_shellfish", userDetails.getPreferences().isAllergy_shellfish());
        updatedPrefs.put("allergy_gluten", userDetails.getPreferences().isAllergy_gluten());
        updatedPrefs.put("allergy_eggs", userDetails.getPreferences().isAllergy_eggs());
        updatedPrefs.put("vegan", userDetails.getPreferences().isVegan());
        updatedPrefs.put("vegetarian", userDetails.getPreferences().isVegetarian());
        updatedPrefs.put("pescatarian", userDetails.getPreferences().isPescatarian());

        usersRef.update("preferences", updatedPrefs); //preferences updated
        usersRef.update("firstAppLaunch", false); //first time login set to false so page wont show again


    }


    private void finishActivity() {

        Log.e(TAG, "Initial User Customisation returning to main activity");

        // User data returned to main menu
        Intent returningIntent = new Intent(InitialUserCustomisation.this, Home.class);
        returningIntent.putExtra("user", userDetails);
        startActivity(returningIntent);

        finish();
    }

    // Disable user from pressing back button on main activity page
    @Override
    public void onBackPressed() {
        //Do nothing
    }

}