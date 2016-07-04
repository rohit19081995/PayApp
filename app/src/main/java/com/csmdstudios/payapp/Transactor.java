package com.csmdstudios.payapp;

/**
 * Created by wayne on 1/7/16.
 */
public class Transactor {
    private String UID;
    private String pic_url;
    private double owed;
    private Boolean unvalidated;
    private String name;
    private long timestamp;

    public Transactor() {
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
