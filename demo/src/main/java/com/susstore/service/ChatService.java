package com.susstore.service;

import com.susstore.mapper.ChatMapper;
import com.susstore.pojo.chat.Chat;
import com.susstore.pojo.chat.ChatHistory;
import com.susstore.pojo.chat.DataBaseChat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatMapper chatMapper;


    public Integer insertNewChatContent(Integer chatId, Boolean isInitiator, Date speakDate, String content){
        return chatMapper.insertNewChatContent(chatId, isInitiator, speakDate, content);
    }

    public List<ChatHistory> getUserChatHistory(Integer userId){
        return chatMapper.getUserChatHistory(userId);
    }

    public Chat getInitContent(Integer dealId, Integer userId, Boolean isInitiator){
        return chatMapper.getInitContent(dealId, userId, isInitiator);
    }

    public Integer getInitiatorId(Integer chatId){
        return chatMapper.getInitiatorId(chatId);
    }

    public Integer getAnnouncerId(Integer chatId){
        return chatMapper.getAnnouncerId(chatId);
    }

    public Integer getChatId(Integer goodsId,Integer userId){
        return chatMapper.getChatId(goodsId,userId);
    }

    public     Integer addChat(DataBaseChat chat){
        chatMapper.addChat(chat);
        return chat.getChatId();
    }

    public Integer addOneNotInitiatorUnread(Integer chatId) {
        return chatMapper.addOneNotInitiatorUnread(chatId);
    }

    public Integer addOneInitiatorUnread(Integer chatId) {
        return chatMapper.addOneInitiatorUnread(chatId);
    }

    public Integer clearInitiatorUnread(Integer chatId) {
        return chatMapper.clearInitiatorUnread(chatId);
    }

    public Integer clearNotInitiatorUnread(Integer chatId) {
        return chatMapper.clearNotInitiatorUnread(chatId);
    }


}
