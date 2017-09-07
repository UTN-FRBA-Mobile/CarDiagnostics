package com.cardiag.models.commands.pressure;

import com.cardiag.models.commands.ObdCommand;
import com.cardiag.models.commands.SystemOfUnits;

/**
 * Abstract pressure command.
 *
 */
public abstract class PressureCommand extends ObdCommand implements
        SystemOfUnits {

    protected int tempValue = 0;
    protected int pressure = 0;

    /**
     * Default ctor
     *
     * @param cmd a {@link String} object.
     */
    public PressureCommand(String cmd, Integer pos) {
        super(cmd, pos);
    }

    public PressureCommand(PressureCommand other) {
        super(other);
    }

    /**
     * Some PressureCommand subclasses will need to implement this method in
     * order to determine the final kPa value.
     * <p>
     * *NEED* to read tempValue
     *
     * @return a int.
     */
    protected int preparePressureValue() {
        return buffer.get(2);
    }

    /**
     * <p>performCalculations.</p>
     */
    protected void performCalculations() {
        // ignore first two bytes [hh hh] of the response
        pressure = preparePressureValue();
    }


    @Override
    public String getFormattedResult() {
        return useImperialUnits ? String.format("%.1f%s", getImperialUnit(), getResultUnit())
                : String.format("%d%s", pressure, getResultUnit());
    }

    /**
     * <p>getMetricUnit.</p>
     *
     * @return the pressure in kPa
     */
    public int getMetricUnit() {
        return pressure;
    }

    /**
     * <p>getImperialUnit.</p>
     *
     * @return the pressure in psi
     */
    public float getImperialUnit() {
        return pressure * 0.145037738F;
    }


    @Override
    public String getCalculatedResult() {
        return useImperialUnits ? String.valueOf(getImperialUnit()) : String.valueOf(pressure);
    }


    @Override
    public String getResultUnit() {
        return useImperialUnits ? "psi" : "kPa";
    }

}
