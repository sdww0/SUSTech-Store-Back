package com.susstore.mapper;

import com.susstore.pojo.Users;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SignMapper {


    Integer login(Map<String,Object> parameterMap);

    Users getUserByEmail(String email);

    Integer addRoleToUser(Map<String,Object> map);


}
