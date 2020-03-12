/*
 * AudioURL.java
 * Version 1.0
 * Programmers: Holly Janes and James Leyland
 * Company: FixPack!
 *
 * */

package com.group4sweng.scranplan.soundhandler;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;

/**
 *
 * The AudioURL class takes a URL from the server and plays the audio associated with the URL. Please upload the desired
 * mp3 file you require.
 *
 * */
public class AudioURL extends AppCompatActivity
{
    MediaPlayer player_URL;
    /*
        Sets the initial state to be playing no sound.

    */
    private Boolean play_URL = false;
    /*
        Timer set to 3 seconds, can change this to whatever length of time required

    */
    private long timeLeftInMilliSeconds = 3000;

    /**
     *  Constructor which initialises the media player. Does not take any parameters.
     *
     *  */
    public AudioURL()
    {
        player_URL = new MediaPlayer();
    }

    /**
     * This function creates a timer to be set before the sound file is to be played.
     *
     * */
    public void URLTimer()
    {
        new CountDownTimer(timeLeftInMilliSeconds, 1000)
        {

            /*
                Method available to at text to screen to show progression of count down timer.

            */
            @Override
            public void onTick(long millisUntilFinished)
            {
                // left blank so text can be added to the screen that is required
            }

            /*
                Once time has completed, sound plays which is called from playURLSound function.

            */
            @Override
            public void onFinish()
            {
                playURLSound();
            }
        }.start();

    }


    /**
     * This function allows a sound file from a URL to be played. In this example I use an example url shown below but can use any URL.
     *
     * */
    public void playURLSound()
    {
        /*
            The audio url to play
        */
        String soundURL = "https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_700KB.mp3";
        /*
            Try to play music/audio from url. Need to throw exceptions due to setDataSource() causing errors.
            Include
                Unsupported audio
                Poorly interleaved audio
                Resolution too high
                Streaming timeout

        */
        try {
            /*
                void setAudioStreamType(int streamtype)
                    Sets the media player's audio stream type

                Parameters
                    int streamtype : the type of file that you want to play, in this case, music

            */
            player_URL.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if (!play_URL) {
                /*
                    void setDataSource (String path)
                        Sets the data source to use.

                    Parameters
                        path String : the path of the file, or the http/rtsp URL of the stream you want to play

                */
                player_URL.setDataSource(soundURL);
                /*
                    Ensures that the media player is released once the audio has stopped.
                    Saves system resources.

                */
                player_URL.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopURLPlayer();
                    }
                });
            }
            /*
                void prepare ()
                    Prepares the media player for playback, synchronously

             */
            player_URL.prepare();
            /*
                void start ()
                    Starts playing audio from http and restarts again if audio has been stopped.
                    Set outside if statement because we want to start it no matter if created or not.

            */
            player_URL.start();
            /*
                Set play to be true once audio playing

            */
            play_URL = true;
        }
        /*
            Throws
                IOException
                    if the url can not be read
                IllegalArgumentException
                    if method has passed an illegal or inappropriate url

        */
        catch (IOException | IllegalArgumentException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This function stop's the url player to be used in the stopURLSound() function. Also used in the playURLSound function to
     * release the sound once completed. Releases the sound file as well as stop it.
     *
     * */
    private void stopURLPlayer()
    {
        if (play_URL)
        {
            /*
                void stop()
                    Stop's playing the audio from http
            */
            player_URL.stop();
            /*
                void reset ()
                    Resets the MediaPlayer to it's uninitialised state so can play audio every time function is called.
            */
            player_URL.reset();
            /*
                Set play to be false once audio has stopped

            */
            play_URL = false;
        }
    }

    /**
     * This function uses the stopURLPlayer() function to stop the sound for the URL in this method and other places too.
     *
     * */
    public void stopURLSound()
    {
        stopURLPlayer();
    }

    /**
     * This function overrides the onStop method in the AppCompatActivity class to stop the player once the app has been exited and releases the media player.
     *
     * */
    @Override
    protected void onStop()
    {
        super.onStop();
        stopURLPlayer();
    }

}
