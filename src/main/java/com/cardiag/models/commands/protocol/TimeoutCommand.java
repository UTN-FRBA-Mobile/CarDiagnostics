package com.cardiag.models.commands.protocol;

/**
 * This will set the value of time in milliseconds (ms) that the OBD interface
 * will wait for a response from the ECU. If exceeds, the response is "NO DATA".
 *
 */
public class TimeoutCommand extends ObdProtocolCommand {

    /**
     * <p>Constructor for TimeoutCommand.</p>
     *
     * @param timeout value between 0 and 255 that multiplied by 4 results in the
     *                desired timeout in milliseconds (ms).
     */
    public TimeoutCommand(int timeout) {
        super("ATST" + Integer.toHexString(0xFF & timeout));
    }

    public TimeoutCommand(TimeoutCommand other) {
        super(other);
    }


    @Override
    public String getFormattedResult() {
        return getResult();
    }


    @Override
    public String getName() {
        return "Timeout";
    }

}
