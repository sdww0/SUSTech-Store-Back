package com.susstore.service;

import com.susstore.config.Constants;
import com.susstore.mapper.DealMapper;
import com.susstore.mapper.UsersMapper;
import com.susstore.method.StageControlMethod;
import com.susstore.pojo.Deal;
import com.susstore.pojo.GoodsAbbreviation;
import com.susstore.pojo.Stage;
import com.susstore.pojo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class DealService {


    @Autowired
    private DealMapper dealMapper;

    @Autowired
    private UsersMapper usersMapper;

    public Deal checkExists(Integer userId, Integer goodsId) {
        return dealMapper.checkExists(userId, goodsId);
    }

    public Integer addDeal(Integer sellerId,Integer buyerId,Integer goodsId) {
        Deal deal = Deal.builder()
                .seller(Users.builder().userId(sellerId).build())
                .buyer(Users.builder().userId(buyerId).build())
                .goodsAbbreviation(GoodsAbbreviation.builder().goodsId(goodsId).build()).build();
        dealMapper.addDeal(deal);
        return deal.getDealId();
    }

    public Deal getDealById(int dealId) {
        return dealMapper.getDealById(dealId);
    }

    public Integer getDealStageBySellerIdAndDealId(Integer sellerId, Integer dealId) {
        return dealMapper.getDealStageBySellerIdAndDealId(sellerId, dealId);
    }

    public Integer getDealStageByBuyerIdAndDealId(Integer buyerId, Integer dealId) {
        return dealMapper.getDealStageByBuyerIdAndDealId(buyerId, dealId);
    }

    public Integer getBuyerIdByDealId(Integer dealId) {
        return dealMapper.getBuyerIdByDealId(dealId);
    }

    public Integer getSellerIdByDealId(Integer dealId) {
        return dealMapper.getSellerIdByDealId(dealId);
    }

    public Integer getDealStageByDealId(Integer dealId) {
        return dealMapper.getDealStageByDealId(dealId);
    }

    public Integer changeDealStage(Integer dealId, Stage stage) {
        return dealMapper.changeDealStage(dealId, stage.ordinal());
    }

    public Float getGoodsPrice(Integer dealId) {
        return dealMapper.getGoodsPrice(dealId);
    }

    public Integer addMailingNumber(Integer dealId,String mailingNumber){
        return dealMapper.addMailingNumber(dealId,mailingNumber);
    }

    public Integer notNeedMailingNumber(Integer dealId){
        return dealMapper.notNeedMailingNumber(dealId);
    }

    public Integer addUserComment(Integer dealId,
                           Integer commentUserId,
                                  Integer targetUserId,
                           Date date,
                           String content){
        return dealMapper.addUserComment(dealId, commentUserId, targetUserId, date, content);
    }

    public Boolean checkUserHadComment(Integer dealId,Integer userId){
        return dealMapper.checkUserHadComment(dealId,userId);
    }

    public Boolean hasDeliver(Integer dealId){
        return dealMapper.hasDeliver(dealId);
    }

    public Integer setAddress(Integer dealId,Integer addressId){
        return dealMapper.setAddress(dealId, addressId);
    }

    public Integer goodComment(Integer userId){
        return usersMapper.changeUserCredit(userId, Constants.GOOD_COMMENT_ADD);
    }

    public Integer badComment(Integer userId){
        return usersMapper.changeUserCredit(userId,Constants.BAD_COMMENT_ADD);
    }


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
                                           ){

        Integer stageIndex = this.getDealStageByDealId(dealId);
        if(stageIndex==null){
            return Map.of("code",-1);
        }
        Stage stage = Stage.values()[stageIndex];
        //检查是否为前置阶段
        switch (wantStage){
            case NOT_BUY:return Map.of("code",-2);
            case BUY_NOT_PAY:if(stage!=Stage.NOT_BUY)return Map.of("code",-2);break;//NOT_BUY  buyer
            case BUY_PAY:if(stage!=Stage.BUY_NOT_PAY&&stage!=Stage.REFUNDING)return Map.of("code",-2);break;//BUY_NOT_PAY/REFUNDING  buyer
            case DELIVERING:if(stage!=Stage.REFUNDING&&stage!=Stage.BUY_PAY)return Map.of("code",-2);break;//REFUNDING/BUY_PAY  buyer/seller
            case COMMENT:if(stage!=Stage.DELIVERING)return Map.of("code",-2);break;//DELIVERING  buyer
            case DEAL_SUCCESS:if(stage!=Stage.COMMENT)return Map.of("code",-2);break;//COMMENT  seller and buyer
            case DEAL_CLOSE:if(stage!=Stage.REFUNDING)return Map.of("code",-2);break;//REFUNDING  seller
            case REFUNDING:if(stage!=Stage.BUY_PAY&&stage!=Stage.DELIVERING)return Map.of("code",-2);break;//BUY_PAY/DELIVERING  buyer
            case APPEALING:if(stage!=Stage.REFUNDING)return Map.of("code",-2);break;//REFUNDING  seller
            case APPEALED:if(stage!=Stage.APPEALING)return Map.of("code",-2);break;

        }
        Integer userId = usersMapper.queryUserByEmail(email);
        Integer otherId = 0;

        boolean isBuyer = false;
        boolean isAll = false;

        switch (wantStage){
            case BUY_NOT_PAY:isBuyer = true;break;//NOT_BUY  buyer
            case BUY_PAY:isBuyer = true;break;//BUY_NOT_PAY/REFUNDING  buyer
            case DELIVERING:
                if(stage==Stage.REFUNDING){
                    isBuyer = true;
                }
                break;//REFUNDING/BUY_PAY  buyer/seller
            case COMMENT:isBuyer = true;break;//DELIVERING  buyer
            case DEAL_SUCCESS:isAll = true;break;//COMMENT  seller and buyer
            case REFUNDING:isBuyer = true;break;//BUY_PAY/DELIVERING  buyer
            case APPEALED:isBuyer = true;break;
        }
        //检查是否是可以操控流程的用户
        if(!isAll){
            otherId = isBuyer? this.getSellerIdByDealId(dealId):this.getBuyerIdByDealId(dealId);
            if(userId.equals(otherId)){
                //不是对应的买卖家
                return Map.of("code",-3);
            }
        }else{
            Integer buyerId =  this.getBuyerIdByDealId(dealId);
            Integer sellerId = this.getSellerIdByDealId(dealId);
            if(!sellerId.equals(userId)&&!buyerId.equals(userId)){
                return Map.of("code",-3);
            }
            if(buyerId.equals(userId)){
                otherId = sellerId;
                isBuyer = true;
            }else{
                otherId = buyerId;
            }
        }

        //运行自定义方法,如果返回null，同样返回null
        Integer code = method.run(userId, otherId,dealId, stage, wantStage, isBuyer);
        if(code==null){
            return Map.of("code",-4);
        }
        if(code!=0){
            return Map.of("code",code);
        }
        //更改阶段
        if(needAutoChange){
            this.changeDealStage(dealId,wantStage);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("dealId",dealId);
        map.put("stage",stage);
        map.put("wantStage",wantStage);
        map.put("isBuyer",isBuyer);
        map.put("code",0);
        return map;
    }




}
