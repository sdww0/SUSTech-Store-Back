package com.susstore.controller;

import com.susstore.result.CommonResult;
import com.susstore.result.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "工具类",tags = {"工具接口"})
public class UtilController {

    @ApiOperation("只是用来返回给vue确认有login这个接口")
    @RequestMapping(path = "/login",method = RequestMethod.OPTIONS)
    public CommonResult login(){
        return new CommonResult(ResultCode.SUCCESS);
    }


}
