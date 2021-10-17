package com.susstore.controller;

import com.susstore.config.Constants;
import com.susstore.method.StageControlMethod;
import com.susstore.pojo.Deal;
import com.susstore.pojo.Goods;
import com.susstore.pojo.GoodsState;
import com.susstore.pojo.Stage;
import com.susstore.result.CommonResult;
import com.susstore.result.ResultCode;
import com.susstore.service.DealService;
import com.susstore.service.GoodsService;
import com.susstore.service.MailServiceThread;
import com.susstore.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.Map;

@RestController
@Api(value = "商品控制器",tags = {"订单访问接口"})
@RequestMapping("/deal")
public class DealController {
    @Autowired
    private GoodsService goodsService;

    @Autowired
    private DealService dealService;

    @Autowired
    private UserService userService;

    @Autowired
    private MailServiceThread mailService;


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{dealId}")
    @ApiOperation("获得订单信息")
    public CommonResult getDeal(
            @ApiParam("SpringSecurity用户认证信息") Principal principal,
            @ApiParam("订单id") @PathVariable("dealId") Integer dealId
    ){
        //查看订单是否存在
        Deal deal = null;
        if ((deal=dealService.getDealById(dealId))==null){
            return new CommonResult(ResultCode.DEAL_NOT_EXIST);
        }

        //获取当前user 看看是不是订单id user的一个
        //不满足返回无权限
        Integer currentUserId = userService.queryUserByEmail(principal.getName());
        if (currentUserId!= deal.getBuyer().getUserId()
                &&currentUserId!=deal.getSeller().getUserId()){
            return new CommonResult(ResultCode.FORBIDDEN);
        }
        return new CommonResult(ResultCode.SUCCESS,deal);

    }


    @PreAuthorize("hasRole('USER')")
    @RequestMapping(path="/addDeal",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    @ApiOperation("生成订单信息")
    public CommonResult addDeal(
            @ApiParam("SpringSecurity用户认证信息") Principal principal,
            @ApiParam("商品id") @RequestParam("goodsId") Integer goodsId
    ){
        //查看商品是不是已经下架
        Integer userId = userService.queryUserByEmail(principal.getName());
        if (goodsService.ifOnShelfById(goodsId)== GoodsState.OFF_SHELL.ordinal()){
            return new CommonResult(ResultCode.DEAL_OFF_SHELF);
        }
        //查看相同订单是否已存在
        // check buyer&seller&goodsId&stage
        Deal deal = null;
        if ((deal=dealService.checkExists(userId,goodsId))!=null){
            return new CommonResult(ResultCode.DEAL_ALREADY_EXISIT);
        }
        Goods goods = goodsService.getGoodsById(goodsId);
        Integer sellerId = (!goods.getIsSell()) ? userId : goods.getAnnouncer().getUserId();
        Integer buyerId = goods.getIsSell() ? userId : goods.getAnnouncer().getUserId();
        Integer id = dealService.addDeal(sellerId,buyerId,goodsId);
        if(id==null||id<0){
            return new CommonResult(ResultCode.DEAL_ADD_FAIL);
        }
        goodsService.increaseView(goodsId);
        return new CommonResult(ResultCode.SUCCESS,id);

    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(path="/check/{dealId}")
    @ApiOperation("确认拍下")
    public CommonResult check(
            @ApiParam("SpringSecurity用户认证信息") Principal principal,
            @ApiParam("订单id") @PathVariable("dealId") Integer dealId,
            @ApiParam("选择的地址id") @RequestParam("addressId") Integer addressId
    ){
        StageControlMethod method = (userId, otherId, dealId1, currentStage, wantStage, isBuyer) -> {
            if(!userService.checkUserHasInputAddress(userId,addressId)){
                return 1;
            }
            dealService.setAddress(dealId1,addressId);
            return 0;
        };
        Map<String,Object> map = dealService.stageControl(principal.getName(), dealId, Stage.BUY_NOT_PAY,true,method);
        Integer code = (Integer)map.get("code");
        if(code!=0){
            return new CommonResult(ResultCode.FORBIDDEN);
        }
        return new CommonResult(ResultCode.SUCCESS);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(path="/pay/{dealId}")
    @ApiOperation("付款")
    public CommonResult pay(
            @ApiParam("SpringSecurity用户认证信息") Principal principal,
            @ApiParam("订单id") @PathVariable("dealId") Integer dealId
    ){
        StageControlMethod method = (userId,otherId,  dealId1, currentStage, wantStage, isBuyer) -> {
            Float money = userService.getUserMoney(userId);
            Float needMoney = dealService.getGoodsPrice(dealId1);
            if(money<needMoney){
                //钱不够
                return 1;
            }
            userService.changeUserMoney(userId,-needMoney);
            dealService.changeDealStage(dealId1,Stage.BUY_PAY);
            return 0;
        };
        Map<String,Object> map = dealService.stageControl(principal.getName(),dealId,Stage.BUY_PAY,true,method);
        Integer code = (Integer)map.get("code");
        if(code!=0){
            return new CommonResult(ResultCode.FORBIDDEN);
        }
        return new CommonResult(ResultCode.SUCCESS);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(path="/deliver/{dealId}")
    @ApiOperation("填写邮递单号")
    public CommonResult deliver(
            @ApiParam("SpringSecurity用户认证信息") Principal principal,
            @ApiParam("订单id") @PathVariable("dealId") Integer dealId,
            @ApiParam("邮递单号，如果为null则代表不需要邮递单号") @RequestParam(value="mailingNumber",required = false) String mailingNumber
    ){
        StageControlMethod method = (userId, otherId, dealId1, currentStage, wantStage, isBuyer) -> {
            if(mailingNumber==null){
                dealService.notNeedMailingNumber(dealId1);
            }else{
                dealService.addMailingNumber(dealId1,mailingNumber);
            }
            return 0;
        };
        Map<String,Object> map = dealService.stageControl(principal.getName(),dealId,Stage.DELIVERING,true,method);
        Integer code = (Integer)map.get("code");
        if(code!=0){
            return new CommonResult(ResultCode.FORBIDDEN);
        }
        return new CommonResult(ResultCode.SUCCESS);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(path="/confirm/{dealId}")
    @ApiOperation("确认收货")
    public CommonResult confirm(
            @ApiParam("SpringSecurity用户认证信息") Principal principal,
            @ApiParam("订单id") @PathVariable("dealId") Integer dealId
    ){
        StageControlMethod method = (userId, otherId, dealId1, currentStage, wantStage, isBuyer) -> {
            //确认收货，卖家加钱
            userService.changeUserMoney(otherId, dealService.getGoodsPrice(dealId1));
            return 0;
        };
        Map<String,Object> map = dealService.stageControl(principal.getName(), dealId, Stage.COMMENT,true,method);
        Integer code = (Integer)map.get("code");
        if(code!=0){
            return new CommonResult(ResultCode.FORBIDDEN);
        }
        return new CommonResult(ResultCode.SUCCESS);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(path="/comment/{dealId}")
    @ApiOperation("评价")
    public CommonResult comment(
            @ApiParam("SpringSecurity用户认证信息") Principal principal,
            @ApiParam("订单id") @PathVariable("dealId") Integer dealId,
            @ApiParam("评价内容") @RequestParam("content") String commentContent,
            @ApiParam("是否是好评") @RequestParam("isGood") Boolean isGood
    ){
        StageControlMethod method = (userId, otherId, dealId1, currentStage, wantStage, isBuyer) -> {
            //查看对方是否被评价
            if(dealService.checkUserHadComment(dealId1,otherId)){
                return 1;
            }
            if(isGood){
                dealService.goodComment(otherId);
            }else{
                dealService.badComment(otherId);
            }
            dealService.addUserComment(dealId1,userId,otherId,new Date(System.currentTimeMillis()),commentContent);
            if(dealService.checkUserHadComment(dealId1,userId)){
                //如果双方都评价了则跳到下一阶段
                dealService.changeDealStage(dealId1,wantStage);
            }
            return 0;
        };
        Map<String,Object> map = dealService.stageControl(principal.getName(), dealId, Stage.DEAL_SUCCESS,false, method);
        Integer code = (Integer)map.get("code");
        if(code!=0){
            return new CommonResult(ResultCode.FORBIDDEN);
        }
        return new CommonResult(ResultCode.SUCCESS);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(path="/refund/{dealId}")
    @ApiOperation("希望退货")
    public CommonResult refund(
            @ApiParam("SpringSecurity用户认证信息") Principal principal,
            @ApiParam("订单id") @PathVariable("dealId") Integer dealId
    ){
        StageControlMethod method = (userId, otherId, dealId1, currentStage, wantStage, isBuyer) -> 0;
        Map<String,Object> map = dealService.stageControl(principal.getName(), dealId, Stage.REFUNDING,true,method);
        Integer code = (Integer)map.get("code");
        if(code!=0){
            return new CommonResult(ResultCode.FORBIDDEN);
        }
        return new CommonResult(ResultCode.SUCCESS);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(path="/cancelRefund/{dealId}")
    @ApiOperation("取消退货")
    public CommonResult cancelRefund(
            @ApiParam("SpringSecurity用户认证信息") Principal principal,
            @ApiParam("订单id") @PathVariable("dealId") Integer dealId
    ){
        StageControlMethod method = (userId, otherId, dealId1, currentStage, wantStage, isBuyer) -> {
            if(dealService.hasDeliver(dealId1)){
                dealService.changeDealStage(dealId1,Stage.DELIVERING);
            }else{
                dealService.changeDealStage(dealId1,Stage.BUY_PAY);
            }
            return 0;
        };
        Map<String,Object> map = dealService.stageControl(principal.getName(), dealId, Stage.DELIVERING,false,method);
        Integer code = (Integer)map.get("code");
        if(code!=0){
            return new CommonResult(ResultCode.FORBIDDEN);
        }
        return new CommonResult(ResultCode.SUCCESS);
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping(path="/agreeRefund/{dealId}")
    @ApiOperation("卖家是否同意退货")
    public CommonResult agreeRefund(
            @ApiParam("SpringSecurity用户认证信息") Principal principal,
            @ApiParam("订单id") @PathVariable("dealId") Integer dealId,
            @ApiParam("是否同意") @RequestParam("agree") Boolean agree
    ){
        StageControlMethod method = (userId, otherId, dealId1, currentStage, wantStage, isBuyer) -> {
            if(agree){
                //同意，买家加钱
                Float price = dealService.getGoodsPrice(dealId1);
                userService.changeUserMoney(otherId,price);
                dealService.changeDealStage(dealId1,Stage.DEAL_CLOSE);
            }else{
                dealService.changeDealStage(dealId1,Stage.APPEALING);
            }
            return 0;
        };
        Map<String,Object> map = dealService.stageControl(principal.getName(), dealId, Stage.DEAL_CLOSE,false,method);
        Integer code = (Integer)map.get("code");
        if(code!=0){
            return new CommonResult(ResultCode.FORBIDDEN);
        }
        return new CommonResult(ResultCode.SUCCESS);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(path="/appealing/{dealId}")
    @ApiOperation("买家申诉")
    public CommonResult appealing(
            @ApiParam("SpringSecurity用户认证信息") Principal principal,
            @ApiParam("订单id") @PathVariable("dealId") Integer dealId,
            @ApiParam("申诉信息") @RequestParam("content") String content
    ){
        StageControlMethod method = (userId, otherId, dealId1, currentStage, wantStage, isBuyer) -> {
            mailService.sendSimpleMail(Constants.WEBSITE_COMMUNICATE_EMAIL,"订单申诉，单号:"+dealId1,
                    "详细描述:"+content);
            return 0;
        };
        Map<String,Object> map = dealService.stageControl(principal.getName(), dealId, Stage.APPEALED,true,method);
        Integer code = (Integer)map.get("code");
        if(code!=0){
            return new CommonResult(ResultCode.FORBIDDEN);
        }
        return new CommonResult(ResultCode.SUCCESS);
    }

}
