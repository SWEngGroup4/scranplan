package com.group4sweng.scranplan;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.List;

public class PlannerFragment extends Fragment {

    List<String> days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_planner, container, false);

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

                ImageButton imageButton = new ImageButton(view.getContext());
                imageButton.setLayoutParams(params);
                imageButton.setAdjustViewBounds(true);
                imageButton.setPadding(10,10,10,10);
                imageButton.setImageResource(R.drawable.add);
                imageButton.setBackground(null);

                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openRecipeDialog();
                    }
                });

                linearLayout.addView(imageButton);
            }
            topView.addView(textView);
            topView.addView(linearLayout);
        }

        return view;
    }

    public void openRecipeDialog() {
        RecipeListFragment recipeListFragment = new RecipeListFragment();
        recipeListFragment.show(getFragmentManager(), "Test");
    }
}
