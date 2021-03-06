package com.group4sweng.scranplan.SearchFunctions;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.PublicProfile;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.RecipeInfo.RecipeInfoFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchRecyclerAdapter.SearchRecipePreviewData;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Building search list infinite scroll fragment.
 * Author(s): LNewman, NBillis
 * (c) CoDev 2020
 *
 *
 *  This class takes a Firestore query and returns a fragment consisting of an infinite list,
 *  that goes of for as long as there is data delivered to the user in blocks of 5 items.
 *  Each item will be presented as just an image, title and description, users can click these images
 *  to get further information about a recipe
 */
public class SearchListFragment extends AppCompatDialogFragment {

    int numberOfColumns = 2;

    final String TAG = "SearchScreen";
    private static final String ALGOLIA_APP_ID = "WK13YORECK";
    private static final String ALGOLIA_SEARCH_API = "e6885a71639407fcec9e79f123dd5567";
    private static final String ALGOLIA_INDEX_NAME ="SCRANPLAN_RECIPES";

    UserInfoPrivate user;
    public SearchListFragment(UserInfoPrivate userSent){
        user = userSent;
    }

    private FirebaseFirestore mDatabase;
    protected Query query;
    private RecyclerView recyclerView;
    private DocumentSnapshot lastVisible;
    List<SearchRecipePreviewData> data;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;
    List<String> objectID;
    String searchIndex;
    String searchBy;
    private Bundle mBundle;
    private Boolean planner = false;


    /**
     * On the create of this view, the alert dialogue is build over the current page within the application
     * This will happen over the home page. This is then filled with the query results.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Building the list from XML page
        View view = inflater.inflate(R.layout.fragment_recipe_list, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        mDatabase = FirebaseFirestore.getInstance();
        data = new ArrayList<>();
        Client client = new Client(ALGOLIA_APP_ID, ALGOLIA_SEARCH_API);

        // Creating a list of the data and building all variables to add to recycler view
        final RecyclerView recyclerView = view.findViewById(R.id.recipeList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager rManager = new GridLayoutManager(getContext(), numberOfColumns);
        recyclerView.setLayoutManager(rManager);
        final RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder> rAdapter = new SearchRecyclerAdapter(SearchListFragment.this, data);
        recyclerView.setAdapter(rAdapter);

        // Completion Handler to parse the JSON incoming file and to display the results
        CompletionHandler completionHandler = new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                SearchJsonParser jsonParser = new SearchJsonParser(content);
                objectID = jsonParser.ParseAndReturnObjectId();
                List<DocumentSnapshot> documents = new ArrayList<DocumentSnapshot>();

                if(objectID != null){
                    for (String object: objectID) {
                        if (!searchIndex.equals("SCRANPLAN_USERS")) {
                            DocumentReference docRef = mDatabase.collection("recipes").document(object);
                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                // Gets the DocumentSnapshot from the document ID
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    DocumentSnapshot document = documentSnapshot;
                                    if (document.exists()) {
                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                        documents.add(document);
                                    } else {
                                        Log.d(TAG, "No such document");
                                    }

                                    // For each document a new recipe preview view is generated
                                    if (documents.size() == objectID.size()) {
                                        for (DocumentSnapshot documentSnap : documents) {
                                            data.add(new SearchRecipePreviewData(
                                                    documentSnap,
                                                    documentSnap.getId(),
                                                    documentSnap.get("Name").toString(),
                                                    documentSnap.get("Description").toString(),
                                                    documentSnap.get("imageURL").toString()
                                            ));
                                        }
                                        isLastItemReached = true;

                                        data.add(new SearchRecipePreviewData(
                                                null,
                                                null,
                                                "No more results",
                                                "We have checked all over and there is nothing more to be found!",
                                                null
                                        ));
                                        rAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }else if(searchIndex.equals("SCRANPLAN_USERS")){
                            DocumentReference docRef = mDatabase.collection("users").document(object);
                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                // Gets the DocumentSnapshot from the document ID
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    DocumentSnapshot document = documentSnapshot;
                                    if (document.exists()) {
                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                        documents.add(document);
                                    } else {
                                        Log.d(TAG, "No such document");
                                    }

                                    // For each document a new recipe preview view is generated
                                    if (documents.size() == objectID.size()) {
                                        for (DocumentSnapshot documentSnap : documents) {
                                            // doesn't display own profile
                                            if(!Objects.equals(documentSnap.get("UID"), user.getUID())){
                                            String displayName = null;
                                            String about = null;
                                            String imageUrl = null;

                                            if(documentSnap.get("displayName") != null){
                                                displayName = documentSnap.get("displayName").toString();
                                            }
                                            if(documentSnap.get("about") != null){
                                                about = documentSnap.get("about").toString();
                                            }
                                            if(documentSnap.get("imageURL") != null){
                                               imageUrl = documentSnap.get("imageURL").toString();
                                            }
                                            data.add(new SearchRecipePreviewData(
                                                    documentSnap,
                                                    documentSnap.getId(),
                                                    displayName,
                                                    about,
                                                    imageUrl
                                            ));
                                        }}
                                        isLastItemReached = true;

                                        data.add(new SearchRecipePreviewData(
                                                null,
                                                null,
                                                "No more results",
                                                "We have checked all over and there is nothing more to be found!",
                                                null
                                        ));
                                        rAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
                }
                // Displays no more results when no recipes are found
                if(objectID == null || objectID.size() == 0){
                    data.add(new SearchRecipePreviewData(
                            null,
                            null,
                            "No results found",
                            "We have checked all over and there is nothing to be found!",
                            null
                    ));
                    rAdapter.notifyDataSetChanged();
                }
            }
        };


        // Check if query is found
        if (query != null && searchIndex != null) {
            Log.e(TAG, "User is searching the following query: " + query.toString());

            Index scoreIndex = client.getIndex(searchIndex);
            //Search for the Query using Algolia
            scoreIndex.searchAsync(query,completionHandler);

            // check if user has scrolled through the view
            RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        isScrolling = true;
                    }
                }
            };
            recyclerView.addOnScrollListener(onScrollListener);
        }
        return view;
    }
    // Setting the query value
    public void setValue(Query sentQuery) {
        this.query = sentQuery;
    }

    public void setIndex(String index){
        this.searchIndex = index;
    }

    // Set the layout of the new window
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }


    public void profileSelected(DocumentSnapshot document){
        Log.e("RECIPE","Clicked open profile!");
        Intent intentProfile = new Intent(getContext(), PublicProfile.class);
        intentProfile.putExtra("UID", (String) document.get("UID"));
        intentProfile.putExtra("user", this.user);
        startActivity(intentProfile);
    }

    /**
     * On click of a recipe a new recipe info fragment is opened and the document is sent through
     * This saves on downloading the data again from the database
     */
    public void recipeSelected(DocumentSnapshot document) {

        //Takes ingredient array from snap shot and reformats before being passed through to fragment
        ArrayList<String> ingredientArray = new ArrayList<>();
        HashMap<String, String> ingredientHashMap = (HashMap<String, String>) document.getData().get("Ingredients");
        HashMap<String, Double> ratingMap =  (HashMap<String, Double>) document.getData().get("rating");

        //Creating a bundle so all data needed from firestore query snapshot can be passed through into fragment class
        mBundle = new Bundle();
        mBundle.putSerializable("ingredientHashMap", ingredientHashMap);
        mBundle.putStringArrayList("ingredientList", ingredientArray);
        mBundle.putString("recipeID", document.getId());
        mBundle.putString("xmlURL", document.get("xml_url").toString());
        mBundle.putString("recipeTitle", document.get("Name").toString());
        mBundle.putSerializable("ratingMap", ratingMap);
        mBundle.putString("imageURL", document.get("imageURL").toString());
        mBundle.putString("recipeDescription", document.get("Description").toString());
        mBundle.putString("chefName", document.get("Chef").toString());
        mBundle.putBoolean("planner", planner);
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

        ArrayList faves = (ArrayList) document.get("favourite");
        mBundle.putBoolean("isFav", faves.contains(user.getUID().hashCode()));

        openRecipeInfo(mBundle);
    }

    protected void openRecipeInfo(Bundle bundle) {
        bundle.putBoolean("planner", false);
        RecipeInfoFragment recipeInfoFragment = new RecipeInfoFragment();
        recipeInfoFragment.setArguments(bundle);
        recipeInfoFragment.show(getFragmentManager(), "Show recipe dialog fragment");
    }


}