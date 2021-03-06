package com.susstore.controller;

import com.susstore.result.CommonResult;
import com.susstore.result.ResultCode;
import com.susstore.service.AdminService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@Api(value = "工具类",tags = {"工具接口"})
public class UtilController {

    @Autowired
    @Qualifier("AdminServiceImpl")
    private AdminService adminService;

    @ApiOperation("只是用来返回给vue确认有login这个接口")
    @RequestMapping(path = "/login",method = RequestMethod.OPTIONS)
    public CommonResult login(){
        return new CommonResult(ResultCode.SUCCESS);
    }

    @ApiOperation("获得当月日历")
    @GetMapping("/calender")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功")
    })
    public CommonResult getCalendar(){
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = DateFormat.getDateInstance();
        int year = calendar.get(Calendar.YEAR);
        calendar.set(year,0,0);
        Date minDate = calendar.getTime();
        calendar.set(year,11,31,23,59,59);
        Date maxDate = calendar.getTime();

        return new CommonResult(ResultCode.SUCCESS,adminService.getEventWithTimeConstrain(minDate,maxDate));
    }


    @ApiOperation("获得轮播图地址以及链接")
    @GetMapping("/carousel")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功")
    })
    public CommonResult carousel(){
        return new CommonResult(ResultCode.SUCCESS, List.of(
                new Carousel("https://newshub.sustech.edu.cn/uploads/full/2021/11/201637375068653950.jpeg",
                        "https://newshub.sustech.edu.cn/html/202111/41450.html"),
                new Carousel("https://www.sustech.edu.cn/uploads/images/2021/11/18131622_64529.jpg",
                        "https://newshub.sustech.edu.cn/html/202111/41436.html"
                        ),
                new Carousel("https://newshub.sustech.edu.cn/uploads/full/2021/08/011627802843506432.jpg",
                        "https://newshub.sustech.edu.cn/html/202108/41098.html")));
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
