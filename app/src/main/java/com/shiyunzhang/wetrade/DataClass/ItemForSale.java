package com.shiyunzhang.wetrade.DataClass;

import java.util.ArrayList;

public class ItemForSale {
    private String name;
    private String imageUrl;
    private String category;
    private String description;
    private String itemID;
    private String userID;
    private String productID;
    private long timestamp;
    private ArrayList<ConditionAndQuantity> conditionAndQuantities;

    public ItemForSale(){}

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ItemForSale(String itemID, String imageUrl, String category, String name, String description, ArrayList<ConditionAndQuantity> conditionAndQuantity, String userID, String productID, long timestamp) {
        this.itemID = itemID;
        this.imageUrl = imageUrl;
        this.category = category;
        this.name = name;
        this.description = description;
        this.userID = userID;
        this.productID = productID;
        this.conditionAndQuantities = conditionAndQuantity;
        this.timestamp = timestamp;
    }

    public ArrayList<ConditionAndQuantity> getConditionAndQuantities() {
        return conditionAndQuantities;
    }

    public void setConditionAndQuantities(ArrayList<ConditionAndQuantity> conditionAndQuantities) {
        this.conditionAndQuantities = conditionAndQuantities;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "categore: " + category + ", name: " + name + ", description: " + description
                + ", productID: " + productID + ", " + conditionAndQuantities.toString()
                + ", imageurl: " + imageUrl;
    }
}
