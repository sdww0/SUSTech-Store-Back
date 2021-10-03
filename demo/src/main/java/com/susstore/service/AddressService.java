package com.susstore.service;

import com.susstore.mapper.AddressMapper;
import com.susstore.pojo.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    AddressMapper addressMapper;

    public List<Address> getAddressByEmail(String email){
        return addressMapper.getUserAddressByEmail(email);
    }

    public Integer addAddress(Address address,String email){
        return addressMapper.addAddress(address,email);
    }

    public Integer deleteAddress(Integer addressId){
        return addressMapper.deleteAddress(addressId);
    }

}
