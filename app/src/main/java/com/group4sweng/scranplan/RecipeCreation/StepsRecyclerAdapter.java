//package com.group4sweng.scranplan.RecipeCreation;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.cardview.widget.CardView;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.group4sweng.scranplan.R;
//import java.util.List;
//
//public class StepsRecyclerAdapter extends RecyclerView.Adapter<StepsRecyclerAdapter.ViewHolder> {
//
//    private List<IngredientData> mData;
//
//    static class IngredientData implements Parcelable {
//        private String ingredient;
//        private String measurement;
//
//        IngredientData(String ingredient, String measurement) {
//            this.ingredient = ingredient;
//            this.measurement = measurement;
//        }
//
//        protected IngredientData(Parcel in) {
//            ingredient = in.readString();
//            measurement = in.readString();
//        }
//
//        public static final Creator<IngredientData> CREATOR = new Creator<IngredientData>() {
//            @Override
//            public IngredientData createFromParcel(Parcel in) {
//                return new IngredientData(in);
//            }
//
//            @Override
//            public IngredientData[] newArray(int size) {
//                return new IngredientData[size];
//            }
//        };
//
//        @Override
//        public int describeContents() {
//            return 0;
//        }
//
//        @Override
//        public void writeToParcel(Parcel dest, int flags) {
//            dest.writeString(ingredient);
//            dest.writeString(measurement);
//        }
//    }
//
//    static class ViewHolder extends RecyclerView.ViewHolder {
//        private ImageButton stepImage;
//        private EditText stepText;
//        private EditText stepTimer;
//
//        private ViewHolder(View v) {
//            super(v);
//            stepImage = v.findViewById(R.id.recipeStepMedia);
//            stepText = v.findViewById(R.id.recipeStepText);
//            stepTimer = v.findViewById(R.id.recipeStepTimerValue);
//        }
//    }
//
//    StepsRecyclerAdapter(List<IngredientData> data) {
//        mData = data;
//    }
//
//    @NonNull
//    @Override
//    public StepsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//        View v = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.ingredient_list, parent, false);
//
//        return new ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull StepsRecyclerAdapter.ViewHolder holder, int position) {
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return mData.size();
//    }
//}
