package com.cardiag.models.commands.control;

import com.cardiag.models.commands.PercentageObdCommand;
import com.cardiag.utils.enums.AvailableCommandNames;

/**
 * Fuel systems that use conventional oxygen sensor display the commanded open
 * loop equivalence ratio while the system is in open loop. Should report 100%
 * when in closed loop fuel.
 * <p>
 * To obtain the actual air/fuel ratio being commanded, multiply the
 * stoichiometric A/F ratio by the equivalence ratio. For example, gasoline,
 * stoichiometric is 14.64:1 ratio. If the fuel control system was commanded an
 * equivalence ratio of 0.95, the commanded A/F ratio to the engine would be
 * 14.64 * 0.95 = 13.9 A/F.
 *
 */
public class EquivalentRatioCommand extends PercentageObdCommand {


    /**
     * Default ctor.
     */
    public EquivalentRatioCommand() {
        super("0144", 67);
    }

    public EquivalentRatioCommand(EquivalentRatioCommand other) {
        super(other);
    }


    @Override
    protected void performCalculations() {
        // ignore first two bytes [hh hh] of the response
        int a = buffer.get(2);
        int b = buffer.get(3);
        percentage = (a * 256 + b) / 32768;
    }


    /**
     * <p>getRatio.</p>
     *
     * @return a double.
     */
    public double getRatio() {
        return (double) percentage;
    }

}
