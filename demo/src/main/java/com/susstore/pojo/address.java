package com.susstore.pojo;

public class address {

    private int addressID;
    private User user;
    private String addressName;
    private long phone;

    public address(int addressID, User user, String addressName, long phone) {
        this.addressID = addressID;
        this.user = user;
        this.addressName = addressName;
        this.phone = phone;
    }

    public address() {
    }

    public int getAddressID() {
        return addressID;
    }

    public void setAddressID(int addressID) {
        this.addressID = addressID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
