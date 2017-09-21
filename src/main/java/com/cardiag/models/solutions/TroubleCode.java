package com.cardiag.models.solutions;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by leo on 05/09/17.
 */

public class  TroubleCode {
    private String name;
    private String description;
    private ArrayList<Solution> solutions;

    public TroubleCode(String dtcCode, String desc) {
        this.setName(dtcCode);
        this.setDescription(desc);
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return this.getName() + "\n - " +getDescription();
    }

    public ArrayList<Solution> getSolutions() {
        return solutions;
    }

    public void setSolutions(ArrayList<Solution> solutions) {
        this.solutions = solutions;
    }
}
