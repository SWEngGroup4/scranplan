package com.group4sweng.scranplan;

public class UserAccountDosentExistException extends Exception {
    public UserAccountDosentExistException(String errorMessage, Throwable err){
        super(errorMessage, err);
    }
}
