package com.group4sweng.scranplan;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.*;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;


public class RecipeFragment extends Fragment {

    // Database objects for accessing recipes
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mColRef = mDatabase.collection("recipes");

    // Auto-generated super method
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Auto-generated onCreate method (everything happens here)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        // Grabs screen size for % layout TODO - change to density pixels + NullPointerException check
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();

        // Procedurally fills topLayout with imageButton content
        LinearLayout topLayout = view.findViewById(R.id.topLayout);

        for (int i = 0; i < 10; i++) {

            // Placeholder text TODO - change to query type
            TextView textView = new TextView(view.getContext());
            String testString = "Test" + i;
            textView.setText(testString);

            // Generates single horizontal scroll view for query
            HorizontalScrollView horizontalScrollView = new HorizontalScrollView(view.getContext());

            // Generates linear layout for holding content with % size according to screen
            LinearLayout linearLayout = new LinearLayout(view.getContext());
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels / 5));

            for (int j =0; j < 10; j++) {

                // Generates imageButton, adds padding and sizes accordingly
                ImageButton imageButton = new ImageButton(view.getContext());
                imageButton.setAdjustViewBounds(true);
                imageButton.setPadding(10,10,10,10);
                imageButton.setBackground(null);
                imageButton.setScaleType(ImageButton.ScaleType.FIT_XY);

                // Function to add image to button from database
                loadImage(imageButton);

                // Button functionality
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openRecipeDialog();
                    }
                });

                linearLayout.addView(imageButton);
            }

            // Adds generated content to appropriate views
            horizontalScrollView.addView(linearLayout);
            topLayout.addView(textView);
            topLayout.addView(horizontalScrollView);
        }
        return view;
    }

    // Private function to get random image from database and load it into an imageButton
    // TODO - Change to structured query search instead of random selection
    // TODO - Edit function so that we don't have to call the entire collection for each image?
    private void loadImage(final ImageButton imageButton) {
        final Random random = new Random();

        /* Adds onSuccessListener - will not run until query is reported successful
           Stops async task from throwing errors */
        mColRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                // Not necessary but worth checking
                if (!querySnapshot.isEmpty()) {
                    List<DocumentSnapshot> docs = querySnapshot.getDocuments(); // Get documents from queried collection
                    int n = random.nextInt(docs.size() - 1); // Random number generated
                    Picasso.get().load(docs.get(n).get("imageURL").toString()).into(imageButton); //Loads image using picasso library TODO - NullPointerException check

                }
            }
        });
    }

    public void openRecipeDialog(){

        RecipeInfoFragment recipeDialogFragment = new RecipeInfoFragment();
        recipeDialogFragment.show(getFragmentManager(), "Show recipe dialog fragment");
    }
}