package com.group4sweng.scranplan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class savedList extends AppCompatActivity implements RecyclerViewAdaptor.ItemClickListener  {
    final String TAG = "SavedShoppingList";

    //Database references
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mUserRef = mDatabase.collection("users");

    //User information
    private com.group4sweng.scranplan.UserInfo.UserInfoPrivate mUser;

    RecyclerViewAdaptor adapter;
    ArrayList<String> viewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savedshoppinglist);

        mUser = (com.group4sweng.scranplan.UserInfo.UserInfoPrivate) getIntent().getSerializableExtra("user");
        Intent i = getIntent();
        //gets the current list and displays it
        viewList = i.getStringArrayListExtra("newList3");
        System.out.println(viewList);

            RecyclerView recyclerViewsaved = findViewById(R.id.ShoppingList);
            recyclerViewsaved.setLayoutManager(new LinearLayoutManager(this));
            adapter = new RecyclerViewAdaptor(this, viewList);
            adapter.setClickListener(this);
            recyclerViewsaved.setAdapter(adapter);


    }

    @Override
    public void onItemClick(View view, int position) {

        if (viewList != null) {
            viewList.remove(position);
        }

        adapter.notifyItemRemoved(position);

    }


}
