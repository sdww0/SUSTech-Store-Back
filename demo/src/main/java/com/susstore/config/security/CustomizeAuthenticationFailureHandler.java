package com.susstore.config.security;

import com.alibaba.fastjson.JSON;
import com.susstore.config.security.exception.UserNotActivateException;
import com.susstore.config.security.exception.ValidateCodeException;
import com.susstore.result.CommonResult;
import com.susstore.result.ResultCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Component
public class CustomizeAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        CommonResult result = new CommonResult(ResultCode.LOGIN_FAIL);
        if(e instanceof UserNotActivateException){
            result = new CommonResult(ResultCode.USER_NOT_ACTIVATE);
        }else if(e instanceof ValidateCodeException){
            result = new CommonResult(ResultCode.CHECK_CODE_WRONG);
        }
        httpServletResponse.setContentType("text/json;charset=utf-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(result));
    }
}
