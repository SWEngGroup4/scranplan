package com.group4sweng.scranplan.MealPlanner.Ingredients;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.group4sweng.scranplan.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class IngredientAdapter extends ArrayAdapter<Ingredient> {

    private Context mContext;
    private ArrayList<Ingredient> ingredientList = new ArrayList<>();

    public IngredientAdapter(@NonNull Context mContext, @NonNull ArrayList<Ingredient> ingredientList) {
        super(mContext, 0, ingredientList); // 0 means we don't inflate yet.

        this.ingredientList = ingredientList;
        this.mContext = mContext;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NotNull ViewGroup parent) {
        View ingredient = convertView;

        if(ingredient == null)
            ingredient = LayoutInflater.from(mContext).inflate(R.layout.ingredient, parent, false);

        Ingredient currentIngredient = ingredientList.get(position);

        TextView name = ingredient.findViewById(R.id.ingredient_name);
        name.setText(currentIngredient.getName());

        ImageView icon = ingredient.findViewById(R.id.ingredient_icon);
        if(currentIngredient.getIcon() != -1){
            icon.setImageResource(currentIngredient.getIcon());
        }

        TextView portionWarning = ingredient.findViewById(R.id.ingredient_warning);
        if(currentIngredient.getWarning() != null){
            portionWarning.setText(currentIngredient.getWarning());
        }

        return ingredient;
    }
}
