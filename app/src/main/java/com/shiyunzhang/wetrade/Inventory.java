package com.shiyunzhang.wetrade;

public class Inventory {
    private String name;
    private int picId;
    private int price;
    private String category;
    private String condition;
    private int quantity;

    public Inventory(String description, int picId, int price, String category,
                      String condition, int quantity) {
        this.name = description;
        this.picId = picId;
        this.price = price;
        this.category = category;
        this.condition = condition;
        this.quantity = quantity;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getPicId() {
        return picId;
    }
    public void setImgId(int picId) {
        this.picId = picId;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
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