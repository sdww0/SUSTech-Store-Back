package com.susstore.mapper;

import com.susstore.pojo.Deal;
import com.susstore.pojo.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Mapper
@Repository
public interface DealMapper {
    Deal getDeal(int sellerId, int buyerId, int goodsId,int stage);

    int addDeal(Deal deal);

    Integer updateDeal(Goods goods);

    Deal getDealById(int dealId);

    Integer getDealStageBySellerIdAndDealId(Integer sellerId,Integer dealId);

    Integer getDealStageByBuyerIdAndDealId(Integer buyerId,Integer dealId);

    Integer getBuyerIdByDealId(Integer dealId);

    Integer getSellerIdByDealId(Integer dealId);

    Integer getDealStageByDealId(Integer dealId);

    Integer changeDealStage(Integer dealId,Integer stage);

    Float getGoodsPrice(Integer dealId);

    Integer addMailingNumber(Integer dealId,String mailingNumber);

    Integer notNeedMailingNumber(Integer dealId);


    Integer addUserComment(Integer dealId,
                           Integer commentUserId,
                           Integer targetUserId,
                           Date date,
                           String content);

    /**
     * 查看某人在某订单是否被评价
     * @param dealId dealId
     * @param userId userId
     * @return 是否评价
     */
    Boolean checkUserHadComment(Integer dealId,Integer userId);

    Boolean hasDeliver(Integer dealId);



}
