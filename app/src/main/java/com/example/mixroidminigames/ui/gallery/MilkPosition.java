package com.example.mixroidminigames.ui.gallery;

public class MilkPosition {
    private String name;
    private int location;

    public MilkPosition(String name, int location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }
}
