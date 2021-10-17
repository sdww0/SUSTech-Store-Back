package com.susstore.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.util.Date;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Goods {
    //必须要传的
    private Integer goodsId;
    private Float price;
    private String title;
    private List<GoodsPicture> picturePath;
    private List<String> labels;
    private String introduce;
    private Users announcer;
    private Boolean isSell;
    private Float postage;
    private Integer want;
    private Integer view;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date announceTime;
    private Integer goodsState;//GoodsState 0--in shelf 1-off shelf


    private List<Comment> comments;


}