package com.cardiag.models.exceptions;


/**
 * Thrown when there is a "BUS INIT... ERROR" message
 *
 */
public class BusInitException extends ResponseException {

    /**
     * <p>Constructor for BusInitException.</p>
     */
    public BusInitException() {
        super("BUS INIT... ERROR");
    }

}
