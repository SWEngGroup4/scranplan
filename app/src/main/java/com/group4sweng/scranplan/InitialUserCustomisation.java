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
        setContentView(R.layout.activity_initial_user_customisation);

        if(userDetails != null){
            userDetails = (UserInfoPrivate) getIntent().getSerializableExtra("user");
            preferences = userDetails.getPreferences();
        }

        getSupportActionBar().hide();
        Log.e(TAG, "Starting initial user customisation activity");
        initCheckBoxes();
        initPageItems();
        getResources().getColor(R.color.colorPrimary);
        initPageListeners();
        initFirebase();
    }

    private void initFirebase() {
        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    private void initPageItems() {
        //Defining all relevant members of signin & register page
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
                Log.e(TAG, "Initial user returning to main activity");

                Intent returningIntent = new Intent();
                setResult(RESULT_OK, returningIntent);

                finish();
            }
        });



        mSubmitButton.setOnClickListener(new View.OnClickListener() {
             public void onClick(View view) {
                 savePref();
                 userDetails.setmFirstTimeLogin(false);
                 Log.e(TAG, "Initial user returning to main activity");
                 Intent returningIntent = new Intent();
                 setResult(RESULT_OK, returningIntent);

                 finish();
             }
        });

    }

    //make function to add info to firestore
    private void savePref() {
        CollectionReference colRef = database.collection("users");
        DocumentReference usersRef = colRef.document(mAuth.getCurrentUser().getUid());

        userDetails.getPreferences().setAllergy_eggs(mEggsBox.isChecked());
        if (mEggsBox.isChecked()) {
            userDetails.getPreferences().setAllergy_eggs(true);
        }

        if (mMilkBox.isChecked()) {
            userDetails.getPreferences().setAllergy_milk(true);
        }

        if (mNutsBox.isChecked()) {
            userDetails.getPreferences().setAllergy_nuts(true);
        }

        if (mShellfishBox.isChecked()) {
            userDetails.getPreferences().setAllergy_shellfish(true);

        }
        if (mSoyBox.isChecked()) {
            userDetails.getPreferences().setAllergy_soya(true);
        }

        if (mWheatBox.isChecked()) {
            userDetails.getPreferences().setAllergy_gluten(true);
        }

        if (mPescatarianBox.isChecked()) {
            userDetails.getPreferences().setPescatarian(true);

        }
        if (mVeganBox.isChecked()) {
            userDetails.getPreferences().setVegan(true);
            mPescatarianBox.setChecked(false);
            mVegetarianBox.setChecked(false);
        }
        if (mVegetarianBox.isChecked()) {
            userDetails.getPreferences().setVegetarian(true);
            mVeganBox.setChecked(false);
            mPescatarianBox.setChecked(false);
        }

        userDetails.setPreferences(preferences);

        HashMap<String, Object> updatedPrefs = userDetails.getPreferences().returnPrefMap();

        usersRef.update("preferences", updatedPrefs);

    }


    private void finishActivity() {

        Log.e(TAG, "Initial User Customisation returning to main activity");

        // User data returned to main menu
        Intent returningIntent = new Intent(InitialUserCustomisation.this, MainActivity.class);
        returningIntent.putExtra("user", userDetails);
        setResult(RESULT_OK, returningIntent);

        finish();
    }

}