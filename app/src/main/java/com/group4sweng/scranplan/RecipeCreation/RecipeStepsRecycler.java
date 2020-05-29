package com.group4sweng.scranplan.RecipeCreation;

import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.group4sweng.scranplan.Drawing.Rectangle;
import com.group4sweng.scranplan.Drawing.Triangle;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.Xml.XmlParser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecipeStepsRecycler extends RecyclerView.Adapter<RecipeStepsRecycler.ViewHolder> {

    private RecipeSteps mRecipeSteps;
    private List<StepData> mData;

    static class StepData {
        private Uri media;
        private boolean mediaChanged = false;
        private String description;
        private Uri audio;
        private boolean audioChanged = false;
        private Float timer;
        private boolean timerChanged = false;
        private ArrayList<XmlParser.Shape> shapes;
        private ArrayList<XmlParser.Triangle> triangles;

        StepData(Uri media, String description, Float timer) {
            this.media = media;
            this.description = description;
            this.timer = timer;
        }

        String getDescription() {
            return this.description;
        }

        void setMedia(Uri media) {
            this.media = media;
            this.mediaChanged = true;
        }

        void removeMedia() {
            this.media = null;
            this.mediaChanged = false;
        }

        void setAudio(Uri audio) {
            this.audio = audio;
            this.audioChanged = true;
        }

        void removeAudio() {
            this.audio = null;
            this.audioChanged = false;
        }

        void setTimer(Float timer) {
            this.timer = timer;
        }

        Float getTimer() {
            return this.timer;
        }

        void showTimer() {
            timerChanged = true;
        }

        void removeTimer() {
            this.timer = null;
            timerChanged = false;
        }

        void setGraphics(ArrayList<XmlParser.Shape> shapes, ArrayList<XmlParser.Triangle> triangles) {
            this.shapes = shapes;
            this.triangles = triangles;
        }

        ArrayList<XmlParser.Shape> getShapes() {
            if (shapes == null)
                shapes = new ArrayList<>();
            return shapes;
        }

        ArrayList<XmlParser.Triangle> getTriangles() {
            if (triangles == null)
                triangles = new ArrayList<>();
            return triangles;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView stepId;
        private ImageButton stepRemove;
        private ImageView stepMedia;
        private EditText stepText;
        private StepListener stepTextListener;
        private EditText timerValue;
        private TimerListener timerValueListener;
        private Button addMedia;
        private TextView mediaUri;
        private Button addAudio;
        private TextView audioUri;
        private Button addTimer;
        private Button addGraphics;

        private ViewHolder(View v, StepListener stepTextListener, TimerListener timerValueListener) {
            super(v);
            cardView = v.findViewById(R.id.recipeStepCardView);
            stepId = v.findViewById(R.id.recipeStepID);
            stepRemove = v.findViewById(R.id.recipeStepRemove);
            stepMedia = v.findViewById(R.id.recipeStepMedia);

            stepText = v.findViewById(R.id.recipeStepText);
            this.stepTextListener = stepTextListener;
            stepText.addTextChangedListener(stepTextListener);

            timerValue = v.findViewById(R.id.recipeStepTimerValue);
            this.timerValueListener = timerValueListener;
            timerValue.addTextChangedListener(timerValueListener);

            addMedia = v.findViewById(R.id.recipeStepAddMedia);
            mediaUri = v.findViewById(R.id.recipeStepMediaUri);
            addAudio = v.findViewById(R.id.recipeStepAddAudio);
            audioUri = v.findViewById(R.id.recipeStepAudioUri);
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
        return new ViewHolder(v, new StepListener(), new TimerListener());
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeStepsRecycler.ViewHolder holder, int position) {
        holder.stepId.setText("Step " + (position + 1));

        holder.stepRemove.setOnClickListener(v -> {
            mData.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mData.size());
        });

        if (mData.get(position).mediaChanged) {
            Picasso.get().load(mData.get(position).media).fit().centerCrop().into(holder.stepMedia);
            holder.stepMedia.setVisibility(View.VISIBLE);

            holder.mediaUri.setText(mData.get(position).media.toString() + " \u2713");
            holder.mediaUri.setVisibility(View.VISIBLE);

            holder.addMedia.setText("Remove media");
            holder.addMedia.setOnClickListener(v -> mRecipeSteps.removeMedia(position));
        } else {
            holder.stepMedia.setVisibility(View.GONE);
            holder.mediaUri.setVisibility(View.GONE);

            holder.addMedia.setText("Add media");
            holder.addMedia.setOnClickListener(v -> mRecipeSteps.addMedia(position));
        }

        holder.stepTextListener.setPosition(position);
        holder.stepText.setText(mData.get(position).description);

        if (mData.get(position).audioChanged) {
            holder.audioUri.setText(mData.get(position).audio.toString() + " \u2713");
            holder.audioUri.setVisibility(View.VISIBLE);

            holder.addAudio.setText("Remove audio");
            holder.addAudio.setOnClickListener(v -> mRecipeSteps.removeAudio(position));
        } else {
            holder.audioUri.setVisibility(View.GONE);

            holder.addAudio.setText("Add audio");
            holder.addAudio.setOnClickListener(v -> mRecipeSteps.addAudio(position));
        }

        if (mData.get(position).timerChanged) {
            holder.timerValueListener.setPosition(position);
            if (mData.get(position).timer == null)
                holder.timerValue.setText("");
            else
                holder.timerValue.setText(String.format("%f", mData.get(position).timer));
            holder.timerValue.setVisibility(View.VISIBLE);

            holder.addTimer.setText("Remove timer");
            holder.addTimer.setOnClickListener(v -> mRecipeSteps.removeTimer(position));
        } else {
            holder.timerValue.setVisibility(View.GONE);

            holder.addTimer.setText("Add timer");
            holder.addTimer.setOnClickListener(v -> mRecipeSteps.addTimer(position));
        }

        holder.addGraphics.setOnClickListener(v -> mRecipeSteps.addGraphics(position));

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private class StepListener implements TextWatcher {

        private Integer position;

        public void setPosition (Integer position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mData.get(position).description = s.toString();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private class TimerListener implements TextWatcher {

        private Integer position;

        public void setPosition (Integer position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals(""))
                mData.get(position).timer = Float.valueOf(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

}
