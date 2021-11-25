package com.susstore.config.security;

import com.susstore.config.security.exception.*;
import com.susstore.pojo.Role;
import com.susstore.pojo.Users;
import com.susstore.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Qualifier("UserServiceImpl")
    private UserService userServiceImpl;

    /**
     * 0 代表用户名密码错误
     * 1 代表用户未激活
     * 2 代表用户封禁
     * 3 代表验证码不正确
     * 抛出异常会打印。。只能这样解决了
     * @param email
     * @return
     * @throws UserNotActivateException
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UserNotActivateException, UsernameNotFoundException {
        Users user = userServiceImpl.getUserByEmail(email);
        if(user==null){
            throw new UsernameNotFoundException("0");
        }
        if(!user.getIsActivate()){
            throw new UsernameNotFoundException("1");
        }
        if(user.getIsBan()){
            throw new UsernameNotFoundException("2");
        }
        List<Integer> roles = userServiceImpl.getUserRole(email);
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


