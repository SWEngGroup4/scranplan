package com.group4sweng.scranplan.MealPlanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.Home;
import com.group4sweng.scranplan.PlannerInfoFragment;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.RecipeFragment;
import com.group4sweng.scranplan.RecipeInfo.RecipeIngredientFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchPrefs;
import com.group4sweng.scranplan.SearchFunctions.SearchQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlannerFragment extends Fragment {

    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mUserRef = mDatabase.collection("users");

    private List<HashMap<String, Object>> plannerList = new ArrayList<>();
    private List<String> days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
    private ImageButton currentSelection;

    private FragmentTransaction fragmentTransaction;

    private com.group4sweng.scranplan.UserInfo.UserInfoPrivate mUser;
    private RecipeFragment recipeFragment;

    private SearchView searchView;
    private SearchPrefs prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_planner, container, false);

        mUser = (com.group4sweng.scranplan.UserInfo.UserInfoPrivate) getActivity().getIntent().getSerializableExtra("user");
        plannerList = mUser.getMealPlanner();

        Home home = (Home) getActivity();
        searchView = home.getSearchView();
        searchView.setVisibility(View.INVISIBLE);
        prefs = home.getSearchPrefs();
        setSearch(searchView);

        LinearLayout topView = view.findViewById(R.id.plannerLinearLayout);
        for (int i = 0; i < 7; i++) {
            TextView textView = new TextView(view.getContext());
            textView.setText(days.get(i));

            LinearLayout linearLayout = new LinearLayout(view.getContext());
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setWeightSum(3);

            for (int j = 0; j < 3; j++) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f
                );
                final ImageButton imageButton = new ImageButton(view.getContext());
                final Integer id = (i*3) + j;
                imageButton.setId(id);
                imageButton.setLayoutParams(params);
                imageButton.setAdjustViewBounds(true);
                imageButton.setPadding(10,10,10,10);
                imageButton.setBackground(null);

                imageButton.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        defaultButton(imageButton);
                        return true;
                    }
                });

                if (plannerList.get(id) == null) {
                    defaultButton(imageButton);
                } else {
                    Picasso.get().load(plannerList.get(id).get("imageURL").toString()).into(imageButton);
                    imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openRecipeInfo(plannerList.get(id));
                        }
                    });
                }
                linearLayout.addView(imageButton);
            }
            topView.addView(textView);
            topView.addView(linearLayout);
        }

        return view;
    }

    private void openRecipeDialog(SearchQuery query) {
        PlannerListFragment plannerListFragment = new PlannerListFragment();
        plannerListFragment.setValue(query.getQuery());
        plannerListFragment.setTargetFragment(this, 1);
        plannerListFragment.show(getFragmentManager(), "search");
    }

    private void defaultButton(final ImageButton imageButton) {
        imageButton.setImageResource(R.drawable.add);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("planner", true);
                currentSelection = imageButton;

                recipeFragment = new RecipeFragment(mUser);
                recipeFragment.setArguments(bundle);
                recipeFragment.setTargetFragment(PlannerFragment.this, 1);
                fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.frameLayout, recipeFragment);
                fragmentTransaction.commitNow();
                getView().setVisibility(View.INVISIBLE);

                searchView.setQuery("", false);
//                searchView.setIconified(false);
                searchView.setVisibility(View.VISIBLE);
                setSearch(searchView);
            }
        });

        plannerList.set(imageButton.getId(), null);
        mUser.setMealPlanner(plannerList);
    }

    private void openRecipeInfo(HashMap<String, Object> map) {
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        map.put("planner", false);
        list.add(map);
        Bundle bundle = new Bundle();
        bundle.putSerializable("hashmap", map);
        PlannerInfoFragment plannerInfoFragment = new PlannerInfoFragment();
        plannerInfoFragment.setArguments(bundle);
        plannerInfoFragment.show(getFragmentManager(), "Show recipe dialog fragment");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            searchView.clearFocus();
            searchView.onActionViewCollapsed();

            final HashMap<String, Object> map = new HashMap<>();
            for (String key : bundle.keySet()) {
                map.put(key, bundle.get(key));
            }

            currentSelection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openRecipeInfo(map);
                }
            });

            Picasso.get().load(bundle.getString("imageURL")).into(currentSelection);

            fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.remove(recipeFragment).commitNow();
            getView().setVisibility(View.VISIBLE);

            plannerList.set(currentSelection.getId(), map);
            mUser.setMealPlanner(plannerList);
        }
    }

    public void setSearch(SearchView searchview) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // Search function
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
}
