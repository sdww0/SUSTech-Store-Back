package com.susstore.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {

    private Integer commentId;
    private String content;
    private String username;
    private Integer userId;
    private String picturePath;
    private Date commentDate;
}
