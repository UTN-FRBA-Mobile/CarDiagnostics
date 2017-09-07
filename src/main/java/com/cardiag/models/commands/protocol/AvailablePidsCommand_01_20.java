package com.cardiag.models.commands.protocol;

import com.cardiag.utils.enums.AvailableCommandNames;

/**
 * Retrieve available PIDs ranging from 01 to 20.
 *
 */
public class AvailablePidsCommand_01_20 extends AvailablePidsCommand {

    /**
     * Default ctor.
     */
    public AvailablePidsCommand_01_20() {
        super("0100", 0);
    }

    public AvailablePidsCommand_01_20(AvailablePidsCommand_01_20 other) {
        super(other);
    }


    @Override
    public String getName() {
        return AvailableCommandNames.PIDS_01_20.getValue();
    }
}
