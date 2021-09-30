package com.susstore.controller;

import com.susstore.config.Constants;
import com.susstore.pojo.Users;
//import com.susstore.service.MailServiceImpl;
import com.susstore.service.UserService;
import com.susstore.util.CommonUtil;
import com.susstore.util.SecretUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {
//
    public static final int USER_PICTURE_SIZE = 100;
//    //ResourceUtils.getURL("classpath:").getPath()

    @Autowired
    private UserService userService;

    @GetMapping("/{queryUserId}")
    public String getUserInformation(Authentication authentication,
                                     @PathVariable("queryUserId")Integer id, Model model){
        Users user = userService.queryUserById(id);

        if(user==null){
            return "redirect:/404.html";
        }
        Integer loginId = -1;
        if(authentication!=null){
            loginId = userService.queryUserByEmail(authentication.getName());
        }
        model.addAttribute("userDetail",user);
        if(loginId==user.getUserId()){
            return "account-detail.html";
        }
        return "account-information.html";
    }
    @GetMapping("/edit-profile")
    public String index1(){
        return "edit-profile.html";
    }
    @GetMapping("/edit-address")
    public String index2(){
        return "edit-address.html";
    }

    /**
     * 更新用户数
     * @param email 邮箱
     * @param password 密码
     * @param photo 图片
     * @param request 请求
     * @return
     * @throws IOException
     */
    @PostMapping("/update")
    public String updateUser(Authentication authentication,
                             @RequestParam("photo")MultipartFile photo, HttpServletRequest request) throws IOException {
        String email = authentication.getName();

        Integer id;
        if ((id = userService.queryUserByEmail(email)) == null) {
            return "login.html";
        }
        String newName = request.getParameter("newName");
        String newEmail = request.getParameter("newEmail");
        String newPhone = request.getParameter("newPhone");
        newName = newName.length() == 0 ? null : newName;
        newEmail = newEmail.length() == 0 ? null : newEmail;
        newPhone = newPhone.length() == 0 ? null : newPhone;

        if(userService.updateUserWithPhoto(photo,newName,newEmail,newPhone,id)){
            return "redirect:/account";
        }
        return "index.html";
    }

//    private Integer checkIsValidUserNeedDecode(String email,String password){
//        if(email.length()==0||password.length()==0){
//            return null;
//        }
//        email = SecretUtils.decode(email);
//        password = SecretUtils.decode(password);
//        return userService.login(email,password);
//    }
//
//    private Integer checkIsValidUser(String email,String password){
//        if(email.length()==0||password.length()==0){
//            return null;
//        }
//        return userService.login(email,password);
//    }





}
