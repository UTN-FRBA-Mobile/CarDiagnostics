package com.cardiag.models.commands.protocol;

/**
 * Warm-start the OBD connection.
 *
 */
public class ObdWarmstartCommand extends ObdProtocolCommand {

    /**
     * <p>Constructor for ObdWarmstartCommand.</p>
     */
    public ObdWarmstartCommand() {
        super("ATWS");
    }

    public ObdWarmstartCommand(ObdWarmstartCommand other) {
        super(other);
    }


    @Override
    public String getFormattedResult() {
        return getResult();
    }


    @Override
    public String getName() {
        return "Warmstart OBD";
    }

}
