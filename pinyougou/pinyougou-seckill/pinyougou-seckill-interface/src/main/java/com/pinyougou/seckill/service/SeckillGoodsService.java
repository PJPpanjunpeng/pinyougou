package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;

public interface SeckillGoodsService extends BaseService<TbSeckillGoods> {

    PageResult search(Integer page, Integer rows, TbSeckillGoods seckillGoods);

    /**
     * 查询已已经审核库存大于0，开始但是还未结束的秒杀商品列表
     * @return 秒杀商品列表
     */
    List<TbSeckillGoods> findList();

    /**
     * 根据商品id 查询redis中对应的商品
     * @param id 商品id
     * @return 秒杀商品详细
     */
    TbSeckillGoods findSeckillGoodsInRedisById(Long id);
}