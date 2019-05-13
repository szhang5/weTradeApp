package com.shiyunzhang.wetrade.DataClass;

public class UserBidPrice {
    private String userId;
    private String imageUrl;
    private String name;
    private double price;

    public UserBidPrice(String userId, String name, double price, String imageUrl) {
        this.userId = userId;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
