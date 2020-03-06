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
import com.group4sweng.scranplan.RecipeInfoFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchRecyclerAdapter.SearchRecipePreviewData;

import java.util.ArrayList;
import java.util.List;

public class SearchListFragment extends AppCompatDialogFragment {

    final String TAG = "SearchScreen";

    private FirebaseFirestore mDatabase;

    protected SearchQuery query;
    private RecyclerView recyclerView;
    private DocumentSnapshot lastVisible;
    List<SearchRecipePreviewData> data;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        mDatabase = FirebaseFirestore.getInstance();
        data = new ArrayList<>();

        final RecyclerView recyclerView = view.findViewById(R.id.recipeList);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager rManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(rManager);


        final RecyclerView.Adapter rAdapter = new SearchRecyclerAdapter(SearchListFragment.this, data);
        recyclerView.setAdapter(rAdapter);

        if (query.getQuery() != null) {
            Log.e(TAG, "User is searching the following query: " + query.toString());

            query.getQuery()
//            mDatabase.collection("recipes").whereArrayContains("listIngredients", "bacon").orderBy("score", Query.Direction.DESCENDING).limit(5)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            data.add(new SearchRecipePreviewData(
                                    document.getId(),
                                    document.get("Name").toString(),
                                    document.get("Description").toString(),
                                    document.get("imageURL").toString()
                            ));
                        }
                        rAdapter.notifyDataSetChanged();
                        if(task.getResult().size() != 0){
                            lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        }else{
                            isLastItemReached = true;
                            data.add(new SearchRecipePreviewData(
                                    null,
                                    "No more results",
                                    "We have checked all over and there is nothing more to be found!",
                                    null
                            ));
                        }

                        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                    isScrolling = true;
                                }
                            }

                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);

                                LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                int visibleItemCount = linearLayoutManager.getChildCount();
                                int totalItemCount = linearLayoutManager.getItemCount();

                                if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                                    isScrolling = false;
                                    Query nextQuery = query.getQuery().startAfter(lastVisible);
                                    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                            if (t.isSuccessful()) {
                                                for (DocumentSnapshot d : t.getResult()) {
                                                    data.add(new SearchRecipePreviewData(
                                                            d.getId(),
                                                            d.get("Name").toString(),
                                                            d.get("Description").toString(),
                                                            d.get("imageURL").toString()
                                                    ));
                                                }
                                                if(isLastItemReached){
                                                    data.add(new SearchRecipePreviewData(
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

    public void setValue(SearchQuery sentQuery) {
        this.query = sentQuery;
    }

    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }



    public void recipeSelected(String recipeID) {
        RecipeInfoFragment recipeDialogFragment = new RecipeInfoFragment();
        recipeDialogFragment.show(getFragmentManager(), "Show recipe dialog fragment");
    }
}