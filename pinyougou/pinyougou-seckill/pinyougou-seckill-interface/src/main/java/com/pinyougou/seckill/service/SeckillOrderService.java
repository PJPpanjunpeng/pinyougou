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

    /**
     * 根据订单id查询放置在redis中的订单
     * @param OrderId 订单id
     * @return 秒杀订单
     */
    TbSeckillOrder getSeckillOrderInRedisByOrderId(String OrderId);

    /**
     * 将redis中对应的订单更新支付状态并保存到数据库中
     * @param orderId
     * @param transactionid
     */
    void saveOrderInRedisToDb(String orderId, String transactionid);

    /**
     * 根据订单id删除在redis中的订单
     * 删除订单后需要恢复对应秒杀商品的库存
     * @param orderId 订单id
     */
    void deleteOrderInRedis(String orderId) throws InterruptedException;
}