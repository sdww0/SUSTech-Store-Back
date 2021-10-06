package com.susstore.pojo;

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
public class Users {
    private Integer userId;
    private String sign;
    private String userName;
    private String password;
    private Integer gender;//Gender
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    private Integer credit;
    private String email;
    private String idCard;
    private Float money;
    private String picturePath;
    private Long phone;
    private List<Address> addresses;
    //以下为非特殊需要不需要查询的
    private Boolean isActivate;
    private String activateCode;




}
