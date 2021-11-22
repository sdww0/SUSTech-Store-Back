package com.susstore.controller;

import com.alibaba.fastjson.JSON;
import com.susstore.pojo.ChatMessage;
import com.susstore.pojo.Goods;
import com.susstore.pojo.chat.Chat;
import com.susstore.pojo.chat.DataBaseChat;
import com.susstore.result.CommonResult;
import com.susstore.result.ResultCode;
import com.susstore.service.ChatService;
import com.susstore.service.GoodsService;
import com.susstore.service.MailServiceThread;
import com.susstore.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    private UserService userService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private MailServiceThread mailService;

    /**
     * 传递消息，保存到数据库，并发到另外一个用户
     * @param requestMsg 消息
     * @param principal 登录信息userId
     */
    @MessageMapping("/chat")
    public void chat(ChatMessage requestMsg, Principal principal) {
        //这里使用的是spring的security的认证体系，所以直接使用Principal获取用户信息即可。
        //注意为什么使用queue，主要目的是为了区分广播和队列的方式。实际采用topic，也没有关系。但是为了好理解
        //messagingTemplate.convertAndSend( "/topic/broadcast", "hhh");
        Integer userId = Integer.parseInt(principal.getName());
        Integer chatId = requestMsg.getChatId();
        Boolean isInitiator = isInitiator(userId,chatId);
        assert isInitiator !=null;
        chatService.insertNewChatContent(chatId,isInitiator,new Date(),requestMsg.getBody());
        Integer otherUserId = isInitiator ? chatService.getAnnouncerId(chatId) : chatService.getInitiatorId(chatId);
        requestMsg.setDate(new Date());
        messagingTemplate.convertAndSendToUser(String.valueOf(otherUserId),"/queue",requestMsg.getBody());
    }

    @ApiOperation("获得聊天历史记录等")
    @GetMapping("/chat/history/{chatId}")
    @PreAuthorize("hasRole('USER')")
    public CommonResult chatHistory(
            @ApiParam("SpringSecurity认证信息") Principal principal,
            @ApiParam("聊天id") @PathVariable("chatId") Integer chatId
    ) {
        Integer userId = userService.queryUserIdByEmail(principal.getName());
        Boolean isInitiator = isInitiator(userId,chatId);
        if(isInitiator==null){
            return new CommonResult(ResultCode.PARAM_NOT_VALID);
        }
        Chat chat = chatService.getInitContent(chatId,userId,isInitiator);
        return new CommonResult(ResultCode.SUCCESS,chat);
    }

    @SubscribeMapping("/subscribe/chat")
    public String initChatList(Principal principal){
        return JSON.toJSONString(chatService.getUserChatHistory(Integer.parseInt(principal.getName())));
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(path="/chat/want",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    @ApiOperation("生成聊天信息")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4090,message = "聊天已存在"),
            @ApiResponse(code = 4091,message = "不可以与自己建立聊天"),
            @ApiResponse(code = 4001,message = "填写的参数有误")
    })
    public CommonResult addChat(
            @ApiParam("SpringSecurity用户认证信息") Principal principal,
            @ApiParam("商品id") @RequestParam("goodsId") Integer goodsId
    ){
        //查看商品是不是已经下架
        // check buyer&seller&goodsId&stage
        Integer userId = userService.queryUserIdByEmail(principal.getName());
        Integer chatId = null;
        if ((chatId=chatService.getChatId(goodsId,userId))!=null){
            return new CommonResult(ResultCode.CHAT_ALREADY_EXISTS,chatId);
        }
        Integer announcerId = goodsService.getAnnouncerId(goodsId);
        if(announcerId==null){
            return new CommonResult(ResultCode.PARAM_NOT_VALID);
        }
        if(announcerId.equals(userId)){
            return new CommonResult(ResultCode.CANNOT_CHAT_WITH_OWN);
        }
        Integer id = chatService.addChat(DataBaseChat.builder().initiatorId(userId).goodsId(goodsId).build());
        if(id==null||id<0){
            return new CommonResult(ResultCode.CHAT_ALREADY_EXISTS);
        }
        mailService.sendSimpleMail(principal.getName(),"有人想要你的商品","有人对你的商品感兴趣,快去看看吧");
        goodsService.increaseWant(goodsId);
        return new CommonResult(ResultCode.SUCCESS,id);
    }

    private Boolean isInitiator(Integer userId,Integer chatId){
        boolean isInitiator = false;
        Integer otherId = chatService.getAnnouncerId(chatId);
        if(otherId==null){
            return null;
        }
        if(!otherId.equals(userId)){
            otherId = chatService.getInitiatorId(chatId);
            if(!otherId.equals(userId)){
                return null;
            }
            isInitiator = true;
        }
        return isInitiator;
    }

}
