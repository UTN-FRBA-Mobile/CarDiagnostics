package com.cardiag.models.commands.pressure;

import com.cardiag.utils.enums.AvailableCommandNames;

/**
 * <p>FuelPressureCommand class.</p>
 *
 */
public class FuelPressureCommand extends PressureCommand {

    /**
     * <p>Constructor for FuelPressureCommand.</p>
     */
    public FuelPressureCommand() {
        super("010A", 9);
    }

    public FuelPressureCommand(FuelPressureCommand other) {
        super(other);
    }

    /**
     * {@inheritDoc}
     * <p>
     * TODO describe of why we multiply by 3
     */
    @Override
    protected final int preparePressureValue() {
        return buffer.get(2) * 3;
    }

}
