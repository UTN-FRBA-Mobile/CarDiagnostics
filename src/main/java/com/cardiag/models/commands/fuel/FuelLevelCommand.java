package com.cardiag.models.commands.fuel;

import com.cardiag.models.commands.PercentageObdCommand;
import com.cardiag.utils.enums.AvailableCommandNames;

/**
 * Get fuel level in percentage
 *
 */
public class FuelLevelCommand extends PercentageObdCommand {

    /**
     * <p>Constructor for FuelLevelCommand.</p>
     */
    public FuelLevelCommand() {
        super("012F", 46);
    }


    @Override
    protected void performCalculations() {
        // ignore first two bytes [hh hh] of the response
        percentage = 100.0f * buffer.get(2) / 255.0f;
    }

    /**
     * <p>getFuelLevel.</p>
     *
     * @return a float.
     */
    public float getFuelLevel() {
        return percentage;
    }

}
