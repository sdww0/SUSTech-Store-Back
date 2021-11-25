package com.susstore.config.security;

import com.alibaba.fastjson.JSON;
import com.susstore.pojo.Users;
import com.susstore.result.CommonResult;
import com.susstore.result.ResultCode;
import com.susstore.service.UserService;

import com.susstore.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Component
public class CustomizeAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private TokenUtil jwtUtil;

    @Autowired
    @Qualifier("UserServiceImpl")
    private UserService userServiceImpl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        Users users =  userServiceImpl.getUserByEmail(authentication.getName());
        users.setPassword("");
        CommonResult result = new CommonResult(ResultCode.SUCCESS,users);
        //生成jwt
        String jwt = jwtUtil.generateToken(authentication.getName());
        //把生成的jwt放在请求头中返回，前端以后访问后端接口请求头都需要带上它
        httpServletResponse.setHeader(jwtUtil.getHeader(),jwt);
        httpServletResponse.setHeader("Access-Control-Expose-Headers", jwtUtil.getHeader());
        httpServletResponse.setContentType("text/json;charset=utf-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(result));
    }
}
