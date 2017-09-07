package com.cardiag.persistence;

import android.provider.BaseColumns;

/**
 * Created by leo on 10/08/17.
 */

class TroubleCodesContract {
    private TroubleCodesContract() {}

    public static class TroubleEntry implements BaseColumns {
        public static final String TABLE_NAME ="error_code";

        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
    }

    public static class SolCodeseEntry implements BaseColumns {
        public static final String TABLE_NAME ="solutions_code";

        public static final String IDCODE = "id_code";
        public static final String IDSOL = "id_solution";
    }

}
