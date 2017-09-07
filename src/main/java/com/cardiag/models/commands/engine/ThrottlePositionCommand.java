package com.cardiag.models.commands.engine;

import com.cardiag.models.commands.PercentageObdCommand;
import com.cardiag.utils.enums.AvailableCommandNames;

/**
 * Read the throttle position in percentage.
 *
 */
public class ThrottlePositionCommand extends PercentageObdCommand {

    /**
     * Default ctor.
     */
    public ThrottlePositionCommand() {
        super("0111", 16);
    }

}
