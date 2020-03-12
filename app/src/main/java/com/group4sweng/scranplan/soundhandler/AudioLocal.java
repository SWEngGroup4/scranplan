/*
* AudioLocal.java
* Version 1.0
* Programmers: Holly Janes and James Leyland
* Company: FixPack!
*
* */

package com.group4sweng.scranplan.soundhandler;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;

/**
 *
 *  The AudioLocal class takes a file from a raw folder (which can be found in the res folder for this project) and plays the file. Please upload the desired
 *  mp3 file you require. This can be done simply by copy and pasting the sound file into the raw folder.
 *
 * */
public class AudioLocal extends AppCompatActivity
{
    MediaPlayer player_Local;
    /*
        Sets the initial state to be playing no sound.

    */
    private Boolean play_Local = false;
    /*
        Timer set to 3 seconds, can change this to whatever length of time required

    */
    private long timeLeftInMilliSeconds = 3000;

    /**
     *  Constructor which initialises the media player. Does not take any parameters.
     *
     *  */
    public AudioLocal()
    {
        player_Local = new MediaPlayer();
    }

    /**
     * This function creates a timer to be set before the sound file is to be played
     *
     * @param context
     *      passed into this function in order for the sound file to play. It has been passed from variable called in MainActivity.
     * */
    public void LocalSoundTimer(final Context context)
    {
        new CountDownTimer(timeLeftInMilliSeconds, 1000)
        {
            /*
                Method available to at text to screen to show progression of count down timer.

            */
            @Override
            public void onTick(long millisUntilFinished)
            {
                //left blank so you can add any text that you need to the screen that you require
            }

            /*
                Once time has completed, sound plays which is called from playLocalSound function.

            */
            @Override
            public void onFinish()
            {
                playLocalSound(context);
            }
        }.start();

    }

    /**
     * This function allows a local sound file to be played from the raw folder located in the res folder. In this example the mp3 file is called song.
     *
     * @param context
     *      passed into this function in order for the sound file to be created in order to play. It has been passed from variable called in MainActivity.
     *
     * */
    public void playLocalSound(Context context)
    {
        if (!play_Local)
        {
            /*
                Creates a MediaPlayer for a given local file only when needed to free the resources.

            */
//            player_Local = MediaPlayer.create(context, R.raw.song);
            /*
                Ensures that the media player is released once the audio has stopped.
                Saves system resources.

            */
            player_Local.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopLocalPlayer();
                }
            });
        }
        /*
            void start ()
                Starts playing audio from local file and restarts again if audio has been stopped.
                Set outside if statement because we want to start it no matter if created or not.

        */
        player_Local.start();
        /*
            Set play to be true once audio playing

        */
        play_Local = true;
    }

    /**
     * This function stop's the local sound file to be used in the stopLocalSound() function. Also used in the playLocalSound function to
     * release the sound once completed. Releases the sound file as well as stop it which also systems resources to be freed.
     *
     * */
    private void stopLocalPlayer()
    {
        if (play_Local)
        {
            /*
                void stop()
                    Stop's playing the audio file to free up system resources
            */
            player_Local.stop();
            /*
                void release()
                    Release resources from MediaPlayer to free up space from system resources.

            */
            player_Local.release();
            /*
                Set play to be false once audio player has stopped

            */
            play_Local = false;
        }
    }

    /**
     * This function calls the stopLocalPlayer in order to stop the audio from playing in this method and in other places also.
     *
     * */
    public void stopLocalSound()
    {
        stopLocalPlayer();
    }

    /**
     * This function overrides the onStop method in the AppCompatActivity class to stop the player once the app has been exited and releases the media player.
     *
     * */
    @Override
    protected void onStop()
    {
        super.onStop();
        stopLocalPlayer();
    }

}
