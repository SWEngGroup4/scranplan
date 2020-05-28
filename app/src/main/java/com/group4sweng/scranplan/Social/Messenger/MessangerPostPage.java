package com.group4sweng.scranplan.Social.Messenger;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.group4sweng.scranplan.Administration.LoadingDialog;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.Social.PostPage;
import com.squareup.picasso.Picasso;

public class MessangerPostPage extends PostPage {

    final static String TAG = "POST"; //Log tag.
    LoadingDialog loadingDialog;
    int numLoad = 15;

    /**  Firebase **/
    FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    public MessangerPostPage(TextView likes, CheckBox likedOrNot, TextView numComments, View view) {
        super(likes, likedOrNot, numComments, view);
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
    private ImageView commentButton;

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

//        initPageListeners(layout);
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
        recipeID = bundle.getString("recipeID");

        numLikes.setVisibility(View.GONE);
        numComments.setVisibility(View.GONE);
        commentButton.setVisibility(View.GONE);
        likedOrNot.setVisibility(View.GONE);

        Glide.with(getContext())
                .load(authorPicURL)
                .apply(RequestOptions.circleCropTransform())
                .into(authorPic);
        authorPic.setVisibility(View.VISIBLE);
        author.setText(bundle.getString("authorName"));
        body.setText(bundle.getString("body"));
        if (bundle.getString("uploadedImageURL") != null) {
            uploadedImageView.setVisibility(View.VISIBLE);
            Picasso.get().load(bundle.getString("uploadedImageURL")).into(uploadedImageView);
        }
        if (bundle.getBoolean("isRecipe")) {
            recipeTitle.setVisibility(View.VISIBLE);
            recipeDescription.setVisibility(View.VISIBLE);
            recipeImageView.setVisibility(View.VISIBLE);
            recipeTitle.setText(bundle.getString("recipeTitle"));
            recipeDescription.setText(bundle.getString("recipeDescription"));
            Picasso.get().load(bundle.getString("recipeImageURL")).into(recipeImageView);
            if (bundle.getBoolean("isReview")) {
                recipeRating.setVisibility(View.VISIBLE);
                recipeRating.setRating(bundle.getFloat("overallRating"));
            }
        }
        timeStamp.setText(bundle.getString("timestamp"));

        // Hide things we don't need in this view
        sendComment.setVisibility(View.GONE);
        newComment.setVisibility(View.GONE);
        commentList.setVisibility(View.GONE);
    }


        //Assigning data passed through into the various xml views
    @Override
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
        commentButton = layout.findViewById(R.id.commentIcon);
    }
}
