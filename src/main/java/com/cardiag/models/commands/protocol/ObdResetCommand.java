package com.cardiag.models.commands.protocol;

/**
 * Reset the OBD connection.
 *
 */
public class ObdResetCommand extends ObdProtocolCommand {

    /**
     * <p>Constructor for ObdResetCommand.</p>
     */
    public ObdResetCommand() {
        super("ATZ");
    }

    public ObdResetCommand(ObdResetCommand other) {
        super(other);
    }


    @Override
    public String getFormattedResult() {
        return getResult();
    }


    @Override
    public String getName() {
        return "Reset OBD";
    }

}
