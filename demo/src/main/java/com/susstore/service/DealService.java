package com.susstore.service;

import com.susstore.mapper.DealMapper;
import com.susstore.pojo.Deal;
import com.susstore.pojo.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DealService {


    @Autowired
    private DealMapper dealMapper;

    public Deal getDeal(int sellerId, int buyerId, int goodsId,int stage){
        return dealMapper.getDeal(sellerId , buyerId , goodsId, stage);
    }

    public int addDeal(Deal deal){
        return dealMapper.addDeal(deal);
    }

    public Deal getDealById(int dealId){
        return dealMapper.getDealById(dealId);
    }

    public Integer getDealStageBySellerIdAndDealId(Integer sellerId,Integer dealId){
        return dealMapper.getDealStageBySellerIdAndDealId(sellerId,dealId);
    }

    public Integer getDealStageByBuyerIdAndDealId(Integer buyerId,Integer dealId){
        return dealMapper.getDealStageByBuyerIdAndDealId(buyerId,dealId);
    }

    public Integer getBuyerIdByDealId(Integer dealId){
        return dealMapper.getBuyerIdByDealId(dealId);
    }

    public Integer getSellerIdByDealId(Integer dealId){
        return dealMapper.getSellerIdByDealId(dealId);
    }

}
