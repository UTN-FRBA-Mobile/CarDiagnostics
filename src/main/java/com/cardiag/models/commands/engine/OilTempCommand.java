package com.cardiag.models.commands.engine;

import com.cardiag.models.commands.temperature.TemperatureCommand;
import com.cardiag.utils.enums.AvailableCommandNames;

/**
 * Displays the current engine Oil temperature.
 *
 */
public class OilTempCommand extends TemperatureCommand {

    /**
     * Default ctor.
     */
    public OilTempCommand() {
        super("015C", 91);
    }

    public OilTempCommand(OilTempCommand other) {
        super(other);
    }

}
