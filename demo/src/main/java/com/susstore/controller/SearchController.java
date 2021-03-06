package com.susstore.controller;

import com.susstore.pojo.GoodsAbbreviation;
import com.susstore.pojo.Users;
import com.susstore.result.CommonResult;
import com.susstore.result.ResultCode;
import com.susstore.service.GoodsService;
import com.susstore.service.UserService;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.susstore.config.Constants.SEARCH_PAGE_SIZE;

@RestController
@Api(value = "搜索",tags = {"搜索访问接口"})
public class SearchController {

    @Autowired
    @Qualifier("GoodsServiceImpl")
    private GoodsService goodsService;
    @Autowired
    @Qualifier("UserServiceImpl")
    private UserService userServiceImpl;

    //@PreAuthorize("hasRole('USER')")
    @GetMapping("/searchGoods/{content}/{currentPage}")
    @ApiOperation("搜索内容")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功")
    })
    public CommonResult searchGoods(
            @ApiParam("搜索内容") @PathVariable("content")String content,
            @ApiParam("当前页") @PathVariable("currentPage")Integer currentPage
    ){
        List<GoodsAbbreviation> goodsAbbreviations = goodsService.searchGoods(content, SEARCH_PAGE_SIZE, currentPage);
        return new CommonResult(ResultCode.SUCCESS,goodsAbbreviations);
    }

    @GetMapping("/searchGoods/{content}")
    @ApiOperation("搜索商品的个数")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功")
    })
    public CommonResult searchGoodsAmount(
            @ApiParam("搜索内容") @PathVariable("content")String content
    ){
        return new CommonResult(ResultCode.SUCCESS,goodsService.searchGoodsAmount(content));
    }

    @GetMapping("/searchUser/{userName}/{currentPage}")
    @ApiOperation("搜索用户")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功")
    })
    public CommonResult searchUser(
            @ApiParam("搜索用户名") @PathVariable("userName")String userName,
            @ApiParam("当前页") @PathVariable("currentPage")Integer currentPage
    ){
        List<Users> users = userServiceImpl.searchUsers(userName, SEARCH_PAGE_SIZE, currentPage);
        System.out.println(users.size());
        return new CommonResult(ResultCode.SUCCESS,users);
    }

    @GetMapping("/searchUser/{userName}")
    @ApiOperation("搜索用户的个数")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功")
    })
    public CommonResult searchUserAmount(
            @ApiParam("搜索用户名") @PathVariable("userName")String userName
    ){
        return new CommonResult(ResultCode.SUCCESS, userServiceImpl.searchUsersAmount(userName));
    }


}
