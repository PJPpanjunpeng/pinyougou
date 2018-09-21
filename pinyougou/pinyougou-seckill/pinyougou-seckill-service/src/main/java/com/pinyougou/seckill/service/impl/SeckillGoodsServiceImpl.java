package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service(interfaceClass = SeckillGoodsService.class)
public class SeckillGoodsServiceImpl extends BaseServiceImpl<TbSeckillGoods> implements SeckillGoodsService {

    //秒杀商品
    public static final String SECKILL_GOODS = "SECKILL_GOODS";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbSeckillGoods seckillGoods) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(seckillGoods.get***())){
            criteria.andLike("***", "%" + seckillGoods.get***() + "%");
        }*/

        List<TbSeckillGoods> list = seckillGoodsMapper.selectByExample(example);
        PageInfo<TbSeckillGoods> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public List<TbSeckillGoods> findList() {

        List<TbSeckillGoods> seckillGoodsList = null;

        try {
            //从redis中查找
            seckillGoodsList = redisTemplate.boundHashOps(SECKILL_GOODS).values();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (seckillGoodsList == null || seckillGoodsList.size() == 0) {

            //本方法要执行；
            // 如：select * from tb_seckill_goods where status='1' and stock_count > 0 and start_time<=? and end_time > ? order by start_time

            Example example = new Example(TbSeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();

            //已经审核
            criteria.andEqualTo("status", "1");
            //库存大于 0
            criteria.andGreaterThan("stockCount", 0);
            //已经开始；开始时间小于等于当前时间
            criteria.andLessThanOrEqualTo("startTime", new Date());
            //还未结束；结束时间大于当前时间
            criteria.andGreaterThan("endTime", new Date());

            //根据开始时间升序排序
            example.orderBy("startTime");

            seckillGoodsList = seckillGoodsMapper.selectByExample(example);

            try {
                //将秒杀商品一个个地存入redis中
                if (seckillGoodsList != null && seckillGoodsList.size() > 0) {
                    for (TbSeckillGoods seckillGoods: seckillGoodsList) {
                        redisTemplate.boundHashOps(SECKILL_GOODS).put(seckillGoods.getId(), seckillGoods);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("从缓存中读取了秒杀商品列表...");
        }
        return seckillGoodsList;
    }
}
