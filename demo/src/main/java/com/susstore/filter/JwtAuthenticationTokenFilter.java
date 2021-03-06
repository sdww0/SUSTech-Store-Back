package com.susstore.filter;

import com.susstore.config.security.UserDetailServiceImpl;
import com.susstore.pojo.Users;
import com.susstore.service.UserService;

import com.susstore.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailServiceImpl jwtUserDetailsService;

    @Autowired
    @Qualifier("UserServiceImpl")
    private UserService userServiceImpl;


    private String tokenHeader = "Authorization";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        String authToken = request.getHeader(this.tokenHeader);
        if(authToken==null){
            authToken = request.getParameter(this.tokenHeader);
        }
        if (authToken != null) {
            String userEmail = TokenUtil.getUserEmailFromToken(authToken);
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Users user = userServiceImpl.getUserByEmail(userEmail);
                UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(userEmail);

                if (TokenUtil.validateToken(authToken, userDetails) /*&& userService.isPermissionApi(user.getId(), request.getRequestURI(), request.getMethod())*/) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(
                            request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }
}