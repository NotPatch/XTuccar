package org.xfurkanadenia.xtuccar.model;

import org.xfurkanadenia.xtuccar.XTuccar;

import java.util.UUID;

public class MarketSellingItem {
    private String seller;
    private UUID sellerUUID;
    private String itemId;
    private int id;
    private Double price;
    private Integer amount;
    public MarketSellingItem(MarketSellingItem marketSellingItem) {
        this.seller = marketSellingItem.getSeller();
        this.itemId = marketSellingItem.getItemId();
        this.id = marketSellingItem.getId();
        this.price = marketSellingItem.getPrice();
        this.amount = marketSellingItem.getAmount();
    }
    public MarketSellingItem(String seller, String itemId, Double price, Integer amount, int id) {
        this.seller = seller;
        this.itemId = itemId;
        this.price = price;
        this.amount = amount;
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getItemId() {
        return itemId;
    }

    public int getId() {
        return id;
    }

    public Double getPrice() {
        return price;
    }

    public String getSeller() {
        return seller;
    }

    public MarketItem getMarketItem() {
        return XTuccar.getInstance().getTuccarManager().getMarketItem(itemId);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void addAmount(Integer amount) {
        this.amount += amount;
    }

    public void removeAmount(Integer amount) {
        this.amount -= amount;
    }
    public void addPrice(Double price) {
        this.price += price;
    }

    public void removePrice(Double price) {
        this.price -= price;
    }
}
