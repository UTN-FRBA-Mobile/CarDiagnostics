package com.cardiag.models.exceptions;

/**
 * Created by Leo on 15/10/2017.
 */

public class BadResponseException extends ResponseException {

    public BadResponseException(){
        super();
    }

    public BadResponseException(String msg){
        super(msg);
    }
}
