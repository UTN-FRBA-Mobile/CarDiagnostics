package com.cardiag.models.commands.control;

import com.cardiag.models.commands.ObdCommand;
import com.cardiag.utils.enums.AvailableCommandNames;

import java.io.IOException;
import java.io.InputStream;

/**
 * It is not needed no know how many DTC are stored.
 * Because when no DTC are stored response will be NO DATA
 * And where are more messages it will be stored in frames that have 7 bytes.
 * In one frame are stored 3 DTC.
 * If we find out DTC P0000 that mean no message are we can end.
 *
 */
public class PermanentTroubleCodesCommand extends ObdCommand {

    /** Constant <code>dtcLetters={'P', 'C', 'B', 'U'}</code> */
    protected final static char[] dtcLetters = {'P', 'C', 'B', 'U'};
    /** Constant <code>hexArray="0123456789ABCDEF".toCharArray()</code> */
    protected final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    protected StringBuilder codes = null;

    /**
     * <p>Constructor for PermanentTroubleCodesCommand.</p>
     */
    public PermanentTroubleCodesCommand() {
        super("0A", 1000);
        codes = new StringBuilder();
    }

    public PermanentTroubleCodesCommand(PermanentTroubleCodesCommand other) {
        super(other);
        codes = new StringBuilder();
    }


    @Override
    protected void fillBuffer() {
    }


    @Override
    protected void performCalculations() {
        final String result = getResult();
        String workingData;
        int startIndex = 0;//Header size.

        String canOneFrame = result.replaceAll("[\r\n]", "");
        int canOneFrameLength = canOneFrame.length();
        if (canOneFrameLength <= 16 && canOneFrameLength % 4 == 0) {//CAN(ISO-15765) protocol one frame.
            workingData = canOneFrame;//4Ayy{codes}
            startIndex = 4;//Header is 4Ayy, yy showing the number of data items.
        } else if (result.contains(":")) {//CAN(ISO-15765) protocol two and more frames.
            workingData = result.replaceAll("[\r\n].:", "");//xxx4Ayy{codes}
            startIndex = 7;//Header is xxx4Ayy, xxx is bytes of information to follow, yy showing the number of data items.
        } else {//ISO9141-2, KWP2000 Fast and KWP2000 5Kbps (ISO15031) protocols.
            workingData = result.replaceAll("^4A|[\r\n]4A|[\r\n]", "");
        }
        for (int begin = startIndex; begin < workingData.length(); begin += 4) {
            String dtc = "";
            byte b1 = hexStringToByteArray(workingData.charAt(begin));
            int ch1 = ((b1 & 0xC0) >> 6);
            int ch2 = ((b1 & 0x30) >> 4);
            dtc += dtcLetters[ch1];
            dtc += hexArray[ch2];
            dtc += workingData.substring(begin+1, begin + 4);
            if (dtc.equals("P0000")) {
                return;
            }
            codes.append(dtc);
            codes.append('\n');
        }
    }

    private byte hexStringToByteArray(char s) {
        return (byte) ((Character.digit(s, 16) << 4));
    }

    /**
     * <p>formatResult.</p>
     *
     * @return the formatted result of this command in string representation.
     * @deprecated use #getCalculatedResult instead
     */
    public String formatResult() {
        return codes.toString();
    }


    @Override
    public String getCalculatedResult() {
        return String.valueOf(codes);
    }



    @Override
    protected void readRawData(InputStream in) throws IOException {
        byte b;
        StringBuilder res = new StringBuilder();

        // read until '>' arrives OR end of stream reached (and skip ' ')
        char c;
        while (true) {
            b = (byte) in.read();
            if (b == -1) // -1 if the end of the stream is reached
            {
                break;
            }
            c = (char) b;
            if (c == '>') // read until '>' arrives
            {
                break;
            }
            if (c != ' ') // skip ' '
            {
                res.append(c);
            }
        }

        rawData = res.toString().trim();

    }


    @Override
    public String getFormattedResult() {
        return codes.toString();
    }


    @Override
    public String getName() {
        return AvailableCommandNames.PERMANENT_TROUBLE_CODES.getValue();
    }

}
