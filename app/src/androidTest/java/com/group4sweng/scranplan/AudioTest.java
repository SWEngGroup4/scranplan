package com.group4sweng.scranplan;

import android.media.MediaPlayer;
import android.os.Looper;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.group4sweng.scranplan.Exceptions.AudioPlaybackError;
import com.group4sweng.scranplan.Presentation.PresentationTimer;
import com.group4sweng.scranplan.SoundHandler.AudioURL;
import com.group4sweng.scranplan.SoundHandler.StockSounds;

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
public class AudioTest extends RecordedEspressoHelper {

    /*
    @Rule
    public ActivityTestRule<PublicProfile> mActivityTestRule = new ActivityTestRule<PublicProfile>(PublicProfile.class);
*/
    private String SOUND_URL = "https://bigsoundbank.com/UPLOAD/mp3/1631.mp3";
    private String SOUND_URL_MP2 = "https://firebasestorage.googleapis.com/v0/b/scran-plan-bc521.appspot.com/o/sounds%2Ftest_wrong_format_eggtimer.mp2?alt=media&token=a34e8c38-16d4-4d9c-85b3-85da48bdd542";
    private String SOUND_URL_FLAC = "https://firebasestorage.googleapis.com/v0/b/scran-plan-bc521.appspot.com/o/sounds%2Ftest_wrong_format_eggtimer.flac?alt=media&token=66be31ab-f20b-49b4-8ddc-a2edeae71512";
    private int THREAD_SLEEP_TIME = 3000;
    private AudioURL testAudio;
    private MediaPlayer player;

    @Before
    public void setUp(){
        testAudio = new AudioURL();
        player = testAudio.getPlayer();
    }

    @Test
    public void testSoundLoads() throws InterruptedException {
        try {
            testAudio.playURLSound(SOUND_URL);
        } catch (AudioPlaybackError e) {
            e.printStackTrace();
        }

        player = testAudio.getPlayer();
        assertTrue(player.isPlaying());

        Thread.sleep(THREAD_SLEEP_TIME);
    }

   @Test
   public void testSoundStops() throws InterruptedException {
        testSoundLoads();

        testAudio.stopURLSound();
        player = testAudio.getPlayer();
        assertFalse(player.isPlaying());
   }

   @Test
   public void testLoopingSound() throws InterruptedException, AudioPlaybackError {
        testAudio.setLooping(true);
        player = testAudio.getPlayer();

        try {
            testAudio.playURLSound(StockSounds.EGG_TIMER.getSoundURL());
        } catch (AudioPlaybackError e){
            e.printStackTrace();
        }
        player.getDuration();

        Thread.sleep(testAudio.getPlayer().getDuration() + THREAD_SLEEP_TIME/2);

        if(!player.isPlaying()){
            fail("Player has not looped.");
        }
   }

   @Test
   public void testUnsupportedAndroidInputFormatFails() throws InterruptedException {
        try {
            testAudio.playURLSound(SOUND_URL_MP2);
            fail("Failed to throw exception when reading in an unsuported file media type.");
        } catch (AudioPlaybackError e){
            assertEquals(e.getMessage(), "Tried to use an invalid audio format. Please resort to using the .mp3 format.");
        }
   }

   @Test
   public void testUnsupportedAppInputFormatFails() {
        try {
            testAudio.playURLSound(SOUND_URL_FLAC);
            fail("Failed to throw exception when reading in an unsuported file media type.");
        } catch (AudioPlaybackError e){
            assertEquals(e.getMessage(), "Tried to use an invalid audio format. Please resort to using the .mp3 format.");
        }
   }

   @Test
   public void testIncorrectInputFails() {
        try {
            testAudio.playURLSound("fakeInput.mp3");
            fail("Failed to throw exception when reading in a fake input.");
        } catch (AudioPlaybackError e){
            assertEquals(e.getMessage(), "Failed to play audio from URL: fakeInput.mp3");
        }
   }


   @Test
   public void testAudioPlaybackTimer() throws InterruptedException {
        testAudio.setLooping(true);

        Looper.prepare();
        PresentationTimer timer = new PresentationTimer(4000, 1000, SOUND_URL);
        player = testAudio.getPlayer();
        assertFalse(player.isPlaying());

        Thread.sleep(10000);
        //assertTrue(player.isPlaying());
   }

   @After
   public void tearDown(){
        player.stop();
        testAudio = null;
   }
}
