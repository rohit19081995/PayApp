package com.csmdstudios.payapp;

/**
 * Created by wayne on 25/6/16.
 */
public class User {

    private String name;
    private String email;
    private String pic_url;

    public User() {
    }

    public User(String name, String email, String pic_url) {
        this.name = name;
        this.email = email;
        this.pic_url = pic_url;
    }

    public User(String name, String email) {
        this.email = email;
        this.name = name;
        pic_url = null;
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
