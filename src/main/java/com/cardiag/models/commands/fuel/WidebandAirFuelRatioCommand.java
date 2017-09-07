package com.cardiag.models.commands.fuel;

import com.cardiag.models.commands.ObdCommand;
import com.cardiag.utils.enums.AvailableCommandNames;

/**
 * Wideband AFR
 *
 */
public class WidebandAirFuelRatioCommand extends ObdCommand {

    private float wafr = 0;

    /**
     * <p>Constructor for WidebandAirFuelRatioCommand.</p>
     */
    public WidebandAirFuelRatioCommand() {
        super("0134", 51);
    }


    @Override
    protected void performCalculations() {
        // ignore first two bytes [01 44] of the response
        float A = buffer.get(2);
        float B = buffer.get(3);
        wafr = (((A * 256) + B) / 32768) * 14.7f;//((A*256)+B)/32768
    }


    @Override
    public String getFormattedResult() {
        return String.format("%.2f", getWidebandAirFuelRatio()) + ":1 AFR";
    }


    @Override
    public String getCalculatedResult() {
        return String.valueOf(getWidebandAirFuelRatio());
    }

    /**
     * <p>getWidebandAirFuelRatio.</p>
     *
     * @return a double.
     */
    public double getWidebandAirFuelRatio() {
        return wafr;
    }

}
