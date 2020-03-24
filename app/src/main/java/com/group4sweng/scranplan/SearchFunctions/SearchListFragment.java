package com.group4sweng.scranplan.SearchFunctions;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.RecipeInfo.RecipeInfoFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchRecyclerAdapter.SearchRecipePreviewData;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  This class takes a Firestore query and returns a fragment consisting of an infinite list,
 *  that goes of for as long as there is data delivered to the user in blocks of 5 items.
 *  Each item will be presented as just an image, title and description, users can click these images
 *  to get further information about a recipe
 */
public class SearchListFragment extends AppCompatDialogFragment {

    final String TAG = "SearchScreen";

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

        // Creating a list of the data and building all variables to add to recycler view
        final RecyclerView recyclerView = view.findViewById(R.id.recipeList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager rManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(rManager);
        final RecyclerView.Adapter rAdapter = new SearchRecyclerAdapter(SearchListFragment.this, data);
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
                            data.add(new SearchRecipePreviewData(
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
                            data.add(new SearchRecipePreviewData(
                                    null,
                                    null,
                                    "No more results",
                                    "We have checked all over and there is nothing more to be found!",
                                    null
                            ));
                        }
                        // check if user has scrolled through the view
                        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                    isScrolling = true;
                                }
                            }
                            // If user is scrolling and has reached the end, more data is loaded
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                // Checking if user is at the end
                                LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                int visibleItemCount = linearLayoutManager.getChildCount();
                                int totalItemCount = linearLayoutManager.getItemCount();
                                // If found to have reached end, more data is requested from the server in the same manner
                                if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                                    isScrolling = false;
                                    Query nextQuery = query.startAfter(lastVisible);
                                    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                            if (t.isSuccessful()) {
                                                for (DocumentSnapshot d : t.getResult()) {
                                                    data.add(new SearchRecipePreviewData(
                                                            d,
                                                            d.getId(),
                                                            d.get("Name").toString(),
                                                            d.get("Description").toString(),
                                                            d.get("imageURL").toString()
                                                    ));
                                                }
                                                if(isLastItemReached){
                                                    data.add(new SearchRecipePreviewData(
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
                        };
                        recyclerView.addOnScrollListener(onScrollListener);
                    }
                }
            });
        }
        return view;
    }
    // Setting the query value
    public void setValue(Query sentQuery) {
        this.query = sentQuery;
    }
    // Set the layout of the new window
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
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