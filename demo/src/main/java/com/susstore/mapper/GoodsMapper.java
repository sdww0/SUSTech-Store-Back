package com.susstore.mapper;

import com.susstore.pojo.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface GoodsMapper {
    /**
     * 添加商品，不需要添加商品图片路径
     * 路径默认为/goods/{goodsId}/image/(1/2/3).jpg
     * @param goods 商品
     * @return 商品id
     *
     */
    Integer addGoods(Goods goods);

    /**
     * 更新商品信息
     * @param goods 商品
     * @return --
     */
    Integer updateGoods(Goods goods);

    /**
     * 删除商品（注意：只需要将商品状态调整为OFF_SHELL）
     * @param goodsId 商品id
     * @return --
     */
    Integer deleteGoods(Integer goodsId);

    /**
     * 根据商品id查询商品信息
     * @param id id
     * @return 商品
     */
    Goods queryGoodsById(Integer id);

    /**
     * 根据用户id查询发布的所有商品略缩信息
     * @param userId 用户id
     * @return 所有商品
     */
    List<GoodsAbbreviation> queryGoodsByUserId(Integer userId);

    List<GoodsAbbreviation> queryGoodsByUserIdAndState(Integer userId,Integer state);

    /**
     * 根据搜索内容查找商品略缩信息
     * @param map String searchContent, Integer pageSize, Integer pageIndex
     * @return 商品略缩信息
     */
    List<GoodsAbbreviation> searchGoods(Map<String,Object> map);

    /**
     * 根据商品id获取用户邮箱
     * @param goodsId 商品id
     * @return 用户邮箱
     */
    Integer getBelongUserId(Integer goodsId);

    Integer ifOnShelfById(int goodsId);

    Integer addGoodsPicture(Integer goodsId,GoodsPicture pictures);

    Integer deactivateGoodsPicture(Integer goodsId);

    Users getAnnounceUser(Integer goodsId);

    List<GoodsPicture> getGoodsPicturePath(Integer goodsId);

    String getDefaultPicturePath(Integer goodsId);

    List<GoodsComment> getGoodsComments(Integer goodsId);

    Integer addGoodsLabels(Integer goodsId,List<Integer> labels);

    List<Integer> getLabelsId(List<String> contents);

    Integer addLabels(List<String> contents);

    Integer deleteGoodsLabels(Integer goodsId);

    Integer increaseView(Integer goodsId);

    Integer increaseWant(Integer goodsId);

    Integer commentGoods(Integer userId, Integer goodsId, String content, Date date);

    Integer deleteGoodsComment(Integer commentId);

    Integer whetherCanDeleteComment(Integer userId,Integer commentId);

    List<GoodsAbbreviation> getRandomGoods();

    int addGoodsComment(GoodsComment comment);

    Integer addGoodsComplain(Integer goodsId, String content, String picturePath, Integer complainerId);

    Float getGoodsTotalPrice(Integer goodsId);

    Integer getAnnouncerId(Integer goodsId);

    List<GoodsAbbreviation> getGoodsFromLabel(String content);

    Boolean updateUserVisitTime(Integer userId,Integer goodsId);

    List<GoodsAbbreviation> getGoodsFromLabelId(Integer labelId);

    GoodsAbbreviation getRandomGoodsFromLabel(Integer labelId);

    GoodsAbbreviation getOneRandomGoods();

    Integer searchGoodsAmount(String searchContent);

    Integer pictureCount(Integer goodsId);
}
