package com.cardiag.models.commands.control;

import com.cardiag.models.commands.PercentageObdCommand;
import com.cardiag.utils.enums.AvailableCommandNames;

/**
 * Timing Advance
 *
 */
public class TimingAdvanceCommand extends PercentageObdCommand {

    /**
     * <p>Constructor for TimingAdvanceCommand.</p>
     */
    public TimingAdvanceCommand() {
        super("010E", 13);
    }

    public TimingAdvanceCommand(TimingAdvanceCommand other) {
        super(other);
    }

}
