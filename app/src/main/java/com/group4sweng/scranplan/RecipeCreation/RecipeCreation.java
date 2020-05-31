package com.group4sweng.scranplan.RecipeCreation;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.ArrayList;
import java.util.HashMap;

public class RecipeCreation extends AppCompatActivity {

    HashMap<String, Object> mRecipeMap;

    FragmentManager fragmentManager;
    FrameLayout frameLayout;

    ProgressBar spinner;

    // Firebase references
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mColRef = mDatabase.collection("recipes");
    private StorageReference mStoreRef = FirebaseStorage.getInstance().getReference();
    private UserInfoPrivate mUser;

    private final static int BASIC_INFO_FLAG = 1;
    private final static int RECIPE_STEPS_FLAG = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        mUser = (UserInfoPrivate) getIntent().getSerializableExtra("user");

        spinner = findViewById(R.id.createRecipeSpinner);
        mRecipeMap = new HashMap<>();

        // Loads initial recipe creation screen
        frameLayout = findViewById(R.id.createRecipeFrame);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.createRecipeFrame, new BasicInfo()).
                setCustomAnimations(R.anim.exit_to_right, R.anim.enter_from_left).commitNow();
        spinner.setVisibility(View.INVISIBLE);
    }

    public void stepComplete(int requestCode, Bundle bundle) {
        switch (requestCode) {
            case BASIC_INFO_FLAG:
                // Stores data from initial screen
                if (bundle != null)
                    for (String key : bundle.keySet())
                        mRecipeMap.put(key, bundle.get(key));

                //Loads presentation creation screen
                fragmentManager.beginTransaction().replace(R.id.createRecipeFrame, new RecipeSteps(mUser)).
                        setCustomAnimations(R.anim.exit_to_right, R.anim.enter_from_left).commitNow();
                break;

            case RECIPE_STEPS_FLAG:
                setContentView(R.layout.activity_create_recipe);

                if (bundle != null)
                    for (String key : bundle.keySet())
                        mRecipeMap.put(key, bundle.get(key));

                // Creating unique storage reference for recipe image
                StorageReference ref = mStoreRef.child("recipe_pictures/" + mUser.getUID() + "_" + mUser.getRecipes());

                //Upload recipe image
                ref.putFile(Uri.parse((String) mRecipeMap.get("imageURL"))).addOnSuccessListener(
                        taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Put any additional data into the hashmap
                            mRecipeMap.put("Chef", mUser.getUID());
                            mRecipeMap.put("imageURL", uri.toString());
                            HashMap<String, Object> ratingMap = new HashMap<>();
                            ratingMap.put("overallRating", 0f);
                            ratingMap.put("totalRates", 0f);
                            mRecipeMap.put("rating", ratingMap);
                            mRecipeMap.put("timestamp", FieldValue.serverTimestamp());
                            mRecipeMap.put("favourite", new ArrayList<>());

                            // Upload hashmap data
                            mColRef.document(mUser.getUID() + "_" +
                                    mUser.getRecipes()).set(mRecipeMap).addOnSuccessListener(aVoid -> {
                                        // Finish activity
                                        mUser.setRecipes(mUser.getRecipes() + 1);
                                        setResult(Activity.RESULT_OK);
                                        finish();
                            });
                }));
                break;
        }
    }
}
