package com.group4sweng.scranplan;
import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class VideoPlayerActivity extends AppCompatActivity {


    PresentationPlayerView videoPlayer;

    String videoURI = "https://firebasestorage.googleapis.com/v0/b/scran-plan-bc521.appspot.com/o/TestVideo%2F20191225_094236.mp4?alt=media&token=784f87fd-21de-43ca-84c1-803c9ee6963e";
    Button mBackButton;

    public VideoPlayerActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoPlayer = new PresentationPlayerView(getApplicationContext());

        ConstraintLayout constraintLayout = findViewById(R.id.video_view);
        constraintLayout.addView(videoPlayer);

        initPageListeners();

    }


    @Override
    protected void onStart() {
        super.onStart();
        videoPlayer.initializePlayer(videoURI);
    }

    @Override
    protected void onStop() {
        super.onStop();
        videoPlayer.releasePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.releasePlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.initializePlayer(videoURI);
    }

//    @SuppressLint("InlinedApi")
//    private void hideSystemUi() {
//        mVideoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//    }



    /**
     * When back button is clicked within the recipe information dialogFragment,
     * Recipe information dialogFragment is closed and returns to recipe fragment
     */
    private void initPageListeners(){

        mBackButton = findViewById(R.id.backButton);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });
    }

}
