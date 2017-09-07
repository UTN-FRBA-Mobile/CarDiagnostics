package com.cardiag.models.commands.temperature;

import com.cardiag.utils.enums.AvailableCommandNames;

/**
 * Engine Coolant Temperature.
 *
 */
public class EngineCoolantTemperatureCommand extends TemperatureCommand {

    /**
     * <p>Constructor for EngineCoolantTemperatureCommand.</p>
     */
    public EngineCoolantTemperatureCommand() {
        super("0105", 4);
    }

}
