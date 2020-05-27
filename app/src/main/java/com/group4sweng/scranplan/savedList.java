package com.group4sweng.scranplan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class savedList extends AppCompatActivity implements RecyclerViewAdaptor.ItemClickListener  {
    final String TAG = "SavedShoppingList";

    //Database references
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mColRef = mDatabase.collection("shoppingLists");

    //User information
    private com.group4sweng.scranplan.UserInfo.UserInfoPrivate mUser;

    RecyclerViewAdaptor adapter;
    List<String> viewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savedshoppinglist);

        mUser = (com.group4sweng.scranplan.UserInfo.UserInfoPrivate) getIntent().getSerializableExtra("user");
        Intent i = getIntent();
        Log.d("Test", mUser.getUID());
        //gets the current list and displays it
        mColRef.document(mUser.getUID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        viewList = (List<String>) documentSnapshot.getData().get("shoppingList");
                        RecyclerView recyclerViewsaved = findViewById(R.id.ShoppingList);
                        recyclerViewsaved.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        adapter = new RecyclerViewAdaptor(getApplicationContext(), viewList);
                        adapter.setClickListener(savedList.this::onItemClick);
                        recyclerViewsaved.setAdapter(adapter);
                    } else {
                        Log.d("Test", "Doc does not exist");

                        Context context = getApplicationContext();
                        CharSequence text = "No shopping list is saved yet";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                } else {
                    Log.d("Test", "Download failed with: " + task.getException());

                    Context context = getApplicationContext();
                    CharSequence text = "Unable to get saved list";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {

        if (viewList != null) {
            viewList.remove(position);
        }

        adapter.notifyItemRemoved(position);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("shoppingList", (ArrayList<String>) viewList);
        setResult(Activity.RESULT_OK, intent);
        super.onBackPressed();
    }

}
