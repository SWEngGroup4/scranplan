package com.group4sweng.scranplan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    Button mShoppingListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppinglist);

        initPageItems();
    }

    private void initPageItems(){

        mShoppingList = findViewById(R.id.shoppingListText);
        mShoppingListButton = (Button) findViewById(R.id.ShoppingListButton);


    }
    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent(this, Home.class);
        returnIntent.putExtra("user", mUserProfile);
        startActivity(returnIntent);
        finish(); //    We don't need to send anything back but do need to destroy the current activity.
    }


}
