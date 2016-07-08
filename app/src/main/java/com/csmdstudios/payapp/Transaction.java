package com.csmdstudios.payapp;

/**
 * Created by wayne on 8/7/16.
 */
public class Transaction {
    private double owed;
    private Boolean unvalidated;
    private String description;
    private long timestamp;

    public Transaction() {
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
