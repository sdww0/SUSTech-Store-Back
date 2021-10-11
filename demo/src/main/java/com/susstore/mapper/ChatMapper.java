package com.susstore.mapper;

import com.susstore.pojo.chat.ChatContent;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
@Repository
@Mapper
public interface ChatMapper {


    Integer insertNewChatContent(Integer belongDealId, Boolean isSellerSpeak, Date speakDate,String content);


}
