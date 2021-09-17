package com.susstore.controller;

import com.susstore.pojo.User;
import com.susstore.service.UserService;
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

    @GetMapping("/allUser")
    public String allUser(@CookieValue(value="userID",defaultValue = "")String userId ,Model model){
        if(userId.length()==0){
            return "fail.html";
        }
        List<User> users = userService.queryUserList();
        model.addAttribute("list",users);
        return "allUser.html";
    }

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

        model.addAttribute("loginUsername",email);
        response.addCookie(new Cookie("user",email));
        response.addCookie(new Cookie("userID",String.valueOf(id)));
        return "loginSuccess.html";


    }

    @PostMapping("/register")
    public String register(Model model,HttpServletRequest request,HttpServletResponse response){
        String username =  request.getParameter("name");
        String password =  request.getParameter("password");
        String email = request.getParameter("email");
        if(username==null||password==null||email==null||username.length()==0||password.length()==0||email.length()==0){
            return "fail.html";
        }
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setEmail(email);
        userService.addUser(user);
        int id = user.getUserId();
        if(id==-1){
            return "fail.html";
        }
        model.addAttribute("loginUsername",username);
        response.addCookie(new Cookie("user",username));
        response.addCookie(new Cookie("userID",String.valueOf(id)));
        return "registerSuccess.html";
    }

    @GetMapping("/account")
    public String account(@CookieValue(value="userID",defaultValue = "")String userId,
                          Model model){
        if(userId.length()==0){
            return "my-account.html";
        }
        User user = userService.queryUserById(Integer.valueOf(userId));
        model.addAttribute("userDetail",user);
        return "account-detail.html";
    }

}
