package com.group4sweng.scranplan.Social;

import android.Manifest;
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
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
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
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.SearchFunctions.RecipeFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchPrefs;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

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
public class FeedFragment extends Fragment {

    final String TAG = "Home horizontal queries";

    // Unique codes for image & permission request activity callbacks.
    private static final int IMAGE_REQUEST_CODE = 2;
    private static final int PERMISSION_CODE = 1001;

    private static final int MAX_IMAGE_FILE_SIZE_IN_MB = 4; // Max storage image size for the profile picture.
    private static boolean IMAGE_IS_UPLOADING = false; // Boolean to determine if the image is uploading currently.

    private Uri mImageUri; // Unique image uri.
    ImageView mUploadedImage;



    // Width size of each scroll view, dictating size of images on home screen
    final int scrollViewSize = 5;

    //Score scroll info
    List<FeedRecyclerAdapter.FeedPostPreviewData> data;
    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;

    protected Button mPostButton;
    protected Button mPostRecipe;
    protected Button mPostReview;
    protected CheckBox mPostPic;
    protected EditText mPostTitleInput;
    protected EditText mPostBodyInput;

    private RecipeFragment recipeFragment;

    //User information
    protected com.group4sweng.scranplan.UserInfo.UserInfoPrivate user;
    private SearchPrefs prefs;

    //Menu items
    private SearchView searchView;
    private MenuItem sortButton;



    // Database objects for accessing recipes
    protected FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mColRef = mDatabase.collection("followers");
    // Firebase user collection and storage references.
    CollectionReference mRef = mDatabase.collection("posts");
    FirebaseStorage mStorage = FirebaseStorage.getInstance();
    StorageReference mStorageReference = mStorage.getReference();





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

        // Grabs screen size for % layout TODO - change to density pixels + NullPointerException check
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();

        // Procedurally fills topLayout with imageButton content
        LinearLayout topLayout = view.findViewById(R.id.topLayout);


        initPageItems(view);
        initPageListeners();

        // Checks users details have been provided
        if(user != null){
            // Build the first horizontal scroll built around organising the recipes via highest rated
            //TODO make new query
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





            final RecyclerView recyclerView = new RecyclerView(view.getContext());
            // Set out the layout of this horizontal view
            RecyclerView.LayoutManager rManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(rManager);
            recyclerView.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels));
            // Array to score downloaded data
            data = new ArrayList<>();
            final RecyclerView.Adapter rAdapter = new FeedRecyclerAdapter(FeedFragment.this, data);
            recyclerView.setAdapter(rAdapter);
            final Query query = mColRef.whereArrayContains("users", user.getUID()).orderBy("lastPost", Query.Direction.DESCENDING).limit(10);
            // Ensure query exists and builds view with query
            if (query != null) {
                Log.e(TAG, "User is searching the following query: " + query.toString());
//                // Give the view a title
//                TextView textView = new TextView(view.getContext());
//                String testString = "Top picks";
//                textView.setTextSize(25);
//                textView.setPadding(20, 5, 5, 5);
//                textView.setTextColor(Color.WHITE);
//                textView.setShadowLayer(4, 0, 0, Color.BLACK);
//                textView.setText(testString);
                // Query listener to add data to view
                query
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.e("FEED", "UID = " + user.getUID());
                            Log.e("FEED", "task success");
                            ArrayList<HashMap> posts = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.e("FEED", "I have found a doc");
                                //posts.addAll((ArrayList)document.get("recent"));
                                String first = (String) document.get("space1");
                                String second = (String) document.get("space1");
                                String third = (String) document.get("space1");
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
//
//                            Collections.sort(posts, new Comparator<Map<String, Object>>() {
//                                @Override
//                                public int compare(Map<String, Object> map1, Map<String, Object> map2) {
//                                    Timestamp firstValue = (Timestamp) map1.get("timestamp");
//                                    Timestamp secondValue = (Timestamp) map2.get("timestamp");
//                                    if (firstValue != null & secondValue != null) {
//                                        return firstValue.toDate().compareTo(secondValue.toDate());
//                                    }
//                                    return 0;
//                                }
//                            });
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
//                                                    for (DocumentSnapshot d : t.getResult()) {
//                                                        data.add(new FeedRecyclerAdapter.FeedPostPreviewData(
//                                                                d,
//                                                                d.getId(),
//                                                                d.get("imageURL").toString()
//                                                        ));
//                                                    }
                                                    ArrayList<HashMap> posts = new ArrayList<>();
                                                    for (DocumentSnapshot document : task.getResult()) {
                                                        posts.add((HashMap)document.get("recent"));
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
                // Add view to page
//                topLayout.addView(textView);
                topLayout.addView(recyclerView);
                Log.e(TAG, "Social feed added");
            }

        }else{
            // If scroll views fail due to no user, this error is reported
            Log.e(TAG, "ERROR: Loading social feed - We were unable to find user.");
        }
        return view;
    }


    /**
     *  Connecting up elements on the screen to variable names
     */
    protected void initPageItems(View v){
        //Defining all relevant members of page
        mPostButton = v.findViewById(R.id.sendPostButton);
        mPostRecipe = v.findViewById(R.id.recipeIcon);
        mPostReview = v.findViewById(R.id.reviewIcon);
        mPostPic = (CheckBox) v.findViewById(R.id.imageIcon);
        mPostBodyInput = v.findViewById(R.id.postBodyInput);
        mUploadedImage = v.findViewById(R.id.userUploadedImageView);

    }

    private void postPicClick(){
        //  Check if the version of Android is above 'Marshmallow' we check for additional permission.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            //  Checks if permission has already been granted to read from external storage (our image picker)
            if(getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                //   Ask for permission.
                String [] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                //  Read permission has been granted already.
                imageSelector();
            }
        } else {
            imageSelector();
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
                    //  Check if the version of Android is above 'Marshmallow' we check for additional permission.
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                        //  Checks if permission has already been granted to read from external storage (our image picker)
                        if(getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                            //   Ask for permission.
                            String [] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                            requestPermissions(permissions, PERMISSION_CODE);
                            imageSelector();
                        } else {
                            //  Read permission has been granted already.
                            imageSelector();
                        }
                    } else {
                        imageSelector();
                    }
                } else {
                    mUploadedImage.setVisibility(View.GONE);
                }
            }
        });

        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = mPostTitleInput.getText().toString();
                String body = mPostBodyInput.getText().toString();
                boolean isPic = false;
                boolean isRecipe = false;
                boolean isReview = false;
                //TODO set these variables from the addition of these items


                mPostTitleInput.getText().clear();
                mPostBodyInput.getText().clear();

                CollectionReference ref = mDatabase.collection("posts");
                Log.e(TAG, "Added new post ");
                // Saving the comment as a new document
                HashMap<String, Object> map = new HashMap<>();
                map.put("author", user.getUID());
                map.put("title", title);
                map.put("body", body);
                map.put("timestamp", FieldValue.serverTimestamp());
                map.put("isPic", isPic);
                map.put("isRecipe", isRecipe);
                map.put("isReview", isReview);
                if(isPic){

                }
                if(isRecipe){
                    if(isReview){

                    }
                }
                // Saving default user to Firebase Firestore database
                ref.add(map);


            }
        });
        mPostRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mPostReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mPostPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                        .into(mUploadedImage);
                mUploadedImage.setVisibility(View.VISIBLE);

//                try {
//                    uploadImage(mImageUri); // Attempt to upload the image in storage to Firebase.
//                } catch (ImageException e) {
//                    e.printStackTrace();
//                    return;
//                }
            }else{
                mPostPic.setChecked(false);
            }
        }
    }

    /** Image checker.
     *  Used to reduce wait times for the user when uploading on a slow network.
     *  Also limits the data that has to be stored and queried from Firebase.
     *  @param uri - The unique uri of the image file location from the users storage.
     *  @throws ImageException - Throws if the image file is too large or the format isn't a supported image format.
     */
    private void checkImage(Uri uri) throws ImageException {

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

    /** Upload the image to Firebase. Initiated before saving preferences if the format and size is supported to give some time for the app to upload the image.
     *  Does not update the users reference to the image. This is updated after saving the users preferences.
     *
     * @param uri - The unique uri of the image file location from the users storage.
     * @throws ImageException - Thrown if URL cannot be retrieved. This will only fail if there is a reference to a blank file.
     *  In normal operation this shouldn't happen.
     */
    private void uploadImage(Uri uri) throws ImageException {
        String extension = getExtension(this.getContext(), uri);

        checkImage(uri); // Check the image doesn't throw any exceptions

        IMAGE_IS_UPLOADING = true; // State that the image is still uploading and therefore we shouldn't save a reference on firebase to it yet.

        /*  Create a unique reference of the format. 'image/profile/[UNIQUE UID]/profile_image.[EXTENSION].
            Whereby [UNIQUE UID] = the Unique id of the user, [EXTENSION] = file image extension. E.g. .jpg,.png. */
        StorageReference mImageStorage = mStorageReference.child("images/posts/" + user.getUID() + "/IMAGEID" + extension); //todo input image id

        //  Check if the upload fails
        mImageStorage.putFile(uri).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to upload profile image.", Toast.LENGTH_SHORT).show()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mImageStorage.getDownloadUrl().addOnSuccessListener(locationUri -> { // Successful upload.
                    // TODO set image URL for post - only save when uploading post
                    //mUserProfile.setImageURL(locationUri.toString()); // Update the UserInfoPrivate class with this new image URL.
                    IMAGE_IS_UPLOADING = false; // State we have finished uploading (a reference exists).
                }).addOnFailureListener(e -> {
                    throw new RuntimeException("Unable to grab image URL from Firebase for image URL being uploaded currently. This shouldn't happen.");
                });
            }
        });
    }

    /**
     * On click of a recipe a new recipe info fragment is opened and the document is sent through
     * This saves on downloading the data again from the database
     */
    public void itemSelected(Map<String, Object> document) {

        //Takes ingredient array from snap shot and reformats before being passed through to fragment
//        ArrayList<String> ingredientArray = new ArrayList<>();
//
//        Map<String, Map<String, Object>> test = (Map) document.getData().get("Ingredients");
//        Iterator hmIterator = test.entrySet().iterator();
//
//        while (hmIterator.hasNext()) {
//            Map.Entry mapElement = (Map.Entry) hmIterator.next();
//            String string = mapElement.getKey().toString() + ": " + mapElement.getValue().toString();
//            ingredientArray.add(string);
//        }
//
//        //Creating a bundle so all data needed from firestore query snapshot can be passed through into fragment class
//        Bundle bundle = new Bundle();
//        bundle.putStringArrayList("ingredientList", ingredientArray);
//        bundle.putString("recipeID", document.getId());
//        bundle.putString("xmlURL", document.get("xml_url").toString());
//        bundle.putString("recipeTitle", document.get("Name").toString());
//        bundle.putString("rating", document.get("score").toString());
//        bundle.putString("imageURL", document.get("imageURL").toString());
//        bundle.putString("recipeDescription", document.get("Description").toString());
//        bundle.putString("chefName", document.get("Chef").toString());
//        bundle.putSerializable("user", user);
//
//
//        RecipeInfoFragment recipeDialogFragment = new RecipeInfoFragment();
//        recipeDialogFragment.setArguments(bundle);
//        recipeDialogFragment.show(getFragmentManager(), "Show recipe dialog fragment");
    }
}