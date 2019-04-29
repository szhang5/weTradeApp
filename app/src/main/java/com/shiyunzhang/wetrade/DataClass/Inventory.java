package com.shiyunzhang.wetrade.DataClass;

public class Inventory {
    private String name;
    private String imageUrl;
    private String category;
    private String condition;
    private String description;
    private int quantity;
    private String itemID;
    private String userID;
    private String productID;

    public Inventory(){}

    public Inventory(String itemID, String imageUrl, String category, String name, String description, int quantity, String condition, String userID, String productID) {
        this.itemID = itemID;
        this.imageUrl = imageUrl;
        this.category = category;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.condition = condition;
        this.userID = userID;
        this.productID = productID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    @Override
    public String toString() {
        return "categore: " + category + ", name: " + name + ", description: " + description
                + ", productID: " + productID + ", quantity: " + quantity + ", condition: " + condition
                + ", imageurl: " + imageUrl;
    }
}