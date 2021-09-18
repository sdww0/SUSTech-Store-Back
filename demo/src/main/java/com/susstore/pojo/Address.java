package com.susstore.pojo;

public class Address {

    private int addressId;
    private int belongToUserId;
    private String recipientName;
    private String addressName;
    private long phone;

    public Address(int addressId, int belongToUserId, String recipientName, String addressName, long phone) {
        this.addressId = addressId;
        this.belongToUserId = belongToUserId;
        this.recipientName=recipientName;
        this.addressName = addressName;
        this.phone = phone;
    }


    public int getBelongToUserId() {
        return belongToUserId;
    }

    public void setBelongToUserId(int belongToUserId) {
        this.belongToUserId = belongToUserId;
    }

    public Address() {
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressID(int addressId) {
        this.addressId = addressId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }
}
