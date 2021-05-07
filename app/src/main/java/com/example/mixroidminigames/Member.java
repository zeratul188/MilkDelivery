package com.example.mixroidminigames;

import java.io.Serializable;

public class Member implements Serializable  {
    private String id, pwd, location, email;
    private int count;

    public Member(String id, String pwd, String location, String email, int count) {
        this.id = id;
        this.pwd = pwd;
        this.location = location;
        this.email = email;
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
