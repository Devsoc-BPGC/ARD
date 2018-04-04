package com.macbitsgoa.ard.models;

/**
 * @author Rushikesh Jogdand.
 */
public class Developer {
    public String name;
    public String phone;
    public String email;
    public String web;
    public String photoUrl;

    public Developer() {
    }

    public Developer(String name, String phone, String email, String web, String photoUrl) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.web = web;
        this.photoUrl = photoUrl;
    }
}
