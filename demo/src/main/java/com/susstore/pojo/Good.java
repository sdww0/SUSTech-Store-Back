package com.susstore.pojo;

import java.sql.Time;
import java.util.List;

public class Good {

    private Integer id;
    private Float price;
    private List<String> picturePath;
    private List<Label> labels;
    private String introduce;
    /**
     * 用户信息，只需要用户id，用户名，用户照片，用户信誉分
     */
    private Integer userId;
    private String userName;
    private String userPicturePath;
    private String userCredit;

    private List<Comment> comments;
    private Integer want;
    private Time time;

    public Good(Integer id, Float price, List<String> picturePath, List<Label> labels, String introduce, Integer userId, String userName, String userPicturePath, String userCredit, List<Comment> comments, Integer want, Time time) {
        this.id = id;
        this.price = price;
        this.picturePath = picturePath;
        this.labels = labels;
        this.introduce = introduce;
        this.userId = userId;
        this.userName = userName;
        this.userPicturePath = userPicturePath;
        this.userCredit = userCredit;
        this.comments = comments;
        this.want = want;
        this.time = time;
    }

    public Good() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public List<String> getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(List<String> picturePath) {
        this.picturePath = picturePath;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPicturePath() {
        return userPicturePath;
    }

    public void setUserPicturePath(String userPicturePath) {
        this.userPicturePath = userPicturePath;
    }

    public String getUserCredit() {
        return userCredit;
    }

    public void setUserCredit(String userCredit) {
        this.userCredit = userCredit;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Integer getWant() {
        return want;
    }

    public void setWant(Integer want) {
        this.want = want;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}
