package com.group4sweng.scranplan.Exceptions;

/** Exception thrown when there is an error playing back audio from the audioURL class **/
public class AudioPlaybackError extends Exception {
    public AudioPlaybackError(String error){
        super(error);
    }
}
