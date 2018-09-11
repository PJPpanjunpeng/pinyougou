package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;

import java.util.List;

public interface GoodsService extends BaseService<TbGoods> {

    PageResult search(Integer page, Integer rows, TbGoods goods);

    void addGoods(Goods goods);

    Goods findGoodsById(Long id);

    void updateGoods(Goods goods);

    void updateStatus(Long[] ids, String status);

    void deleteGoodsByIds(Long[] ids);

    void updownStatus(Long[] ids, String status) throws Exception;

    /**
     * 根据商品SPU id集合和状态查询这些商品对应的sku商品列表
     * @param ids 商品SPU id集合
     * @param status sku商品状态
     * @return sku商品列表
     */
    List<TbItem> findItemListByGoodsIdsAndStatus(Long[] ids, String status);
}