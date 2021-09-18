package com.susstore.service;

import com.susstore.mapper.BillingAddressMapper;
import com.susstore.pojo.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BillingAddressService {

    @Autowired
    private BillingAddressMapper billingAddressMapper ;

    public Integer ifExist(String recipientName,long phone, String addressName){
        return billingAddressMapper.ifExist(recipientName,phone,addressName);
    }
    public Integer addAddress(Address address){
        return billingAddressMapper.addAddress(address);
    }


}
