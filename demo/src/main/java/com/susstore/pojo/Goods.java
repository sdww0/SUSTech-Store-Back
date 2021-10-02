package com.susstore.pojo;

import java.sql.Time;
import java.util.List;

public class Goods {

    private Integer id;
    private Float price;
    private List<String> picturePath;
    private List<String> labels;
    private String introduce;
    private Users announcer;

    private List<Comment> comments;
    private Integer want;
    private Time time;
    private GoodsState goodsState;

    public GoodsState getGoodsState() {
        return goodsState;
    }

    public void setGoodsState(GoodsState goodsState) {
        this.goodsState = goodsState;
    }

    public Goods() {
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

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public Users getAnnouncer() {
        return announcer;
    }

    public void setAnnouncer(Users announcer) {
        this.announcer = announcer;
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
