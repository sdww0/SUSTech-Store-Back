package com.susstore.mapper;

import com.susstore.pojo.Deal;
import com.susstore.pojo.Goods;
import com.susstore.pojo.process.AppealingDeal;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Mapper
@Repository
public interface DealMapper {

    Integer addDeal(Deal deal);

    Integer updateDeal(Deal deal);

    Deal getDealById(int dealId);

    Integer getDealStageBySellerIdAndDealId(Integer sellerId,Integer dealId);

    Integer getDealStageByBuyerIdAndDealId(Integer buyerId,Integer dealId);

    Integer getBuyerIdByDealId(Integer dealId);

    Integer getSellerIdByDealId(Integer dealId);

    Integer getDealStageByDealId(Integer dealId);

    Integer changeDealStage(Integer dealId,Integer stage);

    Integer addMailingNumber(Integer dealId,String mailingNumber);

    Integer notNeedMailingNumber(Integer dealId);


    Integer addUserComment(Integer dealId,
                           Integer commentUserId,
                           Integer targetUserId,
                           Date date,
                           String content,
                           Boolean isGood);

    /**
     * 查看某人在某订单是否被评价
     * @param dealId dealId
     * @param userId userId
     * @return 是否评价
     */
    Boolean checkUserHadComment(Integer dealId,Integer userId);

    Boolean hasDeliver(Integer dealId);

    Integer setAddress(Integer dealId,Integer addressId);

    List<Deal> getDealBySellerAndStage(Integer userId, Integer stage);

    List<Deal> getDealByBuyerAndStage(Integer userId,Integer stage);

    Integer addAppealingContent(AppealingDeal appealingDeal);

    Float getDealPrice(Integer dealId);

    List<Deal> getDealByBuyer(Integer userId);

    List<Deal> getDealBySeller(Integer userId);

}
