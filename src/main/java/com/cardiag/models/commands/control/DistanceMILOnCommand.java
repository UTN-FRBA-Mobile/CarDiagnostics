package com.cardiag.models.commands.control;

import android.graphics.Color;

import com.cardiag.models.commands.ObdCommand;
import com.cardiag.models.commands.SystemOfUnits;
import com.cardiag.velocimetro.Velocimetro;

/**
 * <p>DistanceMILOnCommand class.</p>
 *
 */
public class DistanceMILOnCommand extends ObdCommand
        implements SystemOfUnits {

    private int km = 0;

    /**
     * Default ctor.
     */
    public DistanceMILOnCommand() {
        super("0121", 32);
        digitCount = 8;
    }

    public DistanceMILOnCommand(
            DistanceMILOnCommand other) {
        super(other);
    }


    @Override
    protected void performCalculations() {
        // ignore first two bytes [01 31] of the response
        km = buffer.get(2) * 256 + buffer.get(3);
    }

    /**
     * <p>getFormattedResult.</p>
     *
     * @return a {@link String} object.
     */
    public String getFormattedResult() {
        return useImperialUnits ? String.format("%.2f%s", getImperialUnit(), getResultUnit())
                : String.format("%d%s", km, getResultUnit());
    }


    @Override
    public String getCalculatedResult() {
        return useImperialUnits ? String.valueOf(getImperialUnit()) : String.valueOf(km);
    }


    @Override
    public String getResultUnit() {
        return useImperialUnits ? "m" : "km";
    }


    @Override
    public float getImperialUnit() {
        return km * 0.621371192F;
    }

    /**
     * <p>Getter for the field <code>km</code>.</p>
     *
     * @return a int.
     */
    public int getKm() {
        return km;
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
        velocimetro.setUnitsText(getResultUnit() + " x10000");

        velocidadActual = velocidadActual/10000.0;


        if(velocidadActual < 0) {
            velocimetro.setSpeed(0, 100, 0);
        }
        if(velocidadActual > velocimetro.getMaxSpeed()){
            velocimetro.setSpeed(velocimetro.getMaxSpeed(), 100, 0);
        }
        if(velocidadActual >=  0 && velocidadActual <= velocimetro.getMaxSpeed()){
            velocimetro.setSpeed(velocidadActual, 100, 0);
        }
    }
}
