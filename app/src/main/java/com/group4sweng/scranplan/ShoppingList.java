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
    final FirebaseFirestore database = FirebaseFirestore.getInstance();

    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mUserRef = mDatabase.collection("users");

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

        mUser = (com.group4sweng.scranplan.UserInfo.UserInfoPrivate) getIntent().getSerializableExtra("user");

        if (mUser != null) {
            AddIngredients();
            initPageListeners();
        }

    }

    @Override
    public void onItemClick(View view, int position) {

        if (newList2 != null) {
            newList2.remove(position);
        }
        adapter.notifyItemRemoved(position);
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
                        newList.add(ingredient);
                    }

                }
            }
        }
        Set<String> unique = new HashSet<String>(newList);
        for (String key : unique) {
            newList2.add(Collections.frequency(newList, key) + " Times : " + key);
            java.util.Collections.sort(newList2, Collator.getInstance());
        }
            RecyclerView recyclerView = findViewById(R.id.rvShoppingList);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new RecyclerViewAdaptor(this, newList2);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);

    }

    private void initPageListeners() {
        msaveButton = findViewById(R.id.SavedList);
        mShowButton = findViewById(R.id.ViewSavedList);

        msaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                        for (String ingredient : newList2 ){
                            newList3.add(ingredient);
                        }
                System.out.println(newList3);

            }
        });

        mShowButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (newList3 != null ){
                    Intent saveListIntent = new Intent(ShoppingList.this, savedList.class);
                    saveListIntent.putStringArrayListExtra("newList3", newList3);
                    saveListIntent.putExtra("user", mUser);
                    startActivity(saveListIntent);
                }
            }
        });
        }


    }


