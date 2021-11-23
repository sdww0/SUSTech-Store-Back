package com.susstore.pojo.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat {

    private Integer speakUserId;
    private String speakUserName;
    private String speakUserPicturePath;
    private Integer otherUserId;
    private String otherUserName;
    private String otherUserPicturePath;

    private Integer goodsId;
    private String goodsPicturePath;
    private Float goodsPrice;
    private Boolean isBuyer;

    private List<ChatContent> chatContents;

}
