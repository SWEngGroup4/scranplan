package com.group4sweng.scranplan.SoundHandler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
/**
 * Small Unit Test to test the AudioURL class functions
 * Written 22/3/20 NBillis
 * (c) CoDev 2020
 */
public class AudioURLTest {

    @Test
    public void testPlayingURLSounds() throws Exception{
        AudioURL mAudio = new AudioURL();
        mAudio.playURLSound("https://bigsoundbank.com/UPLOAD/mp3/1631.mp3");
        Boolean result = mAudio.getPlay_URL();
        assertTrue(result);
    }

    @Test
    public void testNewURL() throws Exception{
        AudioURL mAudio = new AudioURL();
        mAudio.playURLSound("https://bigsoundbank.com/UPLOAD/mp3/1631.mp3");
        mAudio.storeURL("https://bigsoundbank.com/UPLOAD/mp3/1610.mp3");
        String result = mAudio.getStoredURL();
        assertEquals("https://bigsoundbank.com/UPLOAD/mp3/1610.mp3",result);
    }



    @Test
    public void testStoppingPlayingSounds() throws Exception{
        AudioURL mAudio = new AudioURL();
        mAudio.playURLSound("https://bigsoundbank.com/UPLOAD/mp3/1631.mp3");
        mAudio.stopURLSound();
        Boolean result = mAudio.getPlay_URL();
        assertFalse(result);
    }
    // Cannot test looping without a device - see androidTest/AudioTest.java for looping test

}