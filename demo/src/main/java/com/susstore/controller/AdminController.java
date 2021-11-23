package com.susstore.controller;


import com.susstore.pojo.Event;
import com.susstore.pojo.Stage;
import com.susstore.pojo.process.AppealingDeal;
import com.susstore.pojo.process.ComplainGoods;
import com.susstore.pojo.process.ComplainUsers;
import com.susstore.result.CommonResult;
import com.susstore.result.ResultCode;
import com.susstore.service.AdminService;
import com.susstore.service.DealService;
import com.susstore.service.GoodsService;
import com.susstore.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;

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

    @Autowired
    private GoodsService goodsService;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/complain/allUser")
    @ApiOperation("获得所有投诉用户信息")
    public CommonResult getComplainUser(){
        return new CommonResult(ResultCode.SUCCESS,adminService.getAllComplainUser());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/complain/allGoods")
    @ApiOperation("获得所有投诉商品信息")
    public CommonResult getComplainGoods(){
        return new CommonResult(ResultCode.SUCCESS,adminService.getAllComplainGoods());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/appealing/allDeal")
    @ApiOperation("获得所有投诉订单信息")
    public CommonResult getComplainDeal(){
        return new CommonResult(ResultCode.SUCCESS,adminService.getAllAppealingDeal());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/complain/user/not")
    @ApiOperation("获得所有未处理投诉用户信息")
    public CommonResult getNotComplainUser(){
        return new CommonResult(ResultCode.SUCCESS,adminService.getAllNotProcessComplainUser());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/complain/goods/not")
    @ApiOperation("获得所有未处理投诉商品信息")
    public CommonResult getNotComplainGoods(){
        return new CommonResult(ResultCode.SUCCESS,adminService.getAllNotProcessComplainGoods());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/appealing/deal/not")
    @ApiOperation("获得所有未处理投诉订单信息")
    public CommonResult getNotComplainDeal(){
        return new CommonResult(ResultCode.SUCCESS,adminService.getAllNotProcessAppealingDeal());
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
            userService.changeUserCredit(goodsService.getAnnouncerId(complainGoods.getGoodsId()),-3);
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

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/calendar")
    @ApiOperation("往日历添加事件（讲座等）")
    public CommonResult addCalendar(
            @ApiParam("事件") @RequestBody Event event
    ){
        adminService.addEvent(event);
        return new CommonResult(ResultCode.SUCCESS,event.getEventId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/calendar")
    @ApiOperation("删除日历某一天的事件")
    @ApiResponses(
            @ApiResponse(code = 4001,message = "请求参数有误")
    )
    public CommonResult deleteCalendar(
            @ApiParam("事件id") @RequestParam("eventId") Integer eventId
    ){
        if(adminService.getEvent(eventId)==null){
            return new CommonResult(ResultCode.PARAM_NOT_VALID);
        }
        adminService.deleteEvent(eventId);
        return new CommonResult(ResultCode.SUCCESS);
    }

}
