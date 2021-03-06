package com.susstore.controller;

import com.susstore.config.Constants;
import com.susstore.pojo.ChatMessage;
import com.susstore.pojo.Users;
import com.susstore.pojo.chat.*;
import com.susstore.result.CommonResult;
import com.susstore.result.ResultCode;
import com.susstore.service.ChatService;
import com.susstore.service.GoodsService;
import com.susstore.service.MailService;
import com.susstore.service.UserService;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.Date;
@Slf4j
@RestController
@Api(value = "聊天",tags = {"聊天访问接口"})
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    @Qualifier("ChatServiceImpl")
    private ChatService chatService;

    @Autowired
    @Qualifier("UserServiceImpl")
    private UserService userServiceImpl;

    @Autowired
    @Qualifier("GoodsServiceImpl")
    private GoodsService goodsService;

    @Autowired
    @Qualifier("MailServiceThreadImpl")
    private MailService mailService;

    @GetMapping("/chat/picture/{chatId}/{fileName}")
    @ApiOperation("获取用户默认头像")
    public void defaultImage(
            @ApiParam("聊天Id") @PathVariable("chatId") Integer chatId,
            @ApiParam("聊天Id") @PathVariable("fileName") String fileName,
            HttpServletResponse response) throws IOException {

        response.setContentType("image/jpeg;charset=utf-8");
        response.setHeader("Content-Disposition", "inline; filename=girls.png");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(Files.readAllBytes(Path.of(Constants.CHAT_PICTURE_PATH+chatId+"/picture/"+fileName)));
        outputStream.flush();
        outputStream.close();
    }



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
        if(isInitiator){
            chatService.addOneNotInitiatorUnread(chatId);
        }else{
            chatService.addOneInitiatorUnread(chatId);
        }
        messagingTemplate.convertAndSendToUser(String.valueOf(otherUserId),"/queue",requestMsg);
    }

    /**
     * 清空数据库未读消息
     * @param requestMsg 消息
     * @param principal 登录信息userId
     */
    @MessageMapping("/clear")
    public void clear(ClearChat requestMsg, Principal principal) {
        //这里使用的是spring的security的认证体系，所以直接使用Principal获取用户信息即可。
        //注意为什么使用queue，主要目的是为了区分广播和队列的方式。实际采用topic，也没有关系。但是为了好理解
        //messagingTemplate.convertAndSend( "/topic/broadcast", "hhh");
        Integer userId = Integer.parseInt(principal.getName());
        Integer chatId = Integer.parseInt(requestMsg.getBody());
        Boolean isInitiator = isInitiator(userId,chatId);
        assert isInitiator !=null;
        log.info(userId+"/"+chatId+"/"+isInitiator);
        if(isInitiator){
            chatService.clearInitiatorUnread(chatId);
        }else{
            chatService.clearNotInitiatorUnread(chatId);
        }
    }

    @ApiOperation("获得聊天历史记录等")
    @GetMapping("/chat/history/{chatId}")
    @PreAuthorize("hasRole('USER')")
    public CommonResult chatHistory(
            @ApiParam("SpringSecurity认证信息") Principal principal,
            @ApiParam("聊天id") @PathVariable("chatId") Integer chatId
    ) {
        Integer userId = userServiceImpl.queryUserIdByEmail(principal.getName());
        Boolean isInitiator = isInitiator(userId,chatId);
        if(isInitiator==null){
            return new CommonResult(ResultCode.PARAM_NOT_VALID);
        }
        Chat chat = chatService.getInitContent(chatId,userId,isInitiator);
        chat.setIsBuyer(!chat.getIsBuyer());
        return new CommonResult(ResultCode.SUCCESS,chat);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/chat/picture/{chatId}")
    @ApiOperation("添加聊天图片")
    public CommonResult chatPicture(
            @ApiParam("SpringSecurity认证信息") Principal principal,
            @ApiParam("聊天图片") @RequestParam("photo") MultipartFile photo,
            @ApiParam("聊天id") @PathVariable("chatId") Integer chatId
    ){
        return new CommonResult(ResultCode.SUCCESS,chatService.storeImage(photo,chatId, userServiceImpl.queryUserIdByEmail(principal.getName())));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/chat/list")
    @ApiOperation("获取所有聊天最新历史")
    public CommonResult initChatList(
            @ApiParam("SpringSecurity认证信息") Principal principal
    ){
        Users users = userServiceImpl.getUserNameAndPictureById(userServiceImpl.queryUserIdByEmail(principal.getName()));
        return new CommonResult(ResultCode.SUCCESS,
                new InitChat().builder()
                .picturePath(users.getPicturePath())
                .userName(users.getUserName())
                .userId(users.getUserId())
                .chatHistories(chatService.getUserChatHistory(userServiceImpl.queryUserIdByEmail(principal.getName()))).build()

                        );
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
        Integer userId = userServiceImpl.queryUserIdByEmail(principal.getName());
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
        mailService.sendSimpleMail(userServiceImpl.getUserEmail(announcerId),"有人想要卖/买的商品","有人想卖/买你的发布商品,快去看看吧");
        goodsService.increaseWant(goodsId);
        Users users = userServiceImpl.getUserNameAndPictureById(userId);
        ChatHistory chatHistory = ChatHistory.builder().chatId(id)
                .otherUserId(userId)
                .otherUserPicturePath(users.getPicturePath())
                .otherUserName(users.getUserName()).build();
        messagingTemplate.convertAndSendToUser(String.valueOf(announcerId),"/queue",chatHistory);
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
