package com.group4sweng.scranplan;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.SearchFunctions.SearchPrefs;
import com.group4sweng.scranplan.UserInfo.FilterType;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ShoppingList extends AppCompatActivity {

    final String TAG = "ShoppingList";
    UserInfoPrivate userDetails;
    final FirebaseFirestore database = FirebaseFirestore.getInstance();

    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mUserRef = mDatabase.collection("users");

    //List for storing current ingredients
    private List<HashMap<String, Object>> ingredientList = new ArrayList<>();
    //Used for hardcoded string generation

    //Fragment handlers
    private FragmentTransaction fragmentTransaction;
    private RecipeFragment recipeFragment;

    //User information
    private com.group4sweng.scranplan.UserInfo.UserInfoPrivate mUser;
    private SearchPrefs prefs;

    TextView mShoppingList;
    private String UID;
    private UserInfoPrivate mUserProfile;
    Button mShoppingListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //initPageItems();
        //AddIngredinets();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppinglist);
        Log.e(TAG, "TEST ONE");
        //initialize text view object
        TextView tv=(TextView)findViewById(R.id.textView2);
        //set text color
        tv.setTextColor(Color.RED);
        //print 1 to 100 numbers using for loop
        //use append method to print all numbers
        Log.e(TAG, "TEST TWO");


        for(int a=0;a<=100;a++)
        {
            tv.setText((CharSequence) ingredientList);
            Log.e(TAG, "TEST three");


        }
    }

    private void initPageItems(){
        mShoppingList = findViewById(R.id.shoppingListText);

    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent(this, Home.class);
        returnIntent.putExtra("user", mUserProfile);
        startActivity(returnIntent);
        finish(); //    We don't need to send anything back but do need to destroy the current activity.
    }

    private void AddIngredients(){
        mUser = (UserInfoPrivate) getIntent().getSerializableExtra("user");
        if (mUser != null) ingredientList = mUser.getIngredientList();


    }


}


