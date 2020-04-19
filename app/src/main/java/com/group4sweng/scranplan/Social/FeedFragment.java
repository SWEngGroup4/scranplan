package com.group4sweng.scranplan.Social;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group4sweng.scranplan.Exceptions.ImageException;
import com.group4sweng.scranplan.Home;
import com.group4sweng.scranplan.LoadingDialog;
import com.group4sweng.scranplan.MealPlanner.PlannerInfoFragment;
import com.group4sweng.scranplan.MealPlanner.PlannerListFragment;
import com.group4sweng.scranplan.PostPage;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.SearchFunctions.RecipeFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchPrefs;
import com.group4sweng.scranplan.SearchFunctions.SearchQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static com.group4sweng.scranplan.Helper.ImageHelpers.getExtension;
import static com.group4sweng.scranplan.Helper.ImageHelpers.getPrintableSupportedFormats;
import static com.group4sweng.scranplan.Helper.ImageHelpers.getSize;
import static com.group4sweng.scranplan.Helper.ImageHelpers.isImageFormatSupported;


/**
 * This class builds the horizontal scrolls of custom preference recipe selection for the user on the
 * home screen. Each of these scrolls is infinite in length, loading 5 recipes at a time to minimise
 * reads from the Firestore yet still giving the user an infinite and responsive experience with
 * scroll listeners to check where the user is interacting with these scrolls.
 */
public class FeedFragment extends Fragment {

    final String TAG = "Home horizontal queries";

    // Unique codes for image & permission request activity callbacks.
    private static final int IMAGE_REQUEST_CODE = 2;
    private static final int PERMISSION_CODE = 1001;

    private static final int MAX_IMAGE_FILE_SIZE_IN_MB = 4; // Max storage image size for the profile picture.
    private static boolean POST_IS_UPLOADING = false; // Boolean to determine if the image is uploading currently.

    private Uri mImageUri; // Unique image uri.
    ImageView mUploadedImage;

    float ratingNum;



    // Width size of each scroll view, dictating size of images on home screen
    final int scrollViewSize = 5;
    LoadingDialog loadingDialog;

    //Score scroll info
    List<FeedRecyclerAdapter.FeedPostPreviewData> data;
    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;

    protected Button mPostButton;
    CheckBox mPostRecipe;
    CheckBox mPostReview;
    CheckBox mPostPic;
    EditText mPostBodyInput;
    Space mPicSpacer;

    ImageView mAttachedRecipeImage;
    TextView mAttachedRecipeTitle;
    TextView mAttachedRecipeInfo;
    String attachedRecipeURL;
    String recipeID;

    View mainView;

    TextView mRecipeRatingText;
    RatingBar mAttachedRecipeReview;

    //Fragment handlers
    private FragmentTransaction fragmentTransaction;
    private RecipeFragment recipeFragment;

    //User information
    private com.group4sweng.scranplan.UserInfo.UserInfoPrivate user;
    private SearchPrefs prefs;

    //Menu items
    private SearchView searchView;
    private MenuItem sortButton;

    Query query;



    // Database objects for accessing recipes
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mColRef = mDatabase.collection("followers");
    // Firebase user collection and storage references.
    CollectionReference mRef = mDatabase.collection("posts");
    FirebaseStorage mStorage = FirebaseStorage.getInstance();
    StorageReference mStorageReference = mStorage.getReference();







    // Auto-generated super method
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Auto-generated onCreate method (everything happens here)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        mainView = view;
        loadingDialog = new LoadingDialog(getActivity());

        // Grabs screen size for % layout TODO - change to density pixels + NullPointerException check
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        user = (com.group4sweng.scranplan.UserInfo.UserInfoPrivate) requireActivity().getIntent().getSerializableExtra("user");

        // Procedurally fills topLayout with imageButton content
        LinearLayout topLayout = view.findViewById(R.id.topLayout);

        initPageItems(view);
        initPageListeners();

        // Checks users details have been provided
        if(user != null){
            // Build the first horizontal scroll built around organising the recipes via highest rated
            //TODO make new query
            Home home = (Home) getActivity();
            if (home != null) {
                // Gets search activity from home class and make it invisible
                searchView = home.getSearchView();
                sortButton = home.getSortView();

                sortButton.setVisible(false);
                searchView.setVisibility(View.INVISIBLE);
                //setSearch();



                //Gets search preferences from home class
                prefs = home.getSearchPrefs();
            }

            addPosts(view);

            // Add view to page
//            topLayout.addView(recyclerView);
//            Log.e(TAG, "Social feed added");

        }else{
            // If scroll views fail due to no user, this error is reported
            Log.e(TAG, "ERROR: Loading social feed - We were unable to find user.");
        }
        return view;
    }

    private void addPosts(View view){
        final RecyclerView recyclerView = view.findViewById(R.id.postsList);
        // Set out the layout of this horizontal view
        RecyclerView.LayoutManager rManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(rManager);
        //recyclerView.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels));
        // Array to score downloaded data
        data = new ArrayList<>();
        final RecyclerView.Adapter rAdapter = new FeedRecyclerAdapter(FeedFragment.this, data, user);
        recyclerView.setAdapter(rAdapter);
        query = mColRef.whereArrayContains("users", user.getUID()).orderBy("lastPost", Query.Direction.DESCENDING).limit(10);
        // Ensure query exists and builds view with query
        if (query != null) {
            Log.e(TAG, "User is searching the following query: " + query.toString());
            // Query listener to add data to view
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.e("FEED", "UID = " + user.getUID());
                        Log.e("FEED", "task success");
                        ArrayList<HashMap> posts = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Log.e("FEED", "I have found a doc");
                            //posts.addAll((ArrayList)document.get("recent"));
                            String first = (String) document.get("space1");
                            String second = (String) document.get("space2");
                            String third = (String) document.get("space3");
                            if(document.get("map" + first) != null){
                                posts.add((HashMap)document.get("map" + first));
                            }
                            if(document.get("map" + second) != null){
                                posts.add((HashMap)document.get("map" + second));
                            }
                            if(document.get("map" + third) != null){
                                posts.add((HashMap)document.get("map" + third));
                            }
                        }
                        // Bubble sort items
                        HashMap<String, Object> temporary;
                        for (int i = 0; i < (posts.size() - 1); i++) {
                            for (int j = 0; j < (posts.size() - i - 1); j++) {

                                if (((Timestamp) posts.get(j).get("timestamp")).toDate().before(((Timestamp) posts.get(j + 1).get("timestamp")).toDate())) {

                                    temporary = posts.get(j);
                                    posts.set(j, posts.get(j + 1));
                                    posts.set(j + 1, temporary);

                                }
                            }
                        }
                        for(int i = 0; i < posts.size(); i++){
                            data.add(new FeedRecyclerAdapter.FeedPostPreviewData(
                                    posts.get(i)));
                        }
                        rAdapter.notifyDataSetChanged();
                        if(task.getResult().size() != 0){
                            lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        }else{
                            isLastItemReached = true;
                        }
                        // Track users location to check if new data download is required
                        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                    isScrolling = true;
                                }
                            }
                            // If scrolled to end then download new data and check if we are out of data
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);

                                LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                int visibleItemCount = linearLayoutManager.getChildCount();
                                int totalItemCount = linearLayoutManager.getItemCount();

                                if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                                    isScrolling = false;
                                    Query nextQuery = query.startAfter(lastVisible);
                                    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                            if (t.isSuccessful()) {
//                                                    for (DocumentSnapshot d : t.getResult()) {
//                                                        data.add(new FeedRecyclerAdapter.FeedPostPreviewData(
//                                                                d,
//                                                                d.getId(),
//                                                                d.get("imageURL").toString()
//                                                        ));
//                                                    }
                                                ArrayList<HashMap> postsNext = new ArrayList<>();
                                                for (DocumentSnapshot d : t.getResult()) {
//                                                        posts.add((HashMap)document.get("recent"));
                                                    Log.e("FEED", "I have found a doc");
                                                    //posts.addAll((ArrayList)document.get("recent"));
                                                    String first = (String) d.get("space1");
                                                    String second = (String) d.get("space2");
                                                    String third = (String) d.get("space3");
                                                    if(d.get("map" + first) != null){
                                                        postsNext.add((HashMap)d.get("map" + first));
                                                    }
                                                    if(d.get("map" + second) != null){
                                                        postsNext.add((HashMap)d.get("map" + second));
                                                    }
                                                    if(d.get("map" + third) != null){
                                                        postsNext.add((HashMap)d.get("map" + third));
                                                    }
                                                }
                                                // Bubble sort items
                                                HashMap<String, Object> temporary;
                                                for (int i = 0; i < (postsNext.size() - 1); i++) {
                                                    for (int j = 0; j < (postsNext.size() - i - 1); j++) {

                                                        if (((Timestamp) postsNext.get(j).get("timestamp")).toDate().before(((Timestamp) posts.get(j + 1).get("timestamp")).toDate())) {

                                                            temporary = postsNext.get(j);
                                                            postsNext.set(j, postsNext.get(j + 1));
                                                            postsNext.set(j + 1, temporary);

                                                        }
                                                    }
                                                }
                                                //posts.addAll(postsNext);

                                                for(int i = 0; i < postsNext.size(); i++){
                                                    data.add(new FeedRecyclerAdapter.FeedPostPreviewData(
                                                            postsNext.get(i)));
                                                }
                                                if(isLastItemReached){
                                                    // Add end here
                                                }
                                                rAdapter.notifyDataSetChanged();
                                                if (t.getResult().size() != 0) {
                                                    lastVisible = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                                }

                                                if (t.getResult().size() < 5) {
                                                    isLastItemReached = true;
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        };
                        recyclerView.addOnScrollListener(onScrollListener);
                    }
                }
            });
        }
    }


    /**
     *  Connecting up elements on the screen to variable names
     */
    protected void initPageItems(View v){
        //Defining all relevant members of page
        mPostButton = v.findViewById(R.id.sendPostButton);
        mPostRecipe = (CheckBox) v.findViewById(R.id.recipeIcon);
        mPostReview = (CheckBox) v.findViewById(R.id.reviewIcon);
        mPostPic = (CheckBox) v.findViewById(R.id.imageIcon);
        mPostBodyInput = v.findViewById(R.id.postBodyInput);
        mUploadedImage = v.findViewById(R.id.userUploadedImageView);
        mPicSpacer = v.findViewById(R.id.picSpacer);
        mAttachedRecipeImage = v.findViewById(R.id.postRecipeImageView);
        mAttachedRecipeTitle = v.findViewById(R.id.postRecipeTitle);
        mAttachedRecipeInfo =  v.findViewById(R.id.postRecipeDescription);
        mAttachedRecipeReview = v.findViewById(R.id.postRecipeRating);
        mRecipeRatingText = v.findViewById(R.id.recipeRate);


    }

    private void postPicClick(){
        //  Check if the version of Android is above 'Marshmallow' we check for additional permission.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            //  Checks if permission has already been granted to read from external storage (our image picker)
            if(getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                //   Ask for permission.
                String [] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                //  Read permission has been granted already.
                imageSelector();
            }
        } else {
            imageSelector();
        }
    }

    /**
     *  Setting up page listeners for when buttons are pressed
     */
    protected void initPageListeners() {
        mPostPic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mPostPic.isChecked()) {
                    if(mUploadedImage.getVisibility() != View.VISIBLE){
                        //  Check if the version of Android is above 'Marshmallow' we check for additional permission.
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            //  Checks if permission has already been granted to read from external storage (our image picker)
                            if(getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                                //   Ask for permission.
                                String [] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                                requestPermissions(permissions, PERMISSION_CODE);
                                mPostPic.setChecked(false);
                                imageSelector();
                            } else {
                                //  Read permission has been granted already.
                                mPostPic.setChecked(false);
                                imageSelector();
                            }
                        } else {
                            mPostPic.setChecked(false);
                            imageSelector();
                        }
                    }
                } else {
                    mUploadedImage.setVisibility(View.GONE);
                }
            }
        });

        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                POST_IS_UPLOADING = true;
                loadingDialog.startLoadingDialog();
                String body = mPostBodyInput.getText().toString();
                //TODO set these variables from the addition of these items

                CollectionReference ref = mDatabase.collection("posts");
                Log.e(TAG, "Added new post ");
                // Saving the comment as a new document
                HashMap<String, Object> map = new HashMap<>();
                HashMap<String, Object> extras = new HashMap<>();
                extras.put("comments", 0);
                extras.put("likes", 0);
                map.put("author", user.getUID());
                map.put("body", body);
                map.put("timestamp", FieldValue.serverTimestamp());
                map.put("isPic", mPostPic.isChecked());
                map.put("isRecipe", mPostRecipe.isChecked());
                map.put("isReview", mPostReview.isChecked());
                if(mPostPic.isChecked()){
                    try {
                        //uploadImage(mImageUri); // Attempt to upload the image in storage to Firebase.
                        checkImage(mImageUri); // Check the image doesn't throw any exceptions

                         // State that the image is still uploading and therefore we shouldn't save a reference on firebase to it yet.

                        /*  Create a unique reference of the format. 'image/profile/[UNIQUE UID]/profile_image.[EXTENSION].
                            Whereby [UNIQUE UID] = the Unique id of the user, [EXTENSION] = file image extension. E.g. .jpg,.png. */
                        StorageReference mImageStorage = mStorageReference.child("images/posts/" + user.getUID() + "/IMAGEID" + (user.getPosts()+1)); //todo input image id

                        //  Check if the upload fails
                        mImageStorage.putFile(mImageUri).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to upload profile image.", Toast.LENGTH_SHORT).show()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                mImageStorage.getDownloadUrl().addOnSuccessListener(locationUri -> { // Successful upload.
                                    map.put("uploadedImageURL", locationUri.toString());
                                    if (mPostRecipe.isChecked()) {
                                        map.put("recipeID", recipeID);
                                        map.put("recipeImageURL", attachedRecipeURL);
                                        map.put("recipeTitle", mAttachedRecipeTitle.getText());
                                        map.put("recipeDescription", mAttachedRecipeInfo.getText());
                                        if (mPostReview.isChecked()) {
                                            map.put("recipeReview", ratingNum);
                                        }
                                    }
                                    // Saving default user to Firebase Firestore database
                                    extras.putAll(map);
                                    ref.add(extras).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                           @Override
                                                                           public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                               if(task.isSuccessful()){
                                                                                   final String docID = task.getResult().getId();
                                                                                   map.put("docID", docID);
                                                                                   mDatabase.collection("followers").document(user.getUID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                       @Override
                                                                                       public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                           if(task.isSuccessful()){
                                                                                               if(task.getResult().exists()){
                                                                                                   // Add post to followers map
                                                                                                   DocumentSnapshot doc = task.getResult();
                                                                                                   String space = "map" + (String) doc.get("space3");
                                                                                                   doc.getReference().update(space, map,
                                                                                                           "space1", doc.get("space3"),
                                                                                                           "space2", doc.get("space1"),
                                                                                                           "space3", doc.get("space2"),
                                                                                                           "lastPost", map.get("timestamp")).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                       @Override
                                                                                                       public void onComplete(@NonNull Task<Void> task) {
                                                                                                           mDatabase.collection("users").document(user.getUID()).update("posts", FieldValue.increment(1));
                                                                                                           mPostBodyInput.getText().clear();
                                                                                                           mPostRecipe.setChecked(false);
                                                                                                           mPostReview.setChecked(false);
                                                                                                           mPostPic.setChecked(false);

                                                                                                           addPosts(mainView);

                                                                                                           // Update the UserInfoPrivate class with this new image URL.
                                                                                                           POST_IS_UPLOADING = false;// State we have finished uploading (a reference exists).
                                                                                                           loadingDialog.dismissDialog();
                                                                                                       }
                                                                                                   });
                                                                                               }else{
                                                                                                   //create new followers map
                                                                                                   HashMap<String, Object> newDoc = new HashMap<>();
                                                                                                   ArrayList<String> arrayList = new ArrayList<>();
                                                                                                   arrayList.add(user.getUID());
                                                                                                   newDoc.put("mapA", map);
                                                                                                   newDoc.put("mapB", (HashMap) null);
                                                                                                   newDoc.put("mapC", (HashMap) null);
                                                                                                   newDoc.put("space1", "A");
                                                                                                   newDoc.put("space2", "B");
                                                                                                   newDoc.put("space3", "C");
                                                                                                   newDoc.put("lastPost", map.get("timestamp"));
                                                                                                   newDoc.put("author", user.getUID());
                                                                                                   newDoc.put("users", arrayList);
                                                                                                   mDatabase.collection("followers").document(user.getUID()).set(newDoc).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                       @Override
                                                                                                       public void onComplete(@NonNull Task<Void> task) {
                                                                                                           mDatabase.collection("users").document(user.getUID()).update("posts", FieldValue.increment(1));
                                                                                                           mPostBodyInput.getText().clear();
                                                                                                           mPostRecipe.setChecked(false);
                                                                                                           mPostReview.setChecked(false);
                                                                                                           mPostPic.setChecked(false);

                                                                                                           addPosts(mainView);

                                                                                                           // Update the UserInfoPrivate class with this new image URL.
                                                                                                           POST_IS_UPLOADING = false;// State we have finished uploading (a reference exists).
                                                                                                           loadingDialog.dismissDialog();
                                                                                                       }
                                                                                                   });
                                                                                               }
                                                                                           }
                                                                                       }
                                                                                   });
                                                                               }
                                                                           }
                                                                       }
                                    );
                                }).addOnFailureListener(e -> {
                                    throw new RuntimeException("Unable to grab image URL from Firebase for image URL being uploaded currently. This shouldn't happen.");
                                });
                            }
                        });
                    } catch (ImageException e) {
                        Log.e(TAG, "Failed to upload photo to Firebase");
                        Toast.makeText(getApplicationContext(),"Failed to upload photo to Firebase, please try again.",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        return;
                    }
                }else {
                    if (mPostRecipe.isChecked()) {
                        map.put("recipeID", recipeID);
                        map.put("recipeImageURL", attachedRecipeURL);
                        map.put("recipeTitle", mAttachedRecipeTitle.getText());
                        map.put("recipeDescription", mAttachedRecipeInfo.getText());
                        if (mPostReview.isChecked()) {
                            map.put("recipeReview", mAttachedRecipeReview.getRating());
                        }
                    }
                    // Saving default user to Firebase Firestore database
                    extras.putAll(map);
                    ref.add(extras).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                              @Override
                                                              public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                  if(task.isSuccessful()){
                                                                      final String docID = task.getResult().getId();
                                                                      map.put("docID", docID);
                                                                      mDatabase.collection("followers").document(user.getUID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                          @Override
                                                                          public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                              if(task.isSuccessful()){
                                                                                  if(task.getResult().exists()){
                                                                                      // Add post to followers map
                                                                                      DocumentSnapshot doc = task.getResult();
                                                                                      String space = "map" + (String) doc.get("space3");
                                                                                      doc.getReference().update(space, map,
                                                                                              "space1", doc.get("space3"),
                                                                                              "space2", doc.get("space1"),
                                                                                              "space3", doc.get("space2"),
                                                                                              "lastPost", map.get("timestamp")).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                          @Override
                                                                                          public void onComplete(@NonNull Task<Void> task) {
                                                                                              mDatabase.collection("users").document(user.getUID()).update("posts", FieldValue.increment(1));
                                                                                              mPostBodyInput.getText().clear();
                                                                                              mPostRecipe.setChecked(false);
                                                                                              mPostReview.setChecked(false);
                                                                                              mPostPic.setChecked(false);

                                                                                              addPosts(mainView);

                                                                                              // Update the UserInfoPrivate class with this new image URL.
                                                                                              POST_IS_UPLOADING = false;// State we have finished uploading (a reference exists).
                                                                                              loadingDialog.dismissDialog();
                                                                                          }
                                                                                      });
                                                                                  }else{
                                                                                      //create new followers map
                                                                                      HashMap<String, Object> newDoc = new HashMap<>();
                                                                                      ArrayList<String> arrayList = new ArrayList<>();
                                                                                      arrayList.add(user.getUID());
                                                                                      newDoc.put("mapA", map);
                                                                                      newDoc.put("mapB", (HashMap) null);
                                                                                      newDoc.put("mapC", (HashMap) null);
                                                                                      newDoc.put("space1", "A");
                                                                                      newDoc.put("space2", "B");
                                                                                      newDoc.put("space3", "C");
                                                                                      newDoc.put("lastPost", map.get("timestamp"));
                                                                                      newDoc.put("author", user.getUID());
                                                                                      newDoc.put("users", arrayList);
                                                                                      mDatabase.collection("followers").document(user.getUID()).set(newDoc).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                          @Override
                                                                                          public void onComplete(@NonNull Task<Void> task) {
                                                                                              mDatabase.collection("users").document(user.getUID()).update("posts", FieldValue.increment(1));
                                                                                              mPostBodyInput.getText().clear();
                                                                                              mPostRecipe.setChecked(false);
                                                                                              mPostReview.setChecked(false);
                                                                                              mPostPic.setChecked(false);

                                                                                              addPosts(mainView);

                                                                                              // Update the UserInfoPrivate class with this new image URL.
                                                                                              POST_IS_UPLOADING = false;// State we have finished uploading (a reference exists).
                                                                                              loadingDialog.dismissDialog();
                                                                                          }
                                                                                      });
                                                                                  }
                                                                              }
                                                                          }
                                                                      });
                                                                  }
                                                              }
                                                          }
                    );

                }

            }
        });
        mPostRecipe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mPostRecipe.isChecked()) {
                    if(mAttachedRecipeImage.getVisibility() != View.VISIBLE){
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("planner", true); //Condition to let child fragments know access is from planner

                        //Creates and launches recipe fragment
                        recipeFragment = new RecipeFragment(user);
                        recipeFragment.setArguments(bundle);
                        recipeFragment.setTargetFragment(FeedFragment.this, 1);
                        fragmentTransaction = getParentFragmentManager().beginTransaction();
                        fragmentTransaction.add(R.id.frameLayout, recipeFragment); //Overlays fragment on existing one
                        fragmentTransaction.commitNow(); //Waits for fragment transaction to be completed
                        requireView().setVisibility(View.INVISIBLE); //Sets current fragment invisible

                        //Makes search bar icon visible
                        searchView.setQuery("", false);
                        searchView.setVisibility(View.VISIBLE);
                        setSearch();

                    }
                } else {
                    mAttachedRecipeImage.setVisibility(View.GONE);
                    mAttachedRecipeInfo.setVisibility(View.GONE);
                    mAttachedRecipeTitle.setVisibility(View.GONE);
                    mPicSpacer.setVisibility(View.GONE);
                    mPostReview.setChecked(false);
                }
            }
        });
        mPostReview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mPostReview.isChecked()) {
                    if(mAttachedRecipeReview.getVisibility() != View.VISIBLE){
                        if(mPostRecipe.isChecked()){
                            //Makes the star rating visible and stores the value of the given rating
                            mRecipeRatingText.setVisibility(View.VISIBLE);
                            mAttachedRecipeReview.setVisibility(View.VISIBLE);
                            mAttachedRecipeReview.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                @Override
                                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                    //TODO do something with rating if needed
                                    ratingNum = rating;
                                }
                            });
                        }else{
                            Log.e(TAG, "No recipe so no review");
                            mPostReview.setChecked(false);
                            Toast.makeText(getContext(),"You need to attach a recipe before you can review it.",Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    mRecipeRatingText.setVisibility(View.GONE);
                    mAttachedRecipeReview.setVisibility(View.GONE);
                }
            }
        });
        mPostPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    //  Open our image picker.
    private void imageSelector(){
        Intent images = new Intent(Intent.ACTION_PICK);
        images.setType("image/*"); // Only open the 'image' file picker. Don't include videos, audio etc...
        startActivityForResult(images, IMAGE_REQUEST_CODE);
        //mPostPic.setChecked(false);// Start the image picker and expect a result once an image is selected.
    }

    /** Handle our activity result for the image picker.
     * @param requestCode - Image request code.
     * @param resultCode - Success/failure code. 0 = success, -1 = failure.
     * @param data - Our associated image data.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //   Check for a valid request code and successful result.
        if(requestCode == IMAGE_REQUEST_CODE && resultCode==RESULT_OK){
            if(data!=null && data.getData()!= null){
                mImageUri = data.getData();

                //  Use Glides image functionality to quickly load a circular, center cropped image.
                Glide.with(this)
                        .load(mImageUri)
                        .apply(RequestOptions.centerCropTransform())
                        .into(mUploadedImage);
                mUploadedImage.setVisibility(View.VISIBLE);
                mPostPic.setChecked(true);
                if(mPostRecipe.isChecked()){
                    mPicSpacer.setVisibility(View.VISIBLE);
                    DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
                    mUploadedImage.setMaxWidth(displayMetrics.widthPixels/2-20);
                    mAttachedRecipeImage.setMaxWidth(displayMetrics.widthPixels/2-20);

//                    if(mUploadedImage.getWidth() > mAttachedRecipeImage.()){
//                        mUploadedImage.setMaxHeight(mAttachedRecipeImage.getHeight());
//                    }else{
//                        mAttachedRecipeImage.setMaxHeight(mUploadedImage.getHeight());
//                    }
                }else {
                    mPicSpacer.setVisibility(View.GONE);
                }
            }else{
                mPostPic.setChecked(false);
            }
        }else if(resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();

            //Hides menu options
            sortButton.setVisible(false);

            //Clears search view
            searchView.clearFocus();
            searchView.onActionViewCollapsed();
            searchView.setVisibility(View.INVISIBLE);

            //Compiles bundle into a hashmap object for serialization
            final HashMap<String, Object> map = new HashMap<>();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    map.put(key, bundle.get(key));
                }

                //Sets new listener for inserted recipe to open info fragment
                mAttachedRecipeImage.setOnClickListener(v -> openRecipeInfo(map));

                //Loads recipe image
                Glide.with(this).
                        load(bundle.getString("imageURL"))
                        .apply(RequestOptions.centerCropTransform())
                        .into(mAttachedRecipeImage);
                attachedRecipeURL = bundle.getString("imageURL");
                recipeID = bundle.getString("recipeID");
//                Picasso.get().load(bundle.getString("imageURL")).into(mAttachedRecipeImage);
                mAttachedRecipeTitle.setText(bundle.getString("recipeTitle"));
                mAttachedRecipeInfo.setText(bundle.getString("recipeDescription"));
                mAttachedRecipeTitle.setVisibility(View.VISIBLE);
                mAttachedRecipeInfo.setVisibility(View.VISIBLE);
                mAttachedRecipeImage.setVisibility(View.VISIBLE);
                if(mPostPic.isChecked()){
                    mPicSpacer.setVisibility(View.VISIBLE);
                    DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
                    mUploadedImage.setMaxWidth(displayMetrics.widthPixels/2-20);
                    mAttachedRecipeImage.setMaxWidth(displayMetrics.widthPixels/2-20);
//                    if(mUploadedImage.getHeight() > mAttachedRecipeImage.getHeight()){
//                        mUploadedImage.setMaxHeight(mAttachedRecipeImage.getHeight());
//                    }else{
//                        mAttachedRecipeImage.setMaxHeight(mUploadedImage.getHeight());
//                    }

                }else{
                    mPicSpacer.setVisibility(View.GONE);
                }

                //Removes recipe fragment overlay and makes planner fragment visible
                fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.remove(recipeFragment).commitNow();
                requireView().setVisibility(View.VISIBLE);

            }
        }
    }

    /** Image checker.
     *  Used to reduce wait times for the user when uploading on a slow network.
     *  Also limits the data that has to be stored and queried from Firebase.
     *  @param uri - The unique uri of the image file location from the users storage.
     *  @throws ImageException - Throws if the image file is too large or the format isn't a supported image format.
     */
    private void checkImage(Uri uri) throws ImageException {

        //  If the image files size is greater than the max file size in mb converted to bytes throw an exception and return this issue to the user.
        if(getSize(this.getContext(), uri) > MAX_IMAGE_FILE_SIZE_IN_MB * 1000000){
            Toast.makeText(this.getContext(), "Image exceeded: " + MAX_IMAGE_FILE_SIZE_IN_MB + "mb limit. Please choose a different file.", Toast.LENGTH_LONG).show();
            throw new ImageException("Profile image exceeded max file size: " + MAX_IMAGE_FILE_SIZE_IN_MB + "mb");
        }

        boolean formatIsSupported = isImageFormatSupported(this.getContext(), uri); // Check if the image is of a supported format
        String extension = getExtension(this.getContext(), uri); // Grab the extension as a string.

        //  If our format isn't supported then throw an exception. Otherwise continue and don't throw an exception indicating a successful image check.
        if(!formatIsSupported) {
            Toast.makeText(this.getContext(), "Image extension: '" + getExtension(this.getContext(), uri) +"' is not supported.", Toast.LENGTH_LONG).show();

            new CountDownTimer(3600, 200){ // Display another toast message after the existing one. Long Toast messages last 3500ms, hence 3600 delay.
                @Override
                public void onTick(long millisUntilFinished) { /*Do Nothing...*/ }
                @Override
                public void onFinish() {
                    //   Make the user aware of the supported formats they can upload.
                    Toast.makeText(getApplicationContext(), "Supported formats: " + getPrintableSupportedFormats(), Toast.LENGTH_LONG).show();
                }
            }.start();

            throw new ImageException("Image format type: " + extension + " is not supported");
        }
    }

//    /** Upload the image to Firebase. Initiated before saving preferences if the format and size is supported to give some time for the app to upload the image.
//     *  Does not update the users reference to the image. This is updated after saving the users preferences.
//     *
//     * @param uri - The unique uri of the image file location from the users storage.
//     * @throws ImageException - Thrown if URL cannot be retrieved. This will only fail if there is a reference to a blank file.
//     *  In normal operation this shouldn't happen.
//     */
//    private void uploadImage(Uri uri) throws ImageException {
//        checkImage(uri); // Check the image doesn't throw any exceptions
//
//        POST_IS_UPLOADING = true; // State that the image is still uploading and therefore we shouldn't save a reference on firebase to it yet.
//
//        /*  Create a unique reference of the format. 'image/profile/[UNIQUE UID]/profile_image.[EXTENSION].
//            Whereby [UNIQUE UID] = the Unique id of the user, [EXTENSION] = file image extension. E.g. .jpg,.png. */
//        StorageReference mImageStorage = mStorageReference.child("images/posts/" + user.getUID() + "/IMAGEID" + (user.getPosts()+1)); //todo input image id
//
//        //  Check if the upload fails
//        mImageStorage.putFile(uri).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to upload profile image.", Toast.LENGTH_SHORT).show()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                mImageStorage.getDownloadUrl().addOnSuccessListener(locationUri -> { // Successful upload.
//                    // TODO set image URL for post - only save when uploading post
//                    //uploadedImageURL = locationUri.toString(); // Update the UserInfoPrivate class with this new image URL.
//                    POST_IS_UPLOADING = false; // State we have finished uploading (a reference exists).
//                }).addOnFailureListener(e -> {
//                    throw new RuntimeException("Unable to grab image URL from Firebase for image URL being uploaded currently. This shouldn't happen.");
//                });
//            }
//        });
//    }

    //Opens info dialog for selected recipe
    private void openRecipeInfo(HashMap<String, Object> map) {
        map.put("planner", false); //Allows lauching of presentation
        Bundle bundle = new Bundle();
        bundle.putSerializable("hashmap", map);

        //Creates and launches info fragment
        PlannerInfoFragment plannerInfoFragment = new PlannerInfoFragment();
        plannerInfoFragment.setArguments(bundle);
        plannerInfoFragment.show(getParentFragmentManager(), "Show recipe dialog fragment");
    }



    //Quick function to reset search menu functionality
    private void setSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                SearchQuery query = new SearchQuery( s, prefs);
                openRecipeDialog(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    //Opens list fragment on searching
    private void openRecipeDialog(SearchQuery query) {
        //Creates and launches fragment with required query
        PlannerListFragment plannerListFragment = new PlannerListFragment(user);
        plannerListFragment.setValue(query.getQuery());
        plannerListFragment.setTargetFragment(this, 1);
        plannerListFragment.show(getParentFragmentManager(), "search");
    }

    /**
     * On click of a recipe a new recipe info fragment is opened and the document is sent through
     * This saves on downloading the data again from the database
     */
    public void itemSelected(Map<String, Object> document, Bundle mBundle,TextView likes, CheckBox likedOrNot, TextView numComments) {

        PostPage postDialogFragment = new PostPage(likes, likedOrNot, numComments);
        postDialogFragment.setArguments(mBundle);
        postDialogFragment.setTargetFragment(this, 1);
        postDialogFragment.show(getFragmentManager(), "Show post dialog fragment");

        //Takes ingredient array from snap shot and reformats before being passed through to fragment
//        ArrayList<String> ingredientArray = new ArrayList<>();
//
//        Map<String, Map<String, Object>> test = (Map) document.getData().get("Ingredients");
//        Iterator hmIterator = test.entrySet().iterator();
//
//        while (hmIterator.hasNext()) {
//            Map.Entry mapElement = (Map.Entry) hmIterator.next();
//            String string = mapElement.getKey().toString() + ": " + mapElement.getValue().toString();
//            ingredientArray.add(string);
//        }
//
//        //Creating a bundle so all data needed from firestore query snapshot can be passed through into fragment class
//        Bundle bundle = new Bundle();
//        bundle.putStringArrayList("ingredientList", ingredientArray);
//        bundle.putString("recipeID", document.getId());
//        bundle.putString("xmlURL", document.get("xml_url").toString());
//        bundle.putString("recipeTitle", document.get("Name").toString());
//        bundle.putString("rating", document.get("score").toString());
//        bundle.putString("imageURL", document.get("imageURL").toString());
//        bundle.putString("recipeDescription", document.get("Description").toString());
//        bundle.putString("chefName", document.get("Chef").toString());
//        bundle.putSerializable("user", user);
//
//
//        RecipeInfoFragment recipeDialogFragment = new RecipeInfoFragment();
//        recipeDialogFragment.setArguments(bundle);
//        recipeDialogFragment.show(getFragmentManager(), "Show recipe dialog fragment");
    }
}