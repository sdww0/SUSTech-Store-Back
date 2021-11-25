package com.susstore.service;

import com.susstore.config.Constants;
import com.susstore.pojo.*;
import com.susstore.util.CommonUtil;
import com.susstore.util.ImageUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.*;

import static com.susstore.config.Constants.BACK_END_LINK;
import static com.susstore.config.Constants.GOODS_COMPLAIN_PATH;

@Service
public interface GoodsService {
    /**
     * 添加商品
     * goods需要包含除了picturePath以外的东西
     * 其中announceTime,goodsState,want,comments不需要提供
     * @param goods
     * @return
     */
    public Integer addGoods(Goods goods);

    public Integer addGoodsPicture(Integer goodsId, MultipartFile photo);


    /**
     * 根据商品id查询商品
     * @param goodsId 商品id
     * @return 商品
     */
    public Goods showGoods(Integer goodsId);





    /**
     * 编辑商品
     * goods需要包含除了picturePath以外的东西
     * 其中announceTime,goodsState,want,comments不需要提供
     * @param goods 商品信息
     * @return
     */
    public Integer editGoods(Goods goods);



    public Integer getBelongUserId(Integer goodsId);


    public Integer deleteGoods(Integer goods);

    public List<GoodsAbbreviation> searchGoods(String searchContent, Integer pageSize, Integer pageIndex);

    public Integer searchGoodsAmount(String searchContent);

    public Goods getGoodsById(int goodsId);

    public Integer getAnnouncerId(Integer goodsId);


    public int ifOnShelfById(int goodsId);

    public String getDefaultPicturePath(Integer goodsId);

    public Integer increaseView(Integer goodsId);

    public Integer increaseWant(Integer goodsId);

    public Integer commentGoods(Integer userId,Integer goodsId,String content);

    public Integer deleteGoodsComment(Integer userId,Integer commentId);

    public     List<GoodsAbbreviation> getRandomGoods();

    public int addGoodsComment(GoodsComment comment);

    public     List<GoodsAbbreviation> queryGoodsByUserId(Integer userId);

    public     List<GoodsAbbreviation> queryGoodsByUserIdAndState(Integer userId,Integer state);

    public List<GoodsComment> getGoodsComment(Integer goodsId);

    public boolean addGoodsComplain(Integer goodsId,String content,MultipartFile picture,Integer complainerId);
    public Float getGoodsTotalPrice(Integer goodsId);

    public     List<GoodsAbbreviation> getGoodsFromLabel(String content);

    public     List<GoodsAbbreviation> getGoodsFromLabelId(Integer labelId);

    public     Boolean updateUserVisitTime(Integer userId,Integer goodsId);
    public List<GoodsAbbreviation> recommendGoods(Integer userId);

    public Integer deleteGoodsPicture(Integer goodsId);

    public Integer pictureCount(Integer goodsId);




}
