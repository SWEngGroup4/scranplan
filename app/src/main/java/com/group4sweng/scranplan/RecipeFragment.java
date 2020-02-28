package com.group4sweng.scranplan;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

public class RecipeFragment extends Fragment {

    public RecipeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        Button button = view.findViewById(R.id.presentationButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent presentation = new Intent(getActivity(), Presentation.class);

                //allTagsSample
                //presentation.putExtra("xml_URL", "https://firebasestorage.googleapis.com/v0/b/scran-plan-bc521.appspot.com/o/recipe_xml%2FallTagsSample.xml?alt=media&token=fd7ea2cb-3c7d-4251-bb48-da794467d52c");

                //slow_cooker_spiced_root_&_lentil_casserole
                presentation.putExtra("xml_URL", "https://firebasestorage.googleapis.com/v0/b/scran-plan-bc521.appspot.com/o/recipe_xml%2Fslow_cooker_spiced_root_%26_lentil_casserole.xml?alt=media&token=3639f019-2b91-491b-afc9-ccab75a28ff1");

                //Sample
                //presentation.putExtra("xml_URL", "https://firebasestorage.googleapis.com/v0/b/scran-plan-bc521.appspot.com/o/recipe_xml%2FSample.xml?alt=media&token=613d395d-0184-4e2a-b2b7-fed47d09d851");
                startActivity(presentation);
            }
        });

        return view;
    }
}
