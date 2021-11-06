package com.susstore.controller;


import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public class ControllerReceiveClass {


    /**
     * 用户分类
     */
    protected static class Register{

        public String username;
        public String email;
        public String password;
        public Integer gender;

    }

    protected static class UpdateUser{

        public String name;
        public String sign;
        public Integer gender;
        @DateTimeFormat(pattern="yyyy-MM-dd")
        @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
        public Date birthday;
    }

    protected static class UserSecurity{
        public Integer type;
        public String content;
        public Integer checkCode;
    }


    /**
     * 商品分类
     */
    protected static class CommentGoods{
        public Integer goodsId;
        public String content;
    }

    protected static class GoodsInfo{
        public Integer goodsId;
        public String introduce;
        public String title;
        public Float price;
        public Float postage;
        public Boolean isSell;
        public List<String> labels;
    }

    /**
     * 订单分类
     */
    protected static class DealComment{
        public Integer dealId;
        public String content;
        public Boolean isGood;
    }
}
