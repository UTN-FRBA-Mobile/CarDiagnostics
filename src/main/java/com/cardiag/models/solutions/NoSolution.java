package com.cardiag.models.solutions;

import com.cardiag.activity.MapsActivity;

/**
 * Created by leo on 05/09/17.
 */

public class NoSolution extends com.cardiag.models.solutions.Solution {

    public NoSolution() {
        super("Dirigirse al taller o estación de servicio más cercano", null);
    }

    public Class<?> getActivity() {
        return MapsActivity.class;
    }


}
