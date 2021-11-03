package com.susstore.mapper;

import com.susstore.pojo.process.AppealingDeal;
import com.susstore.pojo.process.ComplainGoods;
import com.susstore.pojo.process.ComplainUsers;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AdminMapper {

    List<ComplainUsers> getAllNotProcessComplainUser();

    List<ComplainUsers> getAllComplainUser();

    List<ComplainGoods> getAllNotProcessComplainGoods();

    List<ComplainGoods> getAllComplainGoods();

    List<AppealingDeal> getAllNotProcessAppealingDeal();

    List<AppealingDeal> getAllAppealingDeal();

    ComplainUsers getComplainUser(Integer recordId);

    ComplainGoods getComplainGoods(Integer recordId);

    AppealingDeal getAppealingDeal(Integer recordId);

    Integer processComplainGoods(Integer recordId);

    Integer processComplainUsers(Integer recordId);

    Integer processAppealingDeal(Integer recordId);

    Integer banGoods(Integer banState,Integer goodsId);

    Integer banUser(Integer userId);

    Integer updateDealState(Integer stage,Integer dealId);


}
