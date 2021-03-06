package com.group4sweng.scranplan.Presentation;

import android.os.CountDownTimer;

import com.group4sweng.scranplan.Exceptions.AudioPlaybackException;
import com.group4sweng.scranplan.SoundHandler.AudioURL;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Presentation Timer.
 * Author: JButler
 * (c) CoDev 2020
 *
 * Custom abstract timer class, with additional support for:
 *  - Sound URL playback
 *  - Returning the time in minutes and seconds.
 *  - Force stopping a timer. (Includes stopping audio playback)

 *      NOTE: When implementing the timer all audio and timer stop methods MUST be called before existing the activity.
 *            Also, when creating a new presentation timer the onTick and onFinish methods always have to be inherited.
 *            There is no workaround for this.
 */
abstract class PresentationTimer extends CountDownTimer{

    private boolean soundEnabled = false; // Determine if after the timer is finished a sound should be played.

    //  Audio to be played at the end of the timer countdown and audio played during the countdown respectively.
    private AudioURL finishAudio = null;
    private AudioURL loopingAudio = null;

    //  Remaining time on countdown timer. (Not currently utilized).
    private long millisRemaining;

    /** Basic presentation timer (No audio included)
     * @param countdownTime - Time in milliseconds to count down from
     * @param interval - Interval in milliseconds in which the timer updates.
     */
    PresentationTimer(long countdownTime, long interval){
        super(countdownTime, interval);
    }

    /** Presentation timer with audio for finishing but no looping audio during playback.
     * @param countdownTime - Time in milliseconds to count down from
     * @param interval - Interval in milliseconds in which the timer updates.
     * @param finishAudio - Audio played at the end of the timer countdown.
     */
    PresentationTimer(long countdownTime, long interval, AudioURL finishAudio){
        this(countdownTime, interval);
        soundEnabled = true;
        this.finishAudio = finishAudio;
    }

    /** Presentation timer with all audio supported.
     * @param countdownTime - Time in milliseconds to count down from
     * @param interval - Interval in milliseconds in which the timer updates.
     * @param finishAudio - Audio played at the end of the timer countdown.
     * @param loopingAudio - Audio played during duration of timer countdown.
     */
    PresentationTimer(long countdownTime, long interval, AudioURL finishAudio, AudioURL loopingAudio){
        this(countdownTime, interval, finishAudio);
        this.loopingAudio = loopingAudio;
    }

    //  Start our timer (Custom method that allows us to start audio as well)
    void startTimer() {
        this.start(); // Start timer.

        if(soundEnabled && loopingAudio != null){ // If sound is enabled for the timer & we have audio we can loop
            if(loopingAudio.getStoredURL() != null){ // Also check a URL has been stored prior in the AudioURL object.
                try{
                    loopingAudio.playURLSound(loopingAudio.getStoredURL()); // Attempt to play our sound.
                } catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                //  Cannot reference AudioURL with no URl stored within it.
                throw new NullPointerException("No associated URL stored in AudioURL object. Will not play.");
            }
        }

    }

    /** Soft timer stop.
     *  Stops the timer when the timer playback duration has finished. Doesn't force all audio file playback to stop.
     * @throws AudioPlaybackException - Error returned if the final audio URL fails to play.
     */
    void stopTimer() throws AudioPlaybackException {
        this.cancel();

        if(loopingAudio != null){
            loopingAudio.stopURLSound();
        }

        if(finishAudio != null){
            finishAudio.playURLSound(finishAudio.getStoredURL());
        }

    }

    /** Hard timer stop
     * Stop our timer before the timer has a chance to finish. Must be called when changing activity or pressing the top button.
     * @return - Time left (in milliseconds) on the countdown timer. Useful for restarting a timer after a pause.
     * @throws AudioPlaybackException - Error returned when the player doesn't finish playing.
     */
    long forceStopTimer() throws AudioPlaybackException {
        //  Stop and remove the timer and stop any audio.
        this.cancel();

        if(finishAudio != null) { // Check if audio is supported for the timer. Can't remove audio if no reference exists.
            if(finishAudio.getPlayer().isPlaying()){ // Check if audio was playing initially.
                finishAudio.stopURLSound();
                if(finishAudio.getPlayer().isPlaying()){ // Audio should have stopped playing. Throw exception if not
                    throw new AudioPlaybackException("Audio playback failed to stop for file URL: " + finishAudio.getStoredURL());
                }
            }
        }

        if(loopingAudio != null) {
            loopingAudio.stopURLSound();
            if(loopingAudio.getPlayer().isPlaying()){
                throw new AudioPlaybackException("Audio playback failed to stop for file URL: " + loopingAudio.getStoredURL());
            }
        }

        return millisRemaining; // optional return with time left on the timer. Can be used for a pause function.
    }

    /** Helper function in returning the time
     * @return - Returns the time in a printable format for the presentation.
     * format: [MINS]:[SECONDS]
     */
    static String printOutputTime(long currentMillis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(currentMillis);

        //  Seconds are the current time in seconds (from milliseconds) - time in minutes > to seconds that has passed.
        long seconds = TimeUnit.MILLISECONDS.toSeconds(currentMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentMillis));

        //  Display in a format that allows for a '0' in-front of single number characters. IE 09:05.
        return String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds);
    }

}
