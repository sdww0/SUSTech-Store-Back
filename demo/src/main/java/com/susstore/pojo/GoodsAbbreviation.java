package com.susstore.pojo;

import java.util.Date;
import java.util.List;

public class GoodsAbbreviation {

    private Integer goodsId;
    private Float price;
    private String picturePath;
    private List<String> labels;
    private Users announcer;
    private Integer wantAmount;
    private Date announceTime;

    public GoodsAbbreviation() {
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public Users getAnnouncer() {
        return announcer;
    }

    public void setAnnouncer(Users announcer) {
        this.announcer = announcer;
    }

    public Integer getWantAmount() {
        return wantAmount;
    }

    public void setWantAmount(Integer wantAmount) {
        this.wantAmount = wantAmount;
    }

    public Date getAnnounceTime() {
        return announceTime;
    }

    public void setAnnounceTime(Date announceTime) {
        this.announceTime = announceTime;
    }
}
