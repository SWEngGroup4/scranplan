package com.group4sweng.scranplan.Exceptions;

public class InvalidUserException extends Exception{

    //  Occurs when no valid user is found even after register/login. May happen if Wifi or data is off.
    public InvalidUserException(String error){
        super(error);
    }
}
