package com.susstore.service;

import com.susstore.mapper.ChatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ChatService {

    @Autowired
    private ChatMapper chatMapper;


    public Integer insertNewChatContent(Integer belongDealId, Boolean isSellerSpeak, Date speakDate, String content){
        return chatMapper.insertNewChatContent(belongDealId, isSellerSpeak, speakDate, content);
    }



}
