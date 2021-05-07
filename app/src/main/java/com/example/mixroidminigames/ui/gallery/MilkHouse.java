package com.example.mixroidminigames.ui.gallery;

import java.util.ArrayList;

public class MilkHouse implements Comparable<MilkHouse> {
    private String name, password;
    private int order;
    private ArrayList<Milk> milks;
    private boolean isPassword, isDelivery;

    public MilkHouse(String name, String password, int order, ArrayList<Milk> milks, boolean isPassword, boolean isDelivery) {
        this.name = name;
        this.password = password;
        this.order = order;
        this.milks = milks;
        this.isPassword = isPassword;
        this.isDelivery = isDelivery;
    }

    public boolean isDelivery() {
        return isDelivery;
    }

    public void setDelivery(boolean isDelivery) {
        this.isDelivery = isDelivery;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public ArrayList<Milk> getMilks() {
        return milks;
    }

    public void setMilks(ArrayList<Milk> milks) {
        this.milks = milks;
    }

    public boolean isPassword() {
        return isPassword;
    }

    public void setPassword(boolean password) {
        isPassword = password;
    }

    @Override
    public int compareTo(MilkHouse o) {
        if (this.order > o.order) return 1;
        else if (this.order == o.order) return 0;
        else return -1;
    }
}
