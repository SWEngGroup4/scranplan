package com.group4sweng.scranplan.RecipeInfo;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group4sweng.scranplan.Administration.ContentReporting;
import com.group4sweng.scranplan.Exceptions.ImageException;
import com.group4sweng.scranplan.Administration.LoadingDialog;
import com.group4sweng.scranplan.PublicProfile;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.Social.FeedFragment;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;


public class RecipeReviewFragment extends FeedFragment {

    private RatingBar mStars;
    private CheckBox mImageIcon;
    private CheckBox mRecipeIcon;
    private CheckBox mReviewIcon;
    private TextView mRecipeRate;
    private ImageView mRecipeReviewImage;
    private ConstraintLayout mReviewImagelayout;
    private String mRecipeID;
    private String mRecipeImage;
    private String mRecipeTitle;
    private String mRecipeDescription;
    private String docID;
    private Boolean reviewMade;
    private Boolean urlPic = false;
    private String postID;
    private String oldUserStarRating;
    private ImageButton mReviewMenu;

    protected HashMap<String, Double> ratingMap;

    private List<recipeReviewRecyclerAdapter.reviewData> data;

    CollectionReference reviewRef, postRef;
    DocumentReference reviewDocRef;
    View layout;

    protected static boolean POST_IS_UPLOADING = false; // Boolean to determine if the image is uploading currently.

    private double getNewRating;
    private double oldOverallRating;
    private double newOverallRating;
    private double oldUserRating;
    private double oldTotalRates;
    private double newTotalRates;
    final String TAG = "Data";

    public RecipeReviewFragment(UserInfoPrivate userSent) {
        super(userSent);
        mUser = userSent;
    }

    // Auto-generated super method
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed, null);
        layout = view;

        initPageItems(layout);
        displayItems(layout);
        checkReview();
        addPosts(layout);
        initPageListeners(layout);



        return layout;
    }

    /**
     * Method To check if user has already left a review on a recipe. If the user has, it will populate the review input box with their previous
     * review so they can edit if they wish.
     */
    private void checkReview() {

        mDatabase.collection("reviews").document(docID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        Log.e(TAG, "exists ");
//                        loadingDialog.startLoadingDialog();
                        DocumentSnapshot document = task.getResult();
                        mStars.setRating(Float.parseFloat(document.get("overallRating").toString()));
                        postID = document.get("docID").toString();
                        oldUserRating = (double) document.get("overallRating");

                        mDatabase.collection("posts").document(postID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> nextTask) {
                                Log.d(TAG, "Post data: " + nextTask.getResult());
                                DocumentSnapshot d = nextTask.getResult();
                                if(d.get("body") != null){
                                    Log.d(TAG, "Adding to body: " + d.get("body").toString());
                                    mPostBodyInput.setText(d.get("body").toString());
                                }

                                if ((Boolean) d.get("isPic")){
                                    mReviewImagelayout.setVisibility(View.VISIBLE);
                                    mRecipeReviewImage.setVisibility(View.VISIBLE);
                                    Picasso.get().load((String) d.get("uploadedImageURL")).into(mRecipeReviewImage);
                                    mImageUri = Uri.parse(d.get("uploadedImageURL").toString());
                                    mPostPic.setChecked(true);
                                    urlPic = true;
                                }


                            }
                        });

                        reviewMade = true;


                    } else {
                        Log.e("FdRc", "Unable to retrieve user document in Firestore ");
                        reviewMade = false;
                        mPostPic.setChecked(false);
                        urlPic = false;
                    }
                }
            }

        });
    }


    //Defining all relevant members of page
    public void initPageItems(View layout){
        super.initPageItems(layout);

        loadingDialog = new LoadingDialog(getActivity());

        ratingMap = (HashMap<String, Double>) getArguments().getSerializable("ratingMap");
        mRecipeID = getArguments().getString("recipeID");
        mRecipeImage = getArguments().getString("recipeImageURL");
        mRecipeTitle = getArguments().getString("recipeTitle");
        mRecipeDescription = getArguments().getString("recipeDescription");

        mUser = (com.group4sweng.scranplan.UserInfo.UserInfoPrivate) requireActivity().getIntent().getSerializableExtra("user");

        docID = mUser.getUID() + "-" + mRecipeID;

        postRef = mDatabase.collection("posts");
        reviewRef = mDatabase.collection("reviews");
        reviewDocRef = reviewRef.document(docID);
        mStars = layout.findViewById(R.id.postRecipeRating);
        mImageIcon = layout.findViewById(R.id.imageIcon);
        mRecipeIcon = layout.findViewById(R.id.recipeIcon);
        mReviewIcon = layout.findViewById(R.id.reviewIcon);
        mRecipeRate = layout.findViewById(R.id.recipeRate);
        mRecipeReviewImage = layout.findViewById(R.id.userUploadedImageView);
        mReviewMenu = layout.findViewById(R.id.postMenu);
        mReviewImagelayout = layout.findViewById(R.id.userUploadedImageViewLayout);


    }

    /**
     *  Setting up page listeners for when buttons are pressed
     */
    protected void initPageListeners(View layout) {
        super.initPageListeners();



        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                calculateRating();
                RecipeInfoFragment repInfo = (RecipeInfoFragment) getParentFragment();
                repInfo.updateStarRating(ratingMap.get("overallRating").toString());

                addingReviewFirestore(layout);

//                checkReview();

            }
        });

    }

    private void displayItems(View layout){

        //Setting background colour to match that of the fragment
        FrameLayout root = layout.findViewById(R.id.FrameLayoutid);
        root.setBackgroundColor(Color.parseColor("#bdb5c7"));

        //Makes the star rating visible and stores the value of the given rating
        mStars.setVisibility(View.VISIBLE);
        mStars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (mStars.getRating() < 1) {
                    mStars.setRating(1);
                    getNewRating = 1;
                } else {
                    getNewRating = mStars.getRating();
                }
            }
        });

        //Gets rid of recipe icon
        mRecipeIcon.setVisibility(View.INVISIBLE);

        //Gets rid of review icon
        mReviewIcon.setVisibility(View.INVISIBLE);

        //Sets the text view for rating to visible
        mRecipeRate.setVisibility(View.VISIBLE);

    }

    private void calculateRating(){

        if(reviewMade){

//            oldUserRating = Double.parseDouble(oldUserStarRating);
//            oldTotalRates = ratingMap.get("totalRates")-1;
//            oldOverallRating = (((ratingMap.get("overallRating") * ratingMap.get("totalRates")) - oldUserRating)) / oldTotalRates;
//            Log.i(TAG, "Values: "+ oldOverallRating);
//
//            ratingMap.put("overallRating", oldOverallRating);
//            ratingMap.put("totalRates", oldTotalRates);
//
//            HashMap<String, Object> updateMap = new HashMap<>();
//            CollectionReference ref = mDatabase.collection("recipes");
//            DocumentReference documentReference = ref.document(mRecipeID);
//            updateMap.put("rating", ratingMap);
//            documentReference.update(updateMap);
//
//            Log.i(TAG, "Values: "+ ratingMap);

            revertRating(ratingMap, mRecipeID, oldUserRating);

        }

        newTotalRates = ratingMap.get("totalRates") + 1f;
        newOverallRating = ((ratingMap.get("overallRating") * ratingMap.get("totalRates")) + getNewRating) / newTotalRates;

        ratingMap.put("overallRating", newOverallRating);
        ratingMap.put("totalRates", newTotalRates);

        HashMap<String, Object> updateMap = new HashMap<>();
        CollectionReference ref = mDatabase.collection("recipes");
        DocumentReference documentReference = ref.document(mRecipeID);
        updateMap.put("rating", ratingMap);
        documentReference.update(updateMap);

        Log.i(TAG, "Values: "+ ratingMap);

    }

    protected void revertRating(HashMap<String,Double> ratingMap, String recipeID, double oldUserRating){
        super.revertRating(ratingMap, recipeID, oldUserRating);
    }

    private void addingReviewFirestore(View layout) {

        POST_IS_UPLOADING = true;
        loadingDialog.startLoadingDialog();

        String body = mPostBodyInput.getText().toString();
        HashMap<String, Object> postsMap = new HashMap<>();
        HashMap<String, Object> reviewMap = new HashMap<>();

        // Creating map to store data in posts collection
        postsMap.put("comments", 0);
        postsMap.put("likes", 0);
        postsMap.put("body", body);
        postsMap.put("overallRating", getNewRating);
        postsMap.put("timestamp", FieldValue.serverTimestamp());
        postsMap.put("author", mUser.getUID());
        postsMap.put("isPic", mPostPic.isChecked());
        postsMap.put("isRecipe", true);
        postsMap.put("isReview", true);
        postsMap.put("recipeDescription", mRecipeDescription);
        postsMap.put("recipeTitle", mRecipeTitle);
        postsMap.put("recipeID", mRecipeID);
        postsMap.put("recipeImageURL", mRecipeImage);

        if (!reviewMade) {

            //Saving map to the firestore
            DocumentReference postRef = mDatabase.collection("posts").document();
            Log.e(TAG, "Added new post ");
            //On complete listener makes sure post has been saved on firestore before getting document ID and saving
            //in reviews collection
            if (mPostPic.isChecked()) {
                try {
                    if (!urlPic)
                        checkImage(mImageUri); // Check the image doesn't throw any exceptions

                    // State that the image is still uploading and therefore we shouldn't save a reference on firebase to it yet.

                        /*  Create a unique reference of the format. 'image/profile/[UNIQUE UID]/profile_image.[EXTENSION].
                            Whereby [UNIQUE UID] = the Unique id of the user, [EXTENSION] = file image extension. E.g. .jpg,.png. */
                    StorageReference mImageStorage = mStorageReference.child("images/posts/" + mUser.getUID() + "/IMAGEID" + (mUser.getPosts() + 1)); //todo input image id

                    //  Check if the upload fails
                    mImageStorage.putFile(mImageUri).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to upload profile image.", Toast.LENGTH_SHORT).show()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mImageStorage.getDownloadUrl().addOnSuccessListener(locationUri -> { // Successful upload.
                                postsMap.put("uploadedImageURL", locationUri.toString());

                                postRef.set(postsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //Document ID for review post
                                        docID = mUser.getUID() + "-" + mRecipeID;


                                        reviewMap.put("user", mUser.getUID());
                                        reviewMap.put("overallRating", getNewRating);
                                        reviewMap.put("docID", postRef.getId());
                                        reviewMap.put("timestamp", FieldValue.serverTimestamp());

                                        //Saving review map to the firestore with custom document ID

                                        Log.e(TAG, "Added new post ");
                                        reviewDocRef.set(reviewMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
//                                                mDatabase.collection("users").document(mUser.getUID()).update("posts", FieldValue.increment(1),"livePosts", FieldValue.increment(1));
//                                                mUser.setPosts(mUser.getPosts()+1);
//                                                addPosts(layout);
                                                postsMap.put("docID", postRef.getId());
                                                updateFollowers(postsMap);
                                            }
                                        });

                                        // Update the UserInfoPrivate class with this new image URL.
                                        POST_IS_UPLOADING = false;// State we have finished uploading (a reference exists).
                                        loadingDialog.dismissDialog();
                                        reviewMade = true;
                                        postID = postRef.getId();
                                    }
                                });
                            });
                        }
                    });
                } catch (ImageException e) {
                    Log.e(TAG, "Failed to upload photo to Firebase");
                    Toast.makeText(getApplicationContext(), "Failed to upload photo to Firebase, please try again.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                }
            }
            else {
                postRef.set(postsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Document ID for review post
                        docID = mUser.getUID() + "-" + mRecipeID;
                        //HashMap<String, Object> reviewMap = new HashMap<>();

                        reviewMap.put("user", mUser.getUID());
                        reviewMap.put("overallRating", getNewRating);
                        reviewMap.put("docID", postRef.getId());
                        reviewMap.put("timestamp", FieldValue.serverTimestamp());

                        //Saving review map to the firestore with custom document ID

                        Log.e(TAG, "Added new post ");
                        reviewDocRef.set(reviewMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
//                                mDatabase.collection("users").document(mUser.getUID()).update("posts", FieldValue.increment(1),"livePosts", FieldValue.increment(1));
//                                mUser.setPosts(mUser.getPosts()+1);
//                                addPosts(layout);
                                postsMap.put("docID", postRef.getId());
                                updateFollowers(postsMap);
                            }
                        });

                        // Update the UserInfoPrivate class with this new image URL.
                        POST_IS_UPLOADING = false;// State we have finished uploading (a reference exists).
                        loadingDialog.dismissDialog();
                        reviewMade = true;
                        postID = postRef.getId();

                    }
                });

            }
        }
        else{
            //Saving map to the firestore
            DocumentReference postRef = mDatabase.collection("posts").document(postID);
            Log.e(TAG, "Added new post ");
            //On complete listener makes sure post has been saved on firestore before getting document ID and saving
            //in reviews collection
            if (mPostPic.isChecked()) {
                try {
                    if (!urlPic)
                        checkImage(mImageUri); // Check the image doesn't throw any exceptions

                    // State that the image is still uploading and therefore we shouldn't save a reference on firebase to it yet.

                        /*  Create a unique reference of the format. 'image/profile/[UNIQUE UID]/profile_image.[EXTENSION].
                            Whereby [UNIQUE UID] = the Unique id of the user, [EXTENSION] = file image extension. E.g. .jpg,.png. */
                    StorageReference mImageStorage = mStorageReference.child("images/posts/" + mUser.getUID() + "/IMAGEID" + (mUser.getPosts() + 1)); //todo input image id

                    //  Check if the upload fails
                    mImageStorage.putFile(mImageUri).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to upload profile image.", Toast.LENGTH_SHORT).show()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mImageStorage.getDownloadUrl().addOnSuccessListener(locationUri -> { // Successful upload.
                                postsMap.put("uploadedImageURL", locationUri.toString());

                                postRef.set(postsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //Document ID for review post
                                        docID = mUser.getUID() + "-" + mRecipeID;


                                        reviewMap.put("mUser", mUser.getUID());
                                        reviewMap.put("overallRating", getNewRating);
                                        reviewMap.put("docID", postRef.getId());
                                        reviewMap.put("timestamp", FieldValue.serverTimestamp());

                                        //Saving review map to the firestore with custom document ID

                                        Log.e(TAG, "Added new post ");
                                        reviewDocRef.set(reviewMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
//                                                mDatabase.collection("users").document(mUser.getUID()).update("posts", FieldValue.increment(1));
//                                                mUser.setPosts(mUser.getPosts()+1);
//                                                addPosts(layout);
                                                postsMap.put("docID", postRef.getId());
                                                updateFollowers(postsMap);
                                            }
                                        });

                                        // Update the mUserInfoPrivate class with this new image URL.
                                        POST_IS_UPLOADING = false;// State we have finished uploading (a reference exists).
                                        loadingDialog.dismissDialog();
                                        reviewMade = true;
                                        postID = postRef.getId();
                                    }
                                });
                            });
                        }
                    });
                } catch (ImageException e) {
                    Log.e(TAG, "Failed to upload photo to Firebase");
                    Toast.makeText(getApplicationContext(), "Failed to upload photo to Firebase, please try again.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                }
            }
            else {
                postRef.set(postsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Document ID for review post
                        docID = mUser.getUID() + "-" + mRecipeID;
                        //HashMap<String, Object> reviewMap = new HashMap<>();

                        reviewMap.put("mUser", mUser.getUID());
                        reviewMap.put("overallRating", getNewRating);
                        reviewMap.put("docID", postRef.getId());
                        reviewMap.put("timestamp", FieldValue.serverTimestamp());

                        //Saving review map to the firestore with custom document ID

                        Log.e(TAG, "Added new post ");
                        reviewDocRef.set(reviewMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
//                                mDatabase.collection("mUsers").document(mUser.getUID()).update("posts", FieldValue.increment(1));
//                                mUser.setPosts(mUser.getPosts()+1);
//                                addPosts(layout);
                                postsMap.put("docID", postRef.getId());
                                updateFollowers(postsMap);
                            }
                        });

                        // Update the mUserInfoPrivate class with this new image URL.
                        POST_IS_UPLOADING = false;// State we have finis    hed uploading (a reference exists).
                        loadingDialog.dismissDialog();
                        reviewMade = true;
                        postID = postRef.getId();
                        RecipeInfoFragment infoFragment = (RecipeInfoFragment) getParentFragment();
                        infoFragment.refreshReviewFragment();
                    }
                });

            }



        }
    }

    @Override
    protected void addPosts(View view) {

        // Creating a list of the data and building all variables to add to recycler view
        final RecyclerView recyclerView = view.findViewById(R.id.postsList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager rManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(rManager);
        data = new ArrayList<>();
        final RecyclerView.Adapter rAdapter = new recipeReviewRecyclerAdapter(RecipeReviewFragment.this, data, mUser, mRecipeID);
        recyclerView.setAdapter(rAdapter);
        query = postRef.whereEqualTo("recipeID", mRecipeID).whereEqualTo("isReview", true).orderBy("timestamp", Query.Direction.DESCENDING).limit(10);
        Log.e(TAG, "RECIPE ID" + mRecipeID);
        // Once the data has been returned, dataset populated and components build
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // For each document a new recipe preview view is generated
                    if(task.getResult() != null)
                    {

                        for (DocumentSnapshot document : task.getResult()) {
                            //Pass all data from document
                            String userImage = null;
                            if ((Boolean) document.get("isPic")){
                                userImage = document.get("uploadedImageURL").toString();
                            }
                            HashMap<String, Object> doc = new HashMap<>();
                            doc.put("isReview", document.get("isReview"));
                            doc.put("recipeID", document.get("recipeID"));
                            doc.put("body", document.get("body").toString());
                            doc.put("author", document.get("author").toString());
                            doc.put("overallRating", document.get("overallRating").toString());
                            doc.put("timeStamp", (Timestamp) document.get("timestamp"));
                            doc.put("userImage", userImage);
                            doc.put("docID", document.getId());
                            data.add(new recipeReviewRecyclerAdapter.reviewData(doc));

                        }
                        rAdapter.notifyDataSetChanged();
                        // Set the last document as last mUser can see
                        if(task.getResult().size() != 0){
                            lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        }else{
                            // If no data returned, mUser notified
                            isLastItemReached = true;
//
                        }
                        // check if mUser has scrolled through the view
                        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                    isScrolling = true;
                                }
                            }
                            // If mUser is scrolling and has reached the end, more data is loaded
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                // Checking if mUser is at the end
                                LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                int visibleItemCount = linearLayoutManager.getChildCount();
                                int totalItemCount = linearLayoutManager.getItemCount();
                                // If found to have reached end, more data is requested from the server in the same manner
                                if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                                    isScrolling = false;
                                    Query nextQuery = query.startAfter(lastVisible);
                                    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                            if (t.isSuccessful()) {
                                                for (DocumentSnapshot d : t.getResult()) {
                                                    //Pass all data from document
                                                    String userImage = null;
                                                    if ((Boolean) d.get("isPic")){
                                                        userImage = d.get("uploadedImageURL").toString();
                                                    }
                                                    HashMap<String, Object> doc = new HashMap<>();
                                                    doc.put("isReview", d.get("isReview"));
                                                    doc.put("recipeID", d.get("recipeID"));
                                                    doc.put("body", d.get("body").toString());
                                                    doc.put("author", d.get("author").toString());
                                                    doc.put("overallRating", d.get("overallRating").toString());
                                                    doc.put("timeStamp", (Timestamp) d.get("timestamp"));
                                                    doc.put("userImage", userImage);
                                                    doc.put("docID", d.getId());
                                                    data.add(new recipeReviewRecyclerAdapter.reviewData(doc));
                                                }
                                                if(isLastItemReached){
                                                    // Last comment reached
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
            }
        });

    }

    /** Image checker.
     *  Used to reduce wait times for the user when uploading on a slow network.
     *  Also limits the data that has to be stored and queried from Firebase.
     *  @param uri - The unique uri of the image file location from the users storage.
     *  @throws ImageException - Throws if the image file is too large or the format isn't a supported image format.
     */
    protected void checkImage(Uri uri) throws ImageException {
        super.checkImage(uri);
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
                        deletePost(deleteDocID, document, layout);

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
    public void deletePost(String deleteDocID, HashMap document, View view){
        super.deletePost(deleteDocID, document, view);
    }

    /**
     * Updating the follows section of firebase when creating a new post
     * @param map
     */
    protected void updateFollowers(HashMap map){
        super.updateFollowers(map);
    }

    /**
     * Reset the screen once a new post has been completed
     */
    @Override
    protected void postComplete(){
        mDatabase.collection("users").document(mUser.getUID()).update("posts", FieldValue.increment(1), "livePosts", FieldValue.increment(1)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                                        @Override
                                                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                                            mUser.setPosts(mUser.getPosts()+1);
                                                                                                                                                                            addPosts(layout);
                                                                                                                                                                        }
                                                                                                                                                                    }
        );
    }

}
