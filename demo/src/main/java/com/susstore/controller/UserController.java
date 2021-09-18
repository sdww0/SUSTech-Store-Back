package com.susstore.controller;

import com.susstore.config.Data;
import com.susstore.pojo.User;
import com.susstore.service.MailServiceImpl;
import com.susstore.service.UserService;
import com.susstore.util.CommonUtil;
import com.susstore.util.ImageUtil;
import com.susstore.util.SecretUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

@Controller
public class UserController {

    public static final int USER_PICTURE_SIZE = 100;
    //ResourceUtils.getURL("classpath:").getPath()

    @Autowired
    private UserService userService;

    @Autowired
    private MailServiceImpl mailService;



    @GetMapping("/user/{queryuserId}")
    public String getUserInformation(@CookieValue(value="id",defaultValue = "")String cookieId,
                                     @PathVariable("queryuserId")Integer id,Model model){
        User user = userService.queryUserById(id);
        cookieId = SecretUtils.decode(cookieId);
        if(user.getEmail()==null){
            return "404.html";
        }
        model.addAttribute("userDetail",user);
        if(cookieId!=null&&cookieId.length()!=0&&
                CommonUtil.isInteger(cookieId)&&user.getUserId()==Integer.parseInt(cookieId)){
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
     * 登录
     * 参数判断
     * 登录判断
     * 添加cookie
     * 返回id
     * @param model model
     * @param request 请求
     * @param response 响应
     * @return 跳转页面
     */
    @PostMapping("/login")
    public String login(Model model,HttpServletRequest request,HttpServletResponse response){
        String email = request.getParameter("email");
        String password =  request.getParameter("password");
        if(email==null||password==null||email.length()==0||password.length()==0){
            return "fail.html";
        }
        Integer id = userService.login(email,password);
        if(id==null){
            return "fail.html";
        }
        response.addCookie(new Cookie("email", SecretUtils.encode(email)));
        response.addCookie(new Cookie("password",SecretUtils.encode(password)));
        response.addCookie(new Cookie("id",SecretUtils.encode(String.valueOf(id))));
        return "loginSuccess.html";
    }

    /**
     * 用户注册
     * 首先对参数进行正确性判断
     * 后判断邮箱是否被注册
     * 创建路径等操作
     * 注册后添加cookie跳转
     * @param model model
     * @param request 请求
     * @param response 响应
     * @return 跳转页面
     */
    @PostMapping("/register")
    public String register(Model model,HttpServletRequest request,HttpServletResponse response) throws FileNotFoundException {
        String username =  request.getParameter("name");
        String password =  request.getParameter("password");
        String email = request.getParameter("email");
        if(username==null||password==null||email==null||username.length()==0||password.length()==0||email.length()==0){
            return "fail.html";
        }
        if(userService.queryUserByEmail(email)!=null){
            return "fail.html";
        }
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setEmail(email);
        userService.addUser(user);
        int id = 0;
        if((id=user.getUserId())==-1){
            return "fail.html";
        }
        response.addCookie(new Cookie("email", SecretUtils.encode(email)));
        response.addCookie(new Cookie("password",SecretUtils.encode(password)));
        response.addCookie(new Cookie("id",SecretUtils.encode(String.valueOf(id))));
        String path = Data.USER_UPLOAD_PATH+id+"/image/";
        File file = new File(path);
        file.mkdirs();
        return "registerSuccess.html";
    }

    /**
     * 查看用户详情
     * 先判断是否有cookie
     * 其次判断是否有该用户
     * 注入model
     * 返回
     * @param email 邮箱cookie
     * @param password 密码cookie
     * @param model model
     * @return 页面
     */
    @GetMapping("/account")
    public String account(@CookieValue(value="email",defaultValue = "")String email,
                          @CookieValue(value="password",defaultValue = "")String password,
                          Model model){
        Integer id = -1;
        if((id=checkIsValidUserNeedDecode(email,password))==null){
            return "login-register.html";
        }
        model.addAttribute("userDetail",userService.queryUserById(id));
        return "account-detail.html";
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
    @PostMapping("/update/user")
    public String updateUser(@CookieValue(value="email",defaultValue = "")String email,
                             @CookieValue(value="password",defaultValue = "")String password,
                             @RequestParam("photo")MultipartFile photo, HttpServletRequest request) throws IOException {
        email = SecretUtils.decode(email);
        password = SecretUtils.decode(password);
        Integer id;
        if ((id = checkIsValidUser(email, password)) == null) {
            return "login-register.html";
        }
        String newName = request.getParameter("newName");
        String newEmail = request.getParameter("newEmail");
        String newPhone = request.getParameter("newPhone");
        newName = newName.length() == 0 ? null : newName;
        newEmail = newEmail.length() == 0 ? null : newEmail;
        newPhone = newPhone.length() == 0 ? null : newPhone;

        if(userService.updateUser(photo,newName,newEmail,newPhone,id)){
            return "redirect:/account";
        }
        return "index.html";
    }

    private Integer checkIsValidUserNeedDecode(String email,String password){
        if(email.length()==0||password.length()==0){
            return null;
        }
        email = SecretUtils.decode(email);
        password = SecretUtils.decode(password);
        return userService.login(email,password);
    }

    private Integer checkIsValidUser(String email,String password){
        if(email.length()==0||password.length()==0){
            return null;
        }
        return userService.login(email,password);
    }





}
