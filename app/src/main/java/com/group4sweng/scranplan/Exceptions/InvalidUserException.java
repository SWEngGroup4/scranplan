package com.group4sweng.scranplan.Exceptions;

/** Exception thrown when no valid user credentials can be retrieved from Firebase, **/
public class InvalidUserException extends Exception{
    public InvalidUserException(String error){
        super(error);
    }
}
