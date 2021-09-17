package com.susstore.pojo;

import java.util.Date;

public class User {

    private int userId;
    private String name;
    private String password;
    private Character gender;
    private Date birthday;
    private Integer credit;
    private Long phone;
    private String email;
    private String IDCard;
    private Float money;
    private String picturePath;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Character getGender() {
        return gender;
    }

    public void setGender(Character gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIDCard() {
        return IDCard;
    }

    public void setIDCard(String IDCard) {
        this.IDCard = IDCard;
    }

    public Float getMoney() {
        return money;
    }

    public void setMoney(Float money) {
        this.money = money;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public User(int userId, String name, String password, char gender, Date birthday, int credit, long phone, String email, String IDCard, float money, String picturePath) {
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.gender = gender;
        this.birthday = birthday;
        this.credit = credit;
        this.phone = phone;
        this.email = email;
        this.IDCard = IDCard;
        this.money = money;
        this.picturePath = picturePath;
    }

    public User() {
    }
}
