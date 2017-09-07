package com.cardiag.models.exceptions;

/**
 * Thrown when there is "ERROR" in the response
 *
 */
public class UnknownErrorException extends ResponseException {

    /**
     * <p>Constructor for UnknownErrorException.</p>
     */
    public UnknownErrorException() {
        super("ERROR");
    }

}
