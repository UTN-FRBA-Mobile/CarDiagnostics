package com.cardiag.models.solutions;

import com.cardiag.activity.MapsActivity;

/**
 * Created by leo on 05/09/17.
 */

public class NoSolution extends com.cardiag.models.solutions.Solution {

    public NoSolution() {
        super("No se encuentran soluciones disponibles. Por favor dirigirse a los servicios más cercanos", null);
    }

    public Class<?> getActivity() {
        return MapsActivity.class;
    }


}
