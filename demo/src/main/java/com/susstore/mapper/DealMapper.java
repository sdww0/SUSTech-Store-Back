package com.susstore.mapper;

import com.susstore.pojo.Deal;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface DealMapper {
    Deal getDeal(int sellerId, int buyerId, int goodsId,int stage);

    int addDeal(Deal deal);

    Deal getDealById(int dealId);
}
