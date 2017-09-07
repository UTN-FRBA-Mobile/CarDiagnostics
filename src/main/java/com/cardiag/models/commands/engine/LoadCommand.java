package com.cardiag.models.commands.engine;

import com.cardiag.models.commands.PercentageObdCommand;
import com.cardiag.utils.enums.AvailableCommandNames;

/**
 * Calculated Engine Load value.
 *
 */
public class LoadCommand extends PercentageObdCommand {

    public LoadCommand() {
        super("0104", 3);
    }

}
