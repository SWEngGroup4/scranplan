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
public class ProfilePosts extends Fragment {

    final String TAG = "Home horizontal queries";

    public ProfilePosts(String UID){
        searchUID = UID;
    }
    private String searchUID;


    // Width size of each scroll view, dictating size of images on home screen
    final int scrollViewSize = 5;
    LoadingDialog loadingDialog;

    //Score scroll info
    List<FeedRecyclerAdapter.FeedPostPreviewData> data;
    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;



    View mainView;


    //Fragment handlers
    private FragmentTransaction fragmentTransaction;
    private RecipeFragment recipeFragment;

    //User information
    private com.group4sweng.scranplan.UserInfo.UserInfoPrivate user;


    Query query;



    // Database objects for accessing recipes
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mColRef = mDatabase.collection("posts");





    // Auto-generated super method
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Auto-generated onCreate method (everything happens here)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_posts, container, false);
        mainView = view;
        loadingDialog = new LoadingDialog(getActivity());

        // Grabs screen size for % layout TODO - change to density pixels + NullPointerException check
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        user = (com.group4sweng.scranplan.UserInfo.UserInfoPrivate) requireActivity().getIntent().getSerializableExtra("user");


        Log.e(TAG, "IN TO THE FRAGMENT FOR PROFILE POSTS");
        initPageListeners();
        addPosts(view);

        // Checks users details have been provided
        if(user != null){



        }else{
            // If scroll views fail due to no user, this error is reported
            Log.e(TAG, "ERROR: Loading social feed - We were unable to find user.");
        }
        return view;
    }

    private void addPosts(View view){
        final RecyclerView recyclerView = view.findViewById(R.id.postsList);
        recyclerView.setNestedScrollingEnabled(false);
        // Set out the layout of this horizontal view
        RecyclerView.LayoutManager rManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(rManager);
        //recyclerView.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels));
        // Array to score downloaded data
        data = new ArrayList<>();
        final RecyclerView.Adapter rAdapter = new FeedRecyclerAdapter( this, data, user);
        recyclerView.setAdapter(rAdapter);
        Log.e(TAG, "ERROR: Loading social feed - We were unable to find user. ->" + searchUID);
        query = mColRef.whereEqualTo("author", searchUID).orderBy("timestamp", Query.Direction.DESCENDING).limit(10);
        // Ensure query exists and builds view with query
        if (query != null) {
            Log.e(TAG, "User is searching the following query: " + query.toString());
            // Query listener to add data to view
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        ArrayList<HashMap> posts = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Log.e("TIME", "I have found a doc");
                            HashMap<String, Object> temporary = new HashMap<String, Object>();
                            temporary.put("docID", document.getId());
                            temporary.put("author", document.get("author"));
                            temporary.put("body", document.get("body"));
                            temporary.put("comments", document.get("comments"));
                            temporary.put("isPic", document.get("isPic"));
                            temporary.put("isRecipe", document.get("isRecipe"));
                            temporary.put("isReview", document.get("isReview"));
                            temporary.put("likes", document.get("likes"));
                            temporary.put("recipeDescription", document.get("recipeDescription"));
                            temporary.put("recipeID", document.get("recipeID"));
                            temporary.put("recipeImageURL", document.get("recipeImageURL"));
                            temporary.put("recipeReview", document.get("recipeReview"));
                            temporary.put("recipeTitle", document.get("recipeTitle"));
                            temporary.put("timestamp", document.get("timestamp"));
                            temporary.put("uploadedImageURL", document.get("uploadedImageURL"));
                            data.add(new FeedRecyclerAdapter.FeedPostPreviewData(temporary));
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

                                                for (DocumentSnapshot d : t.getResult()) {
                                                    Log.e("TIME", "I have found a doc");
                                                    HashMap<String, Object> temporary = new HashMap<String, Object>();
                                                    temporary.put("author", d.get("author"));
                                                    temporary.put("body", d.get("body"));
                                                    temporary.put("comments", d.get("comments"));
                                                    temporary.put("isPic", d.get("isPic"));
                                                    temporary.put("isRecipe", d.get("isRecipe"));
                                                    temporary.put("isReview", d.get("isReview"));
                                                    temporary.put("likes", d.get("likes"));
                                                    temporary.put("recipeDescription", d.get("recipeDescription"));
                                                    temporary.put("recipeID", d.get("recipeID"));
                                                    temporary.put("recipeImageURL", d.get("recipeImageURL"));
                                                    temporary.put("recipeReview", d.get("recipeReview"));
                                                    temporary.put("recipeTitle", d.get("recipeTitle"));
                                                    temporary.put("timestamp", d.get("timestamp"));
                                                    temporary.put("uploadedImageURL", d.get("uploadedImageURL"));
                                                    data.add(new FeedRecyclerAdapter.FeedPostPreviewData(temporary));
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
     *  Setting up page listeners for when buttons are pressed
     */
    protected void initPageListeners() {


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


    }
}