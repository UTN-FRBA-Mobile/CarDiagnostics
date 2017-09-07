package com.cardiag.models.commands.protocol;

/**
 * Turns off line-feed.
 *
 */
public class LineFeedOffCommand extends ObdProtocolCommand {

    /**
     * <p>Constructor for LineFeedOffCommand.</p>
     */
    public LineFeedOffCommand() {
        super("ATL0");
    }

    public LineFeedOffCommand(LineFeedOffCommand other) {
        super(other);
    }


    @Override
    public String getFormattedResult() {
        return getResult();
    }


    @Override
    public String getName() {
        return "Line Feed Off";
    }

}
