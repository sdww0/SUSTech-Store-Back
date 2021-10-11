package com.susstore.controller;

import com.susstore.pojo.ChatMessage;
import com.susstore.service.ChatService;
import com.susstore.service.DealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Date;

@RestController
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatService chatService;

    @Autowired
    private DealService dealService;

    /**
     * 用户模式
     * @param requestMsg 消息
     * @param principal 登录信息userId/dealId/(0或1，0为seller，1为buyer)
     */
    @MessageMapping("/chat")
    public void chat(ChatMessage requestMsg, Principal principal) {
        //这里使用的是spring的security的认证体系，所以直接使用Principal获取用户信息即可。
        //注意为什么使用queue，主要目的是为了区分广播和队列的方式。实际采用topic，也没有关系。但是为了好理解
        //messagingTemplate.convertAndSend( "/topic/broadcast", "hhh");
        String[] str = principal.getName().split("/");
        Integer userId = Integer.parseInt(str[0]);
        Integer dealId = Integer.parseInt(str[1]);
        boolean isSeller = Integer.parseInt(str[2])==0;
        chatService.insertNewChatContent(dealId,isSeller,new Date(System.currentTimeMillis()),requestMsg.getBody());
        Integer otherUserId = -1;
        StringBuilder otherUserUrl = new StringBuilder();
        if(isSeller){
            otherUserId = dealService.getBuyerIdByDealId(dealId);
        }else{
            otherUserId = dealService.getSellerIdByDealId(dealId);
        }
        otherUserUrl
                .append(otherUserId).append("/")
                .append(dealId).append("/")
                .append((isSeller?1:0));
        messagingTemplate.convertAndSendToUser(otherUserUrl.toString(), "/queue", requestMsg.getBody());

    }











    //    /**
//     * 广播模式
//     * @param requestMsg
//     * @return
//     */
//    @MessageMapping("/broadcast")
//    @SendTo("/topic/broadcast")
//    public String broadcast(Object requestMsg) {
//        //这里是有return，如果不写@SendTo默认和/topic/broadcast一样
//        return "server:" + requestMsg.toString();
//    }
//
//
//    /**
//     * 订阅模式，只是在订阅的时候触发，可以理解为：访问——>返回数据
//     * @param id
//     * @return
//     */
//    @SubscribeMapping("/subscribe/{dealId}")
//    public String subscribe(Principal principal,@DestinationVariable Long dealId) {
//        return "success, "+principal.getName();
//    }

}
