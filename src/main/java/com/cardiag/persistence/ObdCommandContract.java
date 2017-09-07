package com.cardiag.persistence;

import android.provider.BaseColumns;

/**
 * Created by leo on 04/08/2017.
 */

public final class ObdCommandContract {
    private ObdCommandContract() {}

    public static class CommandEntry implements BaseColumns {
        public static final String TABLE_NAME ="obd_command";

        public static final String NAME = "name";
        public static final String SELECTED = "selected";
        public static final String AVAILABILITY = "available";
    }

}
