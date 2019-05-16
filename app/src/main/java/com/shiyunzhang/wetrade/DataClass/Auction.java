package com.shiyunzhang.wetrade.DataClass;


public class Auction {
    private String AuctionId;
    private Inventory inventory;
    private double price;
    private String winner;
    private String winnerId;
    private String auctionStatus;

    public Auction() {}

    public Auction(String auctionItemId, Inventory inventory, double price) {
        AuctionId = auctionItemId;
        this.inventory = inventory;
        this.price = price;
    }


    public Auction(String auctionId, Inventory inventory, double price, String winner) {
        AuctionId = auctionId;
        this.inventory = inventory;
        this.price = price;
        this.winner = winner;
    }

    public Auction(String auctionId, Inventory inventory, double price, String winner, String winnerId) {
        AuctionId = auctionId;
        this.inventory = inventory;
        this.price = price;
        this.winner = winner;
        this.winnerId = winnerId;
    }

    public Auction(String auctionId, Inventory inventory, double price, String winner, String winnerId, String auctionStatus) {
        AuctionId = auctionId;
        this.inventory = inventory;
        this.price = price;
        this.winner = winner;
        this.winnerId = winnerId;
        this.auctionStatus = auctionStatus;
    }

    public String getAuctionStatus() {
        return auctionStatus;
    }

    public void setAuctionStatus(String auctionStatus) {
        this.auctionStatus = auctionStatus;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
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
