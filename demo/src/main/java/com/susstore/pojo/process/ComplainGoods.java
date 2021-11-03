package com.susstore.pojo.process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplainGoods {

    private Integer recordId;
    private Integer goodsId;
    private Integer complainerId;
    private String content;
    private String picturePath;
    private Boolean isProcess;

}
