package com.csmdstudios.payapp;

/**
 * Created by wayne on 25/6/16.
 */
public class User {

    private String name;
    private String name_search;
    private String email;
    private String pic_url;

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    private String UID;

    public User() {
    }

    public User(String[] userString) {
        UID = userString[0];
        name = userString[1];
        name_search = name.toLowerCase();
        pic_url = userString[2];
        email = null;

    }

    public User(String name, String email, String pic_url) {
        this.name = name;
        name_search = name.toLowerCase();
        this.email = email;
        this.pic_url = pic_url;
    }

    public User(String name, String email) {
        this.email = email;
        this.name = name;
        name_search = name.toLowerCase();
        pic_url = null;
    }

    public String getName_search() {
        return name_search;
    }

    public void setName_search(String name_search) {
        this.name_search = name_search;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}
