package com.group4sweng.scranplan.Social;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.group4sweng.scranplan.RecipeInfo.RecipeInfoFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchRecyclerAdapter;

import java.util.ArrayList;
import java.util.Iterator;
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
public class ProfileRecipes extends Fragment {

    final String TAG = "Profile Recipes";
    int numberOfColumns = 2;
    int dataLim = 6;

    NestedScrollView profileScrollView;
    PublicProfile profile;

    public ProfileRecipes(String UID){
        searchUID = UID;
    }
    private String searchUID;

    LoadingDialog loadingDialog;

    //Score scroll info
    List<SearchRecyclerAdapter.SearchRecipePreviewData> data;
    private DocumentSnapshot lastVisible;
    private boolean isLastItemReached = false;

    View mainView;
    //User information
    private com.group4sweng.scranplan.UserInfo.UserInfoPrivate user;


    Query query;



    // Database objects for accessing recipes
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mColRef = mDatabase.collection("recipes");





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

        View view = inflater.inflate(R.layout.profile_recipes, container, false);
        mainView = view;
        loadingDialog = new LoadingDialog(getActivity());

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        user = (com.group4sweng.scranplan.UserInfo.UserInfoPrivate) requireActivity().getIntent().getSerializableExtra("user");


        Log.e(TAG, "IN TO THE FRAGMENT FOR PROFILE POSTS");

        // Checks users details have been provided
        if(user != null){
            //check if user is allowed to see recipes
            query = mColRef.whereEqualTo("Chef", searchUID).limit(dataLim);


        }else{
            // If scroll views fail due to no user, this error is reported
            Log.e(TAG, "ERROR: Loading social feed - We were unable to find user.");
        }
        addPosts(view);
        return view;
    }

    private void addPosts(View view){
        final RecyclerView recyclerView = view.findViewById(R.id.recipesList);
        //recyclerView.setNestedScrollingEnabled(false);
        // Set out the layout of this horizontal view
        RecyclerView.LayoutManager rManager = new GridLayoutManager(getContext(), numberOfColumns);
        recyclerView.setLayoutManager(rManager);
        //recyclerView.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels));
        // Array to score downloaded data
        data = new ArrayList<>();
        final RecyclerView.Adapter rAdapter = new SearchRecyclerAdapter(ProfileRecipes.this, data);
        recyclerView.setAdapter(rAdapter);

        // Check if query is found
        if (query != null) {
            Log.e(TAG, "User is searching the following query: " + query.toString());

            // Once the data has been returned, dataset populated and components build
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        // For each document a new recipe preview view is generated
                        for (DocumentSnapshot document : task.getResult()) {
                            data.add(new SearchRecyclerAdapter.SearchRecipePreviewData(
                                    document,
                                    document.getId(),
                                    document.get("Name").toString(),
                                    document.get("Description").toString(),
                                    document.get("imageURL").toString()
                            ));
                        }
                        rAdapter.notifyDataSetChanged();
                        // Set the last document as last user can see
                        if(task.getResult().size() != 0){
                            lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        }else{
                            // If no data returned, user notified
                            isLastItemReached = true;
                            data.add(new SearchRecyclerAdapter.SearchRecipePreviewData(
                                    null,
                                    null,
                                    "No more results",
                                    "We have checked all over and there is nothing more to be found!",
                                    null
                            ));
                        }
                        NestedScrollView.OnScrollChangeListener onScrollListener = new NestedScrollView.OnScrollChangeListener() {
                            @Override
                            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                                if (v.getChildAt(v.getChildCount() - 1) != null) {
                                    if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                                            scrollY > oldScrollY) {

                                        GridLayoutManager linearLayoutManager = ((GridLayoutManager) recyclerView.getLayoutManager());
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
                                                            data.add(new SearchRecyclerAdapter.SearchRecipePreviewData(
                                                                    d,
                                                                    d.getId(),
                                                                    d.get("Name").toString(),
                                                                    d.get("Description").toString(),
                                                                    d.get("imageURL").toString()
                                                            ));
                                                        }
                                                        if(isLastItemReached){
                                                            data.add(new SearchRecyclerAdapter.SearchRecipePreviewData(
                                                                    null,
                                                                    null,
                                                                    "No more results",
                                                                    "We have checked all over and there is nothing more to be found!",
                                                                    null
                                                            ));
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
     * On click of a recipe a new recipe info fragment is opened and the document is sent through
     * This saves on downloading the data again from the database
     */
    public void recipeSelected(DocumentSnapshot document) {

        //Takes ingredient array from snap shot and reformats before being passed through to fragment
        ArrayList<String> ingredientArray = new ArrayList<>();

        Map<String, Map<String, Object>> test = (Map) document.getData().get("Ingredients");
        Iterator hmIterator = test.entrySet().iterator();

        while (hmIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry) hmIterator.next();
            String string = mapElement.getKey().toString() + ": " + mapElement.getValue().toString();
            ingredientArray.add(string);
        }

        //Creating a bundle so all data needed from firestore query snapshot can be passed through into fragment class
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("ingredientList", ingredientArray);
        bundle.putString("recipeID", document.getId());
        bundle.putString("xmlURL", document.get("xml_url").toString());
        bundle.putString("recipeTitle", document.get("Name").toString());
        bundle.putString("rating", document.get("score").toString());
        bundle.putString("imageURL", document.get("imageURL").toString());
        bundle.putString("recipeDescription", document.get("Description").toString());
        bundle.putString("chefName", document.get("Chef").toString());
        bundle.putSerializable("user", user);

        ArrayList faves = (ArrayList) document.get("favourite");
        bundle.putBoolean("isFav", faves.contains(user.getUID()));
        bundle.putString("fridgeDays", document.get("fridge").toString());
        bundle.putString("peopleServes", document.get("serves").toString());
        bundle.putString("reheat", document.get("reheat").toString());
        bundle.putBoolean("canFreeze", document.getBoolean("freezer"));
        bundle.putBoolean("noEggs", document.getBoolean("noEggs"));
        bundle.putBoolean("noMilk", document.getBoolean("noMilk"));
        bundle.putBoolean("noNuts", document.getBoolean("noNuts"));
        bundle.putBoolean("noShellfish", document.getBoolean("noShellfish"));
        bundle.putBoolean("noSoy", document.getBoolean("noSoy"));
        bundle.putBoolean("noWheat", document.getBoolean("noWheat"));
        bundle.putBoolean("pescatarian", document.getBoolean("pescatarian"));
        bundle.putBoolean("vegan", document.getBoolean("vegan"));
        bundle.putBoolean("vegetarian", document.getBoolean("vegetarian"));

        openRecipeInfo(bundle);
    }

    protected void openRecipeInfo(Bundle bundle) {
        bundle.putBoolean("planner", false);
        RecipeInfoFragment recipeInfoFragment = new RecipeInfoFragment();
        recipeInfoFragment.setArguments(bundle);
        recipeInfoFragment.show(getFragmentManager(), "Show recipe dialog fragment");
    }
}