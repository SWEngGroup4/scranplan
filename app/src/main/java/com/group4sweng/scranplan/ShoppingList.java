package com.group4sweng.scranplan;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.group4sweng.scranplan.UserInfo.FilterType;

public class ShoppingList extends AppCompatActivity implements FilterType {

    TextView mShoppingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppinglist);

        initPageItems();
    }

    private void initPageItems(){

        mShoppingList = findViewById(R.id.shoppingListText);


    }


}
