package com.group4sweng.scranplan.Social;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.group4sweng.scranplan.Administration.ContentReporting;
import com.group4sweng.scranplan.Presentation.Presentation;
import com.group4sweng.scranplan.PublicProfile;
import com.group4sweng.scranplan.Administration.LoadingDialog;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.RecipeInfo.RecipeInfoFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class for displaying social post with comments.
 * Author(s): LNewman
 * (c) CoDev 2020
 *
 * Page displaying a full post with max sized pictures and an area for users to comment and for all comments to be displayed in an infinite scroll
 */
public class PostPage extends AppCompatDialogFragment {

    final static String TAG = "POST"; //Log tag.
    protected ContentReporting reportContent;
    LoadingDialog loadingDialog;
    int numLoad = 15;

    /**  Firebase **/
    FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    FirebaseStorage mStorage = FirebaseStorage.getInstance();

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
    private String recipeID;
    private String uploadedImageURL;

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
    private boolean isReview;
    private boolean isPic;

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
        recipeID = bundle.getString("recipeID");
        likedOrNot.setChecked(lastPageLikedOrNot.isChecked());

        Glide.with(getContext())
                .load(authorPicURL)
                .apply(RequestOptions.circleCropTransform())
                .into(authorPic);
        authorPic.setVisibility(View.VISIBLE);
        author.setText(bundle.getString("authorName"));
        body.setText(bundle.getString("body"));
        if(bundle.getString("uploadedImageURL") != null){
            isPic = true;
            uploadedImageView.setVisibility(View.VISIBLE);
            Picasso.get().load(bundle.getString("uploadedImageURL")).into(uploadedImageView);
            uploadedImageURL = bundle.getString("uploadedImageURL");
        }
        if(bundle.getBoolean("isRecipe")){
            recipeTitle.setVisibility(View.VISIBLE);
            recipeDescription.setVisibility(View.VISIBLE);
            recipeImageView.setVisibility(View.VISIBLE);
            recipeTitle.setText(bundle.getString("recipeTitle"));
            recipeDescription.setText(bundle.getString("recipeDescription"));
            Picasso.get().load(bundle.getString("recipeImageURL")).into(recipeImageView);
            if(bundle.getBoolean("isReview")){
                isReview = true;
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
                                Intent intentProfile = new Intent(getContext(), PublicProfile.class);

                                intentProfile.putExtra("UID", authorID);
                                intentProfile.putExtra("user", mUser);
                                //setResult(RESULT_OK, intentProfile);
                                startActivity(intentProfile);
                                break;
                            case R.id.reportComment:
                                Log.e(TAG,"Report post clicked!");

                                //HashMap with relevant information to be sent for reporting
                                HashMap<String, Object> reportsMap = new HashMap<>();
                                reportsMap.put("docID", postID);
                                reportsMap.put("usersID", authorID);
                                reportsMap.put("issue","Reporting Content");

                                //creating a dialog box on screen so that the user can report an issue
                                String firebaseLocation = "reporting";
                                ContentReporting reportContent = new ContentReporting(getActivity(), reportsMap, firebaseLocation);
                                reportContent.startReportingDialog();
                                reportContent.title.setText("Report Content");
                                reportContent.message.setText("What is the issue you would like to report?");
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
            if(!newComment.getText().toString().equals("")) {
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
                int newComment = Integer.parseInt((String) numComments.getText()) + 1;
                String commentString = String.valueOf(newComment);
                lastPageComments.setText(commentString);
                numComments.setText(commentString);
                mDatabase.collection("posts").document(postID).update("comments", FieldValue.increment(1));
                addFirestoreComments();
            }else{
                Toast.makeText(getContext(),"You need to write a comment first!",Toast.LENGTH_SHORT).show();
            }

        });

        recipeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingDialog.startLoadingDialog();

                mDatabase.collection("recipes").document(recipeID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {

                                DocumentSnapshot document = task.getResult();
                                recipeSelected(document);

                                loadingDialog.dismissDialog();

                            }
                        }
                    }
                });



            }
        });

    }

    /**
     * Adding capability to delete post from post page
     * @param deleteDocID
     */
    public void deletePost(String deleteDocID){
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();
        String userID = authorID;
        if(authorID != null){
            // Delete review if one
            if(isReview){
                mDatabase.collection("recipes").document(recipeID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult() != null){
                            HashMap<String, Double> ratingMap = (HashMap) task.getResult().get("rating");
                            revertRating(ratingMap, recipeID, recipeRating.getRating());
                        }
                    }
                });
                // UserID-RecipeID
                mDatabase.collection("reviews").document(authorID + "-" + recipeID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            task.getResult().getReference().delete();
                        }
                    }
                });
            }
            // Delete pic if one
            if(isPic){
                if(uploadedImageURL != null){
                    mStorage.getReferenceFromUrl(uploadedImageURL).delete();
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
                                        loadingDialog.dismissDialog();
                                    }
                                });
                            }
                        }else{
                            mDatabase.collection("posts").document(deleteDocID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mDatabase.collection("users").document(userID).update("livePosts", FieldValue.increment(- 1));
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

    protected void revertRating(HashMap<String,Double> ratingMap, String recipeID, float oldUserRating){
        double oldOverallRating;
        double oldTotalRates;
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
                        Intent intentProfile = new Intent(getContext(), PublicProfile.class);

                        intentProfile.putExtra("UID", (String) document.get("author"));
                        intentProfile.putExtra("user", mUser);
                        //setResult(RESULT_OK, intentProfile);
                        startActivity(intentProfile);
                        break;
                    case R.id.reportComment:
                        Log.e(TAG,"Report comment clicked!");

                        //HashMap with relevant information to be sent for reporting
                        HashMap<String, Object> reportsMap = new HashMap<>();
                        reportsMap.put("docID", document.getId());
                        reportsMap.put("usersID", document.get("authorID").toString());
                        reportsMap.put("issue","Reporting Content");

                        //creating a dialog box on screen so that the user can report an issue
                        String firebaseLocation = "reporting";
                        reportContent = new ContentReporting(getActivity(), reportsMap, firebaseLocation);
                        reportContent.startReportingDialog();
                        reportContent.title.setText("Report Content");
                        reportContent.message.setText("What is the issue you would like to report?");
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

    /**
     * On click of a recipe a new recipe info fragment is opened and the document is sent through
     * This saves on downloading the data again from the database
     */
    public void recipeSelected(DocumentSnapshot document) {


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
        mBundle.putBoolean("isFav", faves.contains(mUser.getUID()));

        RecipeInfoFragment recipeDialogFragment = new RecipeInfoFragment();
        recipeDialogFragment.setArguments(mBundle);
        recipeDialogFragment.setTargetFragment(this, 1);
        recipeDialogFragment.show(getFragmentManager(), "Show recipe dialog fragment");
    }
    

}
