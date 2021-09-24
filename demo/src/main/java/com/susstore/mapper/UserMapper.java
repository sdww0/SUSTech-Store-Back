package com.susstore.mapper;

import com.susstore.pojo.Users;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserMapper {


     List<Users> queryUserList();

     Users queryUserById(int id);

     Integer addUser(Users user);

     int updateUser(Users user);

     int deleteUser(int id);

     Integer queryUserByEmail(String email);

     Integer updateToken(Map<String,Object> map);


}
