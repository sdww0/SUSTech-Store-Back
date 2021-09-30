package com.susstore.service;

import com.susstore.mapper.SignMapper;
import com.susstore.pojo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SignService  {

    @Autowired
    SignMapper signMapper;

    public Integer login(String email,String password){
        Map<String,Object> map = new HashMap<>();
        map.put("email",email);
        map.put("password",password);
        return signMapper.login(map);
    }

    public Users getUserByEmail(String email){
        return signMapper.getUserByEmail(email);
    }



}
