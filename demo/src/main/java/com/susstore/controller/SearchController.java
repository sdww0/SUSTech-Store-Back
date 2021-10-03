package com.susstore.controller;

import com.susstore.pojo.GoodsAbbreviation;
import com.susstore.result.CommonResult;
import com.susstore.result.ResultCode;
import com.susstore.service.GoodsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.susstore.config.Constants.SEARCH_PAGE_SIZE;

@RestController
@Api(value = "搜索",tags = {"搜索访问接口"})
public class SearchController {

    @Autowired
    private GoodsService goodsService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/search/{content}/{currentPage}")
    @ApiOperation("搜索内容，要求先登录")
    public CommonResult search(
            @ApiParam("搜索内容") @PathVariable("content")String content,
            @ApiParam("当前页") @PathVariable("currentPage")Integer currentPage
    ){
        List<GoodsAbbreviation> goodsAbbreviations = goodsService.searchGoods(content, SEARCH_PAGE_SIZE, currentPage);
        return new CommonResult(ResultCode.SUCCESS,goodsAbbreviations);
    }


}