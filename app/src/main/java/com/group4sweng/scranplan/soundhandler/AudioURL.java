/*
 * AudioURL.java
 * Version 1.0
 * Programmers: Holly Janes and James Leyland
 * Company: FixPack!
 *
 * */

package com.group4sweng.scranplan.SoundHandler;


import android.media.AudioManager;
import android.media.MediaPlayer;

import com.group4sweng.scranplan.Exceptions.AudioPlaybackError;

import java.io.IOException;

/**
 * The AudioURL class takes an associated file URL with any of the following compressed audio extensions outlined in the 'SupportedFormats' interface.
 * It loads this URL into a 'MediaPlayer'.
 * */
public class AudioURL implements SupportedFormats
{
    //TODO - Add Shared preferences with an option to disable sounds (optional)

    //  The Android media player.
    private MediaPlayer player_URL;
    private String stored_URL = null;

    //  Sets the initial state to be playing no sound.
    private Boolean play_URL = false;

    //  Timer set to 3 seconds, can change this to whatever length of time required
    private long timeLeftInMilliSeconds = 3000;

    /**
     *  Constructor which initialises the media player. Does not take any parameters.
     **/
    public AudioURL()
    {
        player_URL = new MediaPlayer();
    }


    /**
     * This function allows a sound file from a URL to be played.
     * @param soundURL - The URL of the sound file.
     * */
    public void playURLSound(String soundURL) throws AudioPlaybackError {

        int counter = 0;

        //  Fairly loose audio format check. Fails if .mp3, .ogg etc... is included as a duplicate in the name. For example
        //  BeepBeepImASheep.mp3Thing.ogg would fail. Condition is 'very' unlikely to fail though.
        //  Isn't a massive issue since if the input is not readable by Android an AudioPlaybackError will still be thrown later.

        //  Cycles through all available formats to check if the extension outlined matches.
        for(int i = 0; i < Formats.values().length; i++ ){
            if(!soundURL.contains("." + Formats.values()[i])){
                counter++; //Counter is log everytime a format isn't found.
            }
        }

        if(counter == Formats.values().length){ //If no supported formats are found the counter will equal the length of the formats enumeration.
            throw new AudioPlaybackError("Tried to use an invalid audio format. Please resort to using the .mp3 format.");
        }

        /* TODO - Add exceptions due to setDataSource() errors, including
            Unsupported media, poorly interleaved audio, resolution too high, streaming timeout.
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
                player_URL.setDataSource(soundURL);

                /*  Ensures that the media player is released once the audio has stopped.
                    Saves system resources.*/
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
            throw new AudioPlaybackError("Failed to play audio from URL: " + soundURL); //Also throw a playback error for testing purposes.
        }
    }

    /**
     * Decide if we should loop the audio file or not.
     * @param isLooping - true = will loop, false = wont loop.
     */
    public void setLooping(boolean isLooping){
        player_URL.setLooping(isLooping);
    }

    /**
     * Find out the current status of the file and if it is looping.
     * @return - true = is looping, false = isn't looping.
     */
    public boolean isLooping(){
        return player_URL.isLooping();
    }

    /**
     * This function stop's the url player to be used in the stopURLSound() function. Also used in the playURLSound function to
     * release the sound once completed. Releases the sound file as well as stop it.
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
     * */
    public void stopURLSound()
    {
        stopURLPlayer();
    }

    /**
     * Returns a player object. Used for more complex media player operations not defined within this class.
     * @return - player object.
     */
    public MediaPlayer getPlayer(){
        return player_URL;
    }

    public void storeURL(String URL){
        this.stored_URL = URL;
    }

    public String getStoredURL(){
        return stored_URL;
    }
}
