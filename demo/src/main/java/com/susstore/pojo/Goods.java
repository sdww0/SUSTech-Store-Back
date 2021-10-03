package com.susstore.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.Date;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Goods {

    private Integer id;
    private Float price;
    private List<String> picturePath;
    private List<String> labels;
    private String introduce;
    private Users announcer;

    private List<Comment> comments;
    private Integer want;
    private Date announceTime;
    private GoodsState goodsState;
    private Integer pictureAmount;

}