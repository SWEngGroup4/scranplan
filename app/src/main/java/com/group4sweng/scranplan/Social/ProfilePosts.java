package com.group4sweng.scranplan.Social;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.group4sweng.scranplan.LoadingDialog;
import com.group4sweng.scranplan.PublicProfile;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.SearchFunctions.RecipeFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class builds the horizontal scrolls of custom preference recipe selection for the user on the
 * home screen. Each of these scrolls is infinite in length, loading 5 recipes at a time to minimise
 * reads from the Firestore yet still giving the user an infinite and responsive experience with
 * scroll listeners to check where the user is interacting with these scrolls.
 */
public class ProfilePosts extends Fragment {

    final String TAG = "profile Posts";

    PublicProfile profile;

    public ProfilePosts(String UID){
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

        profile = (PublicProfile) getActivity();
        profileScrollView = profile.findViewById(R.id.nestedScrollViewProfile);
        View view = inflater.inflate(R.layout.profile_posts, container, false);
        mainView = view;
        loadingDialog = new LoadingDialog(getActivity());

        // Grabs screen size for % layout TODO - change to density pixels + NullPointerException check
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        user = (com.group4sweng.scranplan.UserInfo.UserInfoPrivate) requireActivity().getIntent().getSerializableExtra("user");


        Log.e(TAG, "IN TO THE FRAGMENT FOR PROFILE POSTS");
        addPosts(view);

        // Checks users details have been provided
        if(user != null){



        }else{
            // If scroll views fail due to no user, this error is reported
            Log.e(TAG, "ERROR: Loading social feed - We were unable to find user.");
        }


        return view;
    }

    void addPosts(View view){
        final RecyclerView recyclerView = view.findViewById(R.id.postsList);
        recyclerView.setNestedScrollingEnabled(false);
        // Set out the layout of this horizontal view
        RecyclerView.LayoutManager rManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(rManager);
        //recyclerView.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels));
        // Array to score downloaded data
        data = new ArrayList<>();
        final RecyclerView.Adapter rAdapter = new FeedRecyclerAdapter( this, data, user, view);
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
                                                                                                        temp.put("recipeReview", d.get("recipeReview"));
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
//                        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
//                            @Override
//                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                                super.onScrollStateChanged(recyclerView, newState);
//                                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
//                                    isScrolling = true;
//                                }
//                            }
//                            // If scrolled to end then download new data and check if we are out of data
//                            @Override
//                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                                super.onScrolled(recyclerView, dx, dy);
//
//                                LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
//                                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
//                                int visibleItemCount = linearLayoutManager.getChildCount();
//                                int totalItemCount = linearLayoutManager.getItemCount();
//
//                                if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
//                                    isScrolling = false;
//                                    Query nextQuery = query.startAfter(lastVisible);
//                                    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<QuerySnapshot> t) {
//                                            if (t.isSuccessful()) {
//                                                Log.e("TIME", "SEARCHING FOR MORE POSTS");
//                                                for (DocumentSnapshot d : t.getResult()) {
//                                                    Log.e("TIME", "POSTS FOUND");
//                                                    Log.e("TIME", "I have found a doc");
//                                                    HashMap<String, Object> temporary = new HashMap<String, Object>();
//                                                    temporary.put("author", d.get("author"));
//                                                    temporary.put("body", d.get("body"));
//                                                    temporary.put("comments", d.get("comments"));
//                                                    temporary.put("isPic", d.get("isPic"));
//                                                    temporary.put("isRecipe", d.get("isRecipe"));
//                                                    temporary.put("isReview", d.get("isReview"));
//                                                    temporary.put("likes", d.get("likes"));
//                                                    temporary.put("recipeDescription", d.get("recipeDescription"));
//                                                    temporary.put("recipeID", d.get("recipeID"));
//                                                    temporary.put("recipeImageURL", d.get("recipeImageURL"));
//                                                    temporary.put("recipeReview", d.get("recipeReview"));
//                                                    temporary.put("recipeTitle", d.get("recipeTitle"));
//                                                    temporary.put("timestamp", d.get("timestamp"));
//                                                    temporary.put("uploadedImageURL", d.get("uploadedImageURL"));
//                                                    data.add(new FeedRecyclerAdapter.FeedPostPreviewData(temporary));
//                                                }
//                                                if(isLastItemReached){
//                                                    // Add end here
//                                                }
//                                                rAdapter.notifyDataSetChanged();
//                                                if (t.getResult().size() != 0) {
//                                                    lastVisible = t.getResult().getDocuments().get(t.getResult().size() - 1);
//                                                }
//
//                                                if (t.getResult().size() < 5) {
//                                                    isLastItemReached = true;
//                                                }
//                                            }
//                                        }
//                                    });
//                                }
//                            }
//                        };
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
     * @param anchor
     */
    public void menuSelected(HashMap document, View anchor, View view){
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(getContext(), anchor);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.menu_comment, popup.getMenu());
        if(document.get("author").toString().equals(user.getUID())){
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
                        //TODO add functionality to open users profile in new fragment
                        break;
                    case R.id.reportComment:
                        Log.e(TAG,"Report comment clicked!");
                        //TODO add functionality to report this comment
                        break;
                    case R.id.deleteComment:
                        Log.e(TAG,"Clicked delete comment!");
                        final String deleteDocID = (String) document.get("docID");
                        deletePost(deleteDocID);
                        break;
                }
                return true;
            }
        });

        popup.show();//showing popup menu
    }

//    public void deleteComment(View view, String deleteDocID){
//        loadingDialog.startLoadingDialog();
//        mDatabase.collection("followers").document(user.getUID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    if(task.getResult().exists()){
//                        // Add post to followers map
//                        DocumentSnapshot doc = task.getResult();
//                        Log.e(TAG, "ERROR: checking followers! -> " + ((HashMap)doc.get("mapA")).get("docID"));
//                        if(((HashMap)doc.get("mapA")).get("docID").equals(deleteDocID)){
//                            String map = "mapA";
//                            mDatabase.collection("followers").document(user.getUID()).update(map, null).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    mDatabase.collection("posts").document(deleteDocID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            //TODO reduce number of posts on profile by 1
//                                            mDatabase.collection("users").document(user.getUID()).update("livePosts", FieldValue.increment(- 1));
//                                        }
//                                    });
//                                    loadingDialog.dismissDialog();
//                                }
//                            });
//                        }else if(((HashMap)doc.get("mapB")).get("docID").equals(deleteDocID)){
//                            String map = "mapB";
//                            mDatabase.collection("followers").document(user.getUID()).update(map, null).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    mDatabase.collection("posts").document(deleteDocID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            //TODO reduce number of posts on profile by 1
//                                            mDatabase.collection("users").document(user.getUID()).update("livePosts", FieldValue.increment(- 1));
//                                        }
//                                    });
//                                    loadingDialog.dismissDialog();
//                                }
//                            });
//                        }else if(((HashMap)doc.get("mapC")).get("docID").equals(deleteDocID)){
//                            String map = "mapC";
//                            mDatabase.collection("followers").document(user.getUID()).update(map, null).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    mDatabase.collection("posts").document(deleteDocID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            //TODO reduce number of posts on profile by 1
//                                            mDatabase.collection("users").document(user.getUID()).update("livePosts", FieldValue.increment(- 1));
//                                        }
//                                    });
//                                    loadingDialog.dismissDialog();
//                                }
//                            });
//                        }else{
//                            mDatabase.collection("posts").document(deleteDocID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    //TODO reduce number of posts on profile by 1
//                                    mDatabase.collection("users").document(user.getUID()).update("livePosts", FieldValue.increment(- 1));
//                                }
//                            });
//                        }
//                    }else{
//                        mDatabase.collection("posts").document(deleteDocID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                //TODO reduce number of posts on profile by 1
//                                mDatabase.collection("users").document(user.getUID()).update("livePosts", FieldValue.increment(- 1));
//                            }
//                        });
//                        loadingDialog.startLoadingDialog();
//                    }
//                }
//            }
//        });
//    }

    public void deletePost(String deleteDocID){
        loadingDialog.startLoadingDialog();
        mDatabase.collection("followers").document(user.getUID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        // Add post to followers map
                        DocumentSnapshot doc = task.getResult();
                        if(((HashMap)doc.get("mapA")) != null){
                            if(((HashMap)doc.get("mapA")).get("docID").equals(deleteDocID)){
                                String map = "mapA";
                                mDatabase.collection("followers").document(user.getUID()).update(map, null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mDatabase.collection("posts").document(deleteDocID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                //TODO reduce number of posts on profile by 1
                                                mDatabase.collection("users").document(user.getUID()).update("livePosts", FieldValue.increment(- 1));
                                                addPosts(mainView);
                                            }
                                        });
                                        loadingDialog.dismissDialog();
                                    }
                                });
                            }
                        }else if(((HashMap)doc.get("mapB")) != null){
                            if(((HashMap)doc.get("mapB")).get("docID").equals(deleteDocID)){
                                String map = "mapB";
                                mDatabase.collection("followers").document(user.getUID()).update(map, null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mDatabase.collection("posts").document(deleteDocID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                //TODO reduce number of posts on profile by 1
                                                mDatabase.collection("users").document(user.getUID()).update("livePosts", FieldValue.increment(- 1));
                                                addPosts(mainView);
                                            }
                                        });
                                        loadingDialog.dismissDialog();
                                    }
                                });
                            }
                        }else if(((HashMap)doc.get("mapC")) != null){
                            if(((HashMap)doc.get("mapC")).get("docID").equals(deleteDocID)){
                                String map = "mapC";
                                mDatabase.collection("followers").document(user.getUID()).update(map, null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mDatabase.collection("posts").document(deleteDocID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                //TODO reduce number of posts on profile by 1
                                                mDatabase.collection("users").document(user.getUID()).update("livePosts", FieldValue.increment(- 1));
                                                addPosts(mainView);
                                            }
                                        });
                                        loadingDialog.dismissDialog();
                                    }
                                });
                            }
                        }else{
                            mDatabase.collection("posts").document(deleteDocID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //TODO reduce number of posts on profile by 1
                                    mDatabase.collection("users").document(user.getUID()).update("livePosts", FieldValue.increment(- 1));
                                    addPosts(mainView);
                                }
                            });
                        }
                    }else{
                        mDatabase.collection("posts").document(deleteDocID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //TODO reduce number of posts on profile by 1
                                mDatabase.collection("users").document(user.getUID()).update("livePosts", FieldValue.increment(- 1));
                                addPosts(mainView);
                            }
                        });
                        loadingDialog.startLoadingDialog();
                    }
                }
            }
        });
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