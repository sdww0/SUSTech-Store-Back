package com.susstore.filter;

import com.susstore.config.security.UserDetailServiceImpl;
import com.susstore.pojo.Users;
import com.susstore.service.UserService;
import com.susstore.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
    private UserService userService;


    private String tokenHeader = "Authorization";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        //解决跨域问题，因为这个过滤器在跨域过滤器前面，所以直接放这了...
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
            httpServletResponse.setHeader("Access-Control-Allow-Methods",
                    "POST, GET, OPTIONS, DELETE");
            httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
            httpServletResponse.setHeader("Access-Control-Allow-Headers",
                    "Content-Type, x-requested-with, X-Custom-Header, Authorization");
        }
        String authToken = request.getHeader(this.tokenHeader);
        if(authToken==null){
            authToken = request.getParameter(this.tokenHeader);
        }
        if (authToken != null) {
            String userEmail = TokenUtil.getUserEmailFromToken(authToken);
            logger.info("checking authentication " + userEmail);
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Users user = userService.getUserByEmail(userEmail);
                UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(userEmail);

                if (TokenUtil.validateToken(authToken, userDetails) /*&& userService.isPermissionApi(user.getId(), request.getRequestURI(), request.getMethod())*/) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(
                            request));
                    logger.info("authenticated user " + userEmail + ", setting security context");
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }
}