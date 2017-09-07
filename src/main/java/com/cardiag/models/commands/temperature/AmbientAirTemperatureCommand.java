package com.cardiag.models.commands.temperature;

import com.cardiag.utils.enums.AvailableCommandNames;

/**
 * Ambient Air Temperature.
 *
 */
public class AmbientAirTemperatureCommand extends TemperatureCommand {

    /**
     * <p>Constructor for AmbientAirTemperatureCommand.</p>
     */
    public AmbientAirTemperatureCommand() {
        super("0146", 71);
    }

    public AmbientAirTemperatureCommand(TemperatureCommand other) {
        super(other);
    }

}
