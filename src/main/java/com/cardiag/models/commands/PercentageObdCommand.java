package com.cardiag.models.commands;


import android.graphics.Color;

import com.cardiag.velocimetro.Velocimetro;

/**
 * Abstract class for percentage commands.
 *
 */
public abstract class PercentageObdCommand extends ObdCommand {

    protected float percentage = 0f;

    /**
     * <p>Constructor for PercentageObdCommand.</p>
     *
     * @param command a {@link String} object.
     */
    public PercentageObdCommand(String command, Integer pos) {
        super(command, pos);
    }

    public PercentageObdCommand(PercentageObdCommand other) {
        super(other);
    }


    @Override
    protected void performCalculations() {
        // ignore first two bytes [hh hh] of the response
        percentage = (buffer.get(2) * 100.0f) / 255.0f;
    }


    @Override
    public String getFormattedResult() {
        return String.format("%.1f%s", percentage, getResultUnit());
    }

    /**
     * <p>Getter for the field <code>percentage</code>.</p>
     *
     * @return a float.
     */
    public float getPercentage() {
        return percentage;
    }


    @Override
    public String getResultUnit() {
        return "%";
    }


    @Override
    public String getCalculatedResult() {
        return String.valueOf(percentage);
    }

    public void setVelocimetroProperties (Velocimetro velocimetro) {
                velocimetro.setMaxSpeed(100);
                velocimetro.setMajorTickStep(20);
                velocimetro.setMinorTicks(3);
                velocimetro.clearColoredRanges();
                velocimetro.addColoredRange(0, 30, Color.rgb(255,255,255));
                velocimetro.addColoredRange(30, 70, Color.rgb(59,131,189));
                velocimetro.addColoredRange(70, 100, Color.rgb(52,62,64));
                velocimetro.setUnitsText(getResultUnit());
                velocimetro.setUnitsTextSize(50);
    }
}
