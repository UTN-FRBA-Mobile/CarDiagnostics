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
    private long id;

    public TroubleCode(String dtcCode, String desc, int id) {
        this.setName(dtcCode);
        this.setDescription(desc);
        this.setId(id);
    }

    public TroubleCode() {
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
        return this.getName() + "\n " +getDescription();
    }

    public ArrayList<Solution> getSolutions() {
        return solutions;
    }

    public void setSolutions(ArrayList<Solution> solutions) {
        this.solutions = solutions;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
