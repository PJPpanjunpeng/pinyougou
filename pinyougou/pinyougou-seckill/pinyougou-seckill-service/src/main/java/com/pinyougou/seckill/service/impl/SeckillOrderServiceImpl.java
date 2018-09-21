package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.common.util.RedisLock;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.mapper.SeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.management.OperationsException;
import java.security.PrivateKey;
import java.util.Date;
import java.util.List;
import java.util.PrimitiveIterator;

@Service(interfaceClass = SeckillOrderService.class)
public class SeckillOrderServiceImpl extends BaseServiceImpl<TbSeckillOrder> implements SeckillOrderService {

    //秒杀订单在redis中的键名
    private static  final String SECKILL_ORDERS = "SECKILL_ORDERS";

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Override
    public PageResult search(Integer page, Integer rows, TbSeckillOrder seckillOrder) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(seckillOrder.get***())){
            criteria.andLike("***", "%" + seckillOrder.get***() + "%");
        }*/

        List<TbSeckillOrder> list = seckillOrderMapper.selectByExample(example);
        PageInfo<TbSeckillOrder> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public Long submitOrder(String username, Long seckillId) throws InterruptedException {

        //在秒杀系统的商品详情页面中点击了 立即抢购；判断当前商品是否存在，库存是否足够，
        // 将存在redis中的商品库存减1；生成具体的秒杀商品订单保存到redis中；
        // 如果在秒杀商品库存减1之后的库存量为0的时候；需要将redis中的秒杀商品同步保存回到mysql中

        RedisLock redisLock = new RedisLock(redisTemplate);
        if (redisLock.lock(seckillId.toString())) {//获取分布式锁
            //1、查询秒杀商品
            TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SeckillGoodsServiceImpl.SECKILL_GOODS).get(seckillId);
            if (seckillGoods == null) {
                throw new RuntimeException("商品不存在");
            }
            //判断库存
            if (seckillGoods.getStockCount() == 0) {
                throw new RuntimeException("商品已抢完");
            }
            //2、扣减商品库存
            seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);

            if (seckillGoods.getStockCount() > 0) {
                //更新 redis 中库存
                redisTemplate.boundHashOps(SeckillGoodsServiceImpl.SECKILL_GOODS).put(seckillId, seckillGoods);
            } else {
                //库存为 0 的时候写回数据库并删除 redis 中记录
                seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
                //移除redis中相关数据
                redisTemplate.boundHashOps(SeckillGoodsServiceImpl.SECKILL_GOODS).delete(seckillId);
            }
            //释放分布式锁
            redisLock.unlock(seckillId.toString());

            //3、生成订单并保存到redis中
            Long orderId = idWorker.nextId();
            TbSeckillOrder seckillOrder = new TbSeckillOrder();
            seckillOrder.setId(orderId);
            seckillOrder.setStatus("0");//未支付
            seckillOrder.setUserId(username);
            seckillOrder.setSellerId(seckillGoods.getSellerId());
            seckillOrder.setSeckillId(seckillId);
            seckillOrder.setCreateTime(new Date());
            seckillOrder.setMoney(seckillGoods.getCostPrice());//秒杀价

            redisTemplate.boundHashOps((SECKILL_ORDERS)).put(orderId.toString(), seckillOrder);

            return orderId;
        }

        return null;
    }
}
