package com.cardiag.models.commands.protocol;

/**
 * Turn-off headers.
 *
 */
public class HeadersOffCommand extends ObdProtocolCommand {

    /**
     * <p>Constructor for HeadersOffCommand.</p>
     */
    public HeadersOffCommand() {
        super("ATH0");
    }

    public HeadersOffCommand(HeadersOffCommand other) {
        super(other);
    }


    @Override
    public String getFormattedResult() {
        return getResult();
    }


    @Override
    public String getName() {
        return "Headers disabled";
    }

}
