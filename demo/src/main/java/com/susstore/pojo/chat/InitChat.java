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
public class InitChat {

    private Integer userId;
    private String userName;
    private String picturePath;
    private List<ChatHistory> chatHistories;

}
