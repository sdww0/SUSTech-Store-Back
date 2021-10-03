package com.susstore.mapper;

import com.susstore.pojo.Users;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserMapper {

     /**
      * 查询所有用户
      * @return 用户列表
      */
     List<Users> queryUserList();

     /**
      * 根据用户id查询用户
      * @param id id
      * @return 用户
      */
     Users queryUserById(int id);

     /**
      * 添加用户
      * @param user 用户
      * @return 用户id
      */
     Integer addUser(Users user);

     /**
      * 更新用户数据
      * @param user 用户
      * @return --
      */
     int updateUser(Users user);

     /**
      * 更新用户邮箱
      * @param oldEmail 旧邮箱
      * @param newEmail 新邮箱
      * @return --
      */
     Integer updateUserEmail(String oldEmail,String newEmail);

     /**
      * 根据用户邮箱更新用户（id为null）
      * @param users 用户
      * @return --
      */
     Integer updateUserByEmail(Users users);

     /**
      * 根据id删除用户
      * @param id id
      * @return --
      */
     int deleteUser(int id);

     /**
      * 根据邮箱查询用户id
      * @param email 邮箱
      * @return 用户id
      */
     Integer queryUserByEmail(String email);

     /**
      * 根据用户邮箱查询用户
      * @param email 邮箱
      * @return 用户
      */
     Users getUserByEmail(String email);


}
