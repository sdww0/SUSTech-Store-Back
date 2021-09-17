package com.susstore.controller;

import com.susstore.config.Data;
import com.susstore.pojo.User;
import com.susstore.service.UserService;
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
        if(user.getUserId()==-1){
            return "fail.html";
        }
        response.addCookie(new Cookie("email", SecretUtils.encode(email)));
        response.addCookie(new Cookie("password",SecretUtils.encode(password)));
        String path = Data.USER_UPLOAD_PATH+email+"/image/";
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
        String path = Data.USER_UPLOAD_PATH + email + "/image/";
        String picturePath = null;
        if (!photo.isEmpty()) {
            //String realPath = path.replace('/', '\\').substring(1,path.length());//linux系统再弄
            String realPath = path;
            System.out.println(realPath);
            //获取文件的名称
            final String fileName = photo.getOriginalFilename();
            //限制文件上传的类型
            String contentType = fileName.substring(fileName.lastIndexOf("."));
            if (contentType.length() == 0) {
                return "index.html";
            }
            if (".jpeg".equals(contentType) || ".jpg".equals(contentType) || ".png".equals(contentType)) {
                //完成文件的上传
                BufferedImage srcImage = null;
                try {
                    FileInputStream in = (FileInputStream) photo.getInputStream();
                    srcImage = javax.imageio.ImageIO.read(in);
                    picturePath = "/user/" + email + "/image/index" + contentType;
                    ImageUtil.zoomImage(srcImage, realPath + "index" + contentType, USER_PICTURE_SIZE, USER_PICTURE_SIZE);
                } catch (Exception e) {
                    System.out.println("读取图片文件出错！" + e.getMessage());
                    e.printStackTrace();
                }
                System.out.println("图片上传成功!");
            }
        }
        User user = new User();
        user.setUserId(id);
        user.setName(newName);
        user.setEmail(newEmail);
        if (newPhone != null && isInteger(newPhone)){
            user.setPhone(Long.parseLong(newPhone));
        }
        user.setPicturePath(picturePath);
        userService.updateUser(user);
        return "redirect:/account";


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


    private static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }


}
