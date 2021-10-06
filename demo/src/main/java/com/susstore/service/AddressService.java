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

    public List<Address> getAddressById(Integer id){
        return addressMapper.getUserAddressById(id);
    }

    public Integer addAddress(Address address){
        return addressMapper.addAddress(address);
    }

    public Integer deleteAddress(Integer addressId){
        return addressMapper.deleteAddress(addressId);
    }

}
