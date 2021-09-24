package com.susstore.service;

import com.susstore.login.LoginUsers;
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

import java.util.ArrayList;
import java.util.List;
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private SignMapper signMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = signMapper.getUserByEmail(email);
        if(user==null){
            throw new UsernameNotFoundException("用户不存在");
        }
        List<Role> roles = signMapper.getRolesByUserId(user.getUserId());

        List<GrantedAuthority> authorities = new ArrayList<>();
        for(Role role:roles){
            authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getName()));
        }
//
//        return new LoginUsers(new User(
//                email,
//                user.getPassword(),
//                authorities
//        ),user.getUserId(),user.getSlat());
        return new User(
                email,
                passwordEncoder.encode(user.getPassword()),
                authorities
        );
    }
}
