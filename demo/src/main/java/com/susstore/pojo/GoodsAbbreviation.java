package com.susstore.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Integer want;
    private Date announceTime;
}
