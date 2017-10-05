package com.cardiag.persistence;

import android.provider.BaseColumns;

/**
 * Created by Leo on 1/10/2017.
 */

public final class CategoryContract {

    private CategoryContract() {}

    public static class CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME ="category";

        public static final String NAME = "name";
    }
}
