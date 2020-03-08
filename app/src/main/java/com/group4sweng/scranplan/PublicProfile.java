package com.group4sweng.scranplan;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.UserInfo.FilterType;

public class PublicProfile extends AppCompatActivity implements FilterType{

    // TAG for Profile Settings
    final String TAG = "PublicProfile";

    //  Default filter type enumeration. Types shown in 'FilterType' interface.
    FilterType.filterType currentFilterType = FilterType.filterType.ALLERGENS;

    // Firebase user variables.
    FirebaseApp mApp;
    FirebaseAuth mAuth;
    FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    CollectionReference mRef = mDatabase.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profile);
    }

    
}
