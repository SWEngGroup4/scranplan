package com.group4sweng.scranplan.MealPlanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
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
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.RecipeInfo.RecipeInfoFragment;
import com.group4sweng.scranplan.SearchFunctions.MealTimescaleRecyclerAdapter;
import com.group4sweng.scranplan.SearchFunctions.MealTimescaleQueries;
import com.group4sweng.scranplan.SearchFunctions.MealTimescaleRecyclerAdapter;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MealTimescaleFragment extends Fragment {

    UserInfoPrivate user;
    public MealTimescaleFragment(UserInfoPrivate userSent){
        user = userSent;
    }

    private String recipeID;
    private String imageURL;
    private DocumentSnapshot document;

    List<MealTimescaleRecyclerAdapter.MealTimescaleRecipePreviewData> dataBreakfast;
    private DocumentSnapshot lastVisibleBreakfast;
    private boolean isScrollingBreakfast = false;
    private boolean isLastItemReachedBreakfast = false;

    List<MealTimescaleRecyclerAdapter.MealTimescaleRecipePreviewData> dataLunch;
    private DocumentSnapshot lastVisibleLunch;
    private boolean isScrollingLunch = false;
    private boolean isLastItemReachedLunch = false;

    List<MealTimescaleRecyclerAdapter.MealTimescaleRecipePreviewData> dataDinner;
    private DocumentSnapshot lastVisibleDinner;
    private boolean isScrollingDinner = false;
    private boolean isLastItemReachedDinner = false;

    List<MealTimescaleRecyclerAdapter.MealTimescaleRecipePreviewData> dataFave;
    private DocumentSnapshot lastVisibleFave;
    private boolean isScrollingFave = false;
    private boolean isLastItemReachedFave = false;

    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mColRef = mDatabase.collection("recipes");

    private Bundle mBundle;
    private Boolean planner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null)
            planner = getArguments().getBoolean("planner");
        else planner = false;

        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        // Grabs screen size for % layout TODO - change to density pixels + NullPointerException check
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();

        // Procedurally fills topLayout with imageButton content
        LinearLayout topLayout = view.findViewById(R.id.topLayout);

        final int scrollViewSize = 5;

        if(user != null) {
            MealTimescaleQueries horizontalScrollQueries = new MealTimescaleQueries(user);

            /* Adding the save view as score but with breakfast as a new query
            /  This has been done in the same manner but as there are too many variables to track
            /  this is not workable in any kind of loop. */
            final RecyclerView recyclerViewBreakfast = new RecyclerView(view.getContext());
            RecyclerView.LayoutManager rBreakfast = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerViewBreakfast.setLayoutManager(rBreakfast);
            recyclerViewBreakfast.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels / scrollViewSize));
            dataBreakfast = new ArrayList<>();
            final RecyclerView.Adapter rAdapterBreakfast = new MealTimescaleRecyclerAdapter(MealTimescaleFragment.this, dataBreakfast);
            recyclerViewBreakfast.setAdapter(rAdapterBreakfast);
            final Query queryBreakfast = (Query) horizontalScrollQueries.getQueries().get("breakfast");
            if (queryBreakfast != null) {
                Log.e(TAG, "User is searching the following query: " + queryBreakfast.toString());

                TextView textView = new TextView(view.getContext());
                String testString = "Breakfast";
                textView.setTextSize(25);
                textView.setPadding(20, 5, 5, 5);
                textView.setTextColor(Color.WHITE);
                textView.setShadowLayer(4, 0, 0, Color.BLACK);
                textView.setText(testString);

                queryBreakfast
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                dataBreakfast.add(new MealTimescaleRecyclerAdapter.MealTimescaleRecipePreviewData(
                                        document,
                                        document.getId(),
                                        document.get("Name").toString(),
                                        Float.valueOf(document.get("score").toString()),
                                        document.get("imageURL").toString()
                                ));
                            }
                            rAdapterBreakfast.notifyDataSetChanged();
                            if (task.getResult().size() != 0) {
                                lastVisibleBreakfast = task.getResult().getDocuments().get(task.getResult().size() - 1);
                            } else {
                                isLastItemReachedBreakfast = true;
                            }

                            RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged(recyclerView, newState);
                                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                        isScrollingBreakfast = true;
                                    }
                                }

                                @Override
                                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);

                                    LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                    int visibleItemCount = linearLayoutManager.getChildCount();
                                    int totalItemCount = linearLayoutManager.getItemCount();

                                    if (isScrollingBreakfast && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReachedBreakfast) {
                                        isScrollingBreakfast = false;
                                        Query nextQuery = queryBreakfast.startAfter(lastVisibleBreakfast);
                                        nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                                if (t.isSuccessful()) {
                                                    for (DocumentSnapshot d : t.getResult()) {
                                                        dataBreakfast.add(new MealTimescaleRecyclerAdapter.MealTimescaleRecipePreviewData(
                                                                d,
                                                                d.getId(),
                                                                d.get("Name").toString(),
                                                                Float.valueOf(d.get("score").toString()),
                                                                d.get("imageURL").toString()
                                                        ));
                                                    }
                                                    if (isLastItemReachedBreakfast) {
                                                        // Add end here
                                                    }
                                                    rAdapterBreakfast.notifyDataSetChanged();
                                                    if (t.getResult().size() != 0) {
                                                        lastVisibleBreakfast = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                                    }

                                                    if (t.getResult().size() < 5) {
                                                        isLastItemReachedBreakfast = true;
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            };
                            recyclerViewBreakfast.addOnScrollListener(onScrollListener);
                        }
                    }
                });
                topLayout.addView(textView);
                topLayout.addView(recyclerViewBreakfast);
                Log.e(TAG, "Breakfast horizontal view added");
            }

             /* Adding the save view as score but with lunch as a new query
            /  This has been done in the same manner but as there are too many variables to track
            /  this is not workable in any kind of loop. */
            final RecyclerView recyclerViewLunch = new RecyclerView(view.getContext());
            RecyclerView.LayoutManager rType = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerViewLunch.setLayoutManager(rType);
            recyclerViewLunch.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels / scrollViewSize));
            dataLunch = new ArrayList<>();
            final MealTimescaleRecyclerAdapter rAdapterLunch = new MealTimescaleRecyclerAdapter(MealTimescaleFragment.this, dataLunch);
            recyclerViewLunch.setAdapter(rAdapterLunch);
            final Query queryLunch = (Query) horizontalScrollQueries.getQueries().get("lunch");
            if (queryLunch != null) {
                Log.e(TAG, "User is searching the following query: " + queryLunch.toString());

                TextView textView = new TextView(view.getContext());
                String testString = "Lunch";
                textView.setTextSize(25);
                textView.setPadding(20, 5, 5, 5);
                textView.setTextColor(Color.WHITE);
                textView.setShadowLayer(4, 0, 0, Color.BLACK);
                textView.setText(testString);

                queryLunch
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                dataLunch.add(new MealTimescaleRecyclerAdapter.MealTimescaleRecipePreviewData(
                                        document,
                                        document.getId(),
                                        document.get("Name").toString(),
                                        Float.valueOf(document.get("score").toString()),
                                        document.get("imageURL").toString()
                                ));
                            }
                            rAdapterLunch.notifyDataSetChanged();
                            if (task.getResult().size() != 0) {
                                lastVisibleLunch = task.getResult().getDocuments().get(task.getResult().size() - 1);
                            } else {
                                isLastItemReachedLunch = true;
                            }

                            RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged(recyclerView, newState);
                                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                        isScrollingLunch = true;
                                    }
                                }

                                @Override
                                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);

                                    LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                    int visibleItemCount = linearLayoutManager.getChildCount();
                                    int totalItemCount = linearLayoutManager.getItemCount();

                                    if (isScrollingLunch && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReachedBreakfast) {
                                        isScrollingLunch = false;
                                        Query nextQuery = queryLunch.startAfter(lastVisibleLunch);
                                        nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                                if (t.isSuccessful()) {
                                                    for (DocumentSnapshot d : t.getResult()) {
                                                        dataBreakfast.add(new MealTimescaleRecyclerAdapter.MealTimescaleRecipePreviewData(
                                                                d,
                                                                d.getId(),
                                                                d.get("Name").toString(),
                                                                Float.valueOf(d.get("score").toString()),
                                                                d.get("imageURL").toString()
                                                        ));
                                                    }
                                                    if (isLastItemReachedLunch) {
                                                        // Add end here
                                                    }
                                                    rAdapterLunch.notifyDataSetChanged();
                                                    if (t.getResult().size() != 0) {
                                                        lastVisibleLunch = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                                    }

                                                    if (t.getResult().size() < 10) {
                                                        isLastItemReachedLunch = true;
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            };
                            recyclerViewLunch.addOnScrollListener(onScrollListener);
                        }
                    }
                });
                topLayout.addView(textView);
                topLayout.addView(recyclerViewLunch);
                Log.e(TAG, "Breakfast horizontal view added");
            }

            /* Adding the save view as score but with dinner as a new query
            /  This has been one in the same manner but as there are too many variables to track
            /  this is not workable in any kind of loop. */
            final RecyclerView recyclerViewDinner = new RecyclerView(view.getContext());
            RecyclerView.LayoutManager rDinner = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerViewDinner.setLayoutManager(rDinner);
            recyclerViewDinner.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels / scrollViewSize));
            dataDinner = new ArrayList<>();
            final RecyclerView.Adapter rAdapterDinner = new MealTimescaleRecyclerAdapter(MealTimescaleFragment.this, dataDinner);
            recyclerViewDinner.setAdapter(rAdapterDinner);
            final Query queryDinner = (Query) horizontalScrollQueries.getQueries().get("dinner");
            if (queryDinner != null) {
                Log.e(TAG, "User is searching the following query: " + queryDinner.toString());

                TextView textView = new TextView(view.getContext());
                String testString = "Dinner";
                textView.setTextSize(25);
                textView.setPadding(20, 5, 5, 5);
                textView.setTextColor(Color.WHITE);
                textView.setShadowLayer(4, 0, 0, Color.BLACK);
                textView.setText(testString);

                queryDinner
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                dataDinner.add(new MealTimescaleRecyclerAdapter.MealTimescaleRecipePreviewData(
                                        document,
                                        document.getId(),
                                        document.get("Name").toString(),
                                        Float.valueOf(document.get("score").toString()),
                                        document.get("imageURL").toString()
                                ));
                            }
                            rAdapterDinner.notifyDataSetChanged();
                            if (task.getResult().size() != 0) {
                                lastVisibleDinner = task.getResult().getDocuments().get(task.getResult().size() - 1);
                            } else {
                                isLastItemReachedDinner = true;
                            }

                            RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged(recyclerView, newState);
                                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                        isScrollingDinner = true;
                                    }
                                }

                                @Override
                                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);

                                    LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                    int visibleItemCount = linearLayoutManager.getChildCount();
                                    int totalItemCount = linearLayoutManager.getItemCount();

                                    if (isScrollingDinner && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReachedDinner) {
                                        isScrollingDinner = false;
                                        Query nextQuery = queryDinner.startAfter(lastVisibleDinner);
                                        nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                                if (t.isSuccessful()) {
                                                    for (DocumentSnapshot d : t.getResult()) {
                                                        dataDinner.add(new MealTimescaleRecyclerAdapter.MealTimescaleRecipePreviewData(
                                                                d,
                                                                d.getId(),
                                                                d.get("Name").toString(),
                                                                Float.valueOf(d.get("score").toString()),
                                                                d.get("imageURL").toString()
                                                        ));
                                                    }
                                                    if (isLastItemReachedDinner) {
                                                        // Add end here
                                                    }
                                                    rAdapterDinner.notifyDataSetChanged();
                                                    if (t.getResult().size() != 0) {
                                                        lastVisibleDinner = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                                    }

                                                    if (t.getResult().size() < 5) {
                                                        isLastItemReachedDinner = true;
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            };
                            recyclerViewDinner.addOnScrollListener(onScrollListener);
                        }
                    }
                });
                topLayout.addView(textView);
                topLayout.addView(recyclerViewDinner);
                Log.e(TAG, "Dinner horizontal view added");
            }


            /* Adding the save view as score but with user favourite recipes as a new query
            /  This has been done in the same manner but as there are too many variables to track
            /  this is not workable in any kind of loop. */
            final RecyclerView recyclerViewFave = new RecyclerView(view.getContext());
            RecyclerView.LayoutManager rManagerFave = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerViewFave.setLayoutManager(rManagerFave);
            recyclerViewFave.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels / scrollViewSize));
            dataFave = new ArrayList<>();
            final RecyclerView.Adapter rAdapterFave = new MealTimescaleRecyclerAdapter(MealTimescaleFragment.this, dataFave);
            recyclerViewFave.setAdapter(rAdapterFave);
            final Query queryFave = (Query) horizontalScrollQueries.getQueries().get("favourite");
            if (queryFave != null) {
                Log.e(TAG, "User is searching the following query: " + queryFave.toString());

                TextView textView = new TextView(view.getContext());
                String testString = "My favourites";
                textView.setTextSize(25);
                textView.setPadding(20, 5, 5, 5);
                textView.setTextColor(Color.WHITE);
                textView.setShadowLayer(4, 0, 0, Color.BLACK);
                textView.setText(testString);

                queryFave
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                dataFave.add(new MealTimescaleRecyclerAdapter.MealTimescaleRecipePreviewData(
                                        document,
                                        document.getId(),
                                        document.get("Name").toString(),
                                        Float.valueOf(document.get("score").toString()),
                                        document.get("imageURL").toString()
                                ));
                            }
                            rAdapterFave.notifyDataSetChanged();
                            if(task.getResult().size() != 0){
                                lastVisibleFave = task.getResult().getDocuments().get(task.getResult().size() - 1);
                            }else{
                                isLastItemReachedFave = true;
                            }

                            RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged(recyclerView, newState);
                                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                        isScrollingFave = true;
                                    }
                                }

                                @Override
                                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);

                                    LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                    int visibleItemCount = linearLayoutManager.getChildCount();
                                    int totalItemCount = linearLayoutManager.getItemCount();

                                    if (isScrollingFave && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReachedFave) {
                                        isScrollingFave = false;
                                        Query nextQuery = queryFave.startAfter(lastVisibleFave);
                                        nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                                if (t.isSuccessful()) {
                                                    for (DocumentSnapshot d : t.getResult()) {
                                                        dataFave.add(new MealTimescaleRecyclerAdapter.MealTimescaleRecipePreviewData(
                                                                d,
                                                                d.getId(),
                                                                d.get("Name").toString(),
                                                                Float.valueOf(d.get("score").toString()),
                                                                d.get("imageURL").toString()
                                                        ));
                                                    }
                                                    if(isLastItemReachedFave){
                                                        // Add end here
                                                    }
                                                    rAdapterFave.notifyDataSetChanged();
                                                    if (t.getResult().size() != 0) {
                                                        lastVisibleFave = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                                    }

                                                    if (t.getResult().size() < 10) {
                                                        isLastItemReachedFave = true;
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            };
                            recyclerViewFave.addOnScrollListener(onScrollListener);
                        }
                    }
                });
                topLayout.addView(textView);
                topLayout.addView(recyclerViewFave);
                Log.e(TAG, "Time horizontal view added");
            }
        }else{
            // If scroll views fail due to no user, this error is reported
            Log.e(TAG, "ERROR: Loading scroll views - We were unable to find user.");
        }
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
