package com.cardiag.models.solutions;

import com.cardiag.activity.ErrorSearchActivity;

public class NoErrorSolution extends Solution {
    private String error;

    public NoErrorSolution(String error) {
        super("Buscar el código de error en internet", null);
        this.setError(error);
    }
    public Class<?> getActivity() {
        return ErrorSearchActivity.class;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
