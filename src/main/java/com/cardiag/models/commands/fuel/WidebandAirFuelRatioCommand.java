package com.cardiag.models.commands.fuel;

import android.graphics.Color;

import com.cardiag.models.commands.ObdCommand;
import com.cardiag.velocimetro.Velocimetro;

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
        digitCount = 8;
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
    public String getResultUnit() {
        return "AFR";
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
