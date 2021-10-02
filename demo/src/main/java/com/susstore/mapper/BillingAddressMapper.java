package com.susstore.mapper;
import com.susstore.pojo.Address;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BillingAddressMapper {

    Integer ifExist(String recipientName,long phone, String addressName,int userId);

    Integer addAddress(Address address);

    /**
     * 根据用户id查询用户的地址
     * @param id id
     * @return 所有地址
     */
    List<Address> getUserAddressById(Integer id);

    /**
     * 根据用户邮箱查询用户地址
     * @param email 邮箱
     * @return 所有地址
     */
    List<Address> getUserAddressByEmail(String email);

    /**
     * 更新用户某个地址
     * @param address 地址信息
     * @return --
     */
    Integer updateAddress(Address address);

    /**
     * 删除某个地址
     * @param addressId 地址id
     * @return --
     */
    Integer deleteAddress(Integer addressId);



}