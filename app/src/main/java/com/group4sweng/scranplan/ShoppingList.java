package com.group4sweng.scranplan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.Helper.RecipeHelpers;
import com.group4sweng.scranplan.MealPlanner.Ingredients.Ingredient;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class ShoppingList extends AppCompatActivity implements RecyclerViewAdaptor.ItemClickListener {

    final String TAG = "ShoppingList";

    //User information
    private com.group4sweng.scranplan.UserInfo.UserInfoPrivate mUser;

    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mColRef = mDatabase.collection("shoppingLists");

    private List<HashMap<String, Object>> ShoppingList = new ArrayList<>();
    RecyclerViewAdaptor adapter;
    ArrayList<String> ingredientList = new ArrayList<>();
    ArrayList<String> duplicatesAddedList = new ArrayList<>();
    ArrayList<String> savedList = new ArrayList<>();


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
        if (duplicatesAddedList != null) {
            duplicatesAddedList.remove(position);
        }
        adapter.notifyItemChanged(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, duplicatesAddedList.size());

    }

    public void AddIngredients() {
        //cycle through the users meal plan
        for (int a = 0; a < 20; a++) {
            if (mUser != null) {

                if (mUser.getMealPlanner().get(a) != null) {
                    //retrieve users meal plan
                    ShoppingList = mUser.getMealPlanner();
                    //add ingredients onto a array list
                    HashMap<String, String> updateIngredientList = new HashMap<>();
                    updateIngredientList = (HashMap<String, String>) ShoppingList.get(a).get("ingredientHashMap");
                    assert updateIngredientList != null;
                    ArrayList<String> ingredientArray = RecipeHelpers.convertToIngredientList(updateIngredientList);
                    for (String ingredient : ingredientArray) {
                        //add each individual ingredient in each recipe
                        ingredientList.add(ingredient);
                    }

                }
            }
        }
        Set<String> unique = new HashSet<String>(ingredientList);
        for (String key : unique) {
            //if there are multiple elements of the same ingredients add these together and replace
            duplicatesAddedList.add(Collections.frequency(ingredientList, key) + " Times : " + key);
            java.util.Collections.sort(duplicatesAddedList, Collator.getInstance());
        }
        //add shopping list into the recycler view
            RecyclerView recyclerView = findViewById(R.id.rvShoppingList);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new RecyclerViewAdaptor(this, duplicatesAddedList);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
    }


    private void initPageListeners() {

        msaveButton = findViewById(R.id.SavedList);
        mShowButton = findViewById(R.id.ViewSavedList);

        //save buttons stores the current list into a new list
        msaveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                savedList.clear();
                for (String ingredient : duplicatesAddedList ){
                    //add all current ingredients into a new list
                    savedList.add(ingredient);
                }
                //uploaded to the firebase once it has been saved
                uploadList(savedList);
            }
        });

        mShowButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (savedList != null ){
                    //new intent into a new page that displays the most recent list
                    Intent saveListIntent = new Intent(ShoppingList.this, savedList.class);
                    saveListIntent.putExtra("user", mUser);
                    startActivityForResult(saveListIntent, 1);
                }
            }
        });
    }

    private void uploadList(ArrayList<String> shoppingList) {
        //uploads the eddited shopping list to the firebase
        Map<String, Object> shoppingMap = new HashMap<>();
        shoppingMap.put("shoppingList", shoppingList);
        mColRef.document(mUser.getUID()).set(shoppingMap).addOnSuccessListener(aVoid -> Log.d("Test", "Uploaded"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        savedList = data.getStringArrayListExtra("shoppingList");
        uploadList(savedList);
    }

}


