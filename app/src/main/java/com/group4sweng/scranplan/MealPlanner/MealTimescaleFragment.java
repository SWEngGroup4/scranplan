package com.group4sweng.scranplan.MealPlanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.group4sweng.scranplan.Home;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.RecipeInfo.RecipeInfoFragment;
import com.group4sweng.scranplan.SearchFunctions.HomeQueries;
import com.group4sweng.scranplan.SearchFunctions.HomeRecyclerAdapter;
import com.group4sweng.scranplan.SearchFunctions.SearchListFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchPrefs;
import com.group4sweng.scranplan.SearchFunctions.SearchQuery;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MealTimescaleFragment extends Fragment implements QueryRequestCodes{
    UserInfoPrivate user;
    int requestCode;
    public MealTimescaleFragment(UserInfoPrivate userSent, int code){
        user = userSent;
        requestCode = code;
    }

    List<HomeRecyclerAdapter.HomeRecipePreviewData> dataMealTimescale;
    private DocumentSnapshot lastVisibleMealTimescale;
    private boolean isScrollingMealTimescale = false;
    private boolean isLastItemReachedMealTimescale = false;

    //Score scroll info
    List<HomeRecyclerAdapter.HomeRecipePreviewData> dataScore;
    private DocumentSnapshot lastVisibleScore;
    private boolean isScrollingScore = false;
    private boolean isLastItemReachedScore = false;


    //Votes scroll info
    List<HomeRecyclerAdapter.HomeRecipePreviewData> dataVotes;
    private DocumentSnapshot lastVisibleVotes;
    private boolean isScrollingVotes = false;
    private boolean isLastItemReachedVotes = false;

    //Timestamp scroll info
    List<HomeRecyclerAdapter.HomeRecipePreviewData> dataTime;
    private DocumentSnapshot lastVisibleTime;
    private boolean isScrollingTime = false;
    private boolean isLastItemReachedTime = false;

    //Favourites scroll info
    List<HomeRecyclerAdapter.HomeRecipePreviewData> dataFave;
    private DocumentSnapshot lastVisibleFave;
    private boolean isScrollingFave = false;
    private boolean isLastItemReachedFave = false;

    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mColRef = mDatabase.collection("recipes");

    private SearchView searchView;
    private MenuItem sortView;
    private SearchPrefs prefs;

    private Bundle mBundle;
    private Boolean planner;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void createQueryView(View view, int requestCode){
        final int scrollViewSize = 5;

        // Grabs screen size for % layout TODO - change to density pixels + NullPointerException check
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();

        // Procedurally fills topLayout with imageButton content
        LinearLayout topLayout = view.findViewById(R.id.topLayout);

        if (user != null) {

            /* Adding the save view as score but with breakfast, lunch and dinner as a new query
            /  This has been done in the same manner but as there are too many variables to track
            /  this is not workable in any kind of loop. */
            final RecyclerView recyclerViewMealTimescale = new RecyclerView(view.getContext());
            RecyclerView.LayoutManager rMealTimescale = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerViewMealTimescale.setLayoutManager(rMealTimescale);
            recyclerViewMealTimescale.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels / scrollViewSize));
            dataMealTimescale = new ArrayList<>();
            final RecyclerView.Adapter rAdapterMealTimescale = new HomeRecyclerAdapter(MealTimescaleFragment.this, dataMealTimescale);
            recyclerViewMealTimescale.setAdapter(rAdapterMealTimescale);

            HomeQueries horizontalScrollQueries = new HomeQueries(user);
            String mealTimescaleType = "breakfast";
            String mealTimescaleName = "Breakfast";

            switch (requestCode) {
                case BREAKFAST:
                    mealTimescaleType = "breakfast";
                    mealTimescaleName = "Breakfast";
                    break;
                case LUNCH:
                    mealTimescaleType = "lunch";
                    mealTimescaleName = "Lunch";
                    break;
                case DINNER:
                    mealTimescaleType = "dinner";
                    mealTimescaleName = "Dinner";
                    break;
            }

            final Query query = (Query) horizontalScrollQueries.getQueries().get(mealTimescaleType);
            Log.e(TAG, "User is searching the following query: " + query.toString());

            TextView textView = new TextView(view.getContext());
            textView.setTextSize(25);
            textView.setPadding(20, 5, 5, 5);
            textView.setTextColor(Color.WHITE);
            textView.setShadowLayer(4, 0, 0, Color.BLACK);
            textView.setText(mealTimescaleName);

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            dataMealTimescale.add(new HomeRecyclerAdapter.HomeRecipePreviewData(
                                    document,
                                    document.getId(),
                                    document.get("Name").toString(),
                                    Float.valueOf(document.get("score").toString()),
                                    document.get("imageURL").toString()
                            ));
                        }
                        rAdapterMealTimescale.notifyDataSetChanged();
                        if (task.getResult().size() != 0) {
                            lastVisibleMealTimescale = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        } else {
                            isLastItemReachedMealTimescale = true;
                        }

                        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                    isScrollingMealTimescale = true;
                                }
                            }

                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);

                                LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                int visibleItemCount = linearLayoutManager.getChildCount();
                                int totalItemCount = linearLayoutManager.getItemCount();

                                if (isScrollingMealTimescale && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReachedMealTimescale) {
                                    isScrollingMealTimescale = false;
                                    Query nextQuery = query.startAfter(lastVisibleMealTimescale);
                                    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                            if (t.isSuccessful()) {
                                                for (DocumentSnapshot d : t.getResult()) {
                                                    dataMealTimescale.add(new HomeRecyclerAdapter.HomeRecipePreviewData(
                                                            d,
                                                            d.getId(),
                                                            d.get("Name").toString(),
                                                            Float.valueOf(d.get("score").toString()),
                                                            d.get("imageURL").toString()
                                                    ));
                                                }
                                                if (isLastItemReachedMealTimescale) {
                                                    // Add end here
                                                }
                                                rAdapterMealTimescale.notifyDataSetChanged();
                                                if (t.getResult().size() != 0) {
                                                    lastVisibleMealTimescale = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                                }

                                                if (t.getResult().size() < 5) {
                                                    isLastItemReachedMealTimescale = true;
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        };
                        recyclerViewMealTimescale.addOnScrollListener(onScrollListener);
                    }
                }
            });
            topLayout.addView(textView);
            topLayout.addView(recyclerViewMealTimescale);
            Log.e(TAG, "Breakfast horizontal view added");
        } else {
            // If scroll views fail due to no user, this error is reported
            Log.e(TAG, "ERROR: Loading scroll views - We were unable to find user.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null)
            planner = getArguments().getBoolean("planner");
        else planner = false;

        Home home = (Home) getActivity();
        if (home != null) {
            searchView = home.getSearchView();
            sortView = home.getSortView();
            if (sortView != null) sortView.setVisible(true);
            if (searchView != null) searchView.setVisibility(View.VISIBLE);
            prefs = home.getSearchPrefs();
            if (searchView != null && prefs != null) {

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        // Search function
                        SearchQuery query = new SearchQuery(s, prefs);
                        SearchListFragment searchListFragment = new SearchListFragment(user);
                        searchListFragment.setValue(query.getQuery());
                        Log.e(TAG, "User opening search");
                        searchListFragment.show(getFragmentManager(), "search");
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });
            }
        }
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        createQueryView(view, requestCode);

        return view;
    }

    /**
     * On click of a recipe a new recipe info fragment is opened and the document is sent through
     * This saves on downloading the data again from the database
     */
    public void recipeSelected (DocumentSnapshot document){

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
        mBundle = new Bundle();
        mBundle.putStringArrayList("ingredientList", ingredientArray);
        mBundle.putString("recipeID", document.getId());
        mBundle.putString("xmlURL", document.get("xml_url").toString());
        mBundle.putString("recipeTitle", document.get("Name").toString());
        mBundle.putString("rating", document.get("score").toString());
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


        RecipeInfoFragment recipeDialogFragment = new RecipeInfoFragment();
        recipeDialogFragment.setArguments(mBundle);
        recipeDialogFragment.setTargetFragment(this, 1);
        recipeDialogFragment.show(getFragmentManager(), "Show recipe dialog fragment");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Intent i = new Intent();
            i.putExtras(mBundle);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
        }
    }
}
