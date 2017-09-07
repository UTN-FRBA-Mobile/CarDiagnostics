package com.cardiag.models.runnable;

import com.cardiag.models.commands.ObdCommand;

import java.util.ArrayList;

/**
 * Created by Leo on 30/7/2017.
 */

public class ProgressData {

    Integer progress;
    String progressMessage;
    ArrayList<ObdCommand> commands;
    Boolean error;

    public ProgressData() {
    }

    public ProgressData(Integer progress, String progressMessage) {
        this.progress = progress;
        this.progressMessage = progressMessage;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public String getProgressMessage() {
        return progressMessage;
    }

    public void setProgressMessage(String progressMessage) {
        this.progressMessage = progressMessage;
    }

    public ArrayList<ObdCommand> getCommands() {
        return commands;
    }

    public void setCommands(ArrayList<ObdCommand> commands) {
        this.commands = commands;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }
}
