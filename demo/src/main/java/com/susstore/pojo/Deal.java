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
    /**
     *
     * 0--未下单 （在“我想要”里面）
     * 1--已下单 未支付
     * 2--已支付 未发货
     * 3--已支付 已发货
     * 4--已收货 未评价
     * 5--已过期
     *
     * */
    private Goods goods;
    private Users buyer;
    private Users seller;
    private Chat chat;
    private String mailingNumber;
    private Address shippingAddress;
}
