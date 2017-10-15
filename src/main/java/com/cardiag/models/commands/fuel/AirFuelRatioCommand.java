package com.cardiag.models.commands.fuel;

import com.cardiag.models.commands.ObdCommand;
import com.cardiag.utils.enums.AvailableCommandNames;

/**
 * AFR
 *
 */
public class AirFuelRatioCommand extends ObdCommand {

    private float afr = 0;

    /**
     * <p>Constructor for AirFuelRatioCommand.</p>
     */
    public AirFuelRatioCommand() {
        super("0144", 67);
        digitCount = 8;
    }


    @Override
    protected void performCalculations() {
        // ignore first two bytes [01 44] of the response
        float A = buffer.get(2);
        float B = buffer.get(3);
        afr = (((A * 256) + B) / 32768) * 14.7f;//((A*256)+B)/32768
    }


    @Override
    public String getFormattedResult() {
        return String.format("%.2f", getAirFuelRatio()) + ":1 AFR";
    }


    @Override
    public String getCalculatedResult() {
        return String.valueOf(getAirFuelRatio());
    }

    /**
     * <p>getAirFuelRatio.</p>
     *
     * @return a double.
     */
    public double getAirFuelRatio() {
        return afr;
    }

}
