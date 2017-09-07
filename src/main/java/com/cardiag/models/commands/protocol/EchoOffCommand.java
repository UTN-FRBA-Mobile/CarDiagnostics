package com.cardiag.models.commands.protocol;

/**
 * Turn-off echo.
 *
 */
public class EchoOffCommand extends ObdProtocolCommand {

    /**
     * <p>Constructor for EchoOffCommand.</p>
     */
    public EchoOffCommand() {
        super("ATE0");
    }

    public EchoOffCommand(EchoOffCommand other) {
        super(other);
    }


    @Override
    public String getFormattedResult() {
        return getResult();
    }


    @Override
    public String getName() {
        return "Echo Off";
    }

}
