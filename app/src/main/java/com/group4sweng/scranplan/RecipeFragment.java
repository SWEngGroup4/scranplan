package com.group4sweng.scranplan;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.multidex.MultiDex;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;
import com.squareup.picasso.Picasso;


public class RecipeFragment extends Fragment {

    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    CollectionReference ref = database.collection("recipes");

    public RecipeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        Log.d("Test", documentSnapshot.getId() + " => " + documentSnapshot.getData());
                    }
                } else {
                    Log.w("Test", "Error getting documents", task.getException());
                }
            }
        });

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        LinearLayout topLayout = view.findViewById(R.id.topLayout);

        for (int i = 0; i < 10; i++) {
            TextView textView = new TextView(view.getContext());
            String testString = "Test" + i;
            textView.setText(testString);

            HorizontalScrollView horizontalScrollView = new HorizontalScrollView(view.getContext());

            LinearLayout linearLayout = new LinearLayout(view.getContext());
            for (int j =0; j < 10; j++) {
                ImageButton imageButton = new ImageButton(view.getContext());
                imageButton.setAdjustViewBounds(true);
                imageButton.setPadding(10,10,10,10);
                imageButton.setBackground(null);
                imageButton.setScaleType(ImageButton.ScaleType.FIT_XY);

                String imgUrl = "https://firebasestorage.googleapis.com/v0/b/scran-plan-bc521.appspot.com/o/recipe_pictures%2Fbaconsandwich.jpg?alt=media&token=b903704f-a0e4-490b-a046-4c52ce9e60e8";
                Picasso.get().load(imgUrl).into(imageButton);

                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO launch recipe screen
                    }
                });

                linearLayout.addView(imageButton);
            }
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels / 5));
            horizontalScrollView.addView(linearLayout);
            topLayout.addView(textView);
            topLayout.addView(horizontalScrollView);
        }
        return view;
    }
}