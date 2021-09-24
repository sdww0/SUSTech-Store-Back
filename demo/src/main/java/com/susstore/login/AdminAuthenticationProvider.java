package com.susstore.login;

import com.susstore.mapper.SignMapper;
import com.susstore.mapper.UserMapper;
import com.susstore.pojo.Users;
import com.susstore.service.UserDetailServiceImpl;
import com.susstore.service.UserService;
import com.susstore.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class AdminAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    private UserService userService;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        LoginUsers loginUsers = (LoginUsers)userDetailService.loadUserByUsername(email);

        boolean isValid  = PasswordUtil.isValidPassword(password,loginUsers.getPassword(),loginUsers.getSalt());
        if (!isValid) {
            throw new BadCredentialsException("密码错误！");
        }

        String token = PasswordUtil.encodePassword(System.currentTimeMillis() +
                loginUsers.getSalt(), loginUsers.getSalt());
        userService.updateToken(token,loginUsers.getId());
        return new UsernamePasswordAuthenticationToken(loginUsers,password,loginUsers.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
