package com.cardiag.models.commands.control;

import com.cardiag.models.commands.ObdCommand;
import com.cardiag.models.commands.SystemOfUnits;
import com.cardiag.utils.enums.AvailableCommandNames;

/**
 * <p>DistanceMILOnCommand class.</p>
 *
 */
public class DistanceMILOnCommand extends ObdCommand
        implements SystemOfUnits {

    private int km = 0;

    /**
     * Default ctor.
     */
    public DistanceMILOnCommand() {
        super("0121", 32);
    }

    public DistanceMILOnCommand(
            DistanceMILOnCommand other) {
        super(other);
    }


    @Override
    protected void performCalculations() {
        // ignore first two bytes [01 31] of the response
        km = buffer.get(2) * 256 + buffer.get(3);
    }

    /**
     * <p>getFormattedResult.</p>
     *
     * @return a {@link String} object.
     */
    public String getFormattedResult() {
        return useImperialUnits ? String.format("%.2f%s", getImperialUnit(), getResultUnit())
                : String.format("%d%s", km, getResultUnit());
    }


    @Override
    public String getCalculatedResult() {
        return useImperialUnits ? String.valueOf(getImperialUnit()) : String.valueOf(km);
    }


    @Override
    public String getResultUnit() {
        return useImperialUnits ? "m" : "km";
    }


    @Override
    public float getImperialUnit() {
        return km * 0.621371192F;
    }

    /**
     * <p>Getter for the field <code>km</code>.</p>
     *
     * @return a int.
     */
    public int getKm() {
        return km;
    }

}
