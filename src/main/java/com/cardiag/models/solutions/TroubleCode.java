package com.cardiag.models.solutions;

/**
 * Created by leo on 05/09/17.
 */

public class TroubleCode {
    private String name;
    private String description;

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
}
