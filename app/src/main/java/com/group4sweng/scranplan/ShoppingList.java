package com.group4sweng.scranplan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.collect.ObjectArrays;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.Helper.RecipeHelpers;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class ShoppingList extends AppCompatActivity implements RecyclerViewAdaptor.ItemClickListener {

    final String TAG = "ShoppingList";

    //User information
    private com.group4sweng.scranplan.UserInfo.UserInfoPrivate mUser;

    private List<HashMap<String, Object>> ShoppingList = new ArrayList<>();
    RecyclerViewAdaptor adapter;
    ArrayList<String> newList = new ArrayList<>();
    ArrayList<String> newList2 = new ArrayList<>();
    ArrayList<String> newList3 = new ArrayList<>();


    Button msaveButton;
    Button mShowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppinglist);

        //get user details
        mUser = (com.group4sweng.scranplan.UserInfo.UserInfoPrivate) getIntent().getSerializableExtra("user");
        if (mUser != null) {
            AddIngredients();
            initPageListeners();
        }

    }

    @Override
    public void onItemClick(View view, int position) {
        //when item is clicked delete the item
        if (newList2 != null) {
            newList2.remove(position);
        }
        adapter.notifyItemChanged(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, newList2.size());

    }

    public void AddIngredients() {
        //cycle through the users meal plan
        for (int a = 0; a < 20; a++) {
            if (mUser != null) {

                if (mUser.getMealPlanner().get(a) != null) {
                    //retrieve users meal plan
                    ShoppingList = mUser.getMealPlanner();
                    //add ingredients onto a arrat lsit
                    HashMap<String, String> updateIngredientList = new HashMap<>();
                    updateIngredientList = (HashMap<String, String>) ShoppingList.get(a).get("ingredientHashMap");
                    assert updateIngredientList != null;
                    ArrayList<String> ingredientArray = RecipeHelpers.convertToIngredientListFormat(updateIngredientList);
                    for (String ingredient : ingredientArray) {
                        //add each individual ingredent in each recipe
                        newList.add(ingredient);
                    }

                }
            }
        }
        Set<String> unique = new HashSet<String>(newList);
        for (String key : unique) {

            //if there are multiple elements of the same ingredients add these together and replace
            newList2.add(Collections.frequency(newList, key) + " Times : " + key);
            java.util.Collections.sort(newList2, Collator.getInstance());
        }
        //add shopping list into the recycler view
            RecyclerView recyclerView = findViewById(R.id.rvShoppingList);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new RecyclerViewAdaptor(this, newList2);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
    }


    private void initPageListeners() {

        msaveButton = findViewById(R.id.SavedList);
        mShowButton = findViewById(R.id.ViewSavedList);

        //save buttons stores the current list into a new list
        msaveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                newList3.clear();
                        for (String ingredient : newList2 ){
                            //add all current ingredients into a new list
                            newList3.add(ingredient);
                        }
            }
        });

        mShowButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (newList3 != null ){
                    //new intent into a new page that displays the most recent list
                    Intent saveListIntent = new Intent(ShoppingList.this, savedList.class);
                    saveListIntent.putStringArrayListExtra("newList3", newList3);
                    saveListIntent.putExtra("user", mUser);
                    startActivity(saveListIntent);
                }
            }
        });
        }


    }


