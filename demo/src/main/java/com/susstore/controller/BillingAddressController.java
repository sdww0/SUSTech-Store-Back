package com.susstore.controller;

import com.susstore.pojo.Address;
import com.susstore.service.BillingAddressService;
import com.susstore.service.UserService;
import com.susstore.util.SecretUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class BillingAddressController {

    @Autowired
    private BillingAddressService billingAddressService;

    @Autowired
    private UserService userService;
    /**
     * 增加收货地址
     * 首先对参数进行正确性判断（长度不为0）
     * 后判断地址信息是否已经被添加（姓名 电话 地址信息全部一致）
     * 添加成功后乱跳
     * @param model model
     * @param request 请求
     * @param response 响应
     * @return 跳转页面
     */
    @PostMapping("/addAddress")
    public String addAddress(Model model, HttpServletRequest request, HttpServletResponse response){
        String recipientName =  request.getParameter("recipientName");
        String phoneString = request.getParameter("phone");
        String addressName = request.getParameter("addressName");

        //获取当前的user
        int userId;
        String userEmail="";
        Cookie[] cookies=request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("email")) {
                userEmail = SecretUtils.decode(cookie.getValue());
                break;
            }
        }
        //防止伪造cookie
        if (userService.queryUserByEmail(userEmail)==null){
            return "fail.html";
        }
        else userId=userService.queryUserByEmail(userEmail);

        if(recipientName==null||addressName==null||phoneString==null
                ||recipientName.length()==0||phoneString.length()==0||addressName.length()==0){
            return "fail.html";
        }
        long phone=Long.parseLong(phoneString);
        if(billingAddressService.ifExist(recipientName,phone,addressName)!=null){
            return "fail.html";
        }
        Address address=new Address();
        address.setRecipientName(recipientName);
        address.setPhone(phone);
        address.setBelongToUserId(userId);
        address.setAddressName(addressName);
        address.setAddressID(billingAddressService.addAddress(address));
        if(address.getAddressId()==-1){
            return "fail.html";
        }
//        response.addCookie(new Cookie("email", SecretUtils.encode(email)));
//        response.addCookie(new Cookie("password",SecretUtils.encode(password)));
        return "registerSuccess.html";
    }



}
