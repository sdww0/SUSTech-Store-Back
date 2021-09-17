package com.susstore.controller;

import com.susstore.pojo.User;
import com.susstore.service.UserService;
import com.susstore.util.SecretUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

//    @GetMapping("/allUser")
//    public String allUser(@CookieValue(value="userID",defaultValue = "")String userId ,Model model){
//        if(userId.length()==0){
//            return "fail.html";
//        }
//        List<User> users = userService.queryUserList();
//        model.addAttribute("list",users);
//        return "allUser.html";
//    }

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
        return "loginSuccess.html";
    }

    /**
     * 用户注册
     * 首先对参数进行正确性判断
     * 后判断邮箱是否被注册
     * 注册后添加cookie跳转
     * @param model model
     * @param request 请求
     * @param response 响应
     * @return 跳转页面
     */
    @PostMapping("/register")
    public String register(Model model,HttpServletRequest request,HttpServletResponse response){
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
        if(user.getUserId()==-1){
            return "fail.html";
        }
        response.addCookie(new Cookie("email", SecretUtils.encode(email)));
        response.addCookie(new Cookie("password",SecretUtils.encode(password)));
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
        if(email.length()==0||password.length()==0){
            return "login-register.html";
        }
        email = SecretUtils.decode(email);
        password = SecretUtils.decode(password);

        Integer id = userService.login(email,password);
        if(id==null){
            return "login-register.html";
        }
        model.addAttribute("userDetail",userService.queryUserById(id));
        return "account-detail.html";
    }

}
