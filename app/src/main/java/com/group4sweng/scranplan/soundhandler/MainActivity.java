/*
 * MainActivity.java
 * Version 1.0
 * Programmers: Holly Janes and James Leyland
 * Company: FixPack!
 *
 * */

package com.group4sweng.scranplan.soundhandler;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

/**
 * Main solely used to test whether methods in both classes work correctly.
 *
 * */
public class MainActivity extends AppCompatActivity
{
    /*
        Calls AudioLocal class
    */
    AudioLocal audio_local = new AudioLocal();
    /*
        Calls AudioURL class
    */
    AudioURL audio_url = new AudioURL();
    /*
        Must call Context variable here as this is passed into functions needed in the AudioLocal class.
    */
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
            Set context to equal this to be used within functions in AudioLocal class.

        */
        context = this;

    }

    public void playLocal(View v)
    {
        audio_local.playLocalSound(context);
    }

    public void playLocalWithTimer(View v)
    {
        audio_local.LocalSoundTimer(context);
    }

    public void stopLocal(View v)
    {
        audio_local.stopLocalSound();
    }

    public void playURL(View v)
    {
        audio_url.playURLSound();
    }

    public void playURLWithTimer(View v)
    {
        audio_url.URLTimer();
    }

    public void stopURL(View v)
    {
        audio_url.stopURLSound();
    }


}
