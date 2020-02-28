package com.group4sweng.scranplan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
//Broken

public class InitialUserCustomisation extends AppCompatActivity {
    final String TAG = "userCustomisation";

    FirebaseApp mApp;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    UserInfoPrivate userDetails;
    final FirebaseFirestore database = FirebaseFirestore.getInstance();

    Preferences preferences = userDetails.getPreferences();

    Context mContext = this;

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
    CheckBox mpescatarianBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        Log.e(TAG, "Starting Login activity");

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
        mpescatarianBox = findViewById(R.id.pescatarianCheckBox);


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
            @Override
            public void onClick(View view) {
                savePref();
                Log.e(TAG, "Initial user returning to main activity");

                Intent returningIntent = new Intent();
                setResult(RESULT_OK, returningIntent);

                finish();
            }

            //make function to add info to firestore
            private void savePref() {
                DocumentReference usersRef = database.document(mAuth.getCurrentUser().getUid());
                HashMap<String, Object> preferences = new HashMap<>();

                if (mEggsBox.isChecked()) {
                    preferences.put("allergy_eggs", true);
                    mMilkBox.setChecked(false);
                    mNutsBox.setChecked(false);
                    mShellfishBox.setChecked(false);
                    mSoyBox.setChecked(false);
                    mWheatBox.setChecked(false);
                    mMilkBox.setChecked(false);
                }

                if (mMilkBox.isChecked()) {
                    preferences.put("allergy_milk", true);
                    mEggsBox.setChecked(false);
                    mNutsBox.setChecked(false);
                    mShellfishBox.setChecked(false);
                    mSoyBox.setChecked(false);
                    mWheatBox.setChecked(false);
                    mMilkBox.setChecked(false);
                }

                if (mNutsBox.isChecked()) {
                    preferences.put("allergy_nuts", true);
                    mMilkBox.setChecked(false);
                    mEggsBox.setChecked(false);
                    mShellfishBox.setChecked(false);
                    mSoyBox.setChecked(false);
                    mWheatBox.setChecked(false);
                    mMilkBox.setChecked(false);
                }

                if (mShellfishBox.isChecked()) {
                    preferences.put("allergy_shellfish", true);
                    mMilkBox.setChecked(false);
                    mNutsBox.setChecked(false);
                    mEggsBox.setChecked(false);
                    mSoyBox.setChecked(false);
                    mWheatBox.setChecked(false);
                    mMilkBox.setChecked(false);
                }
                if (mSoyBox.isChecked()) {
                    preferences.put("allergy_soya", true);
                    mMilkBox.setChecked(false);
                    mNutsBox.setChecked(false);
                    mShellfishBox.setChecked(false);
                    mEggsBox.setChecked(false);
                    mWheatBox.setChecked(false);
                    mMilkBox.setChecked(false);
                }
                if (mWheatBox.isChecked()) {
                    preferences.put("allergy_gluten", true);
                    mMilkBox.setChecked(false);
                    mNutsBox.setChecked(false);
                    mShellfishBox.setChecked(false);
                    mSoyBox.setChecked(false);
                    mEggsBox.setChecked(false);
                    mMilkBox.setChecked(false);
                }
                if (mMilkBox.isChecked()) {
                    preferences.put("lactose_free", true);
                    mEggsBox.setChecked(false);
                    mNutsBox.setChecked(false);
                    mShellfishBox.setChecked(false);
                    mSoyBox.setChecked(false);
                    mWheatBox.setChecked(false);
                    mMilkBox.setChecked(false);
                }
                if (mpescatarianBox.isChecked()) {
                    preferences.put("pescatarian", true);
                    mVeganBox.setChecked(false);
                    mVegetarianBox.setChecked(false);
                }
                if (mVeganBox.isChecked()) {
                    preferences.put("vegan", true);
                    mpescatarianBox.setChecked(false);
                    mVegetarianBox.setChecked(false);
                }
                if (mVegetarianBox.isChecked()) {
                    preferences.put("vegetarian", true);
                    mVeganBox.setChecked(false);
                    mpescatarianBox.setChecked(false);
                }

                usersRef.update("preferences", preferences);

                userDetails.setInitialPreferences(preferences);
                finishActivity();
            }


            //TODO need to add opening intent to this page after user is logged in or has returned from the user registration page

            private void finishActivity() {

                Log.e(TAG, "Initial User Customisation returning to main activity");

                // User data returned to main menu
                Intent returningIntent = new Intent();
                returningIntent.putExtra("user", userDetails);
                setResult(RESULT_OK, returningIntent);

                finish();
            }


        });
    }
}