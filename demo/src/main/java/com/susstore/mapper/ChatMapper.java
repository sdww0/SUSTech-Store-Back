package com.susstore.mapper;

import com.susstore.pojo.chat.Chat;
import com.susstore.pojo.chat.ChatHistory;
import com.susstore.pojo.chat.DataBaseChat;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Mapper
public interface ChatMapper {


    Integer insertNewChatContent(Integer chatId, Boolean isInitiatorSpeak, Date speakDate,String content);

    List<ChatHistory> getUserChatHistory(Integer userId);

    Chat getInitContent(Integer chatId, Integer userId,Boolean isInitiator);

    Integer getInitiatorId(Integer chatId);

    Integer getAnnouncerId(Integer chatId);

    Integer getChatId(Integer goodsId,Integer userId);

    Integer addChat(DataBaseChat chat);

}
