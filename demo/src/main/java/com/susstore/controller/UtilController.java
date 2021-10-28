package com.susstore.controller;

import com.susstore.result.CommonResult;
import com.susstore.result.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(value = "工具类",tags = {"工具接口"})
public class UtilController {

    @ApiOperation("只是用来返回给vue确认有login这个接口")
    @RequestMapping(path = "/login",method = RequestMethod.OPTIONS)
    public CommonResult login(){
        return new CommonResult(ResultCode.SUCCESS);
    }

    @ApiOperation("获得轮播图地址以及链接")
    @GetMapping("/carousel")
    public CommonResult carousel(){
        return new CommonResult(ResultCode.SUCCESS, List.of(
                new Carousel("https://www.sustech.edu.cn/uploads/images/2020/09/29150224_67522.jpg",
                        "https://www.sustech.edu.cn/zh/faculty/"),
                new Carousel("https://www.sustech.edu.cn/uploads/full/2020/08/25165059_71675.jpg",
                        "https://www.sustech.edu.cn/zh/about.html")));
    }

    static class Carousel{
        public String picturePath;
        public String link;

        public Carousel(String picturePath, String link) {
            this.picturePath = picturePath;
            this.link = link;
        }
    }

}
