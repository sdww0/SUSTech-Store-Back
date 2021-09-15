package com.susstore.pojo;

import java.util.Date;

public class User {

    private int userId;
    private String name;
    private String password;
    private char gender;
    private Date birthday;
    private int credit;
    private long phone;
    private String email;
    private String IDCard;
    private float money;
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

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
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

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
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
