package com.cardiag.persistence;

import android.provider.BaseColumns;

/**
 * Created by lrocca on 26/07/2017.
 */

public final class StepsContract {
    private StepsContract() {}

    public static class StepEntry implements BaseColumns {
        public static final String TABLE_NAME ="step";

        //   public static final String ID = "id_step";
        public static final String DESCRIPTION = "description";
        public static final String PATH = "path";
        //      public static final String ORDER = "step_order";
    }


    public static class StepSolEntry implements BaseColumns {
        public static final String TABLE_NAME ="step_solution";

        public static final String IDSTEP = "id_step";
        public static final String IDSOL = "id_solution";
        public static final String ORDER = "step_order";
    }

    public static class SolutionEntry implements BaseColumns {
        public static final String TABLE_NAME ="solution";

        public static final String DESCRIPTION = "description";  //Va??
        public static final String NAME = "name";
        public static final String PRIORITY = "priority";
    }

}
