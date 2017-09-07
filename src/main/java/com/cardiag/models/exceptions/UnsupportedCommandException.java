package com.cardiag.models.exceptions;

/**
 * Thrown when there is a "?" message.
 *
 */
public class UnsupportedCommandException extends ResponseException {

    /**
     * <p>Constructor for UnsupportedCommandException.</p>
     */
    public UnsupportedCommandException() {
        super("7F 0[0-A] 1[1-2]", true);
    }

}
