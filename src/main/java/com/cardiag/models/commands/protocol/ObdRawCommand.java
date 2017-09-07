package com.cardiag.models.commands.protocol;

/**
 * This class allows for an unspecified command to be sent.
 */
public class ObdRawCommand extends ObdProtocolCommand {

    /**
     * <p>Constructor for ObdRawCommand.</p>
     *
     * @param command a {@link String} object.
     */
    public ObdRawCommand(String command, Integer pos) {
        super(command);
    }


    @Override
    public String getFormattedResult() {
        return getResult();
    }


    @Override
    public String getName() {
        return "Custom command " + getCommandPID();
    }

}
