package com.group4sweng.scranplan.Social;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.group4sweng.scranplan.Administration.LoadingDialog;
import com.group4sweng.scranplan.PublicProfile;
import com.group4sweng.scranplan.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class for the Posts fragment in profile.
 * Author(s): LNewman
 * (c) CoDev 2020
 *
 * This class builds the vertical scroll of all selected users posts in an infinite scroll
 * using the FeedRecyclerAdapter to display posts in the same way as they are on the feed.
 */
public class ProfilePosts extends FeedFragment {

    final String TAG = "profile Posts";

    PublicProfile profile;

    public ProfilePosts(String UID){
        super();
        searchUID = UID;
    }
    private String searchUID;


    // Width size of each scroll view, dictating size of images on home screen
    final int postsLoaded = 5;
    LoadingDialog loadingDialog;

    //Score scroll info
    List<FeedRecyclerAdapter.FeedPostPreviewData> data;
    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;



    View mainView;
    NestedScrollView profileScrollView;


    //User information
    private com.group4sweng.scranplan.UserInfo.UserInfoPrivate mUser;


    Query query;



    // Database objects for accessing recipes
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mColRef = mDatabase.collection("posts");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Setting up the fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        profile = (PublicProfile) getActivity();
        profileScrollView = profile.findViewById(R.id.nestedScrollViewProfile);
        View view = inflater.inflate(R.layout.profile_posts, container, false);
        mainView = view;
        loadingDialog = new LoadingDialog(getActivity());
        mUser = (com.group4sweng.scranplan.UserInfo.UserInfoPrivate) requireActivity().getIntent().getSerializableExtra("user");


        Log.e(TAG, "IN TO THE FRAGMENT FOR PROFILE POSTS");
        addPosts(view);

        // Checks users details have been provided
        if(mUser != null){



        }else{
            // If scroll views fail due to no user, this error is reported
            Log.e(TAG, "ERROR: Loading social feed - We were unable to find user.");
        }


        return view;
    }

    /**
     * Generates the feed of posts on the profile in reverse timestamp order - infinite scroll loading
     * postsLoaded posts at a time until end of posts are reached
     * @param view
     */
    public void addPosts(View view){
        final RecyclerView recyclerView = view.findViewById(R.id.postsList);
        recyclerView.setNestedScrollingEnabled(false);
        // Set out the layout of this horizontal view
        RecyclerView.LayoutManager rManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(rManager);
        //recyclerView.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels));
        // Array to score downloaded data
        data = new ArrayList<>();
        final RecyclerView.Adapter rAdapter = new FeedRecyclerAdapter( this, data, mUser, view, getActivity());
        recyclerView.setAdapter(rAdapter);
        Log.e(TAG, "ERROR: Loading social feed - We were unable to find user. ->" + searchUID);
        query = mColRef.whereEqualTo("author", searchUID).orderBy("timestamp", Query.Direction.DESCENDING).limit(postsLoaded);
        // Ensure query exists and builds view with query
        if (query != null) {
            Log.e(TAG, "User is searching the following query: " + query.toString());
            // Query listener to add data to view
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
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
                            temporary.put("overallRating", document.get("overallRating"));
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
                        NestedScrollView.OnScrollChangeListener onScrollListener = new NestedScrollView.OnScrollChangeListener() {
                                                                        @Override
                                                                        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                                                                            if (v.getChildAt(v.getChildCount() - 1) != null) {
                                                                                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                                                                                        scrollY > oldScrollY) {

                                                                                    LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                                                                    int visibleItemCount = linearLayoutManager.getChildCount();
                                                                                    int totalItemCount = linearLayoutManager.getItemCount();
                                                                                    int pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
                                                                                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && !isLastItemReached) {
                                                                                        Query nextQuery = query.startAfter(lastVisible);
                                                                                        nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                                                                                if (t.isSuccessful()) {
                                                                                                    Log.e("TIME", "SEARCHING FOR MORE POSTS");
                                                                                                    for (DocumentSnapshot d : t.getResult()) {
                                                                                                        Log.e("TIME", "POSTS FOUND");
                                                                                                        HashMap<String, Object> temp = new HashMap<String, Object>();
                                                                                                        temp.put("docID", d.getId());
                                                                                                        temp.put("author", d.get("author"));
                                                                                                        temp.put("body", d.get("body"));
                                                                                                        temp.put("comments", d.get("comments"));
                                                                                                        temp.put("isPic", d.get("isPic"));
                                                                                                        temp.put("isRecipe", d.get("isRecipe"));
                                                                                                        temp.put("isReview", d.get("isReview"));
                                                                                                        temp.put("likes", d.get("likes"));
                                                                                                        temp.put("recipeDescription", d.get("recipeDescription"));
                                                                                                        temp.put("recipeID", d.get("recipeID"));
                                                                                                        temp.put("recipeImageURL", d.get("recipeImageURL"));
                                                                                                        temp.put("overallRating", d.get("overallRating"));
                                                                                                        temp.put("recipeTitle", d.get("recipeTitle"));
                                                                                                        temp.put("timestamp", d.get("timestamp"));
                                                                                                        temp.put("uploadedImageURL", d.get("uploadedImageURL"));
                                                                                                        data.add(new FeedRecyclerAdapter.FeedPostPreviewData(temp));
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
                                                                            }
                                                                        }
                                                                    };
                        profileScrollView.setOnScrollChangeListener(onScrollListener);
                    }
                }
            });
        }
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
                    case R.id.reportComment:
                        Log.e(TAG,"Report comment clicked!");
                        //TODO add functionality to report this comment
                        break;
                    case R.id.deleteComment:
                        Log.e(TAG,"Clicked delete comment!");
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
    public void deletePost(String deleteDocID, HashMap document, View view){
        super.deletePost(deleteDocID, document, view);
    }

    /**
     * On click of a recipe a new recipe info fragment is opened and the document is sent through
     * This saves on downloading the data again from the database
     */
    public void recipeSelected(DocumentSnapshot document, String userID){
        super.recipeSelected(document, userID);
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


}