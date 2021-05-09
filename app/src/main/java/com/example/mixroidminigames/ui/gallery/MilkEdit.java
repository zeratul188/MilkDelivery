package com.example.mixroidminigames.ui.gallery;

public class MilkEdit {
    private String type, house_key, milk_key;
    private int count;

    public MilkEdit(String type, String house_key, String milk_key, int count) {
        this.type = type;
        this.house_key = house_key;
        this.milk_key = milk_key;
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHouse_key() {
        return house_key;
    }

    public void setHouse_key(String house_key) {
        this.house_key = house_key;
    }

    public String getMilk_key() {
        return milk_key;
    }

    public void setMilk_key(String milk_key) {
        this.milk_key = milk_key;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
