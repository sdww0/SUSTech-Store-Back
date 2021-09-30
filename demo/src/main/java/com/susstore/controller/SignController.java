package com.susstore.controller;

import com.susstore.config.Constants;
import com.susstore.pojo.Users;
import com.susstore.result.CommonResult;
import com.susstore.service.SignService;
import com.susstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.Principal;


@RestController
public class SignController {

    @Autowired
    UserService userService;

    @Autowired
    SignService signService;

//    @GetMapping("/pre-login")
//    public String preLogin(Model model,Principal principal){
//
//        if(principal!=null){
//            model.addAttribute("sign-message","已登录，请不要重复登陆");
//            return this.account(principal,model);
//        }
//        return "/login";
//
//    }
//
//    /**
//     * 登录
//     * 参数判断
//     * 登录判断
//     * 添加cookie
//     * 返回id
//     * @param model model
//     * @param request 请求
//     * @param response 响应
//     * @return 跳转页面
//     */
//    @PostMapping("/login")
//    public String login(Model model, HttpServletRequest request, HttpServletResponse response){
//        String email = request.getParameter("email");
//        String password =  request.getParameter("password");
//        if(email==null||password==null||email.length()==0||password.length()==0){
//            model.addAttribute("sign-message","登录失败");
//            return "/index.html";
//        }
//        Integer id = userService.login(email,password);
//        if(id==null){
//            model.addAttribute("sign-message","登录失败,无此用户");
//            return "/index.html";
//        }
//
//        response.addCookie(new Cookie("email", SecretUtils.encode(email)));
//        response.addCookie(new Cookie("password",SecretUtils.encode(password)));
//        response.addCookie(new Cookie("id",SecretUtils.encode(String.valueOf(id))));
//        return "/index.html";
//    }
//
    @Autowired
    private PasswordEncoder passwordEncoder;
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
    public CommonResult register(HttpServletRequest request, @RequestParam(name = "name")String username) {
        String password =  request.getParameter("password");
        String email = request.getParameter("email");
        if(username==null||password==null||email==null||username.length()==0||password.length()==0||email.length()==0){
            return "fail.html";
        }
        if(userService.queryUserByEmail(email)!=null){
            return "fail.html";
        }
        Users user = new Users();
        user.setUserName(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        userService.addUser(user);
        int id = 0;
        if((id=user.getUserId())==-1){
            return new CommonResult();
        }
        String path = Constants.USER_UPLOAD_PATH+id+"/image/";
        File file = new File(path);
        file.mkdirs();
        return "registerSuccess.html";
    }

    @RequestMapping("/pre-register")
    public String preRegister(){
        return "/register.html";
    }


        /**
     * 查看用户详情
     * 先判断是否有cookie
     * 其次判断是否有该用户
     * 注入model
     * 返回
     * @param model model
     * @return 页面
     */
    @GetMapping("/account")
    public CommonResult account(Principal principal,
                          Model model){

        if(principal==null){
            return "redirect:/login.html";
        }
        Users user = signService.getUserByEmail(principal.getName());
        model.addAttribute("userDetail",userService.queryUserById(user.getUserId()));
        return "/account-detail.html";
    }

}
