package com.susstore.controller;

import com.susstore.config.Constants;
import com.susstore.pojo.Address;
import com.susstore.pojo.Gender;
import com.susstore.pojo.Users;
import com.susstore.service.MailService;
import com.susstore.result.CommonResult;
import com.susstore.result.ResultCode;
import com.susstore.service.AddressService;
import com.susstore.service.UserService;
import com.susstore.util.CommonUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.Date;

@RestController
@Api(value = "用户控制器",tags = {"用户访问接口"})
@RequestMapping("/user")
public class UserController {
//
    public static final int USER_PICTURE_SIZE = 100;
//    //ResourceUtils.getURL("classpath:").getPath()

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @ApiOperation(value = "根据id获得用户信息")
    @GetMapping("/information/{queryUserId}")
    @ApiResponses(value = {
            @ApiResponse(code = 404,message = "没找到用户"),
            @ApiResponse(code = 2000,message = "查询成功,为当前登录用户"),
            @ApiResponse(code = 2001,message = "查询成功")
    })
    public CommonResult getUserInformation(
            @ApiParam("SpringSecurity用户信息认证") Authentication authentication,
            @ApiParam("用户id") @PathVariable("queryUserId")Integer id){
        Users user = userService.queryUserById(id);

        if(user==null){
            return new CommonResult(ResultCode.NOT_FOUND);
        }
        Integer loginId = -1;
        if(authentication!=null){
            loginId = userService.queryUserByEmail(authentication.getName());
        }
        if(loginId==user.getUserId()){
            user.setPassword("");
            return new CommonResult(2000,"查询成功,为当前登录用户",user);
        }
        return new CommonResult(2001,"查询成功",user);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/update")
    @ApiOperation(value = "更新用户信息，包含：个性签名，用户名，性别，生日，头像")
    @ApiResponses(value = {
            @ApiResponse(code=4020,message = "参数错误，请检查参数"),
            @ApiResponse(code = 200,message = "修改成功"),
    })
    public CommonResult updateUser(
                @ApiParam("SpringSecurity用户信息认证") Authentication authentication,
                @ApiParam("用户照片") @RequestParam("photo")MultipartFile photo,
                @ApiParam("新用户名") @RequestParam("name") String name,
                @ApiParam("个性签名") @RequestParam("sign") String sign,
                @ApiParam("性别,m代表男生，f代表女生，s代表秘密") @RequestParam("gender") String gender,
                @ApiParam("生日") @RequestParam("birthday") Date birthday) {
        if(gender.length()!=0){
            return new CommonResult(4020,"参数错误，请检查参数");
        }
        Gender gender1 ;
        switch (gender.charAt(0)){
            case 's':gender1 = Gender.SECRET;break;
            case 'm':gender1 = Gender.MALE;break;
            case 'f':gender1 = Gender.FE_MALE;break;
            default:return new CommonResult(4020,"参数错误，请检查参数");
        }

        String email = authentication.getName();
        name = name.length() == 0 ? null : name;
        sign = sign.length() == 0 ? null : sign;

        Users users = Users.builder().email(email).sign(sign).gender(gender1.ordinal())
                .birthday(birthday).userName(name).build();

        if(userService.updateUserWithPhoto(photo,users)){
            return getUserInformation(authentication,users.getUserId());
        }
        return new CommonResult(ResultCode.FAILED);
    }



    @PostMapping("/register")
    @ApiOperation("注册用户")
    public CommonResult register(
            @ApiParam("用户名")@RequestParam("name")String username,
            @ApiParam("邮箱") @RequestParam("email") String email,
            @ApiParam("密码") @RequestParam("password") String password
    ) {
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

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/address")
    @ApiOperation("查看登录用户所有地址")
    public CommonResult address(
            @ApiParam("SpringSecurity用户信息认证") Principal principal){
        if(principal==null){
            return new CommonResult(ResultCode.USER_NOT_LOGIN);
        }
        return new CommonResult(ResultCode.SUCCESS,addressService.getAddressByEmail(principal.getName()));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/address")
    @ApiOperation("添加用户地址信息")
    public CommonResult addAddress(
            @ApiParam("SpringSecurity用户信息认证") Principal principal,
            @ApiParam("收货人名") @RequestParam("recipientName")String recipientName,
            @ApiParam("地址名") @RequestParam("addressName")String addressName,
            @ApiParam("手机号") @RequestParam("phone")String phone,
            @ApiParam("是否是默认地址") @RequestParam("isDefault")Boolean isDefault){
        if(principal==null){
            return new CommonResult(ResultCode.USER_NOT_LOGIN);
        }
        if(!CommonUtil.isInteger(phone)){
            return new CommonResult(ResultCode.NOT_ACCEPTABLE);
        }
        addressService.addAddress(
                Address.builder().recipientName(recipientName)
                .addressName(addressName)
                .phone(Long.parseLong(phone))
                .isDefault(isDefault).build()
                ,principal.getName());
        return new CommonResult(ResultCode.SUCCESS);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/address")
    @ApiOperation("删除地址")
    public CommonResult deleteAddress(
            @ApiParam("SpringSecurity用户信息认证") Principal principal,
            @ApiParam("地址id") @RequestParam("addressId")String addressId){
        if(principal==null){
            return new CommonResult(ResultCode.USER_NOT_LOGIN);
        }
        if(!CommonUtil.isInteger(addressId)){
            return new CommonResult(ResultCode.NOT_ACCEPTABLE);
        }
        addressService.deleteAddress(Integer.parseInt(addressId));
        return new CommonResult(ResultCode.SUCCESS);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/security")
    @ApiOperation("账户安全,修改密码或者邮箱或者手机号")
    public CommonResult security(
            @ApiParam("SpringSecurity用户信息认证") Principal principal,
            @ApiParam("是哪种类型，密码，邮箱还是手机号，见SecurityType") @RequestParam("type")String type,
            @ApiParam("内容") @RequestParam("content")String content){
        if(principal==null){
            return new CommonResult(ResultCode.USER_NOT_LOGIN);
        }
        if(!CommonUtil.isInteger(type)){
            return new CommonResult(ResultCode.NOT_ACCEPTABLE);
        }
        Integer choose = Integer.parseInt(type);
        if(choose<0||choose>=SecurityType.MAX.ordinal()){
            return new CommonResult(ResultCode.NOT_ACCEPTABLE);
        }
        SecurityType securityType = SecurityType.values()[choose];
        Users users = new Users();
        users.setEmail(principal.getName());
        switch (securityType){
            case PASSWORD:
                userService.updateUserByEmail(users);
                break;
            case EMAIL:
                userService.updateUserEmail(principal.getName(),content);

                break;
            case PHONE:
                if(!CommonUtil.isInteger(content)){
                    return new CommonResult(ResultCode.NOT_ACCEPTABLE);
                }
                userService.updateUserByEmail(users);
                break;
            case MAX:
            default:new CommonResult(ResultCode.NOT_ACCEPTABLE);
        }
        return new CommonResult(ResultCode.SUCCESS);
    }

    static enum SecurityType{
        PASSWORD,
        EMAIL,
        PHONE,
        MAX
    }

}
