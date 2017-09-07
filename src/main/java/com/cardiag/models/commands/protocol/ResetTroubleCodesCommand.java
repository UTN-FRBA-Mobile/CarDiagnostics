package com.cardiag.models.commands.protocol;

import com.cardiag.models.commands.ObdCommand;

/**
 * Reset trouble codes.
 *
 */
public class ResetTroubleCodesCommand extends ObdCommand {

    /**
     * <p>Constructor for ResetTroubleCodesCommand.</p>
     */
    public ResetTroubleCodesCommand() {
        super("04", 1000);
    }


    @Override
    protected void performCalculations() {

    }


    @Override
    public String getFormattedResult() {
        return getResult();
    }


    @Override
    public String getCalculatedResult() {
        return getResult();
    }



    @Override
    public String getName() {
        return getResult();
    }

}
