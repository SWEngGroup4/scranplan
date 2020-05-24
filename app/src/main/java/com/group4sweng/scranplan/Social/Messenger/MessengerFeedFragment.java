package com.group4sweng.scranplan.Social.Messenger;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.group4sweng.scranplan.Home;
import com.group4sweng.scranplan.LoadingDialog;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.SearchFunctions.RecipeFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchPrefs;
import com.group4sweng.scranplan.Social.FeedFragment;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessengerFeedFragment extends FeedFragment {

    final String TAG = "Messenger Feed Fragment";

    // Unique codes for image & permission request activity callbacks.
    private static final int IMAGE_REQUEST_CODE = 2;
    private static final int PERMISSION_CODE = 1001;

    private static final int MAX_IMAGE_FILE_SIZE_IN_MB = 4; // Max storage image size for the profile picture.

    private Uri mImageUri; // Unique image uri.
    ImageView mUploadedImage;

    float ratingNum;

    LoadingDialog loadingDialog;

    //Score scroll info
    List<MessengerFeedRecyclerAdapter.FeedPostPreviewData> data;
    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;

    protected Button mPostButton;
    CheckBox mPostRecipe;
    CheckBox mPostReview;
    CheckBox mPostPic;
    EditText mPostBodyInput;

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



    //Fragment handlers
    private FragmentTransaction fragmentTransaction;
    private RecipeFragment recipeFragment;

    //User information
    private com.group4sweng.scranplan.UserInfo.UserInfoPrivate mUser;
    private com.group4sweng.scranplan.UserInfo.UserInfoPrivate mRecipient;
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


    public MessengerFeedFragment(UserInfoPrivate userSent, UserInfoPrivate messageRecipient) {
        super(userSent);
        mUser = userSent;
        mRecipient = messageRecipient;
    }

    // Auto-generated onCreate method (everything happens here)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Remove from here? and make a see all messages view
        View view = inflater.inflate(R.layout.fragment_messenger_feed, container, false);
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
        if (mUser == null) {
            // If scroll views fail due to no user, this error is reported
            Log.e(TAG, "ERROR: Loading messenger - We were unable to find user.");
        }
        return view;
    }

    @Override
    /**
     * Adding all posts for current user to the feed - implementing an infinite scroll for all
     * followers 3 most recent posts
     * @param view
     */
    protected void addPosts(View view) {
        final RecyclerView recyclerView = view.findViewById(R.id.postsList);

        // Set out the layout of this horizontal view
        LinearLayoutManager rManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        rManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(rManager);
        //recyclerView.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels));
        // Array to score downloaded data
        data = new ArrayList<>();
        final RecyclerView.Adapter rAdapter = new MessengerFeedRecyclerAdapter(MessengerFeedFragment.this, data, mUser, view);
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
                            if (document.get("map" + first) != null) {
                                posts.add((HashMap) document.get("map" + first));
                            }
                            if (document.get("map" + second) != null) {
                                posts.add((HashMap) document.get("map" + second));
                            }
                            if (document.get("map" + third) != null) {
                                posts.add((HashMap) document.get("map" + third));
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
                        for (int i = 0; i < posts.size(); i++) {
                            data.add(new MessengerFeedRecyclerAdapter.FeedPostPreviewData(
                                    posts.get(i)));
                        }
                        rAdapter.notifyDataSetChanged();
                        if (task.getResult().size() != 0) {
                            lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        } else {
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
                                                    if (d.get("map" + first) != null) {
                                                        postsNext.add((HashMap) d.get("map" + first));
                                                    }
                                                    if (d.get("map" + second) != null) {
                                                        postsNext.add((HashMap) d.get("map" + second));
                                                    }
                                                    if (d.get("map" + third) != null) {
                                                        postsNext.add((HashMap) d.get("map" + third));
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

                                                for (int i = 0; i < postsNext.size(); i++) {
                                                    data.add(new MessengerFeedRecyclerAdapter.FeedPostPreviewData(
                                                            postsNext.get(i)));
                                                }
                                                if (isLastItemReached) {
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

    @Override
    protected void savePost(HashMap map, HashMap extras, CollectionReference ref, HashMap<String, Object> newRatings) {
        if (newPostID == null) {
            ref.add(extras).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                      @Override
                                                      public void onComplete(@NonNull Task<DocumentReference> task) {
                                                          if (task.isSuccessful()) {
                                                              final String docID = task.getResult().getId();
                                                              map.put("docID", docID);
                                                              if (mPostReview.isChecked() && mPostRecipe.isChecked()) {
                                                                  newRatings.put("post", docID);
                                                                  mDatabase.collection("reviews").document(mUser.getUID() + "-" + recipeID).set(newRatings);
                                                              }
                                                              updateFollowers(map);
                                                          }
                                                      }
                                                  }
            );
        } else {
            ref.document(newPostID).set(extras);
            map.put("docID", newPostID);
            if (mPostReview.isChecked() && mPostRecipe.isChecked()) {
                newRatings.put("post", newPostID);
                mDatabase.collection("reviews").document(mUser.getUID() + "-" + recipeID).set(newRatings);
            }
            updateFollowers(map);
        }
    }

    @Override
    /**
     * Reset the screen once a new post has been completed
     */
    protected void postComplete() {
        mDatabase.collection("users").document(mUser.getUID()).update("posts", FieldValue.increment(1), "livePosts", FieldValue.increment(1)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                                        @Override
                                                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                                            mUser.setPosts(mUser.getPosts() + 1);
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
}
