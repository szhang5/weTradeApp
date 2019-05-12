package com.shiyunzhang.wetrade.DataClass;


public class Auction {
    private String AuctionId;
    private Inventory inventory;
    private double price;

    public Auction() {}

    public Auction(String auctionItemId, Inventory inventory, double price) {
        AuctionId = auctionItemId;
        this.inventory = inventory;
        this.price = price;
    }

    public String getAuctionId() {
        return AuctionId;
    }

    public void setAuctionId(String auctionId) {
        AuctionId = auctionId;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
