package com.cardiag.models.exceptions;

/**
 * Sent when there is a "STOPPED" message.
 *
 */
public class StoppedException extends ResponseException {

    /**
     * <p>Constructor for StoppedException.</p>
     */
    public StoppedException() {
        super("STOPPED");
    }

}
