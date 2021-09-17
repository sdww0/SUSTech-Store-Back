package com.susstore.mapper;

import com.susstore.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserMapper {


     List<User> queryUserList();

     User queryUserById(int id);

     Integer addUser(User user);

     int updateUser(User user);

     int deleteUser(int id);

     Integer login(Map<String,Object> parameterMap);

     Integer queryUserByEmail(String email);



}
