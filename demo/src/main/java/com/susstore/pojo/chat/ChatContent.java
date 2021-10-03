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
public class ChatContent {


    private Boolean isSpeakUser;
    private String content;
    private Date date;

}
