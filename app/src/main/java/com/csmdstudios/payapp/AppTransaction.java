package com.csmdstudios.payapp;

import com.google.firebase.database.ServerValue;

/**
 * Created by wayne on 8/7/16.
 */
public class AppTransaction {
    private double owed;
    private Boolean unvalidated;
    private String description;
    private Object timestamp;

    public AppTransaction() {
    }

    public AppTransaction(double owed) {
        this.owed = owed;
        timestamp = ServerValue.TIMESTAMP;
    }

    public double getOwed() {
        return owed;
    }

    public void setOwed(double owed) {
        this.owed = owed;
    }

    public Boolean getUnvalidated() {
        return unvalidated;
    }

    public void setUnvalidated(Boolean unvalidated) {
        this.unvalidated = unvalidated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
