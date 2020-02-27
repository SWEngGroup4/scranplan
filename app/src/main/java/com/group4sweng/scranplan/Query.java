package com.group4sweng.scranplan;

import android.widget.CheckBox;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.content.Context;

public class Query {

    CheckBox mVegetarianBox;
    CheckBox mVeganBox;
    CheckBox mNutsBox;
    CheckBox mMilkBox;
    CheckBox mEggsBox;
    CheckBox mWheatBox;
    CheckBox mShellfishBox;
    CheckBox mSoyBox;
    CheckBox mpescatarianBox;

//    Query(){
//        mVegetarianBox = findViewById(R.id.menuVegCheckBox);
//        mVeganBox = findViewById(R.id.menuVeganCheckBox);
//        mNutsBox = findViewById(R.id.menuNutCheckBox);
//        mEggsBox = findViewById(R.id.menuEggCheckBox);
//        mMilkBox = findViewById(R.id.menuMilkCheckBox);
//        mWheatBox = findViewById(R.id.menuWheatCheckBox);
//        mShellfishBox = findViewById(R.id.menuShellfishCheckBox);
//        mSoyBox = findViewById(R.id.menuSoyCheckBox);
//        mpescatarianBox = findViewById(R.id.menuPescatarianCheckBox);
//    }
}
