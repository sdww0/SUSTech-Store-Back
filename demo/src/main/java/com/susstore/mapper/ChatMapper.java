package com.susstore.mapper;

import com.susstore.pojo.chat.Chat;
import com.susstore.pojo.chat.ChatHistory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Mapper
public interface ChatMapper {


    Integer insertNewChatContent(Integer belongDealId, Boolean isSellerSpeak, Date speakDate,String content);

    List<ChatHistory> getUserChatHistory(Integer userId);

    Chat getInitContent(Integer dealId, Integer userId,Boolean isSeller);

}
