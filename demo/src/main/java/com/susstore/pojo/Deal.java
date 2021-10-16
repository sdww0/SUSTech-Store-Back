package com.susstore.pojo;

import com.susstore.pojo.chat.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Deal {

    private Integer dealId;
    private Integer stage;//Stage
    private GoodsAbbreviation goodsAbbreviation;
    private Users buyer;
    private Users seller;
    private Chat chat;
    private String mailingNumber;
    private Boolean needMailing;
    private Address shippingAddress;

}
