package com.cardiag.models.exceptions;

/**
 * Thrown when there is a "?" message.
 *
 */
public class MisunderstoodCommandException extends ResponseException {

    /**
     * <p>Constructor for MisunderstoodCommandException.</p>
     */
    public MisunderstoodCommandException() {
        super("?");
    }

}
