package com.group4sweng.scranplan.RecipeCreation;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private CollectionReference mUserRef = mDatabase.collection("users");
    DocumentReference documentReference;
    private StorageReference mStoreRef = FirebaseStorage.getInstance().getReference();
    private UserInfoPrivate mUser;

    private Long recipeNum;

    private final static int BASIC_INFO_FLAG = 1;
    private final static int RECIPE_STEPS_FLAG = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        mUser = (UserInfoPrivate) getIntent().getSerializableExtra("user");
        documentReference  = mUserRef.document(mUser.getUID());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        recipeNum = (Long) documentSnapshot.get("numRecipes");
                        // Loads initial recipe creation screen
                        frameLayout = findViewById(R.id.createRecipeFrame);
                        fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.createRecipeFrame, new BasicInfo()).
                                setCustomAnimations(R.anim.exit_to_right, R.anim.enter_from_left).commitNow();
                        spinner.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        spinner = findViewById(R.id.createRecipeSpinner);
        mRecipeMap = new HashMap<>();
    }

    public void stepComplete(int requestCode, Bundle bundle) {
        switch (requestCode) {
            case BASIC_INFO_FLAG:
                // Stores data from initial screen
                if (bundle != null)
                    for (String key : bundle.keySet())
                        mRecipeMap.put(key, bundle.get(key));

                //Loads presentation creation screen
                fragmentManager.beginTransaction().replace(R.id.createRecipeFrame, new RecipeSteps(mUser, recipeNum)).
                        setCustomAnimations(R.anim.exit_to_right, R.anim.enter_from_left).commitNow();
                break;

            case RECIPE_STEPS_FLAG:
                setContentView(R.layout.activity_create_recipe);

                if (bundle != null)
                    for (String key : bundle.keySet())
                        mRecipeMap.put(key, bundle.get(key));

                // Creating unique storage reference for recipe image
                StorageReference ref = mStoreRef.child("recipe_pictures/" + mUser.getUID() + "_" + recipeNum);

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
                            mColRef.document(mUser.getUID() + "_" + recipeNum)
                                    .set(mRecipeMap).addOnSuccessListener(aVoid -> {
                                        // Finish activity
                                        mUser.setRecipes(mUser.getRecipes() + 1);
                                        documentReference.update("numRecipes", FieldValue.increment(1));
                                        setResult(Activity.RESULT_OK);
                                        finish();
                            });
                }));
                break;
        }
    }
}
