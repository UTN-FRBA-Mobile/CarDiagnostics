package com.cardiag.models.commands.engine;

import android.graphics.Color;

import com.cardiag.models.commands.ObdCommand;
import com.cardiag.velocimetro.Velocimetro;

/**
 * Displays the current engine revolutions per minute (RPM).
 *
 */
public class RPMCommand extends ObdCommand {

    private int rpm = -1;

    /**
     * Default ctor.
     */
    public RPMCommand() {
        super("010C", 11);
        digitCount = 8;
    }

    public RPMCommand(RPMCommand other) {
        super(other);
    }

     
    @Override
    protected void performCalculations() {
        // ignore first two bytes [41 0C] of the response((A*256)+B)/4
        rpm = (buffer.get(2) * 256 + buffer.get(3)) / 4;
    }

    @Override
    public String getFormattedResult() {
        return String.format("%d%s", rpm, getResultUnit());
    }

     
    @Override
    public String getCalculatedResult() {
        return String.valueOf(rpm);
    }

     
    @Override
    public String getResultUnit() {
        return "RPM";
    }

    /**
     * <p>getRPM.</p>
     *
     * @return a int.
     */
    public int getRPM() {
        return rpm;
    }

    public void setVelocimetroProperties(Velocimetro velocimetro, double velocidadActual) {
        velocimetro.setMaxSpeed(11);
        velocimetro.setMajorTickStep(1);
        velocimetro.setMinorTicks(1);
        velocimetro.clearColoredRanges();
        velocimetro.addColoredRange(0, 4, Color.rgb(255,255,255));
        velocimetro.addColoredRange(4, 7, Color.rgb(59,131,189));
        velocimetro.addColoredRange(7, 11, Color.rgb(52,62,64));
        velocimetro.setUnitsTextSize(30);
        velocimetro.setUnitsText(getResultUnit() + " x1000");
        velocidadActual = velocidadActual/1000.0;

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
