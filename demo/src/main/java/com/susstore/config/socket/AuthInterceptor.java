package com.susstore.config.socket;

import com.susstore.config.security.UserDetailServiceImpl;
import com.susstore.pojo.Users;
import com.susstore.service.DealService;
import com.susstore.service.UserService;
import com.susstore.util.CommonUtil;
import com.susstore.util.TokenUtil;
import com.susstore.config.socket.exception.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;


import java.security.Principal;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthInterceptor implements ChannelInterceptor {

    @Autowired
    private UserService userService;
    @Autowired
    private UserDetailServiceImpl jwtUserDetailsService;
    @Autowired
    private DealService dealService;


    /**
     * 连接前监听
     *
     * @param message
     * @param channel
     * @return
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        //1、判断是否首次连接
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            //2、判断token
            List<String> nativeHeader = accessor.getNativeHeader("Authorization");
            List<String> dealId = accessor.getNativeHeader("dealId");
            if (nativeHeader != null && !nativeHeader.isEmpty()
                    &&dealId!=null&&!dealId.isEmpty()&& CommonUtil.isInteger(dealId.get(0))) {
                String token = nativeHeader.get(0);
                if (StringUtils.isNotBlank(token)) {
                    String userEmail = TokenUtil.getUserEmailFromToken(token);
                    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        Users user = userService.getUserByEmail(userEmail);
                        boolean isSeller = true;
                        Integer stage = dealService.getDealStageBySellerIdAndDealId(user.getUserId(),Integer.parseInt(dealId.get(0)));
                        if(stage==null){
                            stage = dealService.getDealStageByBuyerIdAndDealId(user.getUserId(),Integer.parseInt(dealId.get(0)));
                            isSeller = false;
                        }
                        if(stage!=null) {
                            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(userEmail);
                            UserDetails userDetails1 = new User(
                                    user.getUserId() + "/" + dealId.get(0)+ "/"+(isSeller?0:1),
                                    userDetails.getPassword(), userDetails.getAuthorities());

                            if (TokenUtil.validateToken(token, userDetails)) {
                                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                        userDetails1, null, userDetails1.getAuthorities());
                                //如果存在用户信息，将用户名赋值，后期发送时，可以指定用户名即可发送到对应用户
                                accessor.setUser(authentication);
                                return message;
                            }
                        }
                    }
                }
            }
            return null;
        }
        //不是首次连接，已经登陆成功
        return message;
    }

}
