package com.susstore.pojo;

import com.susstore.pojo.chat.Chat;

public class Deal {

    private Integer dealId;
    private Stage stage;
    private Goods goods;
    private Users buyer;
    private Users seller;
    private Chat chat;
    private String mailingNumber;
    private Address shippingAddress;

    public String getMailingNumber() {
        return mailingNumber;
    }

    public void setMailingNumber(String mailingNumber) {
        this.mailingNumber = mailingNumber;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Integer getDealId() {
        return dealId;
    }

    public void setDealId(Integer dealId) {
        this.dealId = dealId;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Users getBuyer() {
        return buyer;
    }

    public void setBuyer(Users buyer) {
        this.buyer = buyer;
    }

    public Users getSeller() {
        return seller;
    }

    public void setSeller(Users seller) {
        this.seller = seller;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
}
