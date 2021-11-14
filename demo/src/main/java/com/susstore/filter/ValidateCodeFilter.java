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
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        if ("/form".equalsIgnoreCase(httpServletRequest.getRequestURI())
                && "post".equalsIgnoreCase(httpServletRequest.getMethod())) {
            try {
                HttpSession session = httpServletRequest.getSession();
                String codeInReq = httpServletRequest.getParameter("checkCode");
                validateCode(session,codeInReq);
            } catch (UsernameNotFoundException e) {
                authenticationFailureHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, e);
                return;
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
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

