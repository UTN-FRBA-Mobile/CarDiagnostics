package com.cardiag.models.commands.control;

import com.cardiag.models.commands.ObdCommand;
import com.cardiag.utils.enums.AvailableCommandNames;

public class IgnitionMonitorCommand extends ObdCommand {

    private boolean ignitionOn = false;

    /**
     * Default ctor.
     */
    public IgnitionMonitorCommand() {
        super("ATIGN",1000);
    }

    /**
     * Copy ctor.
     *
     * @param other a {@link IgnitionMonitorCommand} object.
     */
    public IgnitionMonitorCommand(IgnitionMonitorCommand other) {
        super(other);
    }

    @Override
    protected void performCalculations() {
        final String result = getResult();
        ignitionOn = result.equalsIgnoreCase("ON");
    }

    @Override
    public String getFormattedResult() {
        return getResult();
    }

    @Override
    public String getName() {
        return AvailableCommandNames.IGNITION_MONITOR.getValue();
    }

    @Override
    public String getCalculatedResult() {
        return getResult();
    }

    @Override
    protected void fillBuffer() {
    }

    public boolean isIgnitionOn() {
        return ignitionOn;
    }
}


