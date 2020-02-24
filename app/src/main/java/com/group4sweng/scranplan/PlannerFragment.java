package com.group4sweng.scranplan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class PlannerFragment extends Fragment {

    List<String> days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
    ImageButton currentSelection;

    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mColRef = mDatabase.collection("recipes");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_planner, container, false);

        LinearLayout topView = view.findViewById(R.id.plannerLinearLayout);
        for (int i = 0; i < 7; i++) {
            TextView textView = new TextView(view.getContext());
            textView.setText(days.get(i));

            LinearLayout linearLayout = new LinearLayout(view.getContext());
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setWeightSum(3);

            for (int j = 0; j < 3; j++) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f
                );

                final ImageButton imageButton = new ImageButton(view.getContext());
                imageButton.setLayoutParams(params);
                imageButton.setAdjustViewBounds(true);
                imageButton.setPadding(10,10,10,10);
                imageButton.setImageResource(R.drawable.add);
                imageButton.setBackground(null);

                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openRecipeDialog(imageButton);
                        currentSelection = imageButton;
                    }
                });

                linearLayout.addView(imageButton);
            }
            topView.addView(textView);
            topView.addView(linearLayout);
        }

        return view;
    }

    public void openRecipeDialog(ImageButton imageButton) {
        RecipeListFragment recipeListFragment = new RecipeListFragment();
        recipeListFragment.setValue(imageButton);
        recipeListFragment.setTargetFragment(PlannerFragment.this, 1);
        recipeListFragment.show(getFragmentManager(), "Test");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            String recipeID = bundle.getString("recipeID");

            currentSelection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO - link to recipe info page
                }
            });

            DocumentReference documentReference = mColRef.document(recipeID);
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Picasso.get().load(documentSnapshot.getData().get("imageURL").toString()).into(currentSelection);
                }
            });
        }
    }
}
