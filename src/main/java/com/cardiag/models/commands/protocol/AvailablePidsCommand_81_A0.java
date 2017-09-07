package com.cardiag.models.commands.protocol;

import com.cardiag.utils.enums.AvailableCommandNames;

/**
 * Created by Leo on 28/7/2017.
 */

public class AvailablePidsCommand_81_A0 extends AvailablePidsCommand {
        /**
         * Default ctor.
         */
        public AvailablePidsCommand_81_A0() {
            super("0180", 127);
        }

        public AvailablePidsCommand_81_A0(AvailablePidsCommand_81_A0 other) {
            super(other);
        }


        @Override
        public String getName() {
            return AvailableCommandNames.PIDS_81_A0.getValue();
        }
    }