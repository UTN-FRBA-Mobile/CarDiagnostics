package com.cardiag.models.commands.engine;

import com.cardiag.models.commands.ObdCommand;
import com.cardiag.utils.enums.AvailableCommandNames;

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

}
