package com.susstore.controller;

import com.susstore.config.Constants;
import com.susstore.pojo.Address;
import com.susstore.pojo.Gender;
import com.susstore.pojo.Stage;
import com.susstore.pojo.Users;
import com.susstore.result.CommonResult;
import static com.susstore.result.ResultCode.*;

import com.susstore.service.*;
import com.susstore.util.CommonUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.susstore.controller.ControllerReceiveClass.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.Random;

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

    @Autowired
    private MailServiceThread mailService;

    @Autowired
    private DealService dealService;

    @Autowired
    private GoodsService goodsService;


    private Random random = new Random();

    @GetMapping("/user_picture_default.png")
    @ApiOperation("获取用户默认头像")
    public void defaultImage(HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg;charset=utf-8");
        response.setHeader("Content-Disposition", "inline; filename=girls.png");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(Files.readAllBytes(Path.of(Constants.USER_UPLOAD_PATH+"/user_picture_default.png")));
        outputStream.flush();
        outputStream.close();
    }

    @GetMapping("/{userId}/image/{file}")
    @ApiOperation("获取用户头像")
    public void getImage(HttpServletResponse response,
                         @ApiParam("用户id") @PathVariable("userId") Integer userId,
                         @ApiParam("图片名称") @PathVariable("file")String  file) throws IOException {
        response.setContentType("image/jpeg;charset=utf-8");
        response.setHeader("Content-Disposition", "inline; filename=picture.png");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(Files.readAllBytes(Path.of(Constants.USER_UPLOAD_PATH+userId+"/image/"+file)));
        outputStream.flush();
        outputStream.close();
    }

    @PreAuthorize("hasRole('USER')")
    @ApiOperation("获取举报用户图片")
    @GetMapping("/complain/{complainerId}/{file}")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功")
    })
    public void getGoodsComplainImage(HttpServletResponse response,
                                      @ApiParam("投诉人id") @PathVariable("complainerId") Integer complainerId,
                                      @ApiParam("图片名称") @PathVariable("file")String  file) throws IOException {
        response.setContentType("image/jpeg;charset=utf-8");
        response.setHeader("Content-Disposition", "inline; filename=picture.png");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(Files.readAllBytes(Path.of(Constants.USER_COMPLAIN_PATH+complainerId+"/"+file)));
        outputStream.flush();
        outputStream.close();
    }


    @ApiOperation(value = "根据id获得用户信息")
    @GetMapping("/information/{queryUserId}")
    @ApiResponses(value = {
            @ApiResponse(code = 4012,message = "没找到用户"),
            @ApiResponse(code = 2001,message = "查询成功,不是当前登录用户"),
            @ApiResponse(code = 2002,message = "查询成功，为当前登录用户")
    })
    public CommonResult getUserInformation(
            @ApiParam("SpringSecurity用户信息认证") Principal principal,
            @ApiParam("用户id") @PathVariable("queryUserId")Integer id){
        Users user = userService.queryUserById(id);
        if(user==null){
            return new CommonResult(USER_NOT_FOUND);
        }
        Integer loginId = -1;
        if(principal!=null){
            loginId = userService.queryUserIdByEmail(principal.getName());
        }
        if(loginId.equals(user.getUserId())){
            user.setPassword("");
            return new CommonResult(QUERY_IS_LOGIN_USER,user);
        }
        return new CommonResult(QUERY_NOT_LOGIN_USER,user);
    }

    @PreAuthorize("hasRole('USER')")
    @ApiOperation("根据登录用户查询用户的评价")
    @GetMapping("/comment")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功")
    })
    public CommonResult getComment(
            @ApiParam("SpringSecurity用户信息认证") Principal principal){
        Integer user = userService.queryUserIdByEmail(principal.getName());
        return new CommonResult(SUCCESS,userService.getUsersComment(user));
    }



    @PreAuthorize("hasRole('USER')")
    @PutMapping("/update")
    @ApiOperation(value = "更新用户信息，包含：个性签名，用户名，性别，生日")
    @ApiResponses(value = {
            @ApiResponse(code=4001,message = "参数错误，请检查参数"),
            @ApiResponse(code = 2000,message = "成功")
    })
    public CommonResult updateUser(
                @ApiParam("SpringSecurity用户信息认证") Principal principal,
                @ApiParam("更新结构体")  @RequestBody  UpdateUser user
    ) {
        if(user.gender>Gender.SECRET.ordinal()||user.gender<0){
            return new CommonResult(PARAM_NOT_VALID);
        }
        Gender gender1 = Gender.values()[user.gender];

        String email = principal.getName();
        user.name = user.name.length() == 0 ? null : user.name;
        user.sign = user.sign.length() == 0 ? null : user.sign;

        Users users = Users.builder().userId(userService.queryUserIdByEmail(principal.getName())).sign(user.sign).gender(gender1.ordinal())
                .birthday(user.birthday).userName(user.name).build();
        userService.updateUserById(users);
        return new CommonResult(PARAM_NOT_VALID);
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/upload/face",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    @ApiOperation(value = "更新用户头像")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功")
    })
    public CommonResult updateUserFace(
            @ApiParam("SpringSecurity用户信息认证") Principal principal,
            @ApiParam("用户照片") @RequestParam(name="photo")MultipartFile photo
    ) {
        Users users = Users.builder().email(principal.getName()).build();
        if(userService.updateUserWithPhoto(photo,users)){
            return new CommonResult(SUCCESS,userService.queryUserById(users.getUserId()));
        }
        return new CommonResult(PARAM_NOT_VALID);
    }

    @RequestMapping(path="/register",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    @ApiOperation("注册用户")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功")
    })
    public CommonResult register(
            @ApiParam("注册结构体") @RequestBody Register register
    ) {

        if(register.username.length()==0||register.password.length()==0||register.email.length()==0){
            return new CommonResult(PARAM_NOT_VALID);
        }
        if(userService.queryUserIdByEmail(register.email)!=null){
            return new CommonResult(EMAIL_EXIST);
        }
        Users user = new Users();
        user.setUserName(register.username);
        user.setPassword(passwordEncoder.encode(register.password));
        user.setEmail(register.email);
        user.setActivateCode(register.email+ CommonUtil.getRandomString(Constants.RANDOM_STRING_SIZE));
        user.setGender(register.gender);
        userService.addUser(user);
        int id = 0;
        if((id=user.getUserId())==-1){
            return new CommonResult(PARAM_NOT_VALID);
        }
        mailService.sendSimpleMail(register.email,"欢迎注册南科闲鱼！",
                "激活链接:"+Constants.WEBSITE_LINK+"/user/activate?activateCode="+user.getActivateCode());
        String path = Constants.USER_UPLOAD_PATH+id+"/image/";
        File file = new File(path);
        file.mkdirs();
        return new CommonResult(SUCCESS);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/address")
    @ApiOperation("查看登录用户所有地址")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功")
    })
    public CommonResult address(
            @ApiParam("SpringSecurity用户信息认证") Principal principal){
        return new CommonResult(SUCCESS,addressService.getUserAddressByEmail(principal.getName()));
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/address",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    @ApiOperation("添加用户地址信息")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功")
    })
    public CommonResult addAddress(
            @ApiParam("SpringSecurity用户信息认证") Principal principal,
            @ApiParam("地址") @RequestBody Address address
    ){
        address.setBelongToUserId(userService.queryUserIdByEmail(principal.getName()));
        addressService.addAddress(address);
        return new CommonResult(SUCCESS);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/address")
    @ApiOperation("更新用户地址信息")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4030,message = "用户地址不存在"),
            @ApiResponse(code = 4003,message = "权限不足不允许访问")
    })
    public CommonResult editAddress(
            @ApiParam("SpringSecurity用户信息认证") Principal principal,
            @ApiParam("地址") @RequestBody Address address
    )
    {
        if(!addressService.getAddress(address.getAddressId())){
            return new CommonResult(ADDRESS_NOT_EXISTS);
        }
        if(!addressService.isBelongAddress(principal.getName(),address.getAddressId())){
            return new CommonResult(ACCESS_DENIED);
        }
        address.setBelongToUserId(userService.queryUserIdByEmail(principal.getName()));
        addressService.updateAddress(address);
        return new CommonResult(SUCCESS);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/address")
    @ApiOperation("删除地址")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4003,message = "权限不足不允许访问")
    })
    public CommonResult deleteAddress(
            @ApiParam("SpringSecurity用户信息认证") Principal principal,
            @ApiParam("地址id") @RequestParam("addressId")Integer addressId){
        if(!addressService.isBelongAddress(principal.getName(),addressId)){
            return new CommonResult(ACCESS_DENIED);
        }
        addressService.deleteAddress(addressId);
        return new CommonResult(SUCCESS);
    }

    @PreAuthorize("hasRole('USER')")
    @ApiOperation("向用户邮箱发送验证码,需要登录")
    @GetMapping("/sendCode1")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功")
    })
    public CommonResult sendCode1(
            @ApiParam("登录信息") Principal principal,
            @ApiParam("邮箱") @RequestParam("email")String email
    ){
        Integer userId = userService.queryUserIdByEmail(principal.getName());
        Integer checkCode = random.nextInt(899999)+100000;
        userService.changeUserCheckCodeById(userId,checkCode);
        mailService.sendSimpleMail(email,"邮箱验证码","验证码为"+checkCode);
        return new CommonResult(SUCCESS);
    }

    @ApiOperation("向用户邮箱发送验证码")
    @GetMapping("/sendCode")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4021,message = "邮箱不存在")

    })
    public CommonResult sendCode(
            @ApiParam("邮箱") @RequestParam("email")String email
    ){
        Integer userId = userService.queryUserIdByEmail(email);
        if(userId==null){
            return new CommonResult(EMAIL_NOT_FOUND);
        }
        Integer checkCode = random.nextInt(899999)+100000;
        userService.changeUserCheckCodeById(userId,checkCode);
        mailService.sendSimpleMail(email,"邮箱验证码","验证码为"+checkCode);
        return new CommonResult(SUCCESS);
    }

    @PutMapping("/activate/{activateCode}")
    @ApiOperation("根据激活码激活账户")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4022,message = "激活码不存在"),
            @ApiResponse(code = 4023,message = "用户已经激活")
    })
    public CommonResult activate(
            @ApiParam("激活码") @PathVariable("activateCode") String activateCode
    ){
        Integer integer = userService.getActivateUser(activateCode);
        if(integer==null){
            return new CommonResult(ACTIVATE_CODE_ILLEGAL);
        }else
        if(integer==-2){
            return new CommonResult(ADDRESS_NOT_EXISTS);
        }
        userService.activateUser(integer);
        return new CommonResult(SUCCESS);
    }


    @PreAuthorize("hasRole('USER')")
    @PutMapping("/charge")
    @ApiOperation("充值")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功")
    })
    public CommonResult charge(
            @ApiParam("springSecurity认证信息") Principal principal,
            @ApiParam("数量") @RequestParam("money") Float money
    ){
        userService.changeUserMoney(userService.queryUserIdByEmail(principal.getName()),money);
        return new CommonResult(SUCCESS);
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/complain",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    @ApiOperation("举报用户")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4001,message = "填写的参数有误"),
            @ApiResponse(code = 4100,message = "投诉失败"),
            @ApiResponse(code = 4101,message = "举报用户不存在")
    })
    public CommonResult complain(
            @ApiParam("SpringSecurity认证信息")Principal principal,
            @ApiParam("用户Id") @RequestParam("userId") Integer userId,
            @ApiParam("举报内容") @RequestParam("content") String content,
            @ApiParam("举报照片") @RequestParam("picture")MultipartFile picture
    ){
        //处理 未处理 撤销 管理员处理
        //get rollback
        //Users users =userService.queryUserById(userId);
        if (content.length()==0){
            return new CommonResult(PARAM_NOT_VALID);
        }
        if(userService.getUserMoney(userId)==null){
            return new CommonResult(COMPLAIN_USER_NOT_EXISTS);
        }
        if (!userService.addUserComplain(userId,
                content,picture,userService.getUserByEmail(principal.getName()).getUserId())){
            return new CommonResult(COMPLAIN_FAIL);
        }
        return new CommonResult(SUCCESS);
    }

    @PreAuthorize("hasRole('USER')")
    @ApiOperation("获取用户购买的订单")
    @GetMapping("/buyDeal/{type}")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4001,message = "填写的参数有误")
    })
    public CommonResult getUserBuyDeal(
            @ApiParam("SpringSecurity认证信息") Principal principal,
            @ApiParam("类型") @PathVariable("type") Integer type
    ){
        if(type>= Stage.values().length||type<0){
            return new CommonResult(PARAM_NOT_VALID);
        }
        return new CommonResult(SUCCESS,
                dealService.getDealByBuyerAndStage(
                        userService.queryUserIdByEmail(principal.getName()
        ),type));

    }

    @PreAuthorize("hasRole('USER')")
    @ApiOperation("获取用户卖出的订单")
    @GetMapping("/sellDeal/{type}")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4001,message = "填写的参数有误")
    })
    public CommonResult getUserSellDeal(
            @ApiParam("SpringSecurity认证信息") Principal principal,
            @ApiParam("类型") @PathVariable("type") Integer type
    ){
        if(type>= Stage.values().length||type<0){
            return new CommonResult(PARAM_NOT_VALID);
        }
        return new CommonResult(SUCCESS,
                dealService.getDealBySellerAndStage(
                        userService.queryUserIdByEmail(principal.getName()
                        ),type));

    }

    @PreAuthorize("hasRole('USER')")
    @ApiOperation("获取用户所有购买的订单")
    @GetMapping("/buyDeal")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功")
    })
    public CommonResult getUserBuyDeal(
            @ApiParam("SpringSecurity认证信息") Principal principal
    ){
        return new CommonResult(SUCCESS,
                dealService.getDealByBuyer(userService.queryUserIdByEmail(principal.getName())));

    }

    @PreAuthorize("hasRole('USER')")
    @ApiOperation("获取用户所有卖出的订单")
    @GetMapping("/sellDeal")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功")
    })
    public CommonResult getUserSellDeal(
            @ApiParam("SpringSecurity认证信息") Principal principal
    ){

        return new CommonResult(SUCCESS,
                dealService.getDealBySeller(userService.queryUserIdByEmail(principal.getName())));
    }

    @PreAuthorize("hasRole('USER')")
    @ApiOperation("获取用户发布的商品,state:0-发布,1-下架,2-所有")
    @GetMapping("/announceGoods/{state}")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4001,message = "填写的参数有误")
    })
    public CommonResult getAnnounceGoods(
            @ApiParam("SpringSecurity认证信息") Principal principal,
            @ApiParam("状态") @PathVariable("state") Integer state
    ){
        if(state>2||state<0){
            return new CommonResult(PARAM_NOT_VALID);
        }
        if(state==2)
        return new CommonResult(SUCCESS,goodsService.queryGoodsByUserId(userService.queryUserIdByEmail(principal.getName())));
        else
        return new CommonResult(SUCCESS,goodsService.queryGoodsByUserIdAndState(userService.queryUserIdByEmail(principal.getName()),state));
    }

    @ApiOperation("获取用户发布的商品,state:0-发布,1-下架,2-所有")
    @GetMapping("/announceGoods/{userId}/{state}")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4001,message = "填写的参数有误")
    })
    public CommonResult getAnnounceGoodsFromId(
            @ApiParam("状态") @PathVariable("state") Integer state,
            @ApiParam("用户id") @PathVariable("userId") Integer userId
    ){
        if(state>2||state<0){
            return new CommonResult(PARAM_NOT_VALID);
        }
        if(userService.getUserMoney(userId)==null){
            return new CommonResult(PARAM_NOT_VALID);
        }
        if(state==2)
            return new CommonResult(SUCCESS,goodsService.queryGoodsByUserId(userId));
        else
            return new CommonResult(SUCCESS,goodsService.queryGoodsByUserIdAndState(userId,state));
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/security",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    @ApiOperation("账户安全,修改密码或者邮箱或者手机号")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4001,message = "填写的参数有误"),
            @ApiResponse(code = 4000,message = "验证码出错")
    })
    public CommonResult security(
            @ApiParam("SpringSecurity用户信息认证") Principal principal,
            @ApiParam("更新信息，type:是哪种类型，密码，邮箱还是手机号，见SecurityType, checkCode可以为null")
            @RequestBody UserSecurity userSecurity
        ){
        //预参数判断
        if(userSecurity.type<0||userSecurity.type>=SecurityType.MAX.ordinal()){
            return new CommonResult(PARAM_NOT_VALID);
        }
        if(userSecurity.checkCode!=null&&String.valueOf(userSecurity.checkCode).length()!=Constants.CHECK_CODE_SIZE){
            return new CommonResult(PARAM_NOT_VALID);
        }
        Integer id = userService.queryUserIdByEmail(principal.getName());
        //检查验证码
        if(userSecurity.checkCode!=null&& !userService.getUserCheckCodeById(id).equals(userSecurity.checkCode)){
            return new CommonResult(CHECK_CODE_WRONG);
        }
        SecurityType securityType = SecurityType.values()[userSecurity.type];
        Users users = new Users();
        users.setUserId(id);
        switch (securityType){
            case PASSWORD:
                users.setPassword(passwordEncoder.encode(userSecurity.content));
                break;
            case PHONE:
                if(!CommonUtil.isInteger(userSecurity.content)){
                    return new CommonResult(PARAM_NOT_VALID);
                }
                users.setPhone(Long.parseLong(userSecurity.content));
                break;
            case EMAIL:
                if(userService.queryUserIdByEmail(userSecurity.content)!=null){
                    return new CommonResult(PARAM_NOT_VALID);
                }
                users.setEmail(userSecurity.content);
                userService.deactivateUsers(id,userSecurity.content);
                break;
            case MAX:
            default:new CommonResult(PARAM_NOT_VALID);
        }
        userService.updateUserById(users);
        return new CommonResult(SUCCESS);
    }

    @PreAuthorize("hasRole('USER')")
    @ApiOperation("检查是否在收藏夹里")
    @PutMapping("/checkCollection")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4001,message = "填写的参数有误")
    })
    public CommonResult checkCollection(
            @ApiParam("SpringSecurity认证信息") Principal principal,
            @ApiParam("商品id") @RequestParam("goodsId")Integer goodsId
    ){
        if(goodsService.getBelongUserId(goodsId)==null){
            return new CommonResult(PARAM_NOT_VALID);
        }
        return new CommonResult(SUCCESS,userService.isInUserCollection(goodsId,principal.getName()));

    }



    @PreAuthorize("hasRole('USER')")
    @ApiOperation("添加到收藏夹")
    @PutMapping("/collection")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4001,message = "填写的参数有误"),
            @ApiResponse(code = 4040,message = "收藏夹已有")
    })
    public CommonResult addCollection(
            @ApiParam("SpringSecurity认证信息") Principal principal,
            @ApiParam("商品id") @RequestParam("goodsId")Integer goodsId
    ){
        if(goodsService.getBelongUserId(goodsId)==null){
            return new CommonResult(PARAM_NOT_VALID);
        }
        if(userService.isInUserCollection(goodsId,principal.getName())){
            return new CommonResult(COLLECTION_EXISTS);
        }
        return new CommonResult(SUCCESS,userService.addCollection(userService.queryUserIdByEmail(principal.getName()),goodsId));
    }

    @PreAuthorize("hasRole('USER')")
    @ApiOperation("获得收藏夹")
    @GetMapping("/collection")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功")
    })
    public CommonResult getCollection(
            @ApiParam("SpringSecurity认证信息") Principal principal
    ){
        return new CommonResult(SUCCESS,userService.getUsersCollection(userService.queryUserIdByEmail(principal.getName())));
    }

    @PreAuthorize("hasRole('USER')")
    @ApiOperation("删除收藏夹某个商品")
    @DeleteMapping("/collection")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4001,message = "填写的参数有误"),
            @ApiResponse(code = 4041,message = "收藏夹未有")
    })
    public CommonResult deleteCollection(
            @ApiParam("SpringSecurity认证信息") Principal principal,
            @ApiParam("商品id") @RequestParam("goodsId")Integer goodsId
    ){
        if(goodsService.getBelongUserId(goodsId)==null){
            return new CommonResult(PARAM_NOT_VALID);
        }
        if(!userService.isInUserCollection(goodsId,principal.getName())){
            return new CommonResult(COLLECTION_NOT_EXISTS);
        }
        userService.deleteCollection(userService.queryUserIdByEmail(principal.getName()),goodsId );
        return new CommonResult(SUCCESS);
    }

    @ApiOperation("检查邮箱")
    @GetMapping("/checkEmail")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4001,message = "填写的参数有误"),
            @ApiResponse(code = 4012,message = "用户不存在")
    })
    public CommonResult checkEmail(
            @ApiParam("邮箱") @RequestParam("email") String email,
            @ApiParam("验证码") @RequestParam("checkCode") Integer checkCode
    ){
        Integer check = userService.getUserCheckCodeByEmail(email);
        if(check==null){
            return new CommonResult(USER_NOT_FOUND);
        }
        if(check.equals(checkCode)){
            return new CommonResult(SUCCESS,true);
        }
        return new CommonResult(PARAM_NOT_VALID);
    }

    @PreAuthorize("hasRole('USER')")
    @ApiOperation("查看我的")
    @GetMapping("/me")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功")
    })
    public CommonResult me(
            @ApiParam("SpringSecurity认证信息") Principal principal
    ){
        return new CommonResult(SUCCESS,userService.queryUserById(userService.queryUserIdByEmail(principal.getName())));
    }







    static enum SecurityType{
        PASSWORD,
        EMAIL,
        PHONE,
        MAX
    }

}
