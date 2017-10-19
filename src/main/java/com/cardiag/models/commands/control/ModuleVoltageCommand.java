package com.cardiag.models.commands.control;

import android.graphics.Color;

import com.cardiag.models.commands.ObdCommand;
import com.cardiag.utils.enums.AvailableCommandNames;
import com.cardiag.velocimetro.Velocimetro;

/**
 * <p>ModuleVoltageCommand class.</p>
 *
 */
public class ModuleVoltageCommand extends ObdCommand {

    // Equivalent ratio (V)
    private double voltage = 0.00;

    /**
     * Default ctor.
     */
    public ModuleVoltageCommand() {
        super("0142", 65);
        digitCount = 8;
    }

    public ModuleVoltageCommand(ModuleVoltageCommand other) {
        super(other);
    }


    @Override
    protected void performCalculations() {
        // ignore first two bytes [hh hh] of the response
        int a = buffer.get(2);
        int b = buffer.get(3);
        voltage = (a * 256.0 + b) / 1000.0;
    }


    @Override
    public String getFormattedResult() {
        return String.format("%.1f%s", voltage, getResultUnit());
    }


    @Override
    public String getResultUnit() {
        return "V";
    }


    @Override
    public String getCalculatedResult() {
        return String.valueOf(voltage);
    }

    /**
     * <p>Getter for the field <code>voltage</code>.</p>
     *
     * @return a double.
     */
    public double getVoltage() {
        return voltage;
    }


    public void setVelocimetroProperties(Velocimetro velocimetro, double velocidadActual) {
        velocimetro.setMaxSpeed(50);
        velocimetro.setMajorTickStep(5);
        velocimetro.setMinorTicks(1);
        velocimetro.clearColoredRanges();
        velocimetro.addColoredRange(0, 15, Color.rgb(255,255,255));
        velocimetro.addColoredRange(15, 30, Color.rgb(59,131,189));
        velocimetro.addColoredRange(30, 50, Color.rgb(52,62,64));
        velocimetro.setUnitsTextSize(30);
        velocimetro.setUnitsText(getResultUnit());
        if(velocidadActual < 0) {
            velocimetro.setSpeed(0, 100, 300);
        }
        if(velocidadActual > velocimetro.getMaxSpeed()){
            velocimetro.setSpeed(velocimetro.getMaxSpeed(), 100, 300);
        }
        if(velocidadActual >=  0 && velocidadActual <= velocimetro.getMaxSpeed()){
            velocimetro.setSpeed(velocidadActual, 100, 300);
        }
    }
}
