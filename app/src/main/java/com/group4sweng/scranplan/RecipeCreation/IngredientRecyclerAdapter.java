package com.group4sweng.scranplan.RecipeCreation;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.group4sweng.scranplan.R;
import java.util.List;

public class IngredientRecyclerAdapter extends RecyclerView.Adapter<IngredientRecyclerAdapter.ViewHolder> {

    private List<IngredientData> mData;

    // Custom Object to store data for ingredients
    static class IngredientData implements Parcelable {
        public String ingredient;
        public String measurement;

        IngredientData(String ingredient, String measurement) {
            this.ingredient = ingredient;
            this.measurement = measurement;
        }

        protected IngredientData(Parcel in) {
            ingredient = in.readString();
            measurement = in.readString();
        }

        // Parcelable methods for bundle insertion
        public static final Creator<IngredientData> CREATOR = new Creator<IngredientData>() {
            @Override
            public IngredientData createFromParcel(Parcel in) {
                return new IngredientData(in);
            }

            @Override
            public IngredientData[] newArray(int size) {
                return new IngredientData[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(ingredient);
            dest.writeString(measurement);
        }
    }

    // Create view holder for recycler view
    static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView ingredientName;
        private TextView ingredientMeasurement;

        private ViewHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.ingredientCardView);
            ingredientName = v.findViewById(R.id.ingredientName);
            ingredientMeasurement = v.findViewById(R.id.ingredientMeasurement);
        }
    }

    IngredientRecyclerAdapter(List<IngredientData> data) {
        mData = data;
    }

    @NonNull
    @Override
    public IngredientRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflate recycler view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_list, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientRecyclerAdapter.ViewHolder holder, int position) {
        // Display ingredient data
        holder.ingredientName.setText(mData.get(position).ingredient);
        holder.ingredientMeasurement.setText(mData.get(position).measurement);

        // Notifies user on deletion method on click
        holder.cardView.setOnClickListener(v -> {
            Toast.makeText(holder.cardView.getContext(), "Hold to delete", Toast.LENGTH_SHORT).show();
        });

        // Removes entry on long press
        holder.cardView.setOnLongClickListener(v -> {
            mData.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mData.size());
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
