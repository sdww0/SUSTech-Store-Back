package com.susstore.mapper;

import com.susstore.pojo.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

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

    Integer addGoodsPicture(Integer goodsId,List<GoodsPicture> pictures);

    Integer deactivateGoodsPicture(Integer goodsId);

    Users getAnnounceUser(Integer goodsId);

    List<GoodsPicture> getGoodsPicturePath(Integer goodsId);

    String getDefaultPicturePath(Integer goodsId);

    List<Comment> getGoodsComments(Integer goodsId);

    Integer addGoodsLabels(Integer goodsId,List<Integer> labels);

    List<Integer> getLabelsId(List<String> contents);

    Integer addLabels(List<String> contents);

    Integer deleteGoodsLabels(Integer goodsId);

    Integer increaseView(Integer goodsId);

    Integer increaseWant(Integer goodsId);


}
