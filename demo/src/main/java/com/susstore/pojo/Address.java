package com.susstore.pojo;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {

    private Integer addressId;
    private Integer belongToUserId;
    private String recipientName;
    private String addressName;
    private Long phone;

}
