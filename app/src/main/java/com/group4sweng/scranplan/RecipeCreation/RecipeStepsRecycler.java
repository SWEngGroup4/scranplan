package com.group4sweng.scranplan.RecipeCreation;

import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.Xml.XmlParser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecipeStepsRecycler extends RecyclerView.Adapter<RecipeStepsRecycler.ViewHolder> {

    private RecipeSteps mRecipeSteps;
    private List<StepData> mData;

    // Custom Object to store data for each slide
    static class StepData {
        private Uri media;
        private boolean mediaChanged = false;
        private String description;
        private Uri audio;
        private boolean audioLooping;
        private Integer audioStartTime;
        private boolean audioChanged = false;
        private Float timer;
        private boolean timerChanged = false;
        private ArrayList<XmlParser.Shape> shapes;
        private ArrayList<XmlParser.Triangle> triangles;

        StepData(Uri media, String description, Float timer, boolean audioLooping, Integer audioStartTime) {
            this.media = media;
            this.description = description;
            this.timer = timer;
            this.audioLooping = audioLooping;
            this.audioStartTime = audioStartTime;
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
            this.audioLooping = false;
            this.audioStartTime = null;
            this.audioChanged = false;
        }

        Float getTimer() {
            return this.timer;
        }

        boolean isLooping() { return this.audioLooping; }

        Integer getStartTime() { return this.audioStartTime; }

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

        // Return non-null list
        ArrayList<XmlParser.Shape> getShapes() {
            if (shapes == null)
                shapes = new ArrayList<>();
            return shapes;
        }

        // Return non-null list
        ArrayList<XmlParser.Triangle> getTriangles() {
            if (triangles == null)
                triangles = new ArrayList<>();
            return triangles;
        }
    }

    // Initialise view holder for recycler view
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
        private EditText audioStartTime;
        private Switch audioLooping;
        private TextView audioStartTimeText;
        private StartTimeListener startTimeListener;
        private Button addTimer;
        private Button addGraphics;

        private ViewHolder(View v, StepListener stepTextListener, TimerListener timerValueListener, StartTimeListener startTimeListener) {
            super(v);
            // Assign all page elements
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
            audioStartTime = v.findViewById(R.id.startTime);
            this.startTimeListener = startTimeListener;
            audioStartTime.addTextChangedListener(startTimeListener);
            audioLooping = v.findViewById(R.id.loopingToggle);
            audioStartTimeText = v.findViewById(R.id.startTimeText);

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

        // Inflate view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_step_recycler, parent, false);
        return new ViewHolder(v, new StepListener(), new TimerListener(), new StartTimeListener());
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeStepsRecycler.ViewHolder holder, int position) {
        // Assign order to step
        holder.stepId.setText("Step " + (position + 1));

        // Remove step when close button is pressed
        holder.stepRemove.setOnClickListener(v -> {
            mData.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mData.size());
        });

        holder.audioLooping.setOnClickListener(v -> {
            boolean switched = ((Switch) v).isChecked();

            mData.get(position).audioLooping = switched;

            if(switched){
                holder.audioStartTime.setText("");
                holder.audioStartTime.setVisibility(View.GONE);
                holder.audioStartTimeText.setVisibility(View.GONE);
            } else {
                holder.audioStartTime.setVisibility(View.VISIBLE);
                holder.audioStartTimeText.setVisibility(View.VISIBLE);
            }
        });

        // Update step when media is loaded in
        if (mData.get(position).mediaChanged) {
            // Load in picture fitted into dimensions
            Picasso.get().load(mData.get(position).media).fit().centerCrop().into(holder.stepMedia);
            holder.stepMedia.setVisibility(View.VISIBLE);

            // Show path to uploaded file
            holder.mediaUri.setText(mData.get(position).media.toString() + " \u2713");
            holder.mediaUri.setVisibility(View.VISIBLE);

            // Change button to remove media
            holder.addMedia.setText("Remove media");
            holder.addMedia.setOnClickListener(v -> mRecipeSteps.removeMedia(position));
        // Update step when media is removed
        } else {
            holder.stepMedia.setVisibility(View.GONE);
            holder.mediaUri.setVisibility(View.GONE);

            holder.addMedia.setText("Add media");
            holder.addMedia.setOnClickListener(v -> mRecipeSteps.addMedia(position));
        }

        // Set text of step
        holder.stepTextListener.setPosition(position);
        holder.stepText.setText(mData.get(position).description);

        // Update step if audio is present
        if (mData.get(position).audioChanged) {
            // Show path to uploaded audio
            holder.audioUri.setText(mData.get(position).audio.toString() + " \u2713");
            holder.startTimeListener.setPosition(position);

            // Show field as empty if no data is added
            if (mData.get(position).audioStartTime == null)
                holder.audioStartTime.setText("");
            else
                holder.audioStartTime.setText(String.format("%d", mData.get(position).audioStartTime));

            holder.audioUri.setVisibility(View.VISIBLE);
            holder.audioLooping.setVisibility(View.VISIBLE);
            holder.audioStartTime.setVisibility(View.VISIBLE);
            holder.audioStartTimeText.setVisibility(View.VISIBLE);

            // Change button to remove audio
            holder.addAudio.setText("Remove audio");
            holder.addAudio.setOnClickListener(v -> mRecipeSteps.removeAudio(position));
        // Update step is audio is removed
        } else {
            holder.audioUri.setVisibility(View.GONE);
            holder.audioLooping.setVisibility(View.GONE);
            holder.audioStartTime.setVisibility(View.GONE);
            holder.audioStartTimeText.setVisibility(View.GONE);

            holder.addAudio.setText("Add audio");
            holder.addAudio.setOnClickListener(v -> mRecipeSteps.addAudio(position));
        }


        // Show timer field if timer is added to step
        if (mData.get(position).timerChanged) {
            holder.timerValueListener.setPosition(position);

            // Show field as empty if no data is added
            if (mData.get(position).timer == null)
                holder.timerValue.setText("");
            else
                holder.timerValue.setText(String.format("%f", mData.get(position).timer));

            holder.timerValue.setVisibility(View.VISIBLE);

            // Change button to remove timer
            holder.addTimer.setText("Remove timer");
            holder.addTimer.setOnClickListener(v -> mRecipeSteps.removeTimer(position));
        // Hide timer field is no timer is present
        } else {
            holder.timerValue.setVisibility(View.GONE);

            holder.addTimer.setText("Add timer");
            holder.addTimer.setOnClickListener(v -> mRecipeSteps.addTimer(position));
        }


        // Graphics button functionality - does not save previous graphical configuration
        holder.addGraphics.setOnClickListener(v -> mRecipeSteps.addGraphics(position));

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    // Listener for step description to update step
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

    // Listener for audio start time
    private class StartTimeListener implements TextWatcher {

        private Integer position;

        public void setPosition (Integer position) { this.position = position; }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals(""))
                mData.get(position).audioStartTime = Integer.parseInt(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    // Listener for timer value to update step
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
