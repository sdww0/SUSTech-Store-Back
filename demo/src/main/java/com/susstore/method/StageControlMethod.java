package com.susstore.method;

import com.susstore.pojo.Stage;

public interface StageControlMethod{

    Integer run(Integer userId, Integer otherId,Integer dealId, Stage currentStage, Stage wantStage, Boolean isBuyer);

}