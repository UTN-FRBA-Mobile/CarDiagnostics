package com.cardiag.models.commands.engine;

import android.graphics.Color;

import com.cardiag.models.commands.ObdCommand;
import com.cardiag.velocimetro.Velocimetro;

/**
 * Engine runtime.
 *
 */
public class RuntimeCommand extends ObdCommand {

    private int value = 0;

    /**
     * Default ctor.
     */
    public RuntimeCommand() {
        super("011F", 30);
        digitCount = 8;
    }

    public RuntimeCommand(RuntimeCommand other) {
        super(other);
    }


    @Override
    protected void performCalculations() {
        // ignore first two bytes [01 0C] of the response
        value = buffer.get(2) * 256 + buffer.get(3);
    }


    @Override
    public String getFormattedResult() {
        // determine time
        final String hh = String.format("%02d", value / 3600);
        final String mm = String.format("%02d", (value % 3600) / 60);
        final String ss = String.format("%02d", value % 60);
        return String.format("%s:%s:%s", hh, mm, ss);
    }


    @Override
    public String getCalculatedResult() {
        return String.valueOf(value);
    }


    @Override
    public String getResultUnit() {
        return "s";
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
            velocimetro.setSpeed(0, 0, 0);
        }
        if(velocidadActual > velocimetro.getMaxSpeed()){
            velocimetro.setSpeed(velocimetro.getMaxSpeed(), 0, 0);
        }
        if(velocidadActual >=  0 && velocidadActual <= velocimetro.getMaxSpeed()){
            velocimetro.setSpeed(velocidadActual, 0, 0);
        }
    }
}
