package com.susstore.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsersLabel {

    private Integer userId;
    private Integer labelId;
    private Integer visitTime;


}
