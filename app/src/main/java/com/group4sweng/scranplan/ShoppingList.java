package com.group4sweng.scranplan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.group4sweng.scranplan.UserInfo.FilterType;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.ArrayList;

public class ShoppingList extends AppCompatActivity implements FilterType {

    TextView mShoppingList;
    private String UID;
    private UserInfoPrivate mUserProfile;
    private ArrayList<String> ingredientArray;

    private ArrayList<String> ingredientList = new ArrayList<>();

    private ListView listViewIngredients;

    private ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppinglist);

        initPageItems();
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


}
