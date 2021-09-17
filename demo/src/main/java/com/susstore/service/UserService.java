package com.susstore.service;

import com.susstore.mapper.UserMapper;
import com.susstore.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;


    public List<User> queryUserList(){
        return userMapper.queryUserList();
    }

    public User queryUserById(int id){
        return userMapper.queryUserById(id);
    }

    public Integer addUser(User user){
        return userMapper.addUser(user);
    }

    public int updateUser(User user){
        return userMapper.updateUser(user);
    }


    public int deleteUser(int id){
        return userMapper.deleteUser(id);
    }

    public Integer login(String email,String password){
        Map<String,Object> map = new HashMap<>();
        map.put("email",email);
        map.put("password",password);
        return userMapper.login(map);
    }

    public Integer queryUserByEmail(String email){
        return userMapper.queryUserByEmail(email);
    }


}
