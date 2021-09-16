package com.susstore.controller;

import com.susstore.pojo.User;
import com.susstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
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
    public String allUser(@CookieValue(value="user",defaultValue = "")String username ,Model model){
        if(username.length()==0){
            return "fail.html";
        }
        List<User> users = userService.queryUserList();
        model.addAttribute("list",users);
        return "allUser.html";
    }

    @PostMapping("/login")
    public String login(Model model,HttpServletRequest request,HttpServletResponse response){
        String username = request.getParameter("username");
        String password =  request.getParameter("password");
        if(username==null||password==null||username.length()==0||password.length()==0){
            return "fail.html";
        }
        Integer id = userService.login(username,password);
        if(id==null){
            return "fail.html";
        }

        model.addAttribute("loginUsername",username);
        response.addCookie(new Cookie("user",username));
        response.addCookie(new Cookie("userID",String.valueOf(id)));
        return "loginSuccess.html";


    }

    @PostMapping("/register")
    public String register(Model model,HttpServletRequest request,HttpServletResponse response){
        String username =  request.getParameter("username");
        String password =  request.getParameter("password");
        String gender = request.getParameter("gender");
        if(username==null||password==null||gender==null||username.length()==0||password.length()==0||gender.length()!=1){
            return "fail.html";
        }
        if(gender.charAt(0)!='f'&&gender.charAt(0)!='m'&&gender.charAt(0)!='n'){
            return "fail.html";
        }
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setGender(gender.charAt(0));
        Integer id = userService.addUser(user);
        if(id==null){
            return "fail.html";
        }
        model.addAttribute("loginUsername",username);
        response.addCookie(new Cookie("user",username));
        response.addCookie(new Cookie("userID",String.valueOf(id)));
        return "registerSuccess.html";

    }


}
