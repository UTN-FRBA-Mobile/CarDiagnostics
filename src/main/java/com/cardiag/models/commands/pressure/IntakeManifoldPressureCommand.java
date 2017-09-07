package com.cardiag.models.commands.pressure;

import com.cardiag.utils.enums.AvailableCommandNames;

/**
 * Intake Manifold Pressure
 *
 */
public class IntakeManifoldPressureCommand extends PressureCommand {

    /**
     * Default ctor.
     */
    public IntakeManifoldPressureCommand() {
        super("010B", 10);
    }

}
