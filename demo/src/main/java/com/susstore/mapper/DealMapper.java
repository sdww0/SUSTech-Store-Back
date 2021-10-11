package com.susstore.mapper;

import com.susstore.pojo.Deal;
import com.susstore.pojo.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

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

}
