package com.susstore.mapper;

import com.susstore.pojo.Goods;
import com.susstore.pojo.GoodsAbbreviation;

import java.util.List;

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
     * @param goods 商品
     * @return --
     */
    Integer deleteGoods(Goods goods);

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
     * @param searchContent 搜索内容
     * @param pageSize 每页大小
     * @return 商品略缩信息
     */
    List<GoodsAbbreviation> searchGoods(String searchContent,Integer pageSize);


}
