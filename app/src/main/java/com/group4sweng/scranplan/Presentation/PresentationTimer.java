package com.group4sweng.scranplan.Presentation;

import android.os.CountDownTimer;
import android.util.Log;

import com.group4sweng.scranplan.Exceptions.AudioPlaybackError;
import com.group4sweng.scranplan.SoundHandler.AudioURL;
import com.group4sweng.scranplan.SoundHandler.StockSounds;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Custom timer class, with additional support for:
 *  - Sound URL playback
 *  - Multiple timers which can have associated URL playback files and messages. (useful for long video presentations).
 *  - Returning the time in minutes and seconds.
 *  - Force stopping a timer.
 *
 *      NOTE: When implementing the timer all audio and timer stop methods MUST be called before existing the activity.
 */
public class PresentationTimer {
    private int interval = 500; //The default interval between timer checks

    //Time left on timer displayed in minutes & seconds respectively.
    private long minutes;
    private long seconds;

    private String TAG ="PresentationTimer";

    private boolean soundEnabled = false; //Determine if after the timer is finished a sound should be played.
    private String soundURL = StockSounds.DING.getSoundURL(); //Default microwave ding sound.
    private String[] soundURLs = {StockSounds.DING.getSoundURL()}; //Collection of sound URLs, used to accompany multiple countdown times.
    private AudioURL audio;
    private CountDownTimer timer; //Timer object
    private long millisRemaining; //Amount of time remaining after each tick update.
    private String millisRemainingPrintable;

    /** Basic presentation timer.
     * @param countdownTime - Time in milliseconds to count down from.
     */
    public PresentationTimer(int countdownTime){
        Log.e(TAG, "Have reached timer creation");

        //  Create our timer.
        timer = new CountDownTimer(countdownTime, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisRemaining = millisUntilFinished;
                minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                Log.e(TAG, "Timer has finished");
                if (soundEnabled) { //Only play audio if enabled.
                    audio = new AudioURL();
                    try {
                        Log.e(TAG, "Reached sound playing bit");
                        audio.playURLSound(soundURL);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                //TODO - Add a call to stop the timer in the presentation
            }
        }.start(); //Start the timer.
    }

    /** Timer with interval input
     * @param countdownTime - Time in milliseconds to count down from.
     * @param interval - duration in milliseconds between a timer update.
     */
    public PresentationTimer(int countdownTime, int interval){
        this(countdownTime);
        this.interval = interval;
    }

    /** Timer with interval & soundURL input
     * @param countdownTime - Time in milliseconds to count down from.
     * @param interval - duration in milliseconds between a timer update.
     * @param soundURL - URL of a soundfile to be played at the end of the timer.
     */
    public PresentationTimer(int countdownTime, int interval, String soundURL){
        this(countdownTime, interval);
        soundEnabled = true;
        this.soundURL = soundURL;
    }

    /** Timer with support for multiple consecutive timers, interval input & a timer message after each timer has been completed.
     * @param multipleCountDownTimers - Time in milliseconds to count down from. (multiple).
     * @param interval - duration in milliseconds between a timer update.
     * @param countDownTimesMessages - Messages to display to the user after each individual timer is finished.
     */
    public PresentationTimer(int[] multipleCountDownTimers, String[] countDownTimesMessages, int interval){
        Arrays.sort(multipleCountDownTimers); //Make sure intervals are in order from lowest to highest.

        for(int i : multipleCountDownTimers) { //Repeat the countdown for all time objects in the array,
            timer = new CountDownTimer(multipleCountDownTimers[i], interval) {
                @Override
                public void onTick(long millisUntilFinished) {
                    millisRemaining = millisUntilFinished;
                    minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                    seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    if (soundEnabled) {
                        audio = new AudioURL();
                        try {
                            audio.playURLSound(soundURLs[i]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    String timeLeft = String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds);
                    //TODO - Add a call that allows us to print the output of our input messages to the presentation once each individual counter finishes.
                }
            }.start();
        }
    }

    /** Timer with support for multiple consecutive timers, interval input & a timer message after each timer has been completed.
     *  Also supports consecutive soundURL inputs.
     * @param multipleCountDownTimers - Time in milliseconds to count down from. (multiple).
     * @param interval - duration in milliseconds between a timer update.
     * @param countDownTimesMessages - Messages to display to the user after each individual timer is finished.
     * @param soundURLs - Sound URL to play after each consecutive timer has been completed.
     */
    public PresentationTimer(int[] multipleCountDownTimers, String[] countDownTimesMessages, int interval, String[] soundURLs){
        this(multipleCountDownTimers, countDownTimesMessages, interval);
        this.soundURLs = soundURLs;
    }

    /** Stop our timer before the timer has a chance to finish. Must be called when changing activity.
     * @return - Time left (in milliseconds) on the countdown timer. Useful for restarting a timer after a pause.
     * @throws AudioPlaybackError - Error returned when the player doesn't finish playing.
     */
    public long forceStopTimer() throws AudioPlaybackError {
        //  Stop and remove the timer and stop any audio.
        timer.cancel();
        timer = null;
        audio.stopURLSound();

        if(!audio.getPlayer().isPlaying()){ //Check if the player is currently playing, is so throw an error.
            throw new AudioPlaybackError("Audio playback failed to stop for file URL: " + soundURL);
        }
        return millisRemaining;
    }

    /** Helper function in returning the time
     * @return - Returns the time in a printable format for the presentation.
     * format: [MINS]:[SECONDS]
     */
    public String printOutputTime() {
        return String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds);
    }

}
