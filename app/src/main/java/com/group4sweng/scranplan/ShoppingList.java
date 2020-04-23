package com.group4sweng.scranplan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.MealPlanner.PlannerListFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchPrefs;
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
    private List<HashMap<String, Object>> updateIngredientList = new ArrayList<>();
    //Used for hardcoded string generation


    //User information
    private com.group4sweng.scranplan.UserInfo.UserInfoPrivate mUser;
    private SearchPrefs prefs;

    TextView mShoppingList;
    private String UID;
    private UserInfoPrivate mUserProfile;
    Button mShoppingListButton;
    private List<HashMap<String, Object>> ShoppingList = new ArrayList<>();
    private List<HashMap<String, Object>> PlannerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppinglist);

        if (getIntent().getSerializableExtra("user") != null) {
            mUser = (com.group4sweng.scranplan.UserInfo.UserInfoPrivate) getIntent().getSerializableExtra("user");
        }

        if (mUser != null) {
            ShoppingList = mUser.getMealPlanner();
            AddIngredients();
        }

        }

        @Override
        public void onBackPressed () {
            Intent returnIntent = new Intent(this, Home.class);
            returnIntent.putExtra("user", mUserProfile);
            startActivity(returnIntent);
            finish(); //    We don't need to send anything back but do need to destroy the current activity.
        }

        public void AddIngredients(){


            if (mUser != null) {

                for (int x = 0; x <= mUser.getMealPlanner().size(); x++) {

                    ShoppingList.get(x).get("ingredients");
                    HashMap<String, Object> updateIngredientList = new HashMap<>();
                    updateIngredientList.put("ingredient", mUser.getIngredientList());
                    TextView tv = (TextView) findViewById(R.id.textView2);
                    for (HashMap<String, Object> ingredients : ShoppingList) {
                        tv.append("updateIngredientList");

                    }
                }
            }
        }

}
