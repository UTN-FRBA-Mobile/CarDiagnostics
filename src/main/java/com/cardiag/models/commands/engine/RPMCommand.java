package com.cardiag.models.commands.engine;

import com.cardiag.models.commands.ObdCommand;
import com.cardiag.utils.enums.AvailableCommandNames;

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

}
