package com.cardiag.models.commands.pressure;

import android.graphics.Color;

import com.cardiag.models.commands.ObdCommand;
import com.cardiag.models.commands.SystemOfUnits;
import com.cardiag.velocimetro.Velocimetro;

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

    public void setVelocimetroProperties(Velocimetro velocimetro, double velocidadActual) {
        velocimetro.setMaxSpeed(10);
        velocimetro.setMajorTickStep(1);
        velocimetro.setMinorTicks(1);
        velocimetro.clearColoredRanges();
        velocimetro.addColoredRange(0, 4, Color.rgb(255,255,255));
        velocimetro.addColoredRange(4, 7, Color.rgb(59,131,189));
        velocimetro.addColoredRange(7, 10, Color.rgb(52,62,64));
        velocimetro.setUnitsTextSize(30);
        velocimetro.setUnitsText(getResultUnit() + " x100");

        velocidadActual = velocidadActual/100.0;
        if(velocidadActual < 0) {
            velocimetro.setSpeed(0, 100, 300);
        }
        if(velocidadActual > velocimetro.getMaxSpeed()){
            velocimetro.setSpeed(velocimetro.getMaxSpeed(), 100, 300);
        }
        if(velocidadActual >=  0 && velocidadActual < velocimetro.getMaxSpeed()){
            velocimetro.setSpeed(velocidadActual, 100, 300);
        }
    }
}
