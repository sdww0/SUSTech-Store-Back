package com.susstore.service;

import com.susstore.config.Constants;
import com.susstore.mapper.GoodsMapper;
import com.susstore.pojo.Goods;
import com.susstore.pojo.GoodsAbbreviation;
import com.susstore.pojo.GoodsPicture;
import com.susstore.pojo.GoodsState;
import com.susstore.util.ImageUtil;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.*;

import static com.susstore.config.Constants.GOODS_MAX_PICTURE;

@Service
public class GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 添加商品
     * goods需要包含除了picturePath以外的东西
     * 其中announceTime,goodsState,want,comments不需要提供
     * @param photos
     * @param goods
     * @return
     */
    public Integer addGoods(MultipartFile[] photos, Goods goods){
        int count = 0;
        for(MultipartFile photo:photos){
            String contentType = Objects.requireNonNull(photo.getOriginalFilename()).substring(photo.getOriginalFilename().lastIndexOf("."));
            if(contentType.length()==0){
                return -1;
            }
            if (!(".jpeg".equals(contentType) ||
                    ".jpg".equals(contentType) ||
                    ".png".equals(contentType))) {
                return -1;
            }
            count++;
        }
        if(count==0||count>GOODS_MAX_PICTURE){
            return -1;
        }
        goods.setAnnounceTime(new Date());
        goods.setWant(0);
        goods.setGoodsState(GoodsState.PUBLISHED.ordinal());

        goodsMapper.addGoods(goods);
        List<GoodsPicture> picturePaths = new ArrayList<>();
        String[] uuids = new String[count];
        for(int n = 0;n<count;n++){
            uuids[n] = UUID.randomUUID().toString();
            picturePaths.add(new GoodsPicture("goods/"+goods.getGoodsId()+"/image/"+uuids[n]+".png",n==0));
        }
        goodsMapper.addGoodsPicture(goods.getGoodsId(),picturePaths);
        goodsMapper.addLabels(goods.getLabels());
        goodsMapper.addGoodsLabels(goods.getGoodsId(), goodsMapper.getLabelsId(goods.getLabels()));
        String picturePath = Constants.GOODS_UPLOAD_PATH+goods.getGoodsId()+"/image/";
        for(int n = 0;n<count;n++) {
            try {
                FileInputStream in = (FileInputStream) photos[n].getInputStream();
                BufferedImage srcImage = javax.imageio.ImageIO.read(in);
                ImageUtil.storeImage(srcImage, picturePath + uuids[n] + ".png");
            } catch (Exception e) {
                System.out.println("读取图片文件出错！" + e.getMessage());
                e.printStackTrace();
            }
        }
        return goods.getGoodsId();
    }


    /**
     * 根据商品id查询商品
     * @param goodsId 商品id
     * @return 商品
     */
    public Goods showGoods(Integer goodsId){
        return goodsMapper.queryGoodsById(goodsId);
    }
    /**
     * 编辑商品
     * goods需要包含除了picturePath以外的东西
     * 其中announceTime,goodsState,want,comments不需要提供
     * @param photos 图片
     * @param goods 商品信息
     * @return
     */
    public Integer editGoods(MultipartFile[] photos, Goods goods){
        int count = 0;
        for(MultipartFile photo:photos){
            String contentType = photo.getOriginalFilename().substring(photo.getOriginalFilename().lastIndexOf("."));
            if(contentType.length()==0){
                return -1;
            }
            if (!(".jpeg".equals(contentType) ||
                    ".jpg".equals(contentType) ||
                    ".png".equals(contentType))) {
                return -1;
            }
            count++;
        }
        if(count==0||count>GOODS_MAX_PICTURE){
            return -1;
        }
        List<GoodsPicture> picturePaths = new ArrayList<>();
        String[] uuids = new String[count];
        for(int n = 0;n<count;n++){
            uuids[n] = UUID.randomUUID().toString();
            picturePaths.add(new GoodsPicture("goods/"+goods.getGoodsId()+"/image/"+uuids[n]+".png",n==0));
        }
        goods.setGoodsState(GoodsState.PUBLISHED.ordinal());
        goodsMapper.deleteGoodsLabels(goods.getGoodsId());
        goodsMapper.addGoodsLabels(goods.getGoodsId(), goodsMapper.getLabelsId(goods.getLabels()));
        String picturePath = Constants.GOODS_UPLOAD_PATH+goods.getGoodsId()+"/image/";
        for(int n = 0;n<count;n++) {
            try {
                FileInputStream in = (FileInputStream) photos[n].getInputStream();
                BufferedImage srcImage = javax.imageio.ImageIO.read(in);
                ImageUtil.storeImage(srcImage, picturePath + uuids[n] + ".png");
            } catch (Exception e) {
                System.out.println("读取图片文件出错！" + e.getMessage());
                e.printStackTrace();
            }
        }
        goodsMapper.updateGoods(goods);
        goodsMapper.deactivateGoodsPicture(goods.getGoodsId());
        goodsMapper.addGoodsPicture(goods.getGoodsId(),picturePaths);
        return 0;
    }


    /**
     * 根据商品id获取用户邮箱
     * @param goodsId 商品id
     * @return 用户邮箱
     */
    public Integer getBelongUserId(Integer goodsId){
        return goodsMapper.getBelongUserId(goodsId);
    }

    /**
     * 删除商品（注意：只需要将商品状态调整为OFF_SHELL）
     * @param goods 商品
     * @return --
     */
    public Integer deleteGoods(Integer goods){
        return goodsMapper.deleteGoods(goods);
    }
    /**
     * 根据搜索内容查找商品略缩信息
     * @param searchContent 搜索内容
     * @param pageSize 每页大小
     * @return 商品略缩信息
     */
    public List<GoodsAbbreviation> searchGoods(String searchContent, Integer pageSize, Integer pageIndex){
        return goodsMapper.searchGoods(
                Map.of("searchContent",searchContent,"pageSize",pageSize,"pageIndex",pageIndex));
    }

    public Goods getGoodsById(int goodsId){
        return goodsMapper.queryGoodsById(goodsId);
    }

    public int ifOnShelfById(int goodsId){
        return goodsMapper.ifOnShelfById(goodsId);
    }

    public String getDefaultPicturePath(Integer goodsId){
        return goodsMapper.getDefaultPicturePath(goodsId);
    }

    public Integer increaseView(Integer goodsId){
        return goodsMapper.increaseView(goodsId);
    }

    public Integer increaseWant(Integer goodsId){
        return goodsMapper.increaseWant(goodsId);
    }

    public Integer commentGoods(Integer userId,Integer goodsId,String content){
        return goodsMapper.commentGoods(userId,goodsId,content,new Date());
    }

    public Integer deleteGoodsComment(Integer userId,Integer commentId){
        if(goodsMapper.whetherCanDeleteComment(userId,commentId)==null){
            return -1;
        }
        return goodsMapper.deleteGoodsComment(commentId);

    }

}
