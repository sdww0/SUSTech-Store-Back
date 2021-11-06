package com.susstore.config.socket;

import com.susstore.config.security.UserDetailServiceImpl;
import com.susstore.service.ChatService;
import com.susstore.service.UserService;
import com.susstore.util.CommonUtil;
import com.susstore.util.TokenUtil;

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
import org.springframework.stereotype.Component;


import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthInterceptor implements ChannelInterceptor {

    @Autowired
    private UserService userService;
    @Autowired
    private UserDetailServiceImpl jwtUserDetailsService;
    @Autowired
    private ChatService chatService;


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
            List<String> chatId = accessor.getNativeHeader("chatId");
            if (nativeHeader != null && !nativeHeader.isEmpty()
                    &&chatId!=null&&!chatId.isEmpty()&& CommonUtil.isInteger(chatId.get(0))) {
                String token = nativeHeader.get(0);
                if (StringUtils.isNotBlank(token)) {
                    String userEmail = TokenUtil.getUserEmailFromToken(token);
                    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        Integer userId = userService.queryUserIdByEmail(userEmail);
                        boolean isInitiator = false;
                        Integer otherId = chatService.getAnnouncerId(Integer.parseInt(chatId.get(0)));
                        if(otherId==null){
                            return null;
                        }
                        if(!otherId.equals(userId)){
                            otherId = chatService.getInitiatorId(Integer.parseInt(chatId.get(0)));
                            isInitiator = true;
                        }
                        if(otherId!=null) {
                            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(userEmail);
                            UserDetails userDetails1 = new User(
                                    userId + "/" + chatId.get(0)+ "/"+(isInitiator?1:0),
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
