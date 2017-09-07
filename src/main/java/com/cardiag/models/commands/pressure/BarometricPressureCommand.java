package com.cardiag.models.commands.pressure;

import com.cardiag.utils.enums.AvailableCommandNames;

/**
 * Barometric pressure.
 *
 */
public class BarometricPressureCommand extends PressureCommand {

    /**
     * <p>Constructor for BarometricPressureCommand.</p>
     */
    public BarometricPressureCommand() {
        super("0133", 50);
    }

}
