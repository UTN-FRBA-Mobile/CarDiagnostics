package com.cardiag.models.commands.protocol;

import com.cardiag.models.commands.ObdCommand;

/**
 * <p>Abstract ObdProtocolCommand class.</p>
 */
public abstract class ObdProtocolCommand extends ObdCommand {
    /**
     * Default ctor to use
     *
     * @param command the command to send
     */
    public ObdProtocolCommand(String command) {
        super(command, 1000);
    }

    /**
     * Copy ctor.
     *
     * @param other the ObdCommand to copy.
     */
    public ObdProtocolCommand(ObdProtocolCommand other) {
        this(other.cmd);
    }

    /**
     * <p>performCalculations.</p>
     */
    protected void performCalculations() {
        // ignore
    }

    /**
     * <p>fillBuffer.</p>
     */
    protected void fillBuffer() {
        // settings commands don't return a value appropriate to place into the
        // buffer, so do nothing
    }


    @Override
    public String getCalculatedResult() {
        return String.valueOf(getResult());
    }
}
