package com.group4sweng.scranplan.Exceptions.UI;

public class BlankInputException extends Exception{

    //  Tests if a text input field is blank or == null.
    BlankInputException(String error){
        super(error);
    }
}
