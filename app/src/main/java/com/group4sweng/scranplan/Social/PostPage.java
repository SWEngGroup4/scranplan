package com.group4sweng.scranplan.Social;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.group4sweng.scranplan.LoadingDialog;
import com.group4sweng.scranplan.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class for displaying social post with comments.
 * Author(s): LNewman
 * (c) CoDev 2020
 *
 * Page displaying a full post with max sized pictures and an area for users to comment and for all comments to be displayed in an infinite scroll
 */
public class PostPage extends AppCompatDialogFragment {

    final static String TAG = "POST"; //Log tag.
    LoadingDialog loadingDialog;
    int numLoad = 15;

    /**  Firebase **/
    FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    public PostPage(TextView likes, CheckBox likedOrNot, TextView numComments, View view){
        lastPageLikedOrNot = likedOrNot;
        lastPageLikes = likes;
        lastPageComments = numComments;

        this.bottomView = view;
    }

    View bottomView;
    NestedScrollView postScrollView;

    /**  Comment additions **/
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;
    private DocumentSnapshot lastVisible;
    private RecyclerView commentList;
    private Query query;
    com.group4sweng.scranplan.UserInfo.UserInfoPrivate mUser;

    private CheckBox lastPageLikedOrNot;
    private TextView lastPageLikes;
    private TextView lastPageComments;

    private String postID;
    private String authorPicURL;
    private String authorID;

    private Button sendComment;
    private EditText newComment;

    private TextView author;
    private TextView body;
    private TextView timeStamp;
    private ImageView authorPic;
    private ImageView uploadedImageView;

    private ImageView recipeImageView;
    private TextView recipeDescription;
    private TextView recipeTitle;

    private TextView numLikes;
    private TextView numComments;
    private CheckBox likedOrNot;

    private boolean likedB4;

    private RatingBar recipeRating;

    private ImageView menu;

    // Auto-generated super method
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View layout = inflater.inflate(R.layout.post_page, null);
        postScrollView = layout.findViewById(R.id.postScrollView);

        builder.setView(layout);

        //This method holds all the arguments from the bundle
        initPageItems(layout);
        initBundleItems(layout, getArguments());

        loadingDialog = new LoadingDialog(getActivity());

        initPageListeners(layout);

        addFirestoreComments();

        return layout;
    }

    /**
     * Method that scales the pop up dialog box to fill the majority of the screen
     */
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

    }

    /**
     * Arguments from the bundle passed into the fragment that contains data for the info page from the firestore
     */
    private void initBundleItems(View layout, Bundle bundle) {
        mUser = (com.group4sweng.scranplan.UserInfo.UserInfoPrivate) requireActivity().getIntent().getSerializableExtra("user");
        authorID = bundle.getString("authorID");
        postID = bundle.getString("postID");
        authorPicURL = bundle.getString("authorPicURL");
        likedB4 = bundle.getBoolean("likedB4");
        likedOrNot.setChecked(lastPageLikedOrNot.isChecked());

        Glide.with(getContext())
                .load(authorPicURL)
                .apply(RequestOptions.circleCropTransform())
                .into(authorPic);
        authorPic.setVisibility(View.VISIBLE);
        author.setText(bundle.getString("authorName"));
        body.setText(bundle.getString("body"));
        if(bundle.getString("uploadedImageURL") != null){
            uploadedImageView.setVisibility(View.VISIBLE);
            Picasso.get().load(bundle.getString("uploadedImageURL")).into(uploadedImageView);
        }
        if(bundle.getBoolean("isRecipe")){
            recipeTitle.setVisibility(View.VISIBLE);
            recipeDescription.setVisibility(View.VISIBLE);
            recipeImageView.setVisibility(View.VISIBLE);
            recipeTitle.setText(bundle.getString("recipeTitle"));
            recipeDescription.setText(bundle.getString("recipeDescription"));
            Picasso.get().load(bundle.getString("recipeImageURL")).into(recipeImageView);
            if(bundle.getBoolean("isReview")){
                recipeRating.setVisibility(View.VISIBLE);
                recipeRating.setRating(bundle.getFloat("overallRating"));
            }
        }
        timeStamp.setText(bundle.getString("timestamp"));
        numLikes.setText(lastPageLikes.getText());
        numComments.setText(lastPageComments.getText());

    }

    /**
     * When back button is clicked within the recipe information dialogFragment,
     * Recipe information dialogFragment is closed and returns to recipe fragment
     */
    protected void initPageListeners(View layout) {
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getContext(), view);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.menu_comment, popup.getMenu());
                if(authorID.equals(mUser.getUID())){
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
                                Log.e(TAG,"Clicked delete comment! &&&&&& post ID = " + postID);
                                bottomView.setVisibility(View.GONE);
                                deletePost(postID);
                                getDialog().dismiss();
                                break;
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });


        likedOrNot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (likedOrNot.isChecked()) {
                    Log.e("FERC", "liked post");
                    int newLiked = Integer.parseInt((String) numLikes.getText())+1;
                    String test = String.valueOf(newLiked);
                    numLikes.setText(test);

                } else {
                    Log.e("FERC", "unliked post");
                    int newLiked = Integer.parseInt((String) numLikes.getText())-1;
                    String likeString = String.valueOf(newLiked);
                    numLikes.setText(likeString);
                }
                lastPageLikedOrNot.setChecked(likedOrNot.isChecked());
            }
        });

        /* Setting up the post comment listener, removing the text from the box and saving
         it as a new document in the Firestore, the data is also reloaded */
        sendComment.setOnClickListener(v -> {
            String content = newComment.getText().toString();
            newComment.getText().clear();

            CollectionReference ref = mDatabase.collection("posts").document(postID).collection("comments");
            Log.e(TAG, "Added new doc ");
            // Saving the comment as a new document
            HashMap<String, Object> map = new HashMap<>();
            map.put("authorID", mUser.getUID());
            map.put("likes", 0);
            map.put("comment", content);
            map.put("timestamp", FieldValue.serverTimestamp());
            // Saving default user to Firebase Firestore database
            ref.add(map);
            int newComment = Integer.parseInt((String) numComments.getText())+1;
            String commentString = String.valueOf(newComment);
            lastPageComments.setText(commentString);
            numComments.setText(commentString);
            mDatabase.collection("posts").document(postID).update("comments", FieldValue.increment(1));
            addFirestoreComments();


        });

    }

    /**
     *  Adding capabilities to delete post from post page
     * @param deleteDocID
     */
    public void deletePost(String deleteDocID){
        mDatabase.collection("followers").document(mUser.getUID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        // Add post to followers map
                        DocumentSnapshot doc = task.getResult();
                        if(((HashMap)doc.get("mapA")) != null){
                            if(((HashMap)doc.get("mapA")).get("docID").equals(deleteDocID)){
                                String map = "mapA";
                                mDatabase.collection("followers").document(mUser.getUID()).update(map, null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mDatabase.collection("posts").document(deleteDocID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mDatabase.collection("users").document(mUser.getUID()).update("livePosts", FieldValue.increment(- 1));
                                            }
                                        });
                                    }
                                });
                            }
                        }else if(((HashMap)doc.get("mapB")) != null){
                            if(((HashMap)doc.get("mapB")).get("docID").equals(deleteDocID)){
                                String map = "mapB";
                                mDatabase.collection("followers").document(mUser.getUID()).update(map, null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mDatabase.collection("posts").document(deleteDocID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mDatabase.collection("users").document(mUser.getUID()).update("livePosts", FieldValue.increment(- 1));
                                            }
                                        });
                                    }
                                });
                            }
                        }else if(((HashMap)doc.get("mapC")) != null){
                            if(((HashMap)doc.get("mapC")).get("docID").equals(deleteDocID)){
                                String map = "mapC";
                                mDatabase.collection("followers").document(mUser.getUID()).update(map, null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mDatabase.collection("posts").document(deleteDocID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mDatabase.collection("users").document(mUser.getUID()).update("livePosts", FieldValue.increment(- 1));
                                            }
                                        });
                                    }
                                });
                            }
                        }else{
                            mDatabase.collection("posts").document(deleteDocID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mDatabase.collection("users").document(mUser.getUID()).update("livePosts", FieldValue.increment(- 1));
                                }
                            });
                        }
                    }else{
                        mDatabase.collection("posts").document(deleteDocID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mDatabase.collection("users").document(mUser.getUID()).update("livePosts", FieldValue.increment(- 1));
                            }
                        });
                    }
                }
            }
        });
    }



    //Assigning data passed through into the various xml views
    protected void initPageItems(View layout) {
        //Buttons
        sendComment = layout.findViewById(R.id.sendCommentButton);
        newComment = layout.findViewById(R.id.addCommentEditText);
        commentList = layout.findViewById(R.id.commentList);
        commentList.setNestedScrollingEnabled(false);
        authorPic = layout.findViewById(R.id.postAuthorPic);
        author = layout.findViewById(R.id.postAuthor);
        body = layout.findViewById(R.id.postBody);
        uploadedImageView = layout.findViewById(R.id.userUploadedImageViewAdapter);
        recipeTitle = layout.findViewById(R.id.postRecipeTitleAdapter);
        recipeImageView = layout.findViewById(R.id.postRecipeImageViewAdapter);
        recipeDescription = layout.findViewById(R.id.postRecipeDescriptionAdapter);
        timeStamp = layout.findViewById(R.id.postTimeStamp);
        recipeRating = layout.findViewById(R.id.postRecipeRatingAdapter);
        numLikes = layout.findViewById(R.id.postNumLike);
        numComments = layout.findViewById(R.id.postNumComments);
        likedOrNot = layout.findViewById(R.id.likeIconPost);
        menu = layout.findViewById(R.id.postMenu);
    }

    /**
     * This method checks what comment is selected and opens up a menu to either open up another
     * users profile or if the comment was made my this user, user can delete the comment.
     * @param document
     * @param anchor
     */
    public void commentSelected(DocumentSnapshot document, View anchor){
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(getContext(), anchor);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.menu_comment, popup.getMenu());
        if(document.get("authorID").toString().equals(mUser.getUID())){
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
                        document.getReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                int newComment = Integer.parseInt((String) numComments.getText())-1;
                                String commentString = String.valueOf(newComment);
                                lastPageComments.setText(commentString);
                                numComments.setText(commentString);
                                mDatabase.collection("posts").document(postID).update("comments", FieldValue.increment(- 1));
                                addFirestoreComments();
                            }
                        });
                        break;
                }
                return true;
            }
        });

        popup.show();//showing popup menu
    }



    /**
     * Function to set up a new recycler view that takes all comments and downloads them from the server
     * when there is more than 5 comments, the data is downloaded 5 items at a time and loads new comments
     * as the user scrolls down through the comments
     */
    private void addFirestoreComments(){

        List<CommentRecyclerAdapter.CommentData> data;
        data = new ArrayList<>();

        // Creating a list of the data and building all variables to add to recycler view
        final RecyclerView recyclerView = commentList;
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager rManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(rManager);
        final RecyclerView.Adapter rAdapter = new CommentRecyclerAdapter(PostPage.this, data, mUser, postID);
        recyclerView.setAdapter(rAdapter);
        query = mDatabase.collection("posts").document(postID).collection("comments").limit(numLoad).orderBy("timestamp");

        // Once the data has been returned, dataset populated and components build
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // For each document a new recipe preview view is generated
                    if(task.getResult() != null)
                    {
                        for (DocumentSnapshot document : task.getResult()) {
                            data.add(new CommentRecyclerAdapter.CommentData(
                                    document,
                                    document.get("authorID").toString(),
                                    document.get("comment").toString(),
                                    (Timestamp) document.getTimestamp("timestamp"),
                                    document.get("likes").toString(),
                                    document.getId()
                            ));
                        }
                        rAdapter.notifyDataSetChanged();
                        // Set the last document as last user can see
                        if(task.getResult().size() != 0){
                            lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        }else{
                            // If no data returned, user notified
                            isLastItemReached = true;
                            data.add(new CommentRecyclerAdapter.CommentData(
                                    null,
                                    null,
                                    "No comments here yet, be the first!",
                                    null,
                                    null,
                                    null
                            ));
                        }
                        // check if user has scrolled through the view

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
                                                            data.add(new CommentRecyclerAdapter.CommentData(
                                                                    d,
                                                                    d.get("authorID").toString(),
                                                                    d.get("comment").toString(),
                                                                    (Timestamp) d.getTimestamp("timestamp"),
                                                                    d.get("likes").toString(),
                                                                    d.getId()
                                                            ));
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
                        postScrollView.setOnScrollChangeListener(onScrollListener);
                    }
                }
            }
        });
    }
    

}
