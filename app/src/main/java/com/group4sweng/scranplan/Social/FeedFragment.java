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
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.group4sweng.scranplan.Administration.ContentReporting;
import com.group4sweng.scranplan.Exceptions.ImageException;
import com.group4sweng.scranplan.Home;
import com.group4sweng.scranplan.Administration.LoadingDialog;
import com.group4sweng.scranplan.MealPlanner.PlannerInfoFragment;
import com.group4sweng.scranplan.MealPlanner.PlannerListFragment;
import com.group4sweng.scranplan.PublicProfile;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.RecipeInfo.RecipeInfoFragment;
import com.group4sweng.scranplan.SearchFunctions.RecipeFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchPrefs;
import com.group4sweng.scranplan.SearchFunctions.SearchQuery;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static com.group4sweng.scranplan.Helper.ImageHelpers.getExtension;
import static com.group4sweng.scranplan.Helper.ImageHelpers.getPrintableSupportedFormats;
import static com.group4sweng.scranplan.Helper.ImageHelpers.getSize;
import static com.group4sweng.scranplan.Helper.ImageHelpers.isImageFormatSupported;


/**
 * Class for the home page feed fragment.
 * Author(s): LNewman
 * (c) CoDev 2020
 *
 * This class builds the vertical scroll of followed users in the feed fragment but also adds the
 * functionality to create a new posts that will then populate on the feed and within this users profile.
 */
public class FeedFragment extends Fragment {

    final String TAG = "Home horizontal queries";

    // Unique codes for image & permission request activity callbacks.
    private static final int IMAGE_REQUEST_CODE = 2;
    private static final int PERMISSION_CODE = 1001;

    private static final int MAX_IMAGE_FILE_SIZE_IN_MB = 4; // Max storage image size for the profile picture.

    protected Uri mImageUri; // Unique image uri.
    ImageView mUploadedImage;

    float ratingNum;

    protected LoadingDialog loadingDialog;
    protected ContentReporting reportContent;

    //Score scroll info
    List<FeedRecyclerAdapter.FeedPostPreviewData> data;
    protected DocumentSnapshot lastVisible;
    protected boolean isScrolling = false;
    protected boolean isLastItemReached = false;

    protected Button mPostButton;
    CheckBox mPostRecipe;
    CheckBox mPostReview;
    protected CheckBox mPostPic;
    protected EditText mPostBodyInput;

    ImageView mAttachedRecipeImage;
    TextView mAttachedRecipeTitle;
    TextView mAttachedRecipeInfo;
    String attachedRecipeURL;
    String recipeID;
    String newPostID;

    ConstraintLayout mUserUploadedImageViewLayout;
    ConstraintLayout mPostRecipeImageViewLayout;

    View mainView;

    TextView mRecipeRatingText;
    RatingBar mAttachedRecipeReview;


    protected double oldOverallRating;
    protected double oldTotalRates;

    //Fragment handlers
    private FragmentTransaction fragmentTransaction;
    private RecipeFragment recipeFragment;

    //User information
    protected com.group4sweng.scranplan.UserInfo.UserInfoPrivate mUser;
    private SearchPrefs prefs;

    //Menu items
    private SearchView searchView;
    private MenuItem sortButton;

    protected Query query;



    // Database objects for accessing recipes
    protected FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mColRef = mDatabase.collection("followers");
    // Firebase user collection and storage references.
    CollectionReference mRef = mDatabase.collection("posts");
    FirebaseStorage mStorage = FirebaseStorage.getInstance();
    protected StorageReference mStorageReference = mStorage.getReference();


    public FeedFragment(UserInfoPrivate userSent){mUser = userSent;}

    public FeedFragment() {

    }


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

        initPageItems(view);
        initPageListeners();

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

        // Checks users details have been provided
        if(mUser == null){
            // If scroll views fail due to no user, this error is reported
            Log.e(TAG, "ERROR: Loading social feed - We were unable to find user.");
        }
        return view;
    }


    /**
     * Adding all posts for current user to the feed - implementing an infinite scroll for all
     * followers 3 most recent posts
     * @param view
     */
    protected void addPosts(View view){
        final RecyclerView recyclerView = view.findViewById(R.id.postsList);
        // Set out the layout of this horizontal view
        RecyclerView.LayoutManager rManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(rManager);
        //recyclerView.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels));
        // Array to score downloaded data
        data = new ArrayList<>();
        final RecyclerView.Adapter rAdapter = new FeedRecyclerAdapter(FeedFragment.this, data, mUser, view, getActivity());
        recyclerView.setAdapter(rAdapter);
        query = mColRef.whereArrayContains("users", mUser.getUID()).orderBy("lastPost", Query.Direction.DESCENDING).limit(5);
        // Ensure query exists and builds view with query
        if (query != null) {
            Log.e(TAG, "User is searching the following query: " + query.toString());
            // Query listener to add data to view
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.e("FEED", "UID = " + mUser.getUID());
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
                                                ArrayList<HashMap> postsNext = new ArrayList<>();
                                                for (DocumentSnapshot d : t.getResult()) {
                                                    Log.e("FEED", "I have found a doc");
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
        mAttachedRecipeImage = v.findViewById(R.id.postRecipeImageView);
        mAttachedRecipeTitle = v.findViewById(R.id.postRecipeTitle);
        mAttachedRecipeInfo =  v.findViewById(R.id.postRecipeDescription);
        mAttachedRecipeReview = v.findViewById(R.id.postRecipeRating);
        mRecipeRatingText = v.findViewById(R.id.recipeRate);
        mUserUploadedImageViewLayout = v.findViewById(R.id.userUploadedImageViewLayout);
        mPostRecipeImageViewLayout = v.findViewById(R.id.postRecipeImageViewLayout);


    }

    /**
     * Updating the follows section of firebase when creating a new post
     * @param map
     */
    protected void updateFollowers(HashMap map){
        mDatabase.collection("followers").document(mUser.getUID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        // Add post to followers map
                        DocumentSnapshot doc = task.getResult();
                        boolean oldReview = false;
                        String delMap = "";
                        if(doc.get("mapA") != null){
                            if(((String)((HashMap)doc.get("mapA")).get("docID")).equals(newPostID)){
                                oldReview = true;
                                delMap = "A";
                            }
                        }else if(doc.get("mapB") != null){
                            if(((String)((HashMap)doc.get("mapB")).get("docID")).equals(newPostID)){
                                oldReview = true;
                                delMap = "B";
                            }
                        }else if(doc.get("mapC") != null){
                            if(((String)((HashMap)doc.get("mapC")).get("docID")).equals(newPostID)){
                                oldReview = true;
                                delMap = "C";
                            }
                        }
                        if(oldReview){
                            if(((String) doc.get("space3")).equals(delMap)){
                                String space = "map" + (String) doc.get("space3");
                                doc.getReference().update(space, map,
                                        "space1", doc.get("space3"),
                                        "space2", doc.get("space1"),
                                        "space3", doc.get("space2"),
                                        "lastPost", map.get("timestamp")).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        postComplete();
                                    }
                                });
                            }else{
                                String space = "map" + (String) doc.get("space3");
                                doc.getReference().update(space, map,
                                        "space1", doc.get("space3"),
                                        "space2", doc.get("space1"),
                                        "space3", doc.get("space2"),
                                        "lastPost", map.get("timestamp"),
                                        "map" + delMap, null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        postComplete();
                                    }
                                });
                            }
                        }else{
                            String space = "map" + (String) doc.get("space3");
                            doc.getReference().update(space, map,
                                    "space1", doc.get("space3"),
                                    "space2", doc.get("space1"),
                                    "space3", doc.get("space2"),
                                    "lastPost", map.get("timestamp")).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    postComplete();
                                }
                            });
                        }

                    }else{
                        //create new followers map
                        HashMap<String, Object> newDoc = new HashMap<>();
                        ArrayList<String> arrayList = new ArrayList<>();
                        arrayList.add(mUser.getUID());
                        ArrayList<String> second = new ArrayList<>();
                        newDoc.put("mapA", map);
                        newDoc.put("mapB", (HashMap) null);
                        newDoc.put("mapC", (HashMap) null);
                        newDoc.put("space1", "A");
                        newDoc.put("space2", "B");
                        newDoc.put("space3", "C");
                        newDoc.put("lastPost", map.get("timestamp"));
                        newDoc.put("author", mUser.getUID());
                        newDoc.put("users", arrayList);
                        newDoc.put("requested", second);
                        mDatabase.collection("followers").document(mUser.getUID()).set(newDoc).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                postComplete();
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * Reset the screen once a new post has been completed
     */
    protected void postComplete(){
        mDatabase.collection("users").document(mUser.getUID()).update("posts", FieldValue.increment(1), "livePosts", FieldValue.increment(1)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                                        @Override
                                                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                                            mUser.setPosts(mUser.getPosts()+1);
                                                                                                                                                                            mPostBodyInput.getText().clear();
                                                                                                                                                                            mPostRecipe.setChecked(false);
                                                                                                                                                                            mPostReview.setChecked(false);
                                                                                                                                                                            mPostPic.setChecked(false);
                                                                                                                                                                            addPosts(mainView);
                                                                                                                                                                            loadingDialog.dismissDialog();
                                                                                                                                                                        }
                                                                                                                                                                    }
        );
    }



    /**
     * Method To check if user has already left a review on a recipe. If the user has, it will populate the review input box with their previous
     * review so they can edit if they wish.
     */
    private void reviewAttached(HashMap map, HashMap extras, CollectionReference ref) {
        mDatabase.collection("recipes").document(recipeID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    final HashMap<String,Object> ratingMap = (HashMap<String,Object>) document.get("rating");
                    mDatabase.collection("reviews").document(mUser.getUID() + "-" + recipeID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                double oldOverallRating;
                                double oldTotalRates;
                                if (task.getResult().exists()) {
                                    Log.e(TAG, "exists ");


                                    newPostID = document.get("post").toString();
                                    String oldUserStarRating = document.get("overallRating").toString();

                                    Double oldUserRating = Double.parseDouble(oldUserStarRating);
                                    oldTotalRates = (double)ratingMap.get("totalRates")-1;
                                    oldOverallRating = ((((double)ratingMap.get("overallRating") * (double)ratingMap.get("totalRates")) - oldUserRating)) / oldTotalRates;
                                    Log.i(TAG, "Values: "+ oldOverallRating);

                                    ratingMap.put("overallRating", oldOverallRating);
                                    ratingMap.put("totalRates", oldTotalRates);

                                    HashMap<String, Object> updateMap = new HashMap<>();
                                    CollectionReference ref = mDatabase.collection("recipes");
                                    DocumentReference documentReference = ref.document(recipeID);
                                    updateMap.put("rating", ratingMap);
                                    documentReference.update(updateMap);

                                    Log.i(TAG, "Values: "+ ratingMap);

                                } else {
                                    Log.e("FdRc", "Unable to retrieve user document in Firestore ");
                                }

                                double newTotalRates = (double)ratingMap.get("totalRates") + 1;
                                double newOverallRating = (((double)ratingMap.get("overallRating") * (double)ratingMap.get("totalRates")) + ratingNum) / newTotalRates;

                                ratingMap.put("overallRating", newOverallRating);
                                ratingMap.put("totalRates", newTotalRates);

                                mDatabase.collection("recipes").document(recipeID).update("rating", ratingMap);
                                HashMap<String, Object> newRatings = new HashMap<>();
                                newRatings.put("overallRating", ratingNum);
                                newRatings.put("timestamp", FieldValue.serverTimestamp());
                                newRatings.put("user", mUser.getUID());
                                Log.i(TAG, "Values: "+ ratingMap);
                                savePost(map, extras, ref, newRatings);
                            }
                        }

                    });
                }
            }
        });


    }


    /**
     *  Adding recipe info if attached to post
     * @param map
     * @param extras
     * @param ref
     */
    protected void addRecipeInfo(HashMap map, HashMap extras, CollectionReference ref){
        if (mPostRecipe.isChecked()) {
            map.put("recipeID", recipeID);
            map.put("recipeImageURL", attachedRecipeURL);
            map.put("recipeTitle", mAttachedRecipeTitle.getText());
            map.put("recipeDescription", mAttachedRecipeInfo.getText());
        }
        extras.putAll(map);
        if (mPostReview.isChecked() && mPostRecipe.isChecked()) {
            map.put("overallRating", mAttachedRecipeReview.getRating());
            extras.put("overallRating", mAttachedRecipeReview.getRating());
            reviewAttached(map, extras, ref);
        }else{
            savePost(map, extras, ref, null);
        }
    }

    protected void savePost(HashMap map, HashMap extras, CollectionReference ref, HashMap<String, Object> newRatings){
        if(newPostID == null){
            ref.add(extras).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                      @Override
                                                      public void onComplete(@NonNull Task<DocumentReference> task) {
                                                          if(task.isSuccessful()){
                                                              final String docID = task.getResult().getId();
                                                              map.put("docID", docID);
                                                              if(mPostReview.isChecked() && mPostRecipe.isChecked()){
                                                                  newRatings.put("post", docID);
                                                                  mDatabase.collection("reviews").document(mUser.getUID() + "-" + recipeID).set(newRatings);
                                                              }
                                                              updateFollowers(map);
                                                          }
                                                      }
                                                  }
            );
        }else{
            ref.document(newPostID).set(extras);
            map.put("docID", newPostID);
            if(mPostReview.isChecked() && mPostRecipe.isChecked()){
                newRatings.put("post", newPostID);
                mDatabase.collection("reviews").document(mUser.getUID() + "-" + recipeID).set(newRatings);
            }
            updateFollowers(map);
        }
    }

    /**
     * Adding image to post if an image is attached
     * @param map
     * @param extras
     * @param ref
     */
    protected void postImageAttached(HashMap map, HashMap extras, CollectionReference ref){
        try {
            checkImage(mImageUri);
            StorageReference mImageStorage = mStorageReference.child("images/posts/" + mUser.getUID() + "/IMAGEID" + (mUser.getPosts()+1));
            mImageStorage.putFile(mImageUri).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to upload profile image.", Toast.LENGTH_SHORT).show()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mImageStorage.getDownloadUrl().addOnSuccessListener(locationUri -> { // Successful upload.
                        map.put("uploadedImageURL", locationUri.toString());
                        addRecipeInfo(map, extras, ref);
                    }).addOnFailureListener(e -> {
                        throw new RuntimeException("Unable to grab image URL from Firebase for image URL being uploaded currently. This shouldn't happen.");
                    });
                }
            });
        } catch (ImageException e) {
            Log.e(TAG, "Failed to upload photo to Firebase");
            e.printStackTrace();
            loadingDialog.dismissDialog();
            return;
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
                    mUserUploadedImageViewLayout.setVisibility(View.GONE);
                }
            }
        });

        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String body = mPostBodyInput.getText().toString();
                if(mPostPic.isChecked() || mPostRecipe.isChecked() || !body.equals("")){
                    loadingDialog.startLoadingDialog();
                    //TODO set these variables from the addition of these items

                    CollectionReference ref = mDatabase.collection("posts");
                    Log.e(TAG, "Added new post ");
                    // Saving the comment as a new document
                    HashMap<String, Object> map = new HashMap<>();
                    HashMap<String, Object> extras = new HashMap<>();
                    extras.put("comments", 0);
                    extras.put("likes", 0);
                    map.put("author", mUser.getUID());
                    map.put("body", body);
                    map.put("timestamp", FieldValue.serverTimestamp());
                    map.put("isPic", mPostPic.isChecked());
                    map.put("isRecipe", mPostRecipe.isChecked());
                    map.put("isReview", mPostReview.isChecked());
                    if(mPostPic.isChecked()){
                        postImageAttached(map, extras, ref);
                    }else {
                        addRecipeInfo(map, extras, ref);
                    }
                }else{
                    Toast.makeText(getContext(),"You need to either write a post, attach a picture or attach a recipe before you can submit new post.",Toast.LENGTH_SHORT).show();
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
                        recipeFragment = new RecipeFragment(mUser, 3);
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
                    mPostRecipeImageViewLayout.setVisibility(View.GONE);
                    mAttachedRecipeInfo.setVisibility(View.GONE);
                    mAttachedRecipeTitle.setVisibility(View.GONE);
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
                mUserUploadedImageViewLayout.setVisibility(View.VISIBLE);
                mPostRecipeImageViewLayout.setVisibility(View.VISIBLE);
                mPostPic.setChecked(true);
                if(mPostRecipe.isChecked()){
                    DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
                    mUploadedImage.setMaxWidth(displayMetrics.widthPixels/2-20);
                    mAttachedRecipeImage.setMaxWidth(displayMetrics.widthPixels/2-20);
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
                mAttachedRecipeTitle.setText(bundle.getString("recipeTitle"));
                mAttachedRecipeInfo.setText(bundle.getString("recipeDescription"));
                mAttachedRecipeTitle.setVisibility(View.VISIBLE);
                mAttachedRecipeInfo.setVisibility(View.VISIBLE);
                mAttachedRecipeImage.setVisibility(View.VISIBLE);
                mPostRecipeImageViewLayout.setVisibility(View.VISIBLE);
                if(mPostPic.isChecked()){
                    DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
                    mUploadedImage.setMaxWidth(displayMetrics.widthPixels/2-20);
                    mAttachedRecipeImage.setMaxWidth(displayMetrics.widthPixels/2-20);
                }

                //Removes recipe fragment overlay and makes planner fragment visible
                fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.remove(recipeFragment).commitNow();
                requireView().setVisibility(View.VISIBLE);

            }
        }else if (requestCode != IMAGE_REQUEST_CODE){

            fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.remove(recipeFragment).commitNow();
            requireView().setVisibility(View.VISIBLE);
            //Hides menu options
            sortButton.setVisible(false);
            //Clears search view
            searchView.clearFocus();
            searchView.onActionViewCollapsed();
            searchView.setVisibility(View.INVISIBLE);
            // removes check
            mPostRecipe.setChecked(false);
        }
    }

    /** Image checker.
     *  Used to reduce wait times for the user when uploading on a slow network.
     *  Also limits the data that has to be stored and queried from Firebase.
     *  @param uri - The unique uri of the image file location from the users storage.
     *  @throws ImageException - Throws if the image file is too large or the format isn't a supported image format.
     */
    protected void checkImage(Uri uri) throws ImageException {

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
        Home home = (Home) getActivity();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                prefs = home.getSearchPrefs();
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
        PlannerListFragment plannerListFragment = new PlannerListFragment(mUser);
        plannerListFragment.setValue(query.getQuery());
        plannerListFragment.setIndex(query.getIndex());
        plannerListFragment.setTargetFragment(this, 1);
        plannerListFragment.show(getParentFragmentManager(), "search");
    }

    /**
     * On click of a recipe a new recipe info fragment is opened and the document is sent through
     * This saves on downloading the data again from the database
     */
    public void itemSelected(Map<String, Object> document, Bundle mBundle,TextView likes, CheckBox likedOrNot, TextView numComments, View view) {

        PostPage postDialogFragment = new PostPage(likes, likedOrNot, numComments, view);
        postDialogFragment.setArguments(mBundle);
        postDialogFragment.setTargetFragment(this, 1);
        postDialogFragment.show(getFragmentManager(), "Show post dialog fragment");

    }


    /**
     * This method checks what comment is selected and opens up a menu to either open up another
     * users profile or if the comment was made my this user, user can delete the comment.
     * @param document
     * @param menu
     */
    public void menuSelected(HashMap document, View menu){
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(getContext(), menu);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.menu_comment, popup.getMenu());
        if(document.get("author").toString().equals(mUser.getUID())){
            popup.getMenu().getItem(0).setVisible(false);
            popup.getMenu().getItem(1).setVisible(false);
            popup.getMenu().getItem(2).setVisible(true);
        }else{
            popup.getMenu().getItem(0).setVisible(true);
            popup.getMenu().getItem(1).setVisible(true);
            popup.getMenu().getItem(2).setVisible(false);
        }

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(
                    MenuItem item) {
                // Give each item functionality
                switch (item.getItemId()) {
                    case R.id.viewCommentProfile:
                        Log.e(TAG,"Clicked open profile!");
                        Intent intentProfile = new Intent(getContext(), PublicProfile.class);

                        intentProfile.putExtra("UID", (String) document.get("author"));
                        intentProfile.putExtra("user", mUser);
                        //setResult(RESULT_OK, intentProfile);
                        startActivity(intentProfile);
                        break;
                    case R.id.reportComment:
                        Log.e(TAG,"Report post clicked!");

                        //HashMap with relevant information to be sent for reporting
                        HashMap<String, Object> reportsMap = new HashMap<>();
                        reportsMap.put("docID", document.get("docID").toString());
                        reportsMap.put("usersID", document.get("author").toString());
                        reportsMap.put("issue","Reporting Content");

                        //creating a dialog box on screen so that the user can report an issue
                        String firebaseLocation = "reporting";
                        reportContent = new ContentReporting(getActivity(), reportsMap, firebaseLocation);
                        reportContent.startReportingDialog();
                        reportContent.title.setText("Report Content");
                        reportContent.message.setText("What is the issue you would like to report?");

                        break;
                    case R.id.deleteComment:
                        Log.e(TAG,"Clicked delete post!");
                        final String deleteDocID = (String) document.get("docID");
                        deletePost(deleteDocID, document, mainView);

                        break;
                }
                return true;
            }
        });

        popup.show();//showing popup menu
    }

    /**
     * Adding capability to delete post from post page
     * @param deleteDocID
     */
    public void deletePost(String deleteDocID, HashMap doc, View view){
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();
        String userID = (String)doc.get("author");
        if((String)doc.get("author") != null){
            // Delete review if one
            if(doc.get("isReview") != null){
                if((boolean)doc.get("isReview")){
                    mDatabase.collection("recipes").document((String)doc.get("recipeID")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult() != null){
                                HashMap<String, Double> ratingMap = (HashMap) task.getResult().get("rating");
                                if(doc.get("overallRating") instanceof Double){
                                    revertRating(ratingMap, (String)doc.get("recipeID"), (double)doc.get("overallRating"));
                                }else{
                                    revertRating(ratingMap, (String)doc.get("recipeID"), Double.parseDouble( (String) doc.get("overallRating")));
                                }
                            }
                        }
                    });
                    // UserID-RecipeID
                    mDatabase.collection("reviews").document((String)doc.get("author") + "-" + (String)doc.get("recipeID")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult().exists()){
                                task.getResult().getReference().delete();
                            }
                        }
                    });
                }
            }
            // Delete pic if one
            if(doc.get("isPic") != null){
                if((boolean)doc.get("isPic")){
                    if(doc.get("uploadedImageURL") != null){
                        mStorage.getReferenceFromUrl((String)doc.get("uploadedImageURL")).delete();
                    }
                }
            }
            // Delete in followers collection if in there
            mDatabase.collection("followers").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if(task.getResult().exists()){
                            // Add post to followers map
                            DocumentSnapshot doc = task.getResult();
                            if(((HashMap)doc.get("mapA")) != null && ((HashMap)doc.get("mapA")).get("docID").equals(deleteDocID)){
                                String map = "mapA";
                                mDatabase.collection("followers").document(userID).update(map, null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mDatabase.collection("posts").document(deleteDocID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mDatabase.collection("users").document(userID).update("livePosts", FieldValue.increment(- 1));
                                                addPosts(view);
                                                loadingDialog.dismissDialog();
                                            }
                                        });
                                    }
                                });
                            }else if(((HashMap)doc.get("mapB")) != null && ((HashMap)doc.get("mapB")).get("docID").equals(deleteDocID)){
                                String map = "mapB";
                                mDatabase.collection("followers").document(userID).update(map, null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mDatabase.collection("posts").document(deleteDocID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mDatabase.collection("users").document(mUser.getUID()).update("livePosts", FieldValue.increment(- 1));
                                                addPosts(view);
                                                loadingDialog.dismissDialog();
                                            }
                                        });
                                    }
                                });
                            }else if(((HashMap)doc.get("mapC")) != null && ((HashMap)doc.get("mapC")).get("docID").equals(deleteDocID)){
                                String map = "mapC";
                                mDatabase.collection("followers").document(userID).update(map, null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mDatabase.collection("posts").document(deleteDocID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mDatabase.collection("users").document(userID).update("livePosts", FieldValue.increment(- 1));
                                                addPosts(view);
                                                loadingDialog.dismissDialog();
                                            }
                                        });
                                    }
                                });
                            }else{
                                // Finally just delete doc
                                mDatabase.collection("posts").document(deleteDocID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mDatabase.collection("users").document(userID).update("livePosts", FieldValue.increment(- 1));
                                        addPosts(view);
                                        loadingDialog.dismissDialog();
                                    }
                                });
                            }
                        }else{
                            mDatabase.collection("posts").document(deleteDocID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mDatabase.collection("users").document(userID).update("livePosts", FieldValue.increment(- 1));
                                    addPosts(view);
                                    loadingDialog.dismissDialog();
                                }
                            });
                        }
                    }
                }
            });
        }
        loadingDialog.dismissDialog();
    }


    /**
     * On click of a recipe a new recipe info fragment is opened and the document is sent through
     * This saves on downloading the data again from the database
     */
    public void recipeSelected(DocumentSnapshot document, String userID) {


        //Takes ingredient and recipe rating array from snap shot and reformats before being passed through to fragment
        ArrayList<String> ingredientArray = new ArrayList<>();

        Map<String, Map<String, Object>> ingredients = (Map) document.getData().get("Ingredients");
        Iterator hmIterator = ingredients.entrySet().iterator();

        HashMap<String, Double> ratingResults = (HashMap) document.getData().get("rating");

        while (hmIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry) hmIterator.next();
            String string = mapElement.getKey().toString() + ": " + mapElement.getValue().toString();
            ingredientArray.add(string);
        }
        //Takes ingredient HashMap from the snapshot.
        HashMap<String, String> ingredientHashMap = (HashMap<String, String>) document.getData().get("Ingredients");


        Bundle mBundle;
        //Creating a bundle so all data needed from firestore query snapshot can be passed through into fragment class
        mBundle = new Bundle();
        mBundle.putSerializable("ingredientHashMap", ingredientHashMap);
        mBundle.putStringArrayList("ingredientList", ingredientArray);
        mBundle.putSerializable("ratingMap", ratingResults);
        mBundle.putString("recipeID", document.getId());
        mBundle.putString("xmlURL", document.get("xml_url").toString());
        mBundle.putString("recipeTitle", document.get("Name").toString());
        mBundle.putString("imageURL", document.get("imageURL").toString());
        mBundle.putString("recipeDescription", document.get("Description").toString());
        mBundle.putString("chefName", document.get("Chef").toString());
        mBundle.putBoolean("planner", false);
        mBundle.putBoolean("canFreeze", document.getBoolean("freezer"));
        mBundle.putString("peopleServes", document.get("serves").toString());
        mBundle.putString("fridgeDays", document.get("fridge").toString());
        mBundle.putString("reheat", document.get("reheat").toString());
        mBundle.putBoolean("noEggs", document.getBoolean("noEggs"));
        mBundle.putBoolean("noMilk", document.getBoolean("noMilk"));
        mBundle.putBoolean("noNuts", document.getBoolean("noNuts"));
        mBundle.putBoolean("noShellfish", document.getBoolean("noShellfish"));
        mBundle.putBoolean("noSoy", document.getBoolean("noSoy"));
        mBundle.putBoolean("noWheat", document.getBoolean("noWheat"));
        mBundle.putBoolean("pescatarian", document.getBoolean("pescatarian"));
        mBundle.putBoolean("vegan", document.getBoolean("vegan"));
        mBundle.putBoolean("vegetarian", document.getBoolean("vegetarian"));

        ArrayList<Integer> faves = (ArrayList) document.get("favourite");
        mBundle.putBoolean("isFav", faves.contains(userID));

        RecipeInfoFragment recipeDialogFragment = new RecipeInfoFragment();
        recipeDialogFragment.setArguments(mBundle);
        recipeDialogFragment.setTargetFragment(this, 1);
        recipeDialogFragment.show(getFragmentManager(), "Show recipe dialog fragment");
    }


    protected void revertRating(HashMap<String,Double> ratingMap, String recipeID, double oldUserRating){
        //oldUserRating = Double.parseDouble(ratingMap.get("overallRating"));
        oldTotalRates = ratingMap.get("totalRates")-1;
        oldOverallRating = (((ratingMap.get("overallRating") * ratingMap.get("totalRates")) - oldUserRating)) / oldTotalRates;
        Log.i(TAG, "Values: "+ oldOverallRating);
        ratingMap.put("overallRating", oldOverallRating);
        ratingMap.put("totalRates", oldTotalRates);
        HashMap<String, Object> updateMap = new HashMap<>();
        CollectionReference ref = mDatabase.collection("recipes");
        DocumentReference documentReference = ref.document(recipeID);
        updateMap.put("rating", ratingMap);
        documentReference.update(updateMap);
    }

}
