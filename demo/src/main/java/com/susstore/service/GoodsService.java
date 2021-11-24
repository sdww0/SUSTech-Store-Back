package com.susstore.service;

import com.susstore.config.Constants;
import com.susstore.mapper.GoodsMapper;
import com.susstore.mapper.UsersMapper;
import com.susstore.pojo.*;
import com.susstore.util.CommonUtil;
import com.susstore.util.ImageUtil;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.*;

import static com.susstore.config.Constants.*;

@Service
public class GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private MailServiceThread mailService;

    /**
     * 添加商品
     * goods需要包含除了picturePath以外的东西
     * 其中announceTime,goodsState,want,comments不需要提供
     * @param goods
     * @return
     */
    public Integer addGoods(Goods goods){
        goods.setAnnounceTime(new Date());
        goods.setWant(0);
        goods.setGoodsState(GoodsState.PUBLISHED.ordinal());
        goodsMapper.addGoods(goods);
        goodsMapper.addLabels(goods.getLabels());
        goodsMapper.addGoodsLabels(goods.getGoodsId(), goodsMapper.getLabelsId(goods.getLabels()));
        return goods.getGoodsId();
    }

    public Integer addGoodsPicture(Integer goodsId,MultipartFile photo) {
        String contentType = Objects.requireNonNull(photo.getOriginalFilename()).substring(photo.getOriginalFilename().lastIndexOf("."));
        if (contentType.length() == 0) {
            return -1;
        }
        if (!(".jpeg".equals(contentType) ||
                ".jpg".equals(contentType) ||
                ".png".equals(contentType))) {
            return -1;
        }

        String uuid = UUID.randomUUID().toString();
        GoodsPicture goodsPicture = new GoodsPicture(BACK_END_LINK + "goods/" + goodsId + "/image/" + uuid + ".png", false);
        if (goodsMapper.pictureCount(goodsId) == 0) {
            goodsPicture.setIsDefaultPicture(true);
        }
        goodsMapper.addGoodsPicture(goodsId, goodsPicture);
        String picturePath = Constants.GOODS_UPLOAD_PATH + goodsId + "/image/";

        try {
            FileInputStream in = (FileInputStream) photo.getInputStream();
            BufferedImage srcImage = javax.imageio.ImageIO.read(in);
            ImageUtil.storeImage(srcImage, picturePath + uuid + ".png");
        } catch (Exception e) {
            System.out.println("读取图片文件出错！" + e.getMessage());
            e.printStackTrace();
        }

        return goodsId;
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
     * @param goods 商品信息
     * @return
     */
    public Integer editGoods(Goods goods){
        goodsMapper.deleteGoodsLabels(goods.getGoodsId());
        goodsMapper.addLabels(goods.getLabels());
        goodsMapper.addGoodsLabels(goods.getGoodsId(), goodsMapper.getLabelsId(goods.getLabels()));
        goodsMapper.updateGoods(goods);
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

    public Integer searchGoodsAmount(String searchContent){
        return goodsMapper.searchGoodsAmount(searchContent);
    }

    public Goods getGoodsById(int goodsId){
        return goodsMapper.queryGoodsById(goodsId);
    }

    public Integer getAnnouncerId(Integer goodsId){
        return goodsMapper.getAnnouncerId(goodsId);
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

    public     List<GoodsAbbreviation> getRandomGoods(){
        return goodsMapper.getRandomGoods();
    }

    public int addGoodsComment(GoodsComment comment){
        return goodsMapper.addGoodsComment(comment);
    };

    public     List<GoodsAbbreviation> queryGoodsByUserId(Integer userId){
        return goodsMapper.queryGoodsByUserId(userId);
    }

    public     List<GoodsAbbreviation> queryGoodsByUserIdAndState(Integer userId,Integer state){
        return goodsMapper.queryGoodsByUserIdAndState(userId, state) ;
    }

    public List<GoodsComment> getGoodsComment(Integer goodsId){
        return goodsMapper.getGoodsComments(goodsId);
    }


    public boolean addGoodsComplain(Integer goodsId,String content,MultipartFile picture,Integer complainerId) {
        String random = UUID.randomUUID().toString();
        String path = GOODS_COMPLAIN_PATH + complainerId + "/" + random + ".png";
        if (!picture.isEmpty()) {
            //获取文件的名称
            final String fileName = picture.getOriginalFilename();
            //限制文件上传的类型
            String contentType = fileName.substring(fileName.lastIndexOf("."));
            if (contentType.length() == 0) {
                return false;
            }
            if (".jpeg".equals(contentType) || ".jpg".equals(contentType) || ".png".equals(contentType)) {
                //完成文件的上传
                BufferedImage srcImage = null;
                try {
                    FileInputStream in = (FileInputStream) picture.getInputStream();
                    srcImage = ImageIO.read(in);
                    ImageUtil.storeImage(srcImage, path);
                } catch (Exception e) {
                    System.out.println("读取图片文件出错！" + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                return false;
            }
        }
        mailService.sendAttachmentMail(Constants.COMPLAIN_HANDING_EMAIL,"您有一份新的举报信息待处理！",
                "\n举报内容:"+content
                        +"\n 举报人："+complainerId
                        +"\n 举报商品id："+goodsId+"\n 举报图片见附件.",path);
        path = BACK_END_LINK+"goods/complain/"+ complainerId + "/" + random + ".png";
        goodsMapper.addGoodsComplain(goodsId, content, path, complainerId);
        return true;
    }

    public Float getGoodsTotalPrice(Integer goodsId){
        return goodsMapper.getGoodsTotalPrice(goodsId);
    }

    public     List<GoodsAbbreviation> getGoodsFromLabel(String content){
        return goodsMapper.getGoodsFromLabel(content);
    }

    public     List<GoodsAbbreviation> getGoodsFromLabelId(Integer labelId){
        return goodsMapper.getGoodsFromLabelId(labelId);
    }

    public     Boolean updateUserVisitTime(Integer userId,Integer goodsId){
        return goodsMapper.updateUserVisitTime(userId,goodsId);
    }

    public List<GoodsAbbreviation> recommendGoods(Integer userId) {
        List<UsersLabel> labels = usersMapper.getUserVisitedLabels(userId);
        List<GoodsAbbreviation> goodsAbbreviations = new ArrayList<>();
        Integer allCount = 0;
        for(UsersLabel label : labels){
            allCount+=label.getVisitTime();
        }
        if(allCount<=15){
            return getRandomGoods();
        }
        Integer[] eachIndex = new Integer[15];
        Integer eachCount = allCount/10;
        for(int n = 0;n<10;n++){
            if(n==9){
                eachIndex[n] = CommonUtil.getRandomInteger(n*eachCount,allCount);
                break;
            }
            eachIndex[n] = CommonUtil.getRandomInteger(n*eachCount,(n+1)*eachCount);
        }
        allCount = 0;
        Integer temp;
        int currentIndex = 0;
        for(UsersLabel label:labels){
            temp = allCount+label.getVisitTime();
            if(allCount<=eachIndex[currentIndex]&&eachIndex[currentIndex]<temp){
                goodsAbbreviations.add(goodsMapper.getRandomGoodsFromLabel(label.getLabelId()));
                currentIndex++;
            }
        }
        while(goodsAbbreviations.size()<15){
            goodsAbbreviations.add(goodsMapper.getOneRandomGoods());
        }
        return goodsAbbreviations;
    }

    public Integer deleteGoodsPicture(Integer goodsId) {
        return goodsMapper.deactivateGoodsPicture(goodsId);
    }

    public Integer pictureCount(Integer goodsId){
        return goodsMapper.pictureCount(goodsId);
    }


}
