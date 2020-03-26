package com.group4sweng.scranplan.SoundHandler;

/**
 *  A collection of stock sounds with the format:
 *      NAME([SOUND_URL]) where [SOUND_URL] is the url of the associated file.
 *      To retrieve the sounds URL simply use StockSounds.NAME.getSoundURL().
 */
public enum StockSounds {
    DING("https://bigsoundbank.com/UPLOAD/mp3/1631.mp3"),

    //  Credits: https://freesound.org/people/V4cuum/sounds/348628/
    EGG_TIMER("https://firebasestorage.googleapis.com/v0/b/scran-plan-bc521.appspot.com/o/sounds%2Feggtimer.mp3?alt=media&token=6cedcd96-fb54-44e3-87a9-ec1d0b68f6fb"),

    // Credits: http://soundbible.com/1175-Two-Geese-On-A-Pond-.html
    GOOSE("https://firebasestorage.googleapis.com/v0/b/scran-plan-bc521.appspot.com/o/sounds%2FTwo%20Geese%20On%20A%20Pond%20-SoundBible.com-1289638194.wav?alt=media&token=0c728079-1105-49fc-8b9c-e7a0992d0b15");

    private String soundURL;
    StockSounds(String soundURL) {
        this.soundURL = soundURL;
    }

    public String getSoundURL() {
        return soundURL;
    }
}
