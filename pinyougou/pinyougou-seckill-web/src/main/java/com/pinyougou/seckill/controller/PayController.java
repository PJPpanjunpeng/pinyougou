package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/pay")
@RestController
public class PayController {

    @Reference
    private SeckillOrderService seckillOrderService;

    @Reference
    private WeixinPayService weixinPayService;


    /**
     * 根据支付日志 id 到微信支付创建支付订单并返回支付二维码地址等信息
     *
     * @param outTradeNo 支付日志 id
     * @return 支付二维码地址等信息
     */
    @GetMapping("/createNative")
    public Map<String, String> createNative(String outTradeNo) {

        try {
            //根据订单 id 查询放置在 redis 中的订单
            TbSeckillOrder order = seckillOrderService.getSeckillOrderInRedisByOrderId(outTradeNo);
            if (order != null) {
                //1、查询出本次要支付总金额
                String totalFee = (long) (order.getMoney().doubleValue() * 100) + "";

                //2、调用支付系统业务对象的生成二维码的方法返回一些信息
                return weixinPayService.createNative(outTradeNo, totalFee);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    /**
     * 根据秒杀订单 id 查询订单支付状态
     *
     * @param outTradeNo 订单 id
     * @return 支付结果
     */
    @GetMapping("/queryPayStatus")
    public Result queryPayStatus(String outTradeNo) {

        Result result = Result.fail("支付失败");

        try {
            int count = 0;
            while (true) {
                //到微信支付查询支付状态
                Map<String, String> resultMap = weixinPayService.queryPayStatus(outTradeNo);

                if (resultMap == null) {
                    break;
                }
                if ("SUCCESS".equals(resultMap.get("trade_state"))) {
                    result = Result.ok("支付成功");
                    //需要更新秒杀订单支付状态
                    seckillOrderService.saveOrderInRedisToDb(outTradeNo, resultMap.get("transaction_id"));

                    break;
                    //return result;
                }

                //每3秒查询一次
                Thread.sleep(3000);

                count++;
                if (count > 20) {
                    result = Result.fail("支付超时");

                    //关闭微信支付的订单
                    resultMap = weixinPayService.closeOrder(outTradeNo);
                    if ("ORDERPAID".equals(resultMap.get("err_code"))) {//如果在关闭中被支付了，那么标识为支付成功
                        //需要更新订单的支付状态
                        seckillOrderService.saveOrderInRedisToDb(outTradeNo, resultMap.get("transaction_id"));

                        result = Result.ok("支付成功");

                        break;
                        //return result;
                    } else {
                        //如果是关闭订单成功；则删除redis中的订单并加回库存
                        seckillOrderService.deleteOrderInRedis(outTradeNo);
                    }

                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
