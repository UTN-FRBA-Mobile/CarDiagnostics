package com.cardiag.models.commands.temperature;

import com.cardiag.models.commands.ObdCommand;
import com.cardiag.models.commands.SystemOfUnits;

/**
 * Abstract temperature command.
 *
 */
public abstract class TemperatureCommand extends ObdCommand implements
        SystemOfUnits {

    private float temperature = 0.0f;
    /**
     * Default ctor.
     *
     * @param cmd a {@link String} object.
     */
    public TemperatureCommand(String cmd, Integer pos) {
        super(cmd, pos);
    }

    public TemperatureCommand(TemperatureCommand other) {
        super(other);
    }


    @Override
    protected void performCalculations() {
        // ignore first two bytes [hh hh] of the response
        temperature = buffer.get(2) - 40;
    }


    /**
     * {@inheritDoc}
     * <p>
     * Get values from 'buff', since we can't rely on char/string for
     * calculations.
     */
    @Override
    public String getFormattedResult() {
        return useImperialUnits ? String.format("%.1f%s", getImperialUnit(), getResultUnit())
                : String.format("%.0f%s", temperature, getResultUnit());
    }


    @Override
    public String getCalculatedResult() {
        return useImperialUnits ? String.valueOf(getImperialUnit()) : String.valueOf(temperature);
    }


    @Override
    public String getResultUnit() {
        return useImperialUnits ? "F" : "C";
    }

    /**
     * <p>Getter for the field <code>temperature</code>.</p>
     *
     * @return the temperature in Celsius.
     */
    public float getTemperature() {
        return temperature;
    }

    /**
     * <p>getImperialUnit.</p>
     *
     * @return the temperature in Fahrenheit.
     */
    public float getImperialUnit() {
        return temperature * 1.8f + 32;
    }

    /**
     * <p>getKelvin.</p>
     *
     * @return the temperature in Kelvin.
     */
    public float getKelvin() {
        return temperature + 273.15f;
    }

}
