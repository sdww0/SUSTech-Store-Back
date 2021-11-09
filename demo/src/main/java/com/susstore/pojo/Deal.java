package com.susstore.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.susstore.pojo.chat.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Deal {

    private Integer dealId;
    private Integer stage;//Stage
    private Float price;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date orderTime;
    private GoodsAbbreviation goodsAbbreviation;
    private Users buyer;
    private Users seller;
    private Chat chat;
    private String mailingNumber;
    private Boolean needMailing;
    private Address shippingAddress;

}
