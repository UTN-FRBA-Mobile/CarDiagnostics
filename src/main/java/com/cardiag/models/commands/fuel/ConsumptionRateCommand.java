package com.cardiag.models.commands.fuel;

import com.cardiag.models.commands.ObdCommand;
import com.cardiag.utils.enums.AvailableCommandNames;

/**
 * Fuel Consumption Rate per hour.
 *
 */
public class ConsumptionRateCommand extends ObdCommand {

    private float fuelRate = -1.0f;

    /**
     * <p>Constructor for ConsumptionRateCommand.</p>
     */
    public ConsumptionRateCommand() {
        super("015E", 93);
        digitCount = 8;
    }

    public ConsumptionRateCommand(ConsumptionRateCommand other) {
        super(other);
    }


    @Override
    protected void performCalculations() {
        // ignore first two bytes [hh hh] of the response
        fuelRate = (buffer.get(2) * 256 + buffer.get(3)) * 0.05f;
    }


    @Override
    public String getFormattedResult() {
        return String.format("%.1f%s", fuelRate, getResultUnit());
    }


    @Override
    public String getCalculatedResult() {
        return String.valueOf(fuelRate);
    }


    @Override
    public String getResultUnit() {
        return "L/h";
    }

    /**
     * <p>getLitersPerHour.</p>
     *
     * @return a float.
     */
    public float getLitersPerHour() {
        return fuelRate;
    }

}
