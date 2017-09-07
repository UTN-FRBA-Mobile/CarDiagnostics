package com.cardiag.models.commands.engine;

import com.cardiag.models.commands.PercentageObdCommand;
import com.cardiag.utils.enums.AvailableCommandNames;

/**
 * <p>AbsoluteLoadCommand class.</p>
 *
 */
public class AbsoluteLoadCommand extends PercentageObdCommand {

    /**
     * Default ctor.
     */
    public AbsoluteLoadCommand() {
        super("0143", 66);
    }

    public AbsoluteLoadCommand(AbsoluteLoadCommand other) {
        super(other);
    }


    @Override
    protected void performCalculations() {
        // ignore first two bytes [hh hh] of the response
        int a = buffer.get(2);
        int b = buffer.get(3);
        percentage = (a * 256 + b) * 100 / 255;
    }

    /**
     * <p>getRatio.</p>
     *
     * @return a double.
     */
    public double getRatio() {
        return percentage;
    }

}
