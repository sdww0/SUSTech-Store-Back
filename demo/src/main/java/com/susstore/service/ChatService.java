package com.susstore.service;

import com.susstore.pojo.chat.Chat;
import com.susstore.pojo.chat.ChatHistory;
import com.susstore.pojo.chat.DataBaseChat;
import com.susstore.util.ImageUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.susstore.config.Constants.BACK_END_LINK;
import static com.susstore.config.Constants.CHAT_PICTURE_PATH;

@Service
public interface ChatService {


    public Integer insertNewChatContent(Integer chatId, Boolean isInitiator, Date speakDate, String content);

    public List<ChatHistory> getUserChatHistory(Integer userId);

    public Chat getInitContent(Integer dealId, Integer userId, Boolean isInitiator);

    public Integer getInitiatorId(Integer chatId);

    public Integer getAnnouncerId(Integer chatId);

    public Integer getChatId(Integer goodsId,Integer userId);

    public     Integer addChat(DataBaseChat chat);

    public Integer addOneNotInitiatorUnread(Integer chatId);

    public Integer addOneInitiatorUnread(Integer chatId);

    public Integer clearInitiatorUnread(Integer chatId);

    public Integer clearNotInitiatorUnread(Integer chatId);

    public String storeImage(MultipartFile photo, Integer chatId, Integer userId);

}
