package com.cardiag.models.commands.protocol;

import com.cardiag.utils.enums.AvailableCommandNames;

/**
 * Retrieve available PIDs ranging from 21 to 40.
 *
 */
public class AvailablePidsCommand_21_40 extends AvailablePidsCommand {

    /**
     * Default ctor.
     */
    public AvailablePidsCommand_21_40() {
        super("0120", 31);
    }

    public AvailablePidsCommand_21_40(AvailablePidsCommand_21_40 other) {
        super(other);
    }


    @Override
    public String getName() {
        return AvailableCommandNames.PIDS_21_40.getValue();
    }
}
