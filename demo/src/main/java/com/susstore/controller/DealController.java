package com.susstore.controller;

import com.susstore.pojo.Goods;
import com.susstore.result.CommonResult;
import com.susstore.result.ResultCode;
import com.susstore.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "商品控制器",tags = {"订单访问接口"})
@RequestMapping("/deal")
public class DealController {



    @GetMapping("/{dealId}")
    @ApiOperation("根据商品id获取商品信息")
    public CommonResult getGoods(
            @ApiParam("商品id") @PathVariable("dealId") String dealId
    ){


        return new CommonResult(ResultCode.SUCCESS);
    }



}
