package com.susstore.filter;

import com.susstore.controller.ValidateCodeController;
import com.susstore.config.security.CustomizeAuthenticationFailureHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import com.susstore.config.security.exception.ValidateCodeException;

@Component
public class ValidateCodeFilter extends OncePerRequestFilter {

    @Autowired
    private CustomizeAuthenticationFailureHandler authenticationFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //解决跨域问题，因为这个过滤器在跨域过滤器前面，所以直接放这了...
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods",
                "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",
                "Content-Type, x-requested-with, X-Custom-Header, Authorization");
        if ("/login".equalsIgnoreCase(request.getRequestURI())
                && "post".equalsIgnoreCase(request.getMethod())) {
            try {
                HttpSession session = request.getSession();
                String codeInReq = request.getParameter("checkCode");
                validateCode(session,codeInReq);
            } catch (UsernameNotFoundException e) {
                authenticationFailureHandler.onAuthenticationFailure(request, response, e);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void validateCode(HttpSession session,String codeInRequest) throws ServletRequestBindingException {
        String codeInSession = (String)session.getAttribute(ValidateCodeController.SESSION_KEY_IMAGE_CODE);

        if (StringUtils.isBlank(codeInRequest)) {
            throw new UsernameNotFoundException("3");
        }
        if (codeInSession == null) {
            throw new UsernameNotFoundException("3");
        }
        if (!codeInRequest.equalsIgnoreCase(codeInSession)) {
            throw new UsernameNotFoundException("3");
        }
        session.removeAttribute(ValidateCodeController.SESSION_KEY_IMAGE_CODE);

    }

}

