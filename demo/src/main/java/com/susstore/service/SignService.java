package com.susstore.service;

import com.susstore.mapper.SignMapper;
import com.susstore.pojo.Role;
import com.susstore.pojo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
