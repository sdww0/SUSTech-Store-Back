package com.susstore.service.impl;

import com.susstore.mapper.AddressMapper;
import com.susstore.pojo.Address;
import com.susstore.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("AddressServiceImpl")
public class AddressServiceImpl implements AddressService {

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

    public List<Address> getUserAddressByEmail(String email) {
        return addressMapper.getUserAddressByEmail(email);
    }

    public Integer updateAddress(Address address ){
        return addressMapper.updateAddress(address);
    }

    public Boolean getAddress(Integer addressId){
        Boolean temp = addressMapper.getAddress(addressId);
        return temp ==null ? false : temp;
    }

    public Boolean isBelongAddress(String email,Integer addressId){
        Boolean temp = addressMapper.isBelongAddress(email,addressId);
        return temp != null && temp;
    }


}
