package com.group4sweng.scranplan.Exceptions;

/** Exception thrown when there is an error playing back audio from the audioURL class **/
public class AudioPlaybackException extends Exception {
    public AudioPlaybackException(String error){
        super(error);
    }
}
