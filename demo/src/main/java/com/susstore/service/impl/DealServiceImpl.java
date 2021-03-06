package com.susstore.service.impl;

import com.susstore.config.Constants;
import com.susstore.mapper.DealMapper;
import com.susstore.mapper.UsersMapper;
import com.susstore.method.StageControlMethod;
import com.susstore.pojo.*;
import com.susstore.pojo.process.AppealingDeal;
import com.susstore.service.AdminService;
import com.susstore.service.DealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("DealServiceImpl")
public class DealServiceImpl implements DealService {


    @Autowired
    private DealMapper dealMapper;

    @Autowired
    private UsersMapper usersMapper;

    public Integer addDeal(Integer sellerId,Integer buyerId,Integer goodsId,Float price,Integer addressId) {
        Deal deal = Deal.builder()
                .seller(Users.builder().userId(sellerId).build())
                .buyer(Users.builder().userId(buyerId).build())
                .orderTime(new Date())
                .goodsAbbreviation(GoodsAbbreviation.builder().goodsId(goodsId).build())
                .shippingAddress(Address.builder().addressId(addressId).build())
                .price(price)
                .build();
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
                           String content,
                                  Boolean isGood){
        return dealMapper.addUserComment(dealId, commentUserId, targetUserId, date, content,isGood);
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
     * @param email ??????
     * @param dealId ??????id
     * @param wantStage ???????????????
     * @param method ????????????
     * @return ??????map???????????????????????????????????????????????????????????????null
     * ????????????key???code
     * ??????:method?????????????????????
     *   0:????????????
     *  -1:???????????????
     *  -2:???????????????????????????????????????/??????????????????
     *  -3:???????????????????????????????????????
     *  -4:???????????????????????????
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
        //???????????????????????????
        switch (wantStage){
            case BUY_NOT_PAY:return Map.of("code",-2);//NOT_BUY  buyer
            case BUY_PAY:if(stage!=Stage.BUY_NOT_PAY&&stage!=Stage.REFUNDING)return Map.of("code",-2);break;//BUY_NOT_PAY/REFUNDING  buyer
            case DELIVERING:if(stage!=Stage.REFUNDING&&stage!=Stage.BUY_PAY)return Map.of("code",-2);break;//REFUNDING/BUY_PAY  buyer/seller
            case COMMENT:if(stage!=Stage.DELIVERING)return Map.of("code",-2);break;//DELIVERING  buyer
            case DEAL_SUCCESS:if(stage!=Stage.COMMENT)return Map.of("code",-2);break;//COMMENT  seller and buyer
            case DEAL_CLOSE:if(stage!=Stage.REFUNDING)return Map.of("code",-2);break;//REFUNDING  seller
            case REFUNDING:if(stage!=Stage.BUY_PAY&&stage!=Stage.DELIVERING)return Map.of("code",-2);break;//BUY_PAY/DELIVERING  buyer
            case APPEALING:if(stage!=Stage.REFUNDING)return Map.of("code",-2);break;//REFUNDING  seller
            case APPEALED:if(stage!=Stage.APPEALING)return Map.of("code",-2);break;

        }
        Integer userId = usersMapper.queryUserIdByEmail(email);
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
        //??????????????????????????????????????????
        if(!isAll){
            otherId = isBuyer? this.getSellerIdByDealId(dealId):this.getBuyerIdByDealId(dealId);
            if(userId.equals(otherId)){
                //????????????????????????
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

        //?????????????????????,????????????null???????????????null
        Integer code = method.run(userId, otherId,dealId, stage, wantStage, isBuyer);
        if(code==null){
            return Map.of("code",-4);
        }
        if(code!=0){
            return Map.of("code",code);
        }
        //????????????
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

    public List<Deal> getDealBySellerAndStage(Integer userId, Integer stage){
        return dealMapper.getDealBySellerAndStage(userId,stage);
    }

    public List<Deal> getDealByBuyerAndStage(Integer userId,Integer stage){
        return dealMapper.getDealByBuyerAndStage(userId,stage);
    }

    public Integer addAppealingContent(Integer dealId,String content,String picturePath){
        AppealingDeal appealingDeal = AppealingDeal.builder()
                .dealId(dealId)
                .content(content)
                .picturePath(picturePath)
                .build();

        dealMapper.addAppealingContent(appealingDeal);
        return appealingDeal.getRecordId();

    }

    public Float getDealPrice(Integer dealId){
        return dealMapper.getDealPrice(dealId);
    }

    public List<Deal> getDealByBuyer(Integer userId){
        return dealMapper.getDealByBuyer(userId);
    }

    public List<Deal> getDealBySeller(Integer userId){
        return dealMapper.getDealBySeller(userId);
    }



}
