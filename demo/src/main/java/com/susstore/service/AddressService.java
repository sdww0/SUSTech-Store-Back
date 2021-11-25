package com.susstore.service;

import com.susstore.pojo.Address;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AddressService {


    public List<Address> getAddressById(Integer id);

    public Integer addAddress(Address address);

    public Integer deleteAddress(Integer addressId);

    public List<Address> getUserAddressByEmail(String email);

    public Integer updateAddress(Address address );

    public Boolean getAddress(Integer addressId);

    public Boolean isBelongAddress(String email,Integer addressId);


}
