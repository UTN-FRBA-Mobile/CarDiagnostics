package com.cardiag.models.commands.engine;

import com.cardiag.models.commands.ObdCommand;
import com.cardiag.utils.enums.AvailableCommandNames;

/**
 * Mass Air Flow (MAF)
 *
 */
public class MassAirFlowCommand extends ObdCommand {

    private float maf = -1.0f;

    /**
     * Default ctor.
     */
    public MassAirFlowCommand() {
        super("0110", 15);
    }

    @Override
    protected void performCalculations() {
        // ignore first two bytes [hh hh] of the response
        maf = (buffer.get(2) * 256 + buffer.get(3)) / 100.0f;
    }


    @Override
    public String getFormattedResult() {
        return String.format("%.2f%s", maf, getResultUnit());
    }


    @Override
    public String getCalculatedResult() {
        return String.valueOf(maf);
    }


    @Override
    public String getResultUnit() {
        return "g/s";
    }

    /**
     * <p>getMAF.</p>
     *
     * @return MAF value for further calculus.
     */
    public double getMAF() {
        return maf;
    }

}
