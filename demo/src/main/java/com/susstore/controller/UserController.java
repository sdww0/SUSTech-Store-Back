package com.susstore.controller;

import com.susstore.config.Constants;
import com.susstore.pojo.Users;
//import com.susstore.service.MailServiceImpl;
import com.susstore.result.CommonResult;
import com.susstore.result.ResultCode;
import com.susstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/user")
public class UserController {
//
    public static final int USER_PICTURE_SIZE = 100;
//    //ResourceUtils.getURL("classpath:").getPath()

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    /**
     *
     * @param authentication
     * @param photo
     * @param request
     * @return
     * @throws IOException
     */
    @PreAuthorize("hasRole(ROLE_USER)")
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


    /**
     *
     * @param request
     * @param username
     * @return
     */
    @PostMapping("/register")
    public CommonResult register(HttpServletRequest request, @RequestParam(name = "name")String username) {
        String password =  request.getParameter("password");
        String email = request.getParameter("email");
        if(username==null||password==null||email==null||username.length()==0||password.length()==0||email.length()==0){
            return new CommonResult(ResultCode.REGISTER_FAIL);
        }
        if(userService.queryUserByEmail(email)!=null){
            return new CommonResult(ResultCode.REGISTER_FAIL);
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
        return new CommonResult(ResultCode.SUCCESS);
    }
    /**
     * 查看用户详情
     * 先判断是否有cookie
     * 其次判断是否有该用户
     * 注入model
     * 返回
     * @param principal
     * @return 页面
     */
    @PreAuthorize("hasRole(ROLE_USER)")
    @GetMapping("/account")
    public CommonResult account(Principal principal){
        if(principal==null){
            return new CommonResult(ResultCode.USER_NOT_LOGIN);
        }
        Users user = userService.getUserByEmail(principal.getName());
        return new CommonResult(ResultCode.SUCCESS,userService.queryUserById(user.getUserId()));
    }




}
