package com.cardiag.models.commands.protocol;

import com.cardiag.utils.enums.AvailableCommandNames;

/**
 * Retrieve available PIDs ranging from 41 to 60.
 *
 */
public class AvailablePidsCommand_41_60 extends AvailablePidsCommand {

    /**
     * Default ctor.
     */
    public AvailablePidsCommand_41_60() {
        super("0140", 63);
    }

    public AvailablePidsCommand_41_60(AvailablePidsCommand_41_60 other) {
        super(other);
    }


    @Override
    public String getName() {
        return AvailableCommandNames.PIDS_41_60.getValue();
    }
}
