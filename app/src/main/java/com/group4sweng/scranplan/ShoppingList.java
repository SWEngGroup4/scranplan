package com.group4sweng.scranplan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.MealPlanner.PlannerFragment;
import com.group4sweng.scranplan.MealPlanner.PlannerInfoFragment;
import com.group4sweng.scranplan.MealPlanner.PlannerListFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchPrefs;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ShoppingList extends AppCompatActivity {

    final String TAG = "ShoppingList";
    final FirebaseFirestore database = FirebaseFirestore.getInstance();

    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mUserRef = mDatabase.collection("users");

    private List<HashMap<String, Object>> updateIngredientList = new ArrayList<>();

    //User information
    private com.group4sweng.scranplan.UserInfo.UserInfoPrivate mUser;

    TextView mShoppingList;
    private List<HashMap<String, Object>> ShoppingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppinglist);

        mUser = (com.group4sweng.scranplan.UserInfo.UserInfoPrivate) getIntent().getSerializableExtra("user");

        if (mUser != null) {
            AddIngredients(2);
        }
        }

        @Override
        public void onBackPressed () {
            Intent returnIntent = new Intent(this, Home.class);
            returnIntent.putExtra("user", mUser);
            startActivity(returnIntent);
            finish(); //    We don't need to send anything back but do need to destroy the current activity.
        }

        public void AddIngredients(int x){

            if (mUser != null) {
                ShoppingList = mUser.getMealPlanner();

                HashMap<String, Object> updateIngredientList = new HashMap<>();

                            Object newIngredient = ShoppingList.get(x).get("ingredientList");
                            updateIngredientList.put("ingredient", newIngredient);
                            System.out.println(updateIngredientList);
                            TextView tv = (TextView) findViewById(R.id.textView2);
                            tv.setText(updateIngredientList.toString());

            }
        }


}
