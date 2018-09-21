package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

public interface SeckillOrderService extends BaseService<TbSeckillOrder> {

    PageResult search(Integer page, Integer rows, TbSeckillOrder seckillOrder);

    /**
     * 提交订单，保存到redis中
     * @param username 用户id
     * @param seckillId 秒杀商品id
     * @return 秒杀商品id
     */
    Long submitOrder(String username, Long seckillId) throws InterruptedException;
}