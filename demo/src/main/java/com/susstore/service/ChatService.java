package com.susstore.service;

import com.susstore.mapper.ChatMapper;
import com.susstore.pojo.chat.Chat;
import com.susstore.pojo.chat.ChatHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatMapper chatMapper;


    public Integer insertNewChatContent(Integer belongDealId, Boolean isSellerSpeak, Date speakDate, String content){
        return chatMapper.insertNewChatContent(belongDealId, isSellerSpeak, speakDate, content);
    }

    public List<ChatHistory> getUserChatHistory(Integer userId){
        return chatMapper.getUserChatHistory(userId);
    }

    public Chat getInitContent(Integer dealId, Integer userId, Boolean isSeller){
        return chatMapper.getInitContent(dealId, userId, isSeller);
    }

}
