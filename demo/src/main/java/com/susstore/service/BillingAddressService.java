package com.susstore.service;

import com.susstore.mapper.BillingAddressMapper;

import java.util.HashMap;
import java.util.Map;

public class BillingAddressService {

    private BillingAddressMapper billingAddressMapper ;

    public Integer add(String name,String phone){
        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("phone",phone);
        return billingAddressMapper.add(map);
    }


}
