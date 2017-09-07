package com.cardiag.models.commands.control;

import com.cardiag.models.commands.ObdCommand;
import com.cardiag.utils.enums.AvailableCommandNames;

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
    }

    public ModuleVoltageCommand(ModuleVoltageCommand other) {
        super(other);
    }


    @Override
    protected void performCalculations() {
        // ignore first two bytes [hh hh] of the response
        int a = buffer.get(2);
        int b = buffer.get(3);
        voltage = (a * 256 + b) / 1000;
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

}
