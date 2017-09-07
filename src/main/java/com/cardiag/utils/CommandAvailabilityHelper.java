package com.cardiag.utils;

import java.util.ArrayList;


public abstract class CommandAvailabilityHelper {

    public static ArrayList<Boolean> convert(byte[] bits, int significantBits) {

        ArrayList<Boolean> retVal = new ArrayList<Boolean>();
        int boolIndex = 0;
        for (int byteIndex = 0; byteIndex < bits.length; ++byteIndex) {
            for (int bitIndex = 7; bitIndex >= 0; --bitIndex) {
                if (boolIndex >= significantBits) {
                    return retVal;
                }
                Boolean flag = new Boolean ((bits[byteIndex] >> bitIndex & 0x01) == 1 ? true : false);
                retVal.add(flag);
            }
        }
        return retVal;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
