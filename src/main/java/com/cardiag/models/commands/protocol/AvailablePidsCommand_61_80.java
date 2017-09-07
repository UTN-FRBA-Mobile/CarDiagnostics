package com.cardiag.models.commands.protocol;

import com.cardiag.utils.enums.AvailableCommandNames;

/**
 * Created by Leo on 28/7/2017.
 */

public class AvailablePidsCommand_61_80 extends AvailablePidsCommand{

    /**
     * Default ctor.
     */
    public AvailablePidsCommand_61_80() {
        super("0160", 95);
    }

    public AvailablePidsCommand_61_80(AvailablePidsCommand_61_80 other) {
        super(other);
    }


    @Override
    public String getName() {
        return AvailableCommandNames.PIDS_61_80.getValue();
    }
}