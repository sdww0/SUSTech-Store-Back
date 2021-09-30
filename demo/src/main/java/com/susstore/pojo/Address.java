package com.susstore.pojo;

public class Address {

    private Integer addressId;
    private Integer belongToUserId;
    private String recipientName;
    private String addressName;
    private Long phone;

    public Address(Integer addressId, Integer belongToUserId, String recipientName, String addressName, Long phone) {
        this.addressId = addressId;
        this.belongToUserId = belongToUserId;
        this.recipientName=recipientName;
        this.addressName = addressName;
        this.phone = phone;
    }


    public Integer getBelongToUserId() {
        return belongToUserId;
    }

    public void setBelongToUserId(Integer belongToUserId) {
        this.belongToUserId = belongToUserId;
    }

    public Address() {
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressID(Integer addressId) {
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

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }
}
