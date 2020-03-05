package com.group4sweng.scranplan;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

public class PresentationPlayerView extends PlayerView {

    private SimpleExoPlayer exoPlayer;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private Context context;

    private LinearLayout.LayoutParams layoutParams;
    private Integer slideHeight;
    private Integer slideWidth;

    public PresentationPlayerView(Context context, Integer slideHeight, Integer slideWidth) {
        super(context);
        this.context = context;
        this.slideHeight = slideHeight;
        this.slideWidth = slideWidth;

        layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        this.setLayoutParams(layoutParams);
    }

    public void initializePlayer(String URL, Boolean playWhenReady){

        exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext());
        this.setPlayer(exoPlayer);

        Uri uri = Uri.parse(URL);
        MediaSource mediaSource = buildMediaSource(uri);

        exoPlayer.setPlayWhenReady(playWhenReady);
        exoPlayer.seekTo(currentWindow, playbackPosition);
        exoPlayer.prepare(mediaSource, false, false);

    }

    /**
     *  Method 
     */
    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(context, "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    public void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    public void setDims(Float width, Float height) {
        layoutParams.width = Math.round(slideWidth * (width / 100));
        layoutParams.height = Math.round(slideHeight * (height / 100));

        Log.d("Test", "Width: " + layoutParams.width);
        Log.d("Test", "Height: " + layoutParams.height);
    }

    public void setPos(Float xPos, Float yPos) {
        layoutParams.setMargins(Math.round(slideWidth * (xPos / 100)),
                Math.round(slideHeight * (yPos / 100)), 0, 0);
    }

}
