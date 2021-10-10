package com.susstore.controller;

import com.susstore.pojo.Deal;
import com.susstore.result.CommonResult;
import com.susstore.result.ResultCode;
import com.susstore.service.DealService;
import com.susstore.service.GoodsService;
import com.susstore.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{dealId}")
    @ApiOperation("获得订单信息")
    public CommonResult getDeal(
            @ApiParam("SpringSecurity用户认证信息") Principal principal,
            @ApiParam("订单id") @PathVariable("dealId") Integer dealId
    ){

        //获取当前user 看看是不是订单id user的一个
        //不满足返回无权限
        Deal deal = dealService.getDealById(dealId);
        int currentUserId = userService.queryUserByEmail(principal.getName());
        if (currentUserId!=deal.getBuyer().getUserId()
                &&currentUserId!=deal.getSeller().getUserId()){
            return new CommonResult(ResultCode.FORBIDDEN);
        }
        //查看订单是否存在
        if (dealService.getDealById(dealId)==null){
            return new CommonResult(ResultCode.DEAL_NOT_EXIST);
        }
        return new CommonResult(ResultCode.SUCCESS,deal);

    }


    //@PreAuthorize("hasRole('USER')")
    @GetMapping("/{addDeal}")
    @ApiOperation("生成订单信息")
    public CommonResult AddDeal(
            /**
             * deal_id serial primary key ,
             *     stage int not null ,
             *     goods_id int not null ,
             *     buyer_id int not null ,
             *     seller_id int not null ,
             *     mailing_number varchar,
             *     shipping_address_id int,
             * */
            @ApiParam("SpringSecurity用户认证信息") Principal principal,
            @ApiParam("订单状态") @RequestParam(name = "stage") Integer stage,
            @ApiParam("商品id") @RequestParam("goodsId") Integer goodsId,
            @ApiParam("买家id") @RequestParam("buyId") Integer buyId ,
            @ApiParam("卖家id") @RequestParam("sellerId") Integer sellerId
    ){
        //查看商品是不是已经下架
        if (goodsService.ifOnShelfById(goodsId)==0){
            return new CommonResult(ResultCode.DEAL_OFF_SHELF);
        }
        //查看相同订单是否已存在
        // check buyer&seller&goodsId&stage
        if (dealService.getDeal(sellerId,buyId,goodsId,stage)!=null){
            return new CommonResult(ResultCode.DEAL_ALREADY_EXISIT);
        }
        // 卖家/卖家是否存在且激活
        if(userService.ifExistById(buyId)==null||userService.ifExistById(sellerId)==null){
            return new CommonResult(ResultCode.USER_NOT_EXIST);
        }
        if (!userService.ifActivatedById(buyId) || !userService.ifActivatedById(sellerId)){
            return new CommonResult(ResultCode.USER_NOT_ACTIVATED);
        }

        Deal deal = Deal.builder().seller(userService.queryUserById(sellerId))
                .buyer(userService.queryUserById(buyId)).goods(goodsService.getGoodsById(goodsId)).stage(stage).build();
        Integer id = dealService.addDeal(deal);
        if(id==null||id<0){
            return new CommonResult(ResultCode.DEAL_ADD_FAIL);
        }

        return new CommonResult(ResultCode.SUCCESS,id);

    }



}
