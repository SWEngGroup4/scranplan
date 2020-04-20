package com.group4sweng.scranplan.RecipeCreation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.group4sweng.scranplan.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeStepsRecycler extends RecyclerView.Adapter<RecipeStepsRecycler.ViewHolder> {

    private RecipeSteps mRecipeSteps;
    private List<StepData> mData;

    private int mPosition;

    private Integer mediaRequestCode = 1;
    private Integer audioRequestCode = 2;

    static class StepData {
        public Uri media;
        private String description;
        private Float timer;

        StepData(Uri media, String description, Float timer) {
            Log.d("Test", "StepData");
            this.media = media;
            this.description = description;
            this.timer = timer;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView stepId;
        private ImageView stepMedia;
        private EditText stepText;
        private EditText timerValue;
        private Button addMedia;
        private Button addAudio;
        private Button addTimer;
        private Button addGraphics;

        private ViewHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.recipeStepCardView);
            stepId = v.findViewById(R.id.recipeStepID);
            stepMedia = v.findViewById(R.id.recipeStepMedia);
            stepText = v.findViewById(R.id.recipeStepText);
            timerValue = v.findViewById(R.id.recipeStepTimerValue);
            addMedia = v.findViewById(R.id.recipeStepAddMedia);
            addAudio = v.findViewById(R.id.recipeStepAddAudio);
            addTimer = v.findViewById(R.id.recipeStepAddTimer);
            addGraphics = v.findViewById(R.id.recipeStepAddGraphics);
        }
    }

    RecipeStepsRecycler(RecipeSteps recipeSteps, List<StepData> data) {
        mRecipeSteps = recipeSteps;
        mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_step_recycler, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeStepsRecycler.ViewHolder holder, int position) {
        holder.stepId.setText("Step " + (position + 1));

        if (mData.get(position).media != null) {
            Log.d("Test", "Loading into position " + position);
//            Picasso.get().load(mData.get(position).media).fit().centerCrop().into(holder.stepMedia);
            holder.stepMedia.setImageURI(mData.get(position).media);
        }

        holder.addMedia.setOnClickListener(v -> mRecipeSteps.addMedia(position));
        holder.addAudio.setOnClickListener(v -> addAudio(holder.addAudio));
        holder.addTimer.setOnClickListener(v -> addTimer(holder.addTimer, holder.timerValue));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private void addAudio(Button button) {
        Intent audioSelect = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
//        ((Activity) context).startActivityForResult(audioSelect, audioRequestCode);

        button.setText("Remove audio");
        button.setOnClickListener(v -> removeAudio(button));
    }

    private void removeAudio(Button button) {
        button.setText("Add audio");
        button.setOnClickListener(v -> addAudio(button));
    }

    private void addTimer(Button button, EditText timer) {
        timer.setVisibility(View.VISIBLE);
        button.setText("Remove timer");
        button.setOnClickListener(v -> removeTimer(button, timer));
    }

    private void removeTimer(Button button, EditText timer) {
        timer.setText("");
        timer.setVisibility(View.INVISIBLE);
        button.setText("Add timer");
        button.setOnClickListener(v -> addTimer(button, timer));
    }

}
