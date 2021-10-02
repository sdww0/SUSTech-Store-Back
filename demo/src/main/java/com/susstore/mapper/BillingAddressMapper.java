package com.susstore.mapper;
import com.susstore.pojo.Address;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface BillingAddressMapper {

    Integer ifExist(String recipientName,long phone, String addressName,int userId);

    Integer addAddress(Address address);



}