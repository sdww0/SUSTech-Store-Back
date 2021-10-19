package com.susstore.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoodsAbbreviation {

    private Integer goodsId;
    private String title;
    private Float price;
    private String picturePath;
    private List<String> labels;
    private Users announcer;
    private Integer view;
    private Integer want;
    private Integer goodsState;


    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date announceTime;
    private Float postage;
    private Boolean isSell;
}
