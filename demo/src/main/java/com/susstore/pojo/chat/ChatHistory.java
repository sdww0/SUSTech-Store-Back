package com.susstore.pojo.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatHistory {

    private Integer dealId;
    private Integer otherUserId;
    private String otherUserName;
    private String otherUserPicturePath;
    private String lastMessageContent;
    private Date lastMessageDate;
}
