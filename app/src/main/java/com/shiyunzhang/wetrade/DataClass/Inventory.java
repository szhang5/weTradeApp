package com.shiyunzhang.wetrade.DataClass;

public class Inventory {
    private String name;
    private int picId;
    private double price;
    private String category;
    private String condition;
    private String description;
    private int quantity;
    private long timestamp;

    public Inventory(int picId, String category, String name, String description, double price, int quantity, String condition, long timestamp) {
        this.picId = picId;
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setPicId(int picId) {
        this.picId = picId;
    }

    public int getPicId() {
        return picId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImgId(int picId) {
        this.picId = picId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}