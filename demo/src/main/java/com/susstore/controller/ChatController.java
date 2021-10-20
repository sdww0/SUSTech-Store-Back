package com.susstore.controller;

import com.alibaba.fastjson.JSON;
import com.susstore.pojo.ChatMessage;
import com.susstore.pojo.Deal;
import com.susstore.pojo.Goods;
import com.susstore.pojo.GoodsState;
import com.susstore.pojo.chat.Chat;
import com.susstore.pojo.chat.DataBaseChat;
import com.susstore.result.CommonResult;
import com.susstore.result.ResultCode;
import com.susstore.service.ChatService;
import com.susstore.service.DealService;
import com.susstore.service.GoodsService;
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

    /**
     * 传递消息，保存到数据库，并发到另外一个用户
     * @param requestMsg 消息
     * @param principal 登录信息userId/chatId/(0或1，1为initiator(发起者)，0为announcer(发布者))
     */
    @MessageMapping("/chat")
    public void chat(ChatMessage requestMsg, Principal principal) {
        //这里使用的是spring的security的认证体系，所以直接使用Principal获取用户信息即可。
        //注意为什么使用queue，主要目的是为了区分广播和队列的方式。实际采用topic，也没有关系。但是为了好理解
        //messagingTemplate.convertAndSend( "/topic/broadcast", "hhh");
        String[] str = principal.getName().split("/");
        Integer userId = Integer.parseInt(str[0]);
        Integer chatId = Integer.parseInt(str[1]);
        boolean isInitiator = Integer.parseInt(str[2])==1;
        chatService.insertNewChatContent(chatId,isInitiator,new Date(System.currentTimeMillis()),requestMsg.getBody());
        Integer otherUserId = -1;
        StringBuilder otherUserUrl = new StringBuilder();
        if(isInitiator){
            otherUserId = chatService.getAnnouncerId(chatId);
        }else{
            otherUserId = chatService.getInitiatorId(chatId);
        }
        otherUserUrl
                .append(otherUserId).append("/")
                .append(chatId).append("/")
                .append((isInitiator?0:1));
        messagingTemplate.convertAndSendToUser(otherUserUrl.toString(), "/queue", requestMsg.getBody());

    }

    /**
     * 订阅,返回建立连接时需要的初始数据
     * @return json格式化的数据
     */
    @SubscribeMapping("/subscribe/chat")
    public String subscribe(Principal principal) {
        String[] str = principal.getName().split("/");
        Integer userId = Integer.parseInt(str[0]);
        Integer chatId = Integer.parseInt(str[1]);
        boolean isInitiator = Integer.parseInt(str[2])==1;
        Chat chat = chatService.getInitContent(chatId,userId,isInitiator);
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
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(path="/chat/want",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    @ApiOperation("生成聊天信息")
    public CommonResult addDeal(
            @ApiParam("SpringSecurity用户认证信息") Principal principal,
            @ApiParam("商品id") @RequestParam("goodsId") Integer goodsId
    ){
        //查看商品是不是已经下架
        // check buyer&seller&goodsId&stage
        Integer userId = userService.queryUserByEmail(principal.getName());
        Integer chatId = null;
        if ((chatId=chatService.getChatId(goodsId,goodsId))!=null){
            return new CommonResult(ResultCode.SUCCESS,chatId);
        }
        Goods goods = goodsService.getGoodsById(goodsId);
        Integer id = chatService.addChat(DataBaseChat.builder().initiatorId(userId).goodsId(goodsId).build());
        if(id==null||id<0){
            return new CommonResult(ResultCode.DEAL_ADD_FAIL);
        }
        goodsService.increaseWant(goodsId);
        return new CommonResult(ResultCode.SUCCESS,id);

    }

}
