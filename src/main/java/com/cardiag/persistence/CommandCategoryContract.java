package com.cardiag.persistence;

import android.provider.BaseColumns;

/**
 * Created by Leo on 1/10/2017.
 */

public class CommandCategoryContract {


    private CommandCategoryContract() {}

    public static class CommandCategoryEntry implements BaseColumns {
        public static final String TABLE_NAME ="command_category";

        public static final String COMMAND = "command_id";
        public static final String GROUP = "category_id";
    }
}
