package com.susstore.config.security;

import com.susstore.config.security.exception.*;
import com.susstore.pojo.Role;
import com.susstore.pojo.Users;
import com.susstore.service.UserService;
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
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UserNotActivateException, UsernameNotFoundException {
        Users user = userService.getUserByEmail(email);
        if(user==null){
            throw new UsernameNotFoundException("用户不存在");
        }
        if(!user.getIsActivate()){
            throw new UserNotActivateException("用户未激活");
        }
        if(user.getIsBan()){
            throw new UserHasBannedException("用户封禁");
        }
        List<Integer> roles = userService.getUserRole(email);
        List<GrantedAuthority> authorities = new ArrayList<>();
        for(Integer i : roles){
            Role role = Role.values()[i];
            switch (role){
                case USER:authorities.add(new SimpleGrantedAuthority("ROLE_USER"));break;
                case ADMIN:authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));break;
                default:break;
            }
        }
        return new User(
                email,
                user.getPassword(),
                authorities
        );
    }
}


