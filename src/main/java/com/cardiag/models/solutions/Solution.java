package com.cardiag.models.solutions;

import com.cardiag.activity.StepsActivity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lrocca on 26/07/2017.
 */
public class Solution  extends TroubleCode implements Serializable{
    private ArrayList<Step> steps;
    private String name;
    private Integer priority = -1;
    private long id =-1;

    public Solution(String name, ArrayList<Step> steps) {
        this.setSteps(steps);
        this.setName(name);
    }

    public Solution(String name,int p,int id) {
        this.setId(id);
        this.setName(name);
        this.setPriority(p);
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
    @Override
    public String toString(){
        return this.getName();
    }

    public Class<?> getActivity() {
        return StepsActivity.class;
    }

}
