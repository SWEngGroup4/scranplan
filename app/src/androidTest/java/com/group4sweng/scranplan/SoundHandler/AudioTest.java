package com.group4sweng.scranplan.SoundHandler;

import android.media.MediaPlayer;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.group4sweng.scranplan.Exceptions.AudioPlaybackException;
import com.group4sweng.scranplan.RecordedEspressoHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AudioTest extends EspressoHelper {

    /**
     * Instrumentation tests for audio.
     * Author: JButler
     * (c) CoDev 2020
     *
     * Manual audio timer tests have been conducted for the following conditions:
     *  (If time permits can try to approach adding Instrumentation tests).
     *
     * - Audio starts when timer play button is pressed.
     * - Audio stops when timer runs out of time
     * - Audio 'force' stops when slide is changed
     * - Audio 'force' stops when the stop button is pressed.
     * - Audio dosen't play on a slide with audio XML tags not included.
     * - Audio XML data is loaded properly & only 1 of each (looping + non-looping) is loaded in.
     */

    //  Our test 'ding' sound.
    private final String SOUND_URL = "https://bigsoundbank.com/UPLOAD/mp3/1631.mp3";

    //  A test 'ticking' sound. Wrong format (completely un-supported)
    private final String SOUND_URL_MP2 = "https://firebasestorage.googleapis.com/v0/b/scran-plan-bc521.appspot.com/o/sounds%2Ftest_wrong_format_eggtimer.mp2?alt=media&token=a34e8c38-16d4-4d9c-85b3-85da48bdd542";

    //  Same file, format supported by Android but not by this app.
    private final String SOUND_URL_FLAC = "https://firebasestorage.googleapis.com/v0/b/scran-plan-bc521.appspot.com/o/sounds%2Ftest_wrong_format_eggtimer.flac?alt=media&token=66be31ab-f20b-49b4-8ddc-a2edeae71512";

    //  Name of the test recipe we want to navigate to.
    private final String TEST_RECIPE_NAME = "Ultimate spaghetti carbonara";


    private int THREAD_SLEEP_TIME = 3000;

    //  Corresponding audio URL and player objects to test.
    private AudioURL testAudio;
    private MediaPlayer player;

    @Before
    public void setUp(){
        testAudio = new AudioURL(); // Create a blank audio URL
        player = testAudio.getPlayer(); // Return the audio player.
    }

    //  Test a supported sound can be loaded in.
    @Test
    public void testSoundLoads() throws InterruptedException {
        try {
            testAudio.playURLSound(SOUND_URL);
        } catch (AudioPlaybackException e) {
            e.printStackTrace();
        }

        player = testAudio.getPlayer();
        assertTrue(player.isPlaying()); // Check that the audio is playing.

        Thread.sleep(THREAD_SLEEP_TIME);
    }

    //  Test we can stop audio playback
   @Test
   public void testSoundStops() throws InterruptedException {
        testSoundLoads();

        testAudio.stopURLSound();
        player = testAudio.getPlayer();
        assertFalse(player.isPlaying()); // Check playback has stopped
   }

   //  Test sound can loop.
   @Test
   public void testLoopingSound() throws InterruptedException {
        testAudio.setLooping(true);
        player = testAudio.getPlayer();

        try {
            testAudio.playURLSound(StockSounds.EGG_TIMER.getSoundURL());
        } catch (AudioPlaybackException e){
            e.printStackTrace();
        }
        player.getDuration();

        //  Sleep for the players duration + some extra time to check it has looped.
        Thread.sleep(testAudio.getPlayer().getDuration() + THREAD_SLEEP_TIME/2);

        if(!player.isPlaying()){ // After the thread sleep time the player should still be playing, if not fail.
            fail("Player has not looped.");
        }
   }

   /*   Test an un-supported media format (both for android and the app) fails. In this case MP2.
        Check the appropriate exception is returned. */
   @Test
   public void testUnsupportedAndroidInputFormatFails() throws InterruptedException {
        try {
            testAudio.playURLSound(SOUND_URL_MP2);
            fail("Failed to throw exception when reading in an unsuported file media type.");
        } catch (AudioPlaybackException e){
            assertEquals(e.getMessage(), "Tried to use an invalid audio format. Please resort to using the .mp3 format.");
        }
   }

   /*   Test an un-supported media format (app only) fails. In this case a FLAC file, chosen due to large uncompressed file size and not
        fitting one of the XML schema selected specification formats.
        Check the appropriate exception is returned. */
   @Test
   public void testUnsupportedAppInputFormatFails() {
        try {
            testAudio.playURLSound(SOUND_URL_FLAC);
            fail("Failed to throw exception when reading in an unsuported file media type.");
        } catch (AudioPlaybackException e){
            assertEquals(e.getMessage(), "Tried to use an invalid audio format. Please resort to using the .mp3 format.");
        }
   }

   /*   Test a false/fake input URL fails
        Check the appropriate exception is returned.  */
   @Test
   public void testIncorrectInputFails() {
        try {
            testAudio.playURLSound("fakeInput.mp3");
            fail("Failed to throw exception when reading in a fake input.");
        } catch (AudioPlaybackException e){
            assertEquals(e.getMessage(), "Failed to play audio from URL: fakeInput.mp3");
        }
   }

   @After
   public void tearDown(){
        player.stop();
        testAudio = null;
   }
}
