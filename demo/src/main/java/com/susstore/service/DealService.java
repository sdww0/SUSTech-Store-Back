package com.susstore.service;

import com.susstore.config.Constants;
import com.susstore.mapper.DealMapper;
import com.susstore.mapper.UsersMapper;
import com.susstore.method.StageControlMethod;
import com.susstore.pojo.*;
import com.susstore.pojo.process.AppealingDeal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public interface DealService {


    public Integer addDeal(Integer sellerId,Integer buyerId,Integer goodsId,Float price,Integer addressId);

    public Deal getDealById(int dealId);

    public Integer getDealStageBySellerIdAndDealId(Integer sellerId, Integer dealId) ;

    public Integer getDealStageByBuyerIdAndDealId(Integer buyerId, Integer dealId) ;

    public Integer getBuyerIdByDealId(Integer dealId);

    public Integer getSellerIdByDealId(Integer dealId);

    public Integer getDealStageByDealId(Integer dealId) ;

    public Integer changeDealStage(Integer dealId, Stage stage) ;


    public Integer addMailingNumber(Integer dealId,String mailingNumber);

    public Integer notNeedMailingNumber(Integer dealId);

    public Integer addUserComment(Integer dealId,
                                  Integer commentUserId,
                                  Integer targetUserId,
                                  Date date,
                                  String content,
                                  Boolean isGood);

    public Boolean checkUserHadComment(Integer dealId,Integer userId);

    public Boolean hasDeliver(Integer dealId);

    public Integer setAddress(Integer dealId,Integer addressId);

    public Integer goodComment(Integer userId);

    public Integer badComment(Integer userId);


    /**
     *
     * @param email 邮箱
     * @param dealId 订单id
     * @param wantStage 期望的阶段
     * @param method 运行方法
     * @return 一个map，里面包含错误码以及可能会返回的数据，不为null
     * 状态码的key为code
     * 正数:method自定义的状态码
     *   0:正常退出
     *  -1:不存在订单
     *  -2:当前阶段不能跳转到目标阶段/目标阶段错误
     *  -3:不是对应的买卖家，拒绝访问
     *  -4:错误的自定义状态码
     */
    public Map<String,Object> stageControl(String email,
                                           Integer dealId,
                                           Stage wantStage,
                                           boolean needAutoChange,
                                           StageControlMethod method
    );

    public List<Deal> getDealBySellerAndStage(Integer userId, Integer stage);

    public List<Deal> getDealByBuyerAndStage(Integer userId,Integer stage);

    public Integer addAppealingContent(Integer dealId,String content,String picturePath);

    public Float getDealPrice(Integer dealId);
    public List<Deal> getDealByBuyer(Integer userId);
    public List<Deal> getDealBySeller(Integer userId);




}
