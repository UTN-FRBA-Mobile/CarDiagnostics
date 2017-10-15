package com.cardiag.models.exceptions;

/**
 * Generic message error
 *
 */
public class ResponseException extends RuntimeException {

    private String message;

    private String response;

    private String command;

    private boolean matchRegex;

    /**
     * <p>Constructor for ResponseException.</p>
     *
     * @param message a {@link String} object.
     */
    public ResponseException(String message) {
        this.message = message;
    }

    public ResponseException() {
    }

    /**
     * <p>Constructor for ResponseException.</p>
     *
     * @param message a {@link String} object.
     * @param matchRegex a boolean.
     */
    protected ResponseException(String message, boolean matchRegex) {
        this.message = message;
        this.matchRegex = matchRegex;
    }

    private static String clean(String s) {
        return s == null ? "" : s.replaceAll("\\s", "").toUpperCase();
    }

    /**
     * <p>isError.</p>
     *
     * @param response a {@link String} object.
     * @return a boolean.
     */
    public boolean isError(String response) {
        this.response = response;
        if (matchRegex) {
            return clean(response).matches(clean(message));
        } else {
            return clean(response).contains(clean(message));
        }
    }

    /**
     * <p>Setter for the field <code>command</code>.</p>
     *
     * @param command a {@link String} object.
     */
    public void setCommand(String command) {
        this.command = command;
    }


    @Override
    public String getMessage() {
        return "Error running " + command + ", response: " + response;
    }

}
