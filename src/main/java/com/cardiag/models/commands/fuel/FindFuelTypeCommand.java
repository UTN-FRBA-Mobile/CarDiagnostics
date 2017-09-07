package com.cardiag.models.commands.fuel;

import com.cardiag.models.commands.ObdCommand;
import com.cardiag.utils.enums.AvailableCommandNames;
import com.cardiag.utils.enums.FuelType;

/**
 * This command is intended to determine the vehicle fuel type.
 *
 */
public class FindFuelTypeCommand extends ObdCommand {

    private int fuelType = 0;

    /**
     * Default ctor.
     */
    public FindFuelTypeCommand() {
        super("0151", 80);
    }

    public FindFuelTypeCommand(FindFuelTypeCommand other) {
        super(other);
    }


    @Override
    protected void performCalculations() {
        // ignore first two bytes [hh hh] of the response
        fuelType = buffer.get(2);
    }


    @Override
    public String getFormattedResult() {
        try {
            return FuelType.fromValue(fuelType).getDescription();
        } catch (NullPointerException e) {
            return "-";
        }
    }


    @Override
    public String getCalculatedResult() {
        return String.valueOf(fuelType);
    }

}
