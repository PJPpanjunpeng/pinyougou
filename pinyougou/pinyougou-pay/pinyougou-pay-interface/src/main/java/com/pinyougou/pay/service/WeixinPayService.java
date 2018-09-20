package com.pinyougou.pay.service;

import java.util.Map;

public interface WeixinPayService {

    /**
     * 根据支付日志id到微信支付创建支付订单并返回支付二维码地址信息
     * @param outTrandeNo 支付日志id
     * @param totalFee 支付总金额
     * @return 支付二维码地址信息
     */
    Map<String, String> createNative(String outTrandeNo, String totalFee);
}
