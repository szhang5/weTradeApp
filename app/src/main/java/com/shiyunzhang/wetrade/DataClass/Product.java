package com.shiyunzhang.wetrade.DataClass;

public class Product {
    private String productId;
    private String name;
    private String description;
    private String category;
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Product(){}

    public Product(String productId, String name, String description, String category, String image) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.image = image;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString(){
        return "productid: " + productId + ", name: " + name;
    }
}
