package com.shiyunzhang.wetrade.DataClass;

public class Transaction {
    private String name;
    private String imageUrl;
    private String category;
    private String description;
    private String transactionId;
    private String itemId;
    private String sellerId;
    private String customerId;
    private String productId;
    private String condition;
    private double price;
    private int quantity;
    private long timestamp;

    public Transaction(){}

    public Transaction(String transactionId, String name, String imageUrl, String category, String description,
                       String itemId, String sellerId, String customerId,
                       String productId, String condition, double price, int quantity, long timestamp) {
        this.transactionId = transactionId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.category = category;
        this.description = description;
        this.itemId = itemId;
        this.sellerId = sellerId;
        this.customerId = customerId;
        this.productId = productId;
        this.condition = condition;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", itemId='" + itemId + '\'' +
                ", sellerId='" + sellerId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", productId='" + productId + '\'' +
                ", condition='" + condition + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", timestamp=" + timestamp +
                '}';
    }
}
