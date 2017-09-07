package com.cardiag.models.commands;

import com.cardiag.utils.enums.AvailableCommandNames;

/**
 * Current speed.
 *
 */
public class SpeedCommand extends ObdCommand implements SystemOfUnits {

    private int metricSpeed = 0;

    /**
     * Default ctor.
     */
    public SpeedCommand() {
        super("010D", 12);
    }

    public SpeedCommand(SpeedCommand other) {
        super(other);
    }


    @Override
    protected void performCalculations() {
        // Ignore first two bytes [hh hh] of the response.
        metricSpeed = buffer.get(2);
    }

    /**
     * <p>Getter for the field <code>metricSpeed</code>.</p>
     *
     * @return the speed in metric units.
     */
    public int getMetricSpeed() {
        return metricSpeed;
    }

    /**
     * <p>getImperialSpeed.</p>
     *
     * @return the speed in imperial units.
     */
    public float getImperialSpeed() {
        return getImperialUnit();
    }

    /**
     * Convert from km/h to mph
     *
     * @return a float.
     */
    public float getImperialUnit() {
        return metricSpeed * 0.621371192F;
    }

    /**
     * <p>getFormattedResult.</p>
     *
     * @return a {@link String} object.
     */
    public String getFormattedResult() {
        return useImperialUnits ? String.format("%.2f%s", getImperialUnit(), getResultUnit())
                : String.format("%d%s", getMetricSpeed(), getResultUnit());
    }


    @Override
    public String getCalculatedResult() {
        return useImperialUnits ? String.valueOf(getImperialUnit()) : String.valueOf(getMetricSpeed());
    }


    @Override
    public String getResultUnit() {
        return useImperialUnits ? "mph" : "km/h";
    }

}
