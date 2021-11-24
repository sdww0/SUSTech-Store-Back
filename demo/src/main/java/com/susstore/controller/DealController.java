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
import com.susstore.util.ImageUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

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
    @ApiOperation("获取订单申诉图片")
    @GetMapping("/appealing/{dealId}/{file}")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功")
    })
    public void getGoodsComplainImage(HttpServletResponse response,
                                      @ApiParam("投诉人id") @PathVariable("dealId") Integer dealId,
                                      @ApiParam("图片名称") @PathVariable("file")String  file) throws IOException {
        response.setContentType("image/jpeg;charset=utf-8");
        response.setHeader("Content-Disposition", "inline; filename=picture.png");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(Files.readAllBytes(Path.of(Constants.USER_COMPLAIN_PATH+dealId+"/"+file)));
        outputStream.flush();
        outputStream.close();
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{dealId}")
    @ApiOperation("获得订单信息")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4070,message = "订单不存在"),
            @ApiResponse(code = 4003,message = "权限不足不允许访问")
    })
    public CommonResult getDeal(
            @ApiParam("SpringSecurity用户认证信息") Principal principal,
            @ApiParam("订单id") @PathVariable("dealId") Integer dealId
    ){
        //查看订单是否存在
        Deal deal = null;
        if ((deal=dealService.getDealById(dealId))==null){
            return new CommonResult(ResultCode.DEAL_NOT_EXISTS);
        }

        //获取当前user 看看是不是订单id user的一个
        //不满足返回无权限
        Integer currentUserId = userService.queryUserIdByEmail(principal.getName());
        if (!currentUserId.equals(deal.getBuyer().getUserId())
                && !currentUserId.equals(deal.getSeller().getUserId())){
            return new CommonResult(ResultCode.ACCESS_DENIED);
        }
        return new CommonResult(ResultCode.SUCCESS,deal);

    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(path="/addDeal",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    @ApiOperation("生成订单信息")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4051,message = "商品下架"),
            @ApiResponse(code = 4003,message = "权限不足"),
            @ApiResponse(code = 4014,message = "用户信誉分过低"),
            @ApiResponse(code = 4071,message = "添加订单失败")
    })
    public CommonResult addDeal(
            @ApiParam("SpringSecurity用户认证信息") Principal principal,
            @ApiParam("商品id") @RequestParam("goodsId") Integer goodsId,
            @ApiParam("地址id") @RequestParam("addressId") Integer addressId
    ){

        if(userService.getUserCredit(principal.getName())<Constants.NOT_BUY_OR_SELL_GOODS_CREDIT){
            return new CommonResult(ResultCode.CREDIT_LOW);
        }
        Integer userId = userService.queryUserIdByEmail(principal.getName());
        //查看商品是不是已经下架
        if (goodsService.ifOnShelfById(goodsId)== GoodsState.OFF_SHELL.ordinal()){
            return new CommonResult(ResultCode.GOODS_OFF_SHELL);
        }
        if(!userService.checkUserHasInputAddress(userId,addressId)){
            return new CommonResult(ResultCode.ACCESS_DENIED);
        }
        //查看相同订单是否已存在
        // check buyer&seller&goodsId&stage
        Goods goods = goodsService.getGoodsById(goodsId);
        Integer sellerId = (!goods.getIsSell()) ? userId : goods.getAnnouncer().getUserId();
        Integer buyerId = goods.getIsSell() ? userId : goods.getAnnouncer().getUserId();
        Integer id = dealService.addDeal(sellerId,buyerId,goodsId, goodsService.getGoodsTotalPrice(goodsId),addressId);
        if(id==null||id<0){
            return new CommonResult(ResultCode.DEAL_ADD_FAIL);
        }
        return new CommonResult(ResultCode.SUCCESS,id);

    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping(path="/pay/{dealId}")
    @ApiOperation("付款")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4070,message = "订单不存在"),
            @ApiResponse(code = 4072,message = "订单不能跨越阶段"),
            @ApiResponse(code = 4003,message = "权限不足不允许访问"),
            @ApiResponse(code = 4073,message = "没有足够的钱")
    })
    public CommonResult pay(
            @ApiParam("SpringSecurity用户认证信息") Principal principal,
            @ApiParam("订单id") @PathVariable("dealId") Integer dealId,
            HttpServletRequest request
    ){
        StageControlMethod method = (userId,otherId,  dealId1, currentStage, wantStage, isBuyer) -> {
            Float money = userService.getUserMoney(userId);
            Float needMoney = dealService.getDealPrice(dealId1);
            if(money<needMoney){
                //钱不够
                return 1;
            }
            userService.changeUserMoney(userId,-needMoney,request,dealId);
            dealService.changeDealStage(dealId1,Stage.BUY_PAY);
            mailService.sendSimpleMail(userService.getUserEmail(otherId),"你的商品有人付款","你的商品有人付款，去看看吧");
            return 0;
        };
        Map<String,Object> map = dealService.stageControl(principal.getName(),dealId,Stage.BUY_PAY,true,method);
        Integer code = (Integer)map.get("code");
        ResultCode resultCode = preCheck(code);
        if(resultCode!=null){
            return new CommonResult(resultCode);
        }else{
            return new CommonResult(ResultCode.NOT_ENOUGH_MONEY);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping(path="/deliver/{dealId}")
    @ApiOperation("填写邮递单号")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4070,message = "订单不存在"),
            @ApiResponse(code = 4072,message = "订单不能跨越阶段"),
            @ApiResponse(code = 4003,message = "权限不足不允许访问")
    })
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
            mailService.sendSimpleMail(userService.getUserEmail(otherId),"你的订单发货了","你的订单发货了,去看看吧");
            return 0;
        };
        Map<String,Object> map = dealService.stageControl(principal.getName(),dealId,Stage.DELIVERING,true,method);
        Integer code = (Integer)map.get("code");
        ResultCode resultCode = preCheck(code);
        if(resultCode!=null){
            return new CommonResult(resultCode);
        }else{
            return new CommonResult(ResultCode.ACCESS_DENIED);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping(path="/confirm/{dealId}")
    @ApiOperation("确认收货")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4070,message = "订单不存在"),
            @ApiResponse(code = 4072,message = "订单不能跨越阶段"),
            @ApiResponse(code = 4003,message = "权限不足不允许访问")
    })
    public CommonResult confirm(
            @ApiParam("SpringSecurity用户认证信息") Principal principal,
            @ApiParam("订单id") @PathVariable("dealId") Integer dealId
    ){
        StageControlMethod method = (userId, otherId, dealId1, currentStage, wantStage, isBuyer) -> {
            //确认收货，卖家加钱
            userService.changeUserMoney(otherId, dealService.getDealPrice(dealId1),null,null);
            mailService.sendSimpleMail(userService.getUserEmail(otherId),"买家已经收货","买家已经收货,钱已经打到账上");
            return 0;
        };
        Map<String,Object> map = dealService.stageControl(principal.getName(), dealId, Stage.COMMENT,true,method);
        Integer code = (Integer)map.get("code");
        ResultCode resultCode = preCheck(code);
        if(resultCode!=null){
            return new CommonResult(resultCode);
        }else{
            return new CommonResult(ResultCode.ACCESS_DENIED);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(path="/comment",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    @ApiOperation("评价")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4070,message = "订单不存在"),
            @ApiResponse(code = 4072,message = "订单不能跨越阶段"),
            @ApiResponse(code = 4003,message = "权限不足不允许访问"),
            @ApiResponse(code = 4074,message = "已经评价")
    })
    public CommonResult comment(
            @ApiParam("SpringSecurity用户认证信息") Principal principal,
            @ApiParam("评价信息") @RequestBody ControllerReceiveClass.DealComment dealComment
            ){
        StageControlMethod method = (userId, otherId, dealId1, currentStage, wantStage, isBuyer) -> {
            //查看对方是否被评价
            if(dealService.checkUserHadComment(dealId1,otherId)){
                return 1;
            }
            if(dealComment.isGood){
                dealService.goodComment(otherId);
            }else{
                dealService.badComment(otherId);
            }
            dealService.addUserComment(dealId1,userId,otherId,new Date(System.currentTimeMillis()),dealComment.content,dealComment.isGood);
            mailService.sendSimpleMail(userService.getUserEmail(otherId),"有人评价了你","有人评价了你");
            if(dealService.checkUserHadComment(dealId1,userId)){
                //如果双方都评价了则跳到下一阶段
                dealService.changeDealStage(dealId1,wantStage);
            }
            return 0;
        };
        Map<String,Object> map = dealService.stageControl(principal.getName(), dealComment.dealId, Stage.DEAL_SUCCESS,false, method);
        Integer code = (Integer)map.get("code");
        ResultCode resultCode = preCheck(code);
        if(resultCode!=null){
            return new CommonResult(resultCode);
        }else{
            return new CommonResult(ResultCode.ALREADY_COMMENT);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping(path="/refund/{dealId}")
    @ApiOperation("希望退货")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4070,message = "订单不存在"),
            @ApiResponse(code = 4072,message = "订单不能跨越阶段"),
            @ApiResponse(code = 4003,message = "权限不足不允许访问")
    })
    public CommonResult refund(
            @ApiParam("SpringSecurity用户认证信息") Principal principal,
            @ApiParam("订单id") @PathVariable("dealId") Integer dealId
    ){
        StageControlMethod method = (userId, otherId, dealId1, currentStage, wantStage, isBuyer) -> 0;
        Map<String,Object> map = dealService.stageControl(principal.getName(), dealId, Stage.REFUNDING,true,method);
        Integer code = (Integer)map.get("code");
        ResultCode resultCode = preCheck(code);
        if(resultCode!=null){
            return new CommonResult(resultCode);
        }else{
            return new CommonResult(ResultCode.ACCESS_DENIED);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping(path="/cancelRefund/{dealId}")
    @ApiOperation("取消退货")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4070,message = "订单不存在"),
            @ApiResponse(code = 4072,message = "订单不能跨越阶段"),
            @ApiResponse(code = 4003,message = "权限不足不允许访问")
    })
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
        ResultCode resultCode = preCheck(code);
        if(resultCode!=null){
            return new CommonResult(resultCode);
        }else{
            return new CommonResult(ResultCode.ACCESS_DENIED);
        }
    }


    @PreAuthorize("hasRole('USER')")
    @PutMapping(path="/agreeRefund/{dealId}")
    @ApiOperation("卖家是否同意退货")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4070,message = "订单不存在"),
            @ApiResponse(code = 4072,message = "订单不能跨越阶段"),
            @ApiResponse(code = 4003,message = "权限不足不允许访问")
    })
    public CommonResult agreeRefund(
            @ApiParam("SpringSecurity用户认证信息") Principal principal,
            @ApiParam("订单id") @PathVariable("dealId") Integer dealId,
            @ApiParam("是否同意") @RequestParam("agree") Boolean agree
    ){
        StageControlMethod method = (userId, otherId, dealId1, currentStage, wantStage, isBuyer) -> {
            if(agree){
                //同意，买家加钱
                Float price = dealService.getDealPrice(dealId1);
                userService.changeUserMoney(otherId,price,null,null);
                dealService.changeDealStage(dealId1,Stage.DEAL_CLOSE);
            }else{
                dealService.changeDealStage(dealId1,Stage.APPEALING);
            }
            return 0;
        };
        Map<String,Object> map = dealService.stageControl(principal.getName(), dealId, Stage.DEAL_CLOSE,false,method);
        Integer code = (Integer)map.get("code");
        ResultCode resultCode = preCheck(code);
        if(resultCode!=null){
            return new CommonResult(resultCode);
        }else{
            return new CommonResult(ResultCode.ACCESS_DENIED);
        }
    }





    @PreAuthorize("hasRole('USER')")
    @GetMapping(path="/appealing/{dealId}")
    @ApiOperation("买家填写申诉信息")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4070,message = "订单不存在"),
            @ApiResponse(code = 4072,message = "订单不能跨越阶段"),
            @ApiResponse(code = 4003,message = "权限不足不允许访问"),
            @ApiResponse(code = 4001,message = "参数有误")
    })
    public CommonResult appealing(
            @ApiParam("SpringSecurity用户认证信息") Principal principal,
            @ApiParam("订单id") @PathVariable("dealId") Integer dealId,
            @ApiParam("申诉信息") @RequestParam("content") String content,
            @ApiParam("申诉图片（仅限一张）可有可无") @RequestParam(value = "picture",required = false)
            MultipartFile picture
    ){
        StageControlMethod method = (userId, otherId, dealId1, currentStage, wantStage, isBuyer) -> {
            String contentPath = null;
            if(picture!=null) {
                String uuid = UUID.randomUUID().toString();
                String path = Constants.DEAL_APPEALING_PATH + dealId1 +"/"+ uuid + ".png";
                ImageUtil.storeImage(picture, path);
                contentPath = Constants.BACK_END_LINK+"deal/appealing/"+dealId1 +"/"+ uuid + ".png";
            }
            mailService.sendSimpleMail(Constants.WEBSITE_COMMUNICATE_EMAIL,"订单申诉，单号:"+dealId1,
                    "详细描述:"+content);
            dealService.addAppealingContent(dealId1,content,contentPath);
            return 0;
        };
        Map<String,Object> map = dealService.stageControl(principal.getName(), dealId, Stage.APPEALED,true,method);
        Integer code = (Integer)map.get("code");
        ResultCode resultCode = preCheck(code);
        if(resultCode!=null){
            return new CommonResult(resultCode);
        }else{
            return new CommonResult(ResultCode.PARAM_NOT_VALID);
        }

    }

    private ResultCode preCheck(Integer code){
        switch (code){
            case 0:return ResultCode.SUCCESS;
            case -1:return ResultCode.DEAL_NOT_EXISTS;
            case -2:return ResultCode.STAGE_WRONG;
            case -3:return ResultCode.ACCESS_DENIED;
            default:return null;
        }

    }

}
