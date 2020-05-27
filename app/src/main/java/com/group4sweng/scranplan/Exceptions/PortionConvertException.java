package com.group4sweng.scranplan.Exceptions;

/** Exception thrown when there is an error converting portion amounts **/
public class PortionConvertException extends Exception {
    public PortionConvertException(String error){
        super(error);
    }
}
