package com.group4sweng.scranplan.Social.Messenger;

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
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group4sweng.scranplan.Administration.ContentReporting;
import com.group4sweng.scranplan.Administration.LoadingDialog;
import com.group4sweng.scranplan.Exceptions.ImageException;
import com.group4sweng.scranplan.MealPlanner.PlannerListFragment;
import com.group4sweng.scranplan.PublicProfile;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.SearchFunctions.RecipeFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchPrefs;
import com.group4sweng.scranplan.SearchFunctions.SearchQuery;
import com.group4sweng.scranplan.Social.FeedFragment;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.sentry.core.Sentry;

import static android.app.Activity.RESULT_OK;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static com.group4sweng.scranplan.Helper.ImageHelpers.getExtension;
import static com.group4sweng.scranplan.Helper.ImageHelpers.getPrintableSupportedFormats;
import static com.group4sweng.scranplan.Helper.ImageHelpers.getSize;
import static com.group4sweng.scranplan.Helper.ImageHelpers.isImageFormatSupported;

public class MessengerFeedFragment extends FeedFragment {

    private final String TAG = "Messenger Feed Fragment";

    // Unique codes for image & permission request activity callbacks.
    private static final int IMAGE_REQUEST_CODE = 2;
    private static final int PERMISSION_CODE = 1001;

    private static final int MAX_IMAGE_FILE_SIZE_IN_MB = 4; // Max storage image size for the profile picture.

    private Uri mImageUri; // Unique image uri.
    private ImageView mUploadedImage;

    private float ratingNum;

    private LoadingDialog loadingDialog;

    //Score scroll info
    private List<MessengerFeedRecyclerAdapter.FeedPostPreviewData> data;
    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean refreshData = true;
    private boolean isLastItemReached = false;

    private Button mPostButton;
    private CheckBox mPostRecipe;
    private CheckBox mPostReview;
    private CheckBox mPostPic;
    private EditText mPostBodyInput;

    private ImageView mAttachedRecipeImage;
    private TextView mAttachedRecipeTitle;
    private TextView mAttachedRecipeInfo;
    private String attachedRecipeURL;
    private String recipeID;
    private String newPostID;

    private ConstraintLayout mUserUploadedImageViewLayout;
    private ConstraintLayout mPostRecipeImageViewLayout;

    private View mainView;
    FloatingActionButton mNewMessage;


    private TextView mRecipeRatingText;
    private RatingBar mAttachedRecipeReview;


    //Fragment handlers
    private FragmentTransaction fragmentTransaction;
    private RecipeFragment recipeFragment;

    //User information
    private com.group4sweng.scranplan.UserInfo.UserInfoPrivate mUser;
    private String mRecipient;
    private Boolean recipientNotDeleted;
    private SearchPrefs prefs;

    //Menu items
    private SearchView searchView;
    private MenuItem sortButton;

    private Query query;
    private RecyclerView recyclerView;


    // Database objects for accessing recipes
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mColRef = mDatabase.collection("followers");

    // Firebase user collection and storage references.
    private CollectionReference mRef;
    private CollectionReference mRecipientRef;
    private DocumentReference mUserInteractionRef;
    private DocumentReference mRecipientInteractionRef;
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private StorageReference mStorageReference = mStorage.getReference();


    MessengerFeedFragment(UserInfoPrivate userSent, String messageRecipient) {
        super(userSent);
        mUser = userSent;
        if(messageRecipient == null){
            Sentry.captureException(new NullPointerException("Message Recipent cannot be Null."));
            mRecipient = userSent.getUID();
        }else{
        mRecipient = messageRecipient;
        }
    }

    // Auto-generated onCreate method (everything happens here)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Remove from here? and make a see all messages view
        View view = inflater.inflate(R.layout.fragment_messenger_feed, container, false);
        mainView = view;
        loadingDialog = new LoadingDialog(getActivity());
        mRef = mDatabase.collection("users").document(mUser.getUID()).collection("userInteractions").document(mRecipient).collection("messages");
        mRecipientRef = mDatabase.collection("users").document(mRecipient).collection("userInteractions").document(mUser.getUID()).collection("messages");
        mUserInteractionRef = mDatabase.collection("users").document(mUser.getUID()).collection("userInteractions").document(mRecipient);
        mRecipientInteractionRef = mDatabase.collection("users").document(mRecipient).collection("userInteractions").document(mUser.getUID());

        checkCollections(mUserInteractionRef, mRecipientInteractionRef);
        checkRecipient(mRecipient);

        initPageItems(view);
        initPageListeners();

        MessengerMenu messengerMenu = (MessengerMenu) getActivity();

        if (messengerMenu != null) {
            // Gets search activity from home class and make it invisible
            searchView = messengerMenu.getSearchView();
            sortButton = messengerMenu.getSortView();

            sortButton.setVisible(false);
            searchView.setVisibility(View.INVISIBLE);
            //setSearch();

            //Gets search preferences from home class
            prefs = messengerMenu.getSearchPrefs();
        }

        addPosts(view);

        // Checks users details have been provided
        if (mUser == null) {
            // If scroll views fail due to no user, this error is reported
            Log.e(TAG, "ERROR: Loading messenger - We were unable to find user.");
        }
        return view;
    }

    private void checkRecipient(String mRecipient) {
        mDatabase.collection("users").document(mRecipient).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        recipientNotDeleted = true;
                    } else {
                        Log.d(TAG, "No such document");
                        recipientNotDeleted = false;
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void checkCollections(DocumentReference mUserInteractionRef, DocumentReference mRecipientInteractionRef) {
        mUserInteractionRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                        Map<String, Object> uidInfo = new HashMap<>();
                        uidInfo.put("UID", mRecipient);
                        mUserInteractionRef.set(uidInfo);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        mRecipientInteractionRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                        Map<String, Object> uidInfo = new HashMap<>();
                        uidInfo.put("UID", mUser.getUID());
                        mRecipientInteractionRef.set(uidInfo);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }

    /**
     * Connecting up elements on the screen to variable names
     */
    @Override
    protected void initPageItems(View v) {
        //Defining all relevant members of page
        mPostButton = v.findViewById(R.id.sendPostButton);
        mPostRecipe = (CheckBox) v.findViewById(R.id.recipeIcon);
        mPostReview = (CheckBox) v.findViewById(R.id.reviewIcon);
        mPostPic = (CheckBox) v.findViewById(R.id.imageIcon);
        mPostBodyInput = v.findViewById(R.id.postBodyInput);
        mUploadedImage = v.findViewById(R.id.userUploadedImageView);
        mAttachedRecipeImage = v.findViewById(R.id.postRecipeImageView);
        mAttachedRecipeTitle = v.findViewById(R.id.postRecipeTitle);
        mAttachedRecipeInfo = v.findViewById(R.id.postRecipeDescription);
        mAttachedRecipeReview = v.findViewById(R.id.postRecipeRating);
        mRecipeRatingText = v.findViewById(R.id.recipeRate);
        mUserUploadedImageViewLayout = v.findViewById(R.id.userUploadedImageViewLayout);
        mPostRecipeImageViewLayout = v.findViewById(R.id.postRecipeImageViewLayout);

        //TODO Add back
        mPostReview.setVisibility(View.INVISIBLE);
    }

    /**
     * Setting up page listeners for when buttons are pressed
     */
    @Override
    protected void initPageListeners() {
        mPostPic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mPostPic.isChecked()) {
                    if (mUploadedImage.getVisibility() != View.VISIBLE) {
                        //  Check if the version of Android is above 'Marshmallow' we check for additional permission.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            //  Checks if permission has already been granted to read from external storage (our image picker)
                            if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                                //   Ask for permission.
                                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
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
                if(recipientNotDeleted){
                String body = mPostBodyInput.getText().toString();
                if (mPostPic.isChecked() || mPostRecipe.isChecked() || !body.equals("")) {
                    loadingDialog.startLoadingDialog();
                    //TODO set these variables from the addition of these items
                    Log.e(TAG, "Added new post ");
                    // Saving the comment as a new document
                    HashMap<String, Object> map = new HashMap<>();
                    HashMap<String, Object> extras = new HashMap<>();
                    Timestamp time = Timestamp.now();
                    long finalTime = time.getSeconds() ;
                    map.put("docId", mUser.getUID() + finalTime + (time.getNanoseconds() * 10^9));
                    extras.put("comments", 0);
                    extras.put("likes", 0);
                    map.put("author", mUser.getUID());
                    map.put("body", body);
                    map.put("timestamp", FieldValue.serverTimestamp());
                    map.put("isPic", mPostPic.isChecked());
                    map.put("isRecipe", mPostRecipe.isChecked());
                    map.put("isReview", mPostReview.isChecked());
                    if (mPostPic.isChecked()) {
                        postImageAttached(map, extras, mRef);
                    } else {
                        addRecipeInfo(map, extras, mRef, mRecipientRef);
                    }
                } else {
                    Toast.makeText(getContext(), "You need to either write a post, attach a picture or attach a recipe before you can submit new post.", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(getContext(), "You cannot message deleted members", Toast.LENGTH_SHORT).show();
            }}
        });
        mPostRecipe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mPostRecipe.isChecked()) {
                    if (mAttachedRecipeImage.getVisibility() != View.VISIBLE) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("planner", true); //Condition to let child fragments know access is from planner

                        //Creates and launches recipe fragment
                        recipeFragment = new RecipeFragment(mUser);
                        recipeFragment.setArguments(bundle);
                        recipeFragment.setTargetFragment(MessengerFeedFragment.this, 1);
                        fragmentTransaction = getParentFragmentManager().beginTransaction();
                        fragmentTransaction.add(R.id.messageFrameLayout, recipeFragment); //Overlays fragment on existing one
                        fragmentTransaction.commitNow(); //Waits for fragment transaction to be completed
                        requireView().setVisibility(View.INVISIBLE); //Sets current fragment invisible

                        //Makes search bar icon visible
                        searchView.setQuery("", false);
                        searchView.setVisibility(View.GONE);
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
                    if (mAttachedRecipeReview.getVisibility() != View.VISIBLE) {
                        if (mPostRecipe.isChecked()) {
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
                        } else {
                            Log.e(TAG, "No recipe so no review");
                            mPostReview.setChecked(false);
                            Toast.makeText(getContext(), "You need to attach a recipe before you can review it.", Toast.LENGTH_SHORT).show();
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
    private void imageSelector() {
        Intent images = new Intent(Intent.ACTION_PICK);
        images.setType("image/*"); // Only open the 'image' file picker. Don't include videos, audio etc...
        startActivityForResult(images, IMAGE_REQUEST_CODE);
        //mPostPic.setChecked(false);// Start the image picker and expect a result once an image is selected.
    }

    /**
     * Adding recipe info if attached to post
     *
     * @param map post info
     * @param extras post info
     * @param ref where to post
     */
    private void addRecipeInfo(HashMap map, HashMap extras, CollectionReference ref, CollectionReference recipientRef) {
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
        } else {
            savePost(map, extras, ref, recipientRef, null);
        }
    }

    /**
     * Adding all posts for current user to the feed
     * @param view view to add the posts
     */
    @Override
    protected void addPosts(View view) {
        recyclerView = view.findViewById(R.id.messagesList);

        // Set out the layout of this horizontal view
        LinearLayoutManager rManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        recyclerView.setLayoutManager(rManager);
        //recyclerView.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels));
        // Array to score downloaded data
        data = new ArrayList<>();
        final MessengerFeedRecyclerAdapter rAdapter = new MessengerFeedRecyclerAdapter(MessengerFeedFragment.this, data, mUser, view);
        recyclerView.setAdapter(rAdapter);
        int numberOfMessages = 50;
        query = mRef.orderBy("timestamp", Query.Direction.DESCENDING).limit(numberOfMessages);
        final boolean[] initalData = {true};
        // Ensure query exists and builds view with query
        if (query != null) {
            Log.e(TAG, "User is searching the following query: " + query.toString());
            // Query listener to add data to view
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "listen Failed", e);
                        Sentry.captureException(e);
                        return;
                    }
                    Log.e("Messanger", "UID = " + mUser.getUID());
                    Log.e("Messanger", "task success");
                    data.clear();
                    ArrayList<HashMap> posts = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        if(!document.getMetadata().hasPendingWrites()){
                        Log.e("FEED", "I have found a doc");
                        HashMap postInfomation;
                        postInfomation = (HashMap) document.getData();
                        posts.add(postInfomation);
                    }}
                        for (int i = 0; i < posts.size(); i++) {
                            data.add(new MessengerFeedRecyclerAdapter.FeedPostPreviewData(
                                    posts.get(i)));
                        }
                        if (queryDocumentSnapshots.size() != 0) {
                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        } else {
                            isLastItemReached = true;
                        }
                    rAdapter.notifyDataSetChanged();

//                    // Track users location to check if new data download is required
//                    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
//                        @Override
//                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                            super.onScrollStateChanged(recyclerView, newState);
//                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
//                                isScrolling = true;
//                            }
//                        }
//
//                        // If scrolled to end then download new data and check if we are out of data
//                        @Override
//                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                            super.onScrolled(recyclerView, dx, dy);
//
//                            LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
//                            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
//                            int visibleItemCount = linearLayoutManager.getChildCount();
//                            int totalItemCount = linearLayoutManager.getItemCount();
//
//                            if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
//                                isScrolling = false;
//                                Query nextQuery = query.startAfter(lastVisible);
//                                nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<QuerySnapshot> t) {
//                                        if (t.isSuccessful()) {
//                                            ArrayList<HashMap> postsNext = new ArrayList<>();
//                                            for (DocumentSnapshot d : t.getResult()) {
//                                                Log.e("FEED", "I have found a doc");
//                                                HashMap postInfomation;
//                                                postInfomation = (HashMap) d.getData();
//                                                posts.add(postInfomation);                                            }
//
//                                            for (int i = 0; i < postsNext.size(); i++) {
//                                                data.add(new MessengerFeedRecyclerAdapter.FeedPostPreviewData(
//                                                        postsNext.get(i)));
//                                            }
//                                            if (isLastItemReached) {
//                                                // Add end here
//                                            }
//                                            rAdapter.notifyDataSetChanged();
//                                            if (t.getResult().size() != 0) {
//                                                lastVisible = t.getResult().getDocuments().get(t.getResult().size() - 1);
//                                            }
//
//                                            if (t.getResult().size() < 5) {
//                                                isLastItemReached = true;
//                                            }
//                                        }
//                                    }
//                                });
//                            }
//                        }
//                    };
//                    recyclerView.addOnScrollListener(onScrollListener);
                }
            });

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                    }
                }
            });
        }
    }

    private void savePost(HashMap map, HashMap extras, CollectionReference myRef, CollectionReference recipientRef, HashMap<String, Object> newRatings) {
        HashMap<String, Object> latestMessage = new HashMap<>();
        latestMessage.put("latestMessage",FieldValue.serverTimestamp());
        if (mUser.getUID().equals(mRecipient)) {
            if (newPostID == null) {
                myRef.add(extras).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                if (task.isSuccessful()) {
                                                                    final String docID = task.getResult().getId();
                                                                    map.put("docID", docID);
                                                                    mUserInteractionRef.update(latestMessage);
                                                                    postComplete();
                                                                }
                                                            }
                                                        }
                );
            } else {
                myRef.document(newPostID).set(extras);
                map.put("docID", newPostID);
                mUserInteractionRef.update(latestMessage);
                postComplete();
            }
        }
        if (!mUser.getUID().equals(mRecipient)){
            if (newPostID == null) {
                myRef.add(extras).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            final String docID = task.getResult().getId();
                            map.put("docID", docID);
                            mUserInteractionRef.update(latestMessage);
                        }
                    }
                });
                recipientRef.add(extras).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            final String docID = task.getResult().getId();
                            mRecipientInteractionRef.update(latestMessage);
                        }
                    }
                });
                postComplete();
            } else {
                myRef.document(newPostID).set(extras);
                recipientRef.document(newPostID).set(extras);
                map.put("docID", newPostID);
                mUserInteractionRef.update(latestMessage);
                mRef.add(map);
                mRecipientInteractionRef.update(latestMessage);
                mRecipientRef.add(map);
                postComplete();
            }
        }
    }

    /** Handle our activity result for the image picker.
     * @param requestCode - Image request code.
     * @param resultCode - Success/failure code. 0 = success, -1 = failure.
     * @param data - Our associated image data.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    @Override
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

    //Quick function to reset search menu functionality
    private void setSearch() {
        MessengerMenu messengerMenu = (MessengerMenu) getActivity();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                prefs = messengerMenu.getSearchPrefs();
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
     * Adding image to post if an image is attached
     * @param map post info
     * @param extras post info
     * @param ref where to post
     */
    @Override
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
     * On click of a recipe a new recipe info fragment is opened and the document is sent through
     * This saves on downloading the data again from the database
     */
    public void itemSelected(Map<String, Object> document, Bundle mBundle,TextView likes, CheckBox likedOrNot, TextView numComments, View view) {
        MessangerPostPage postDialogFragment = new MessangerPostPage(likes, likedOrNot, numComments, view);
        postDialogFragment.setArguments(mBundle);
        postDialogFragment.setTargetFragment(this, 1);
        postDialogFragment.show(getFragmentManager(), "Show post dialog fragment");
    }


    /**
     * This method checks what comment is selected and opens up a menu to either open up another
     * users profile or if the comment was made my this user, user can delete the comment.
     * @param document item selected
     * @param menu menu selected
     */
    void menuSelected(HashMap document, View menu, int position){
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
                        Log.e(TAG,"Report comment clicked!");

                        //HashMap with relevant information to be sent for reporting
                        HashMap<String, Object> reportsMap = new HashMap<>();
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
                        Log.e(TAG,"Clicked delete comment!");
                        final String deleteDocID = document.get("docId").toString();
                        deletePost(deleteDocID,position);

                        break;
                }
                return true;
            }
        });

        popup.show();//showing popup menu
    }

    /**
     * Adding capability to delete post from post page
     * @param deleteDocID docId of the document to delete
     */
    private void deletePost(String deleteDocID, int position){
        mRef.whereEqualTo("docId",deleteDocID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot : task.getResult()){
                        mRef.document(documentSnapshot.getId()).delete();

                }
            }
        });

        mRecipientRef.whereEqualTo("docId",deleteDocID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot : task.getResult()){
                        mRecipientRef.document(documentSnapshot.getId()).delete();
                }
            }
        });
    }

    /**
     *  Adding recipe info if attached to post
     * @param map post info
     * @param extras post info
     * @param ref where to post
     */
    @Override
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
        }else{
            savePost(map, extras, ref, mRecipientRef, null);
        }
    }

    @Override
    // Reset the screen once a new post has been completed
    protected void postComplete() {
        mPostBodyInput.getText().clear();
        mPostRecipe.setChecked(false);
        mPostReview.setChecked(false);
        mPostPic.setChecked(false);
        loadingDialog.dismissDialog();
    }
}
