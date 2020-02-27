package com.group4sweng.scranplan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchListFragment extends AppCompatDialogFragment {

    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mColRef = mDatabase.collection("recipes");
    private Query query;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        loadRecipes(view);

        return view;
    }

    public void setValue(Query sentQuery) {
        this.query = sentQuery;
    }

    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    public void loadRecipes(final View view) {

        mColRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if (!querySnapshot.isEmpty()) {
                    List<SearchRecyclerAdapter.SearchRecipePreviewData> data = new ArrayList<>();
                    List<DocumentSnapshot> docs = querySnapshot.getDocuments();
                    for (int i = 0; i < docs.size(); i++) {
                        DocumentSnapshot doc = docs.get(i);
                        data.add(new SearchRecyclerAdapter.SearchRecipePreviewData(
                                doc.getId(),
                                doc.get("Name").toString(),
                                doc.get("Description").toString(),
                                doc.get("imageURL").toString()
                        ));
                    }

                    RecyclerView recyclerView = view.findViewById(R.id.recipeList);
                    recyclerView.setHasFixedSize(true);

                    RecyclerView.LayoutManager rManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(rManager);


                    RecyclerView.Adapter rAdapter = new SearchRecyclerAdapter(SearchListFragment.this, data);
                    recyclerView.setAdapter(rAdapter);
                }
            }
        });
    }

    public void recipeSelected(String recipeID) {
//        Toast.makeText(getContext(), "" + recipeID, Toast.LENGTH_SHORT).show();
        Intent i = new Intent();
        i.putExtra("recipeID", recipeID);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
        dismiss();
    }

}