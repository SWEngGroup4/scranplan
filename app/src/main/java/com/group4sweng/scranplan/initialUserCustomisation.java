package com.group4sweng.scranplan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Exclude;

import java.util.HashMap;

public class initialUserCustomisation extends AppCompatActivity {
    final String TAG = "userCustomisation";

    FirebaseApp mApp;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    Button mSkipButton;
    CheckBox mVegetarianBox;
    CheckBox mVeganBox;
    CheckBox mNutsBox;
    CheckBox mMilkBox;
    CheckBox mEggsBox;
    CheckBox mWheatBox;
    CheckBox mShellfishBox;
    CheckBox mSoyBox;

    Boolean mVegBox = false;
    Boolean mVveganBoxBool = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        Log.e(TAG, "Starting Login activity");

        initPageItems();

        initPageListeners();
    }

    private void initPageItems(){
        //Defining all relevant members of signin & register page
        mSkipButton = (Button) findViewById(R.id.skipButton);
        mVegetarianBox = (CheckBox) findViewById(R.id.VegCheckBox);
        mVeganBox = (CheckBox) findViewById(R.id.veganCheckBox);
        mNutsBox = (CheckBox) findViewById(R.id.nutCheckBox);
        mEggsBox = (CheckBox) findViewById(R.id.eggCheckBox);
        mMilkBox = (CheckBox) findViewById(R.id.milkCheckBox);
        mWheatBox = (CheckBox) findViewById(R.id.wheatCheckBox);
        mShellfishBox = (CheckBox) findViewById(R.id.shellfishCheckBox);
        mSoyBox = (CheckBox) findViewById(R.id.soyCheckBox);

    }

    private void initPageListeners(){
        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"Initial user returning to main activity");

                Intent returningIntent = new Intent();
                setResult(RESULT_OK, returningIntent);

                finish();
            }
        });

    }
@Exclude
public  Map<String, Object> toMap() {
    HashMap<String, Object> result = new HashMap<>();

    if (mVegetarianBox.isChecked()){
        result.put("vegetarian", true);
    }
    if (mVeganBox.isChecked()){
        result.put("vegetarian", true);
    }
}

}
