package com.example.mixroidminigames.ui.home;

public class MilkNext implements Comparable<MilkNext> {
    private String name;
    private int order;

    public MilkNext(String name, int order) {
        this.name = name;
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int compareTo(MilkNext o) {
        if (this.order > o.order) return 1;
        else if (this.order == o.order) return 0;
        else return -1;
    }
}
