package com.susstore.controller;

import com.alibaba.fastjson.JSON;
import com.susstore.pojo.ChatMessage;
import com.susstore.pojo.chat.Chat;
import com.susstore.result.CommonResult;
import com.susstore.result.ResultCode;
import com.susstore.service.ChatService;
import com.susstore.service.DealService;
import com.susstore.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Date;

@RestController
@Api(value = "聊天",tags = {"聊天访问接口"})
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatService chatService;

    @Autowired
    private DealService dealService;

    @Autowired
    private UserService userService;

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

    /**
     * 订阅模式，只是在订阅的时候触发，可以理解为：访问——>返回数据
     * @return json格式化的数据
     */
    @SubscribeMapping("/subscribe/chat")
    public String subscribe(Principal principal) {
        String[] str = principal.getName().split("/");
        Integer userId = Integer.parseInt(str[0]);
        Integer dealId = Integer.parseInt(str[1]);
        boolean isSeller = Integer.parseInt(str[2])==0;
        Chat chat = chatService.getInitContent(dealId,userId,isSeller);
        return JSON.toJSONString(chat);
    }

    @PreAuthorize("hasRole('USER')")
    @ApiOperation("获得登录用户的所有聊天历史最新记录")
    @GetMapping("/chat/list")
    public CommonResult chatList(Principal principal){
        return new CommonResult(ResultCode.SUCCESS,chatService.getUserChatHistory(userService.queryUserByEmail(principal.getName())));
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


}
