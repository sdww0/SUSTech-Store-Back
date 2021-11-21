package com.susstore.controller;

import com.susstore.config.Constants;
import com.susstore.pojo.Goods;
import com.susstore.pojo.GoodsComment;
import com.susstore.pojo.Users;
import com.susstore.result.CommonResult;
import com.susstore.result.ResultCode;
import com.susstore.service.GoodsService;
import com.susstore.service.UserService;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.susstore.controller.ControllerReceiveClass.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.List;

import static com.susstore.config.Constants.GOODS_MAX_PICTURE;
import static com.susstore.config.Constants.LABELS_MAX_AMOUNT;

@RestController
@Api(value = "商品控制器",tags = {"商品访问接口"})
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private UserService userService;

    @GetMapping("/{goodsId}")
    @ApiOperation("根据商品id获取商品信息")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4050,message = "商品不存在")
    })
    public CommonResult getGoods(
            @ApiParam("SpringSecurity认证信息") Principal principal,
            @ApiParam("商品id") @PathVariable("goodsId") Integer goodsId
    ){
        Goods goods = goodsService.showGoods(goodsId);
        if(goods==null){
            return new CommonResult(ResultCode.GOODS_NOT_FOUND);
        }
        if(principal!=null){
            goodsService.updateUserVisitTime(userService.queryUserIdByEmail(principal.getName()),goodsId);
        }
        goodsService.increaseView(goodsId);
        return new CommonResult(ResultCode.SUCCESS,goods);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/recommend")
    @ApiOperation("商品推荐")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功")
    })
    public CommonResult recommend(
            @ApiParam("SpringSecurity认证信息") Principal principal
    ){
        return new CommonResult(ResultCode.SUCCESS,
                goodsService.recommendGoods(userService.queryUserIdByEmail(principal.getName())));
    }

    @GetMapping("/{goodsId}/image/{file}")
    @ApiOperation("获取商品图片")
    public void getImage(HttpServletResponse response,
                         @ApiParam("商品id") @PathVariable("goodsId") Integer goodsId,
                         @ApiParam("图片名称") @PathVariable("file")String  file) throws IOException {
        response.setContentType("image/jpeg;charset=utf-8");
        response.setHeader("Content-Disposition", "inline; filename=picture.png");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(Files.readAllBytes(Path.of(Constants.GOODS_UPLOAD_PATH+goodsId+"/image/"+file)));
        outputStream.flush();
        outputStream.close();
    }

    @PreAuthorize("hasRole('USER')")
    @ApiOperation("获取举报商品图片")
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
        outputStream.write(Files.readAllBytes(Path.of(Constants.GOODS_COMPLAIN_PATH+complainerId+"/"+file)));
        outputStream.flush();
        outputStream.close();
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/comment",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    @ApiOperation("评价商品")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4050,message = "商品不存在")
    })
    public CommonResult comment(
            @ApiParam("SpringSecurity用户认证信息")Principal principal,
            @ApiParam("评价") @RequestBody CommentGoods commentGoods
            ){
        if(goodsService.getBelongUserId(commentGoods.goodsId)==null){
            return new CommonResult(ResultCode.GOODS_NOT_FOUND);
        }
        goodsService.commentGoods(userService.queryUserIdByEmail(principal.getName()),
                commentGoods.goodsId,
                commentGoods.content);
        return new CommonResult(ResultCode.SUCCESS);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/comment")
    @ApiOperation("删除商品评价")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4003,message = "权限不足不允许访问")
    })
    public CommonResult delete(
            @ApiParam("SpringSecurity用户认证信息")Principal principal,
            @ApiParam("商品id") @RequestParam("commentId") Integer commentId
    ){
        if(goodsService.deleteGoodsComment(userService.queryUserIdByEmail(principal.getName()),commentId)==-1){
            return new CommonResult(ResultCode.ACCESS_DENIED);
        }
        return new CommonResult(ResultCode.SUCCESS);
    }

    @GetMapping("/comment/{goodsId}")
    @ApiOperation("获得商品评价")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4050,message = "商品不存在")
    })
    public CommonResult comment(
            @ApiParam("商品id") @PathVariable("goodsId") Integer goodsId
    ){
        List<GoodsComment> goodsComments = goodsService.getGoodsComment(goodsId);
        if(goodsComments==null){
            return new CommonResult(ResultCode.GOODS_NOT_FOUND);
        }
        return new CommonResult(ResultCode.SUCCESS,goodsComments);
    }

    @ApiOperation("添加商品")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/add",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4001,message = "填写的参数有误"),
            @ApiResponse(code = 4060,message = "添加商品失败")
    })
    public CommonResult addGoods(
            @ApiParam("SpringSecurity用户认证信息")Principal principal,
            @ApiParam("商品") @RequestBody GoodsInfo goodsInfo
        ){
        if(goodsInfo.labels.size()>LABELS_MAX_AMOUNT){
            return new CommonResult(ResultCode.PARAM_NOT_VALID);
        }
        Goods goods = Goods.builder().labels(goodsInfo.labels)
                .price(goodsInfo.price).introduce(goodsInfo.introduce)
                .announcer(Users.builder().userId(userService.queryUserIdByEmail(principal.getName())).build()).
                        title(goodsInfo.title).isSell(goodsInfo.isSell).postage(goodsInfo.postage).build();

        Integer id = goodsService.addGoods(goods);
        if(id==null||id<0){
            return new CommonResult(ResultCode.ADD_GOODS_FAILED);
        }
        return new CommonResult(ResultCode.SUCCESS,id);
    }

    @ApiOperation("上传商品图片")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/upload/picture",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    @ApiResponses(value={
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4003,message = "权限不足不允许访问"),
            @ApiResponse(code = 4050,message = "商品不存在")
    })
    public CommonResult uploadPicture(
            @ApiParam("SpringSecurity用户认证信息")Principal principal,
            @ApiParam("商品图片") @RequestParam(name = "photos") MultipartFile photo,
            @ApiParam("商品id") @RequestParam("goodsId") Integer goodsId
    ){
        Integer userId = goodsService.getBelongUserId(goodsId);
        if(userId==null){
            return new CommonResult(ResultCode.GOODS_NOT_FOUND);
        }
        if(!(userId.equals(userService.queryUserIdByEmail(principal.getName())))){
            return new CommonResult(ResultCode.ACCESS_DENIED);
        }
        MultipartFile[] photos = new MultipartFile[1];
        photos[0] = photo;
        Integer id = goodsService.addGoodsPicture(goodsId,photos);
        return new CommonResult(ResultCode.SUCCESS,id);
    }


    @ApiOperation("更新商品，不需要图片")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/edit",method = {RequestMethod.OPTIONS,RequestMethod.POST})
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4001,message = "填写的参数有误"),
            @ApiResponse(code = 4003,message = "权限不足不允许访问"),
            @ApiResponse(code = 4050,message = "商品不存在")
    })
    public CommonResult editGoods(
            @ApiParam("SpringSecurity用户认证信息")Principal principal,
            @ApiParam("商品") @RequestBody GoodsInfo goodsInfo
    ){
        if(goodsInfo.labels.size()>LABELS_MAX_AMOUNT){
            return new CommonResult(ResultCode.PARAM_NOT_VALID);
        }
        Integer userId = goodsService.getBelongUserId(goodsInfo.goodsId);
        if(userId==null){
            return new CommonResult(ResultCode.GOODS_NOT_FOUND);
        }
        if(!(userId.equals(userService.queryUserIdByEmail(principal.getName())))){
            return new CommonResult(ResultCode.ACCESS_DENIED);
        }
        Goods goods = Goods.builder().labels(goodsInfo.labels)
                .price(goodsInfo.price).introduce(goodsInfo.introduce).title(goodsInfo.title)
                .goodsId(goodsInfo.goodsId).isSell(goodsInfo.isSell).postage(goodsInfo.postage).build();
        Integer id = goodsService.editGoods(goods);
        return new CommonResult(ResultCode.SUCCESS,id);
    }

    @ApiOperation("删除商品")
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/delete")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4001,message = "填写的参数有误"),
            @ApiResponse(code = 4003,message = "权限不足不允许访问")
    })
    public CommonResult deleteGoods(
            @ApiParam("SpringSecurity用户认证信息")Principal principal,
            @ApiParam("商品id") @RequestParam("goodsId") Integer goodsId
    ){
        Integer id = goodsService.getBelongUserId(goodsId);
        if(id==null){
            return new CommonResult(ResultCode.PARAM_NOT_VALID);
        }
        if(!(id==userService.queryUserIdByEmail(principal.getName()))){
            return new CommonResult(ResultCode.ACCESS_DENIED);
        }
        goodsService.deleteGoods(goodsId);
        return new CommonResult(ResultCode.SUCCESS);
    }

    @ApiOperation("获取随机商品")
    @GetMapping("/random")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功")
    })
    public CommonResult randomGoods(){
        return new CommonResult(ResultCode.SUCCESS,goodsService.getRandomGoods());
    }

    @ApiOperation("举报商品")
    @RequestMapping(value = "/complain",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    @PreAuthorize("hasRole('USER')")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功"),
            @ApiResponse(code = 4001,message = "填写的参数有误"),
            @ApiResponse(code = 4050,message = "商品不存在")
    })
    public CommonResult complain(
        @ApiParam("SpringSecurity认证信息")Principal principal,
        @ApiParam("商品Id") @RequestParam("goodsId") Integer goodsId,
        @ApiParam("举报内容") @RequestParam("content") String content,
        @ApiParam("举报照片") @RequestParam("picture")MultipartFile picture
    ){
            //处理 未处理 撤销 管理员处理
            //get rollback
            //Users users =userService.queryUserById(userId);
            if (goodsService.getBelongUserId(goodsId)==null){
                return new CommonResult(ResultCode.GOODS_NOT_FOUND);
            }
            if (content.length()==0){
                return new CommonResult(ResultCode.PARAM_NOT_VALID);
            }
            if (!goodsService.addGoodsComplain(goodsId,
                    content,picture,userService.getUserByEmail(principal.getName()).getUserId())){
                return new CommonResult(ResultCode.COMPLAIN_FAIL);
            }
            return new CommonResult(ResultCode.SUCCESS);
    }

    @PreAuthorize("hasRole('USER')")
    @ApiOperation("根据商品标签获取商品")
    @GetMapping("/label/{content}")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功")
    })
    public CommonResult getGoodsFromLabels(
            @ApiParam("内容") @PathVariable("content") String content
    ){
        return new CommonResult(ResultCode.SUCCESS,goodsService.getGoodsFromLabel(content));
    }

    @PreAuthorize("hasRole('USER')")
    @ApiOperation("根据商品标签id获取商品")
    @GetMapping("/label/id/{labelId}")
    @ApiResponses(value = {
            @ApiResponse(code = 2000,message = "成功")
    })
    public CommonResult getGoodsFromLabelsId(
            @ApiParam("内容") @PathVariable("labelId") Integer content
    ){
        return new CommonResult(ResultCode.SUCCESS,goodsService.getGoodsFromLabelId(content));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class acceptGoods{
        private Integer goodsId;
        private Float price;
        private String title;
        private MultipartFile[] photos;
        private List<String> labels;
        private String introduce;
    }

}
