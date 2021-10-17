package com.susstore.controller;

import com.alibaba.fastjson.JSONObject;
import com.susstore.config.Constants;
import com.susstore.pojo.Goods;
import com.susstore.pojo.Users;
import com.susstore.result.CommonResult;
import com.susstore.result.ResultCode;
import com.susstore.service.GoodsService;
import com.susstore.service.UserService;
import com.susstore.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public CommonResult getGoods(
            @ApiParam("商品id") @PathVariable("goodsId") String goodsId
    ){
        if(goodsId==null|| !CommonUtil.isInteger(goodsId)){
            return new CommonResult(ResultCode.NOT_FOUND);
        }
        Goods goods = goodsService.showGoods(Integer.valueOf(goodsId));
        if(goods==null){
            return new CommonResult(ResultCode.NOT_FOUND);
        }
        goodsService.increaseView(Integer.valueOf(goodsId));
        return new CommonResult(ResultCode.SUCCESS,goods);
//        JSONObject result = new JSONObject();
//        if(goodsId==null|| !CommonUtil.isInteger(goodsId)){
//            result.put("result",new CommonResult(ResultCode.NOT_FOUND));
//            return result.toJSONString();
//        }
//        Goods goods = goodsService.showGoods(Integer.valueOf(goodsId));
//        if(goods==null){
//            result.put("result",new CommonResult(ResultCode.NOT_FOUND));
//            return result.toJSONString();
//        }
//        result.put("result",new CommonResult(ResultCode.SUCCESS,goods));
//        return result.toJSONString();
    }

    @GetMapping("/{goodsId}/image/{file}")
    @ApiOperation("获取商品图片")
    public void getImage(HttpServletResponse response,
                         @ApiParam("商品id") @PathVariable("goodsId") Integer goodsId,
                         @ApiParam("图片名称") @PathVariable("file")String  file) throws IOException {
        response.setContentType("image/jpeg;charset=utf-8");
        response.setHeader("Content-Disposition", "inline; filename=girls.png");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(Files.readAllBytes(Path.of(Constants.GOODS_UPLOAD_PATH+goodsId+"/image/"+file)));
        outputStream.flush();
        outputStream.close();
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/comment")
    @ApiParam("评价商品")
    public CommonResult comment(
            @ApiParam("SpringSecurity用户认证信息")Principal principal,
            @ApiParam("商品id") @RequestParam("goodsId") Integer goodsId,
            @ApiParam("评价信息") @RequestParam("content") String content
    ){
        if(goodsService.getBelongUserId(goodsId)==null){
            return new CommonResult(ResultCode.NOT_FOUND);
        }
        goodsService.commentGoods(userService.queryUserByEmail(principal.getName()),goodsId,content);
        return new CommonResult(ResultCode.SUCCESS);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/comment")
    @ApiParam("删除商品评价")
    public CommonResult delete(
            @ApiParam("SpringSecurity用户认证信息")Principal principal,
            @ApiParam("商品id") @RequestParam("commentId") Integer commentId
    ){
        if(goodsService.deleteGoodsComment(userService.queryUserByEmail(principal.getName()),commentId)==-1){
            return new CommonResult(ResultCode.FORBIDDEN);
        }
        return new CommonResult(ResultCode.SUCCESS);
    }

    @ApiOperation("添加商品")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/addGoods")
    public CommonResult addGoods(
            @ApiParam("SpringSecurity用户认证信息")Principal principal,
            @ApiParam("商品图片") @RequestParam(name = "photos") MultipartFile photo,
            @ApiParam("标题") @RequestParam("title") String title,
            @ApiParam("价格") @RequestParam("price") Float price,
            @ApiParam("标签") @RequestParam("labels") List<String> labels,
            @ApiParam("商品介绍") @RequestParam("introduce") String introduce,
            @ApiParam("是卖出还是买入") @RequestParam("isSell") Boolean isSell,
            @ApiParam("邮费") @RequestParam("postage") Float postage
        ){
        MultipartFile[] photos = new MultipartFile[1];
        photos[0] = photo;
        if(labels.size()>LABELS_MAX_AMOUNT||photos==null||photos.length>GOODS_MAX_PICTURE){
            return new CommonResult(ResultCode.NOT_ACCEPTABLE);
        }
        Goods goods = Goods.builder().labels(labels).price(price).introduce(introduce)
                .announcer(Users.builder().userId(userService.queryUserByEmail(principal.getName())).build()).
                        title(title).isSell(isSell).postage(postage).build();
        Integer id = goodsService.addGoods(photos, goods);
        if(id==null||id<0){
            return new CommonResult(4040,"添加商品失败");
        }
        return new CommonResult(ResultCode.SUCCESS,id);
    }

    @ApiOperation("编辑商品")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/editGoods")
    public CommonResult editGoods(
            @ApiParam("SpringSecurity用户认证信息")Principal principal,
            @ApiParam("商品id") @RequestParam("goodsId") Integer goodsId,
            @ApiParam("标题") @RequestParam("title") String title,
            @ApiParam("价格") @RequestParam("price") Float price,
            @ApiParam("标签") @RequestParam("labels") List<String> labels,
            @ApiParam("商品介绍") @RequestParam("introduce") String introduce,
            @ApiParam("商品图片") @RequestParam("photos") MultipartFile photo,
            @ApiParam("是卖出还是买入") @RequestParam("isSell") Boolean isSell,
            @ApiParam("邮费") @RequestParam("postage") Float postage
    ){
        MultipartFile[] photos = new MultipartFile[1];
        photos[0] = photo;
        if(labels.size()>LABELS_MAX_AMOUNT||photos.length>GOODS_MAX_PICTURE){
            return new CommonResult(ResultCode.NOT_ACCEPTABLE);
        }
        Integer userId = goodsService.getBelongUserId(goodsId);
        if(userId==null){
            return new CommonResult(ResultCode.NOT_ACCEPTABLE);
        }
        if(!(userId==userService.queryUserByEmail(principal.getName()))){
            return new CommonResult(ResultCode.FORBIDDEN);
        }
        Goods goods = Goods.builder().labels(labels).price(price).introduce(introduce).title(title)
                .goodsId(goodsId).isSell(isSell).postage(postage).build();
        Integer id = goodsService.editGoods(photos, goods);
        return new CommonResult(ResultCode.SUCCESS,id);
    }

    @ApiOperation("删除商品")
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/deleteGoods")
    public CommonResult deleteGoods(
            @ApiParam("SpringSecurity用户认证信息")Principal principal,
            @ApiParam("商品id") @RequestParam("goodsId") Integer goodsId
    ){
        Integer id = goodsService.getBelongUserId(goodsId);
        if(id==null){
            return new CommonResult(ResultCode.NOT_ACCEPTABLE);
        }
        if(!(id==userService.queryUserByEmail(principal.getName()))){
            return new CommonResult(ResultCode.FORBIDDEN);
        }
        goodsService.deleteGoods(goodsId);
        return new CommonResult(ResultCode.SUCCESS);
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
