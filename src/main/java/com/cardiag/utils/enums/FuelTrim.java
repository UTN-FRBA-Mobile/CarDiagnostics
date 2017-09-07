package com.cardiag.utils.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Select one of the Fuel Trim percentage banks to access.
 *
 */
public enum FuelTrim {

    SHORT_TERM_BANK_1(0x06, "Short Term Fuel Trim Bank 1", 5),
    LONG_TERM_BANK_1(0x07, "Long Term Fuel Trim Bank 1", 6),
    SHORT_TERM_BANK_2(0x08, "Short Term Fuel Trim Bank 2",7),
    LONG_TERM_BANK_2(0x09, "Long Term Fuel Trim Bank 2",8);

    /** Constant <code>map</code> */
    private static Map<Integer, FuelTrim> map = new HashMap<>();

    static {
        for (FuelTrim error : FuelTrim.values())
            map.put(error.getValue(), error);
    }

    private final int value;
    private final String bank;
    private final int pos;

    private FuelTrim(final int value, final String bank, final int pos) {
        this.value = value;
        this.bank = bank;
        this.pos = pos;
    }

    public static FuelTrim fromValue(final int value) {
        return map.get(value);
    }

    /**
     * <p>Getter for the field <code>value</code>.</p>
     *
     * @return a int.
     */
    public int getValue() {
        return value;
    }

    /**
     * <p>Getter for the field <code>bank</code>.</p>
     *
     * @return a {@link String} object.
     */
    public String getBank() {
        return bank;
    }

    public int getPos() { return pos; }
    /**
     * <p>buildObdCommand.</p>
     *
     * @return a {@link String} object.
     */
    public final String buildObdCommand() {
        return new String("010" + value);
    }

}
