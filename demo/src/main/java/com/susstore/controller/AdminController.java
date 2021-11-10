package com.susstore.controller;


import com.susstore.pojo.Stage;
import com.susstore.pojo.process.AppealingDeal;
import com.susstore.pojo.process.ComplainGoods;
import com.susstore.pojo.process.ComplainUsers;
import com.susstore.result.CommonResult;
import com.susstore.result.ResultCode;
import com.susstore.service.AdminService;
import com.susstore.service.DealService;
import com.susstore.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value = "管理员",tags = {"管理员访问接口"})
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @Autowired
    private DealService dealService;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/complain/user")
    @ApiOperation("获得所有投诉用户信息")
    public CommonResult getComplainUser(){
        return new CommonResult(ResultCode.SUCCESS,adminService.getAllComplainUser());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/complain/goods")
    @ApiOperation("获得所有投诉商品信息")
    public CommonResult getComplainGoods(){
        return new CommonResult(ResultCode.SUCCESS,adminService.getAllComplainGoods());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/appealing/deal")
    @ApiOperation("获得所有投诉订单信息")
    public CommonResult getComplainDeal(){
        return new CommonResult(ResultCode.SUCCESS,adminService.getAllAppealingDeal());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/operate/goods")
    @ApiOperation("操作商品，下架还是驳回")
    public CommonResult operateGoods(
            @ApiParam("id") @RequestParam("recordId") Integer recordId,
            @ApiParam("是否下架") @RequestParam("offShell")Boolean offShell
    ){
        ComplainGoods complainGoods = adminService.getComplainGoods(recordId);
        if(offShell){
            adminService.banGoods(complainGoods.getGoodsId());
        }
        adminService.processComplainGoods(recordId);
        return new CommonResult(ResultCode.SUCCESS);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/operate/user")
    @ApiOperation("操作用户，封禁还是驳回")
    public CommonResult operateUsers(
            @ApiParam("id") @RequestParam("recordId") Integer recordId,
            @ApiParam("是否封禁") @RequestParam("needBanned")Boolean needBanned
    ){
        ComplainUsers complainUsers = adminService.getComplainUser(recordId);
        if(needBanned){
            adminService.banUser(complainUsers.getUsersId());
        }
        adminService.processComplainUsers(recordId);
        return new CommonResult(ResultCode.SUCCESS);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/operate/deal")
    @ApiOperation("操作订单，让退款还是不让")
    public CommonResult operateDeal(
            @ApiParam("id") @RequestParam("recordId") Integer recordId,
            @ApiParam("是否退款") @RequestParam("canRefund")Boolean canRefund
    ){
        AppealingDeal appealingDeal = adminService.getAppealingDeal(recordId);
        if(canRefund){
            Integer sellerId = dealService.getSellerIdByDealId(appealingDeal.getDealId());
            userService.changeUserMoney(sellerId,dealService.getDealPrice(appealingDeal.getDealId()));
        }
        adminService.updateDealState(Stage.DEAL_CLOSE.ordinal(),appealingDeal.getDealId());
        adminService.processAppealingDeal(recordId);
        return new CommonResult(ResultCode.SUCCESS);
    }

}