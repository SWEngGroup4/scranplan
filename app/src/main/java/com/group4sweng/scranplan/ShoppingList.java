package com.group4sweng.scranplan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.Helper.RecipeHelpers;
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



public class ShoppingList extends AppCompatActivity implements RecyclerViewAdaptor.ItemClickListener {

    final String TAG = "ShoppingList";
    final FirebaseFirestore database = FirebaseFirestore.getInstance();

    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mUserRef = mDatabase.collection("users");

    private List<HashMap<String, Object>> updateIngredientList = new ArrayList<>();

    //User information
    private com.group4sweng.scranplan.UserInfo.UserInfoPrivate mUser;

    TextView mShoppingList;
    private List<HashMap<String, Object>> ShoppingList = new ArrayList<>();
    RecyclerViewAdaptor adapter;
    ArrayList<String> newList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_shoppinglist);

        mUser = (com.group4sweng.scranplan.UserInfo.UserInfoPrivate) getIntent().getSerializableExtra("user");

        if (mUser != null) {
            AddIngredients();
        }

    }

    @Override
    public void onItemClick(View view, int position) {

    if (newList != null) {
        newList.remove(position);
    }
    adapter.notifyItemRemoved(position);
    System.out.println(newList);
    }


    public void AddIngredients() {

        for (int a = 0; a < 20; a++) {
            if (mUser != null) {

                if (mUser.getMealPlanner().get(a) != null) {

                    ShoppingList = mUser.getMealPlanner();
                    HashMap<String, String> updateIngredientList = new HashMap<>();
                    updateIngredientList = (HashMap<String, String>) ShoppingList.get(a).get("ingredientHashMap");
                    assert updateIngredientList != null;
                    ArrayList<String> ingredientArray = RecipeHelpers.convertToIngredientListFormat(updateIngredientList);
                    for (String ingredient : ingredientArray) {
                        if (newList.contains(ingredient)){
                            newList.remove(ingredient);
                            newList.add("2x " + ingredient);
                        }

                        else {
                            newList.add(ingredient);
                        }
                    }
                }
            }
        }
        System.out.println(newList);
        RecyclerView recyclerView = findViewById(R.id.rvShoppingList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdaptor(this, newList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed () {
        Intent returnIntent = new Intent(this, Home.class);
        returnIntent.putExtra("user", mUser);
        startActivity(returnIntent);
        finish(); //    We don't need to send anything back but do need to destroy the current activity.
    }
}