package com.cardiag.models.commands.protocol;

import com.cardiag.models.commands.ObdCommand;
import com.cardiag.models.commands.PersistentCommand;
import com.cardiag.utils.CommandAvailabilityHelper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

/**
 * Retrieve available PIDs ranging from 21 to 40.
 *
 */
public abstract class AvailablePidsCommand extends ObdCommand {

    /**
     * Default ctor.
     *
     * @param command a {@link String} object.
     */
    public AvailablePidsCommand(String command, Integer pos) {
        super(command, pos);
    }

    public AvailablePidsCommand(AvailablePidsCommand other) {
        super(other);
    }


    @Override
    protected void performCalculations() {

    }


    @Override
    public String getFormattedResult() {
        return getCalculatedResult();
    }


    @Override
    public String getCalculatedResult() {
        //First 4 characters are a copy of the command code, don't return those
        return getFlags().get(0).toString();
    }

    @Override
    public ArrayList<Integer> getBuffer(){
        return (ArrayList<Integer>) buffer.subList(4,buffer.size());
    }

    public ArrayList<Boolean> getFlags() {
        String hexaString = getData();

        ArrayList<Boolean>  flags;

        byte[] bytes = CommandAvailabilityHelper.hexStringToByteArray(hexaString);

        flags = CommandAvailabilityHelper.convert(bytes,32);

        return flags;
    }

    public String getData(){
        return String.valueOf(rawData).replace(".", "").substring(4);
    }



}
